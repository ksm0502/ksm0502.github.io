<template>
  <div class="py-5">
    <div class="container">
      <!-- Breadcrumb -->
      <nav class="mb-4">
        <ol class="breadcrumb" style="font-size: 13px;">
          <li class="breadcrumb-item"><RouterLink to="/" class="text-muted">홈</RouterLink></li>
          <li class="breadcrumb-item"><RouterLink to="/products" class="text-muted">상품</RouterLink></li>
          <li class="breadcrumb-item active text-dark">{{ product?.name }}</li>
        </ol>
      </nav>

      <!-- Loading -->
      <div v-if="store.loading" class="text-center py-5">
        <div class="spinner-border" style="color: var(--color-accent);"></div>
      </div>

      <!-- Not found -->
      <div v-else-if="!product" class="text-center py-5">
        <i class="bi bi-exclamation-circle fs-1 text-muted mb-3 d-block"></i>
        <p class="text-muted">상품을 찾을 수 없습니다.</p>
        <RouterLink to="/products" class="btn btn-dark btn-sm mt-2">상품 목록으로</RouterLink>
      </div>

      <template v-else>
        <div class="row g-5">
          <!-- Images -->
          <div class="col-md-6">
            <div class="position-relative overflow-hidden mb-3" style="border-radius: 4px; background: var(--color-light);">
              <img :src="selectedImage" :alt="product.name" class="w-100" style="object-fit: cover; max-height: 600px; transition: opacity 0.3s;" />
              <span v-if="product.badge" class="position-absolute top-0 start-0 m-3 badge-custom" :class="badgeClass">
                {{ product.badge }}
              </span>
              <!-- Wishlist -->
              <button
                class="position-absolute top-0 end-0 m-3 btn rounded-circle"
                :class="isWishlisted ? 'btn-dark' : 'btn-light'"
                @click="store.toggleWishlist(product)"
                style="width: 42px; height: 42px; display: flex; align-items: center; justify-content: center;"
              >
                <i class="bi" :class="isWishlisted ? 'bi-heart-fill' : 'bi-heart'"></i>
              </button>
            </div>
            <!-- Thumbnails -->
            <div class="d-flex gap-2">
              <div
                v-for="(img, idx) in product.images"
                :key="idx"
                class="thumb-img"
                :class="{ active: selectedImage === img }"
                @click="selectedImage = img"
                style="width: 80px; height: 80px; border-radius: 4px; overflow: hidden; cursor: pointer; border: 2px solid transparent; transition: border-color 0.2s;"
              >
                <img :src="img" :alt="`${product.name} ${idx+1}`" class="w-100 h-100" style="object-fit: cover;" />
              </div>
            </div>
          </div>

          <!-- Detail -->
          <div class="col-md-6">
            <p class="text-muted text-uppercase mb-1" style="font-size: 12px; letter-spacing: 2px;">{{ categoryName }}</p>
            <h1 class="font-display mb-2" style="font-size: 2rem;">{{ product.name }}</h1>

            <!-- Rating -->
            <div class="d-flex align-items-center gap-2 mb-3">
              <div>
                <i v-for="n in 5" :key="n" class="bi text-warning"
                   :class="n <= Math.round(product.rating) ? 'bi-star-fill' : 'bi-star'"></i>
              </div>
              <span class="text-muted" style="font-size: 13px;">{{ product.rating }} ({{ product.reviewCount }}개 리뷰)</span>
            </div>

            <!-- Price -->
            <div class="d-flex align-items-center gap-3 mb-4">
              <span class="fs-3 fw-bold">{{ formatPrice(product.price) }}</span>
              <span v-if="discountRate > 0" class="badge text-bg-danger">{{ discountRate }}% OFF</span>
              <span v-if="product.price < product.originalPrice" class="text-muted text-decoration-line-through">
                {{ formatPrice(product.originalPrice) }}
              </span>
            </div>

            <p class="text-muted mb-4" style="line-height: 1.8; font-size: 15px;">{{ product.description }}</p>

            <!-- Color -->
            <div class="mb-3">
              <p class="fw-500 mb-2" style="font-size: 14px;">색상 <span class="text-muted fw-normal" style="font-size: 12px;">{{ selectedColor || '선택해주세요' }}</span></p>
              <div class="d-flex gap-2">
                <button
                  v-for="color in product.colors"
                  :key="color"
                  class="color-btn"
                  :class="{ selected: selectedColor === color }"
                  :style="{ background: color }"
                  @click="selectedColor = color"
                  :title="color"
                ></button>
              </div>
            </div>

            <!-- Size -->
            <div class="mb-4">
              <div class="d-flex justify-content-between mb-2">
                <p class="fw-500 mb-0" style="font-size: 14px;">사이즈 <span class="text-muted fw-normal" style="font-size: 12px;">{{ selectedSize || '선택해주세요' }}</span></p>
                <a href="#" class="text-muted" style="font-size: 13px;">사이즈 가이드</a>
              </div>
              <div class="d-flex flex-wrap gap-2">
                <button
                  v-for="size in product.sizes"
                  :key="size"
                  class="btn btn-sm"
                  :class="selectedSize === size ? 'btn-dark' : 'btn-outline-secondary'"
                  style="min-width: 52px; font-size: 13px;"
                  @click="selectedSize = size"
                >
                  {{ size }}
                </button>
              </div>
            </div>

            <!-- Qty + Add -->
            <div class="d-flex gap-3 mb-4 align-items-center">
              <div class="d-flex align-items-center border" style="border-radius: 4px;">
                <button class="btn px-3 py-2" @click="qty = Math.max(1, qty - 1)">
                  <i class="bi bi-dash"></i>
                </button>
                <span class="px-3" style="min-width: 40px; text-align: center; font-weight: 500;">{{ qty }}</span>
                <button class="btn px-3 py-2" @click="qty++">
                  <i class="bi bi-plus"></i>
                </button>
              </div>
              <button class="btn btn-dark flex-grow-1 py-2" @click="addToCart" :disabled="!selectedSize">
                <i class="bi bi-bag-plus me-2"></i>
                {{ !selectedSize ? '사이즈를 선택해주세요' : '장바구니 담기' }}
              </button>
            </div>

            <button class="btn btn-outline-dark w-100 py-2 mb-4" @click="buyNow" :disabled="!selectedSize">
              바로 구매하기
            </button>

            <!-- Stock info -->
            <div class="d-flex align-items-center gap-2 text-muted mb-4" style="font-size: 13px;">
              <i class="bi bi-box-seam"></i>
              <span>재고 {{ product.stock }}개 남음</span>
              <span>·</span>
              <i class="bi bi-truck"></i>
              <span>오늘 주문시 내일 배송</span>
            </div>

            <!-- Tags -->
            <div class="d-flex flex-wrap gap-2">
              <span v-for="tag in product.tags" :key="tag" class="badge text-bg-light text-muted border" style="font-size: 12px; font-weight: 400;">
                #{{ tag }}
              </span>
            </div>
          </div>
        </div>

        <!-- Reviews -->
        <div class="mt-5 pt-4 border-top">
          <h3 class="font-display mb-4">상품 리뷰</h3>
          <div v-if="productReviews.length === 0" class="text-muted">아직 리뷰가 없습니다.</div>
          <div v-else class="row g-3">
            <div v-for="review in productReviews" :key="review.id" class="col-12 col-md-6">
              <div class="p-4 bg-light-cream" style="border-radius: 4px;">
                <div class="d-flex justify-content-between align-items-start mb-2">
                  <div>
                    <span class="fw-500">{{ review.author }}</span>
                    <div>
                      <i v-for="n in 5" :key="n" class="bi text-warning"
                         :class="n <= review.rating ? 'bi-star-fill' : 'bi-star'" style="font-size: 12px;"></i>
                    </div>
                  </div>
                  <span class="text-muted" style="font-size: 12px;">{{ review.date }}</span>
                </div>
                <p class="mb-0 text-muted" style="font-size: 14px; line-height: 1.7;">{{ review.content }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Related Products -->
        <div class="mt-5 pt-4 border-top">
          <h3 class="font-display mb-4">관련 상품</h3>
          <div class="row g-3 g-lg-4">
            <div v-for="p in relatedProducts" :key="p.id" class="col-6 col-md-3">
              <ProductCard :product="p" />
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useShopStore } from '@/store/shop'
import ProductCard from '@/components/ProductCard.vue'

const store = useShopStore()
const route = useRoute()
const router = useRouter()

const product = computed(() => store.getProductById(route.params.id))
const selectedImage = ref('')
const selectedColor = ref(null)
const selectedSize = ref(null)
const qty = ref(1)

watch(product, (p) => {
  if (p) {
    selectedImage.value = p.images[0]
    selectedColor.value = p.colors[0]
    selectedSize.value = null
    qty.value = 1
  }
}, { immediate: true })

const isWishlisted = computed(() => product.value && store.isInWishlist(product.value.id))
const categoryName = computed(() => {
  if (!product.value) return ''
  const cat = store.categories.find(c => c.id === product.value.category)
  return cat ? cat.name : ''
})
const discountRate = computed(() => {
  if (!product.value || product.value.price >= product.value.originalPrice) return 0
  return Math.round((1 - product.value.price / product.value.originalPrice) * 100)
})
const badgeClass = computed(() => ({
  'badge-sale': product.value?.badge === 'SALE',
  'badge-new': product.value?.badge === 'NEW',
  'badge-best': product.value?.badge === 'BEST'
}))
const productReviews = computed(() => store.getReviewsByProductId(route.params.id))
const relatedProducts = computed(() => {
  if (!product.value) return []
  return store.products.filter(p => p.category === product.value.category && p.id !== product.value.id).slice(0, 4)
})

function formatPrice(v) { return v.toLocaleString('ko-KR') + '원' }

function addToCart() {
  if (!selectedSize.value) { store.showToast('사이즈를 선택해주세요.', 'error'); return }
  store.addToCart(product.value, qty.value, selectedColor.value, selectedSize.value)
}

function buyNow() {
  if (!selectedSize.value) { store.showToast('사이즈를 선택해주세요.', 'error'); return }
  store.addToCart(product.value, qty.value, selectedColor.value, selectedSize.value)
  router.push('/checkout')
}
</script>

<style scoped>
.color-btn {
  width: 32px; height: 32px;
  border-radius: 50%;
  border: 2px solid #ddd;
  cursor: pointer;
  transition: all 0.2s;
}
.color-btn.selected { border-color: var(--color-primary); box-shadow: 0 0 0 2px white, 0 0 0 4px var(--color-primary); }
.thumb-img:hover { border-color: var(--color-accent) !important; }
.thumb-img.active { border-color: var(--color-primary) !important; }
</style>