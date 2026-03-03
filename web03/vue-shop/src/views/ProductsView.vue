<template>
  <div class="py-5">
    <div class="container">
      <!-- Page header -->
      <div class="mb-4">
        <h1 class="font-display mb-1">{{ pageTitle }}</h1>
        <p class="text-muted">{{ filteredProducts.length }}개의 상품</p>
      </div>

      <div class="row g-4">
        <!-- Sidebar Filters -->
        <div class="col-lg-3">
          <div class="filter-sidebar p-3 p-lg-4 bg-light-cream" style="border-radius: 4px; position: sticky; top: 90px;">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h6 class="fw-600 mb-0" style="letter-spacing: 0.5px;">필터</h6>
              <button class="btn btn-sm btn-link p-0 text-muted" @click="resetFilters" style="font-size: 13px;">초기화</button>
            </div>

            <!-- Category -->
            <div class="mb-4">
              <p class="fw-500 mb-2" style="font-size: 13px; letter-spacing: 0.5px; text-transform: uppercase;">카테고리</p>
              <div class="d-flex flex-column gap-2">
                <label v-for="cat in store.categories" :key="cat.id" class="d-flex align-items-center gap-2" style="cursor: pointer;">
                  <input type="radio" v-model="filters.category" :value="cat.id" class="form-check-input m-0" />
                  <span style="font-size: 14px;">{{ cat.name }}</span>
                </label>
              </div>
            </div>

            <!-- Price Range -->
            <div class="mb-4">
              <p class="fw-500 mb-2" style="font-size: 13px; letter-spacing: 0.5px; text-transform: uppercase;">가격대</p>
              <input type="range" class="form-range" v-model="filters.maxPrice" min="0" max="400000" step="10000" />
              <div class="d-flex justify-content-between">
                <span style="font-size: 13px;">0원</span>
                <span style="font-size: 13px; color: var(--color-accent);">{{ filters.maxPrice.toLocaleString() }}원</span>
              </div>
            </div>

            <!-- Badge -->
            <div class="mb-4">
              <p class="fw-500 mb-2" style="font-size: 13px; letter-spacing: 0.5px; text-transform: uppercase;">뱃지</p>
              <div class="d-flex flex-wrap gap-2">
                <button
                  v-for="badge in ['SALE', 'NEW', 'BEST']" :key="badge"
                  class="btn btn-sm"
                  :class="filters.badge === badge ? 'btn-dark' : 'btn-outline-secondary'"
                  style="font-size: 12px; letter-spacing: 0.5px;"
                  @click="filters.badge = filters.badge === badge ? '' : badge"
                >
                  {{ badge }}
                </button>
              </div>
            </div>

            <!-- Rating -->
            <div>
              <p class="fw-500 mb-2" style="font-size: 13px; letter-spacing: 0.5px; text-transform: uppercase;">최소 평점</p>
              <div class="d-flex gap-2">
                <button
                  v-for="r in [4, 4.5, 4.8]" :key="r"
                  class="btn btn-sm"
                  :class="filters.minRating === r ? 'btn-dark' : 'btn-outline-secondary'"
                  style="font-size: 12px;"
                  @click="filters.minRating = filters.minRating === r ? 0 : r"
                >
                  {{ r }}+ <i class="bi bi-star-fill text-warning" style="font-size: 10px;"></i>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Product Grid -->
        <div class="col-lg-9">
          <!-- Sort + Search -->
          <div class="d-flex flex-column flex-sm-row gap-2 justify-content-between align-items-start align-items-sm-center mb-4">
            <div class="input-group" style="max-width: 280px;">
              <span class="input-group-text bg-light border-0"><i class="bi bi-search"></i></span>
              <input type="text" class="form-control bg-light border-0" v-model="filters.search" placeholder="상품 검색..." style="font-family: var(--font-body);" />
            </div>
            <select v-model="filters.sort" class="form-select" style="max-width: 180px; font-family: var(--font-body); font-size: 14px;">
              <option value="default">기본 정렬</option>
              <option value="price-asc">가격 낮은 순</option>
              <option value="price-desc">가격 높은 순</option>
              <option value="rating">평점 높은 순</option>
              <option value="review">리뷰 많은 순</option>
            </select>
          </div>

          <!-- Loading -->
          <div v-if="store.loading" class="text-center py-5">
            <div class="spinner-border" style="color: var(--color-accent);"></div>
          </div>

          <!-- Empty -->
          <div v-else-if="filteredProducts.length === 0" class="text-center py-5">
            <i class="bi bi-search fs-1 text-muted mb-3 d-block"></i>
            <p class="text-muted">검색 결과가 없습니다.</p>
            <button class="btn btn-outline-dark btn-sm mt-2" @click="resetFilters">필터 초기화</button>
          </div>

          <!-- Grid -->
          <div v-else class="row g-3 g-lg-4">
            <div
              v-for="product in filteredProducts"
              :key="product.id"
              class="col-6 col-md-4"
            >
              <ProductCard :product="product" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useShopStore } from '@/store/shop'
import ProductCard from '@/components/ProductCard.vue'

const store = useShopStore()
const route = useRoute()

const filters = ref({
  category: 'all',
  maxPrice: 400000,
  badge: '',
  search: '',
  minRating: 0,
  sort: 'default'
})

// Sync filters from URL query params
onMounted(() => {
  if (route.query.category) filters.value.category = route.query.category
  if (route.query.search) filters.value.search = route.query.search
  if (route.query.badge) filters.value.badge = route.query.badge
})

watch(() => route.query, (q) => {
  if (q.category) filters.value.category = q.category
  if (q.search) filters.value.search = q.search
  if (q.badge) filters.value.badge = q.badge
}, { deep: true })

const filteredProducts = computed(() => {
  let list = [...store.products]
  if (filters.value.category !== 'all') list = list.filter(p => p.category === filters.value.category)
  if (filters.value.badge) list = list.filter(p => p.badge === filters.value.badge)
  if (filters.value.search) {
    const q = filters.value.search.toLowerCase()
    list = list.filter(p => p.name.toLowerCase().includes(q) || p.description.toLowerCase().includes(q))
  }
  list = list.filter(p => p.price <= filters.value.maxPrice)
  if (filters.value.minRating > 0) list = list.filter(p => p.rating >= filters.value.minRating)

  switch (filters.value.sort) {
    case 'price-asc': return list.sort((a, b) => a.price - b.price)
    case 'price-desc': return list.sort((a, b) => b.price - a.price)
    case 'rating': return list.sort((a, b) => b.rating - a.rating)
    case 'review': return list.sort((a, b) => b.reviewCount - a.reviewCount)
    default: return list
  }
})

const pageTitle = computed(() => {
  const cat = store.categories.find(c => c.id === filters.value.category)
  return cat && cat.id !== 'all' ? cat.name : '전체 상품'
})

function resetFilters() {
  filters.value = { category: 'all', maxPrice: 400000, badge: '', search: '', minRating: 0, sort: 'default' }
}
</script>