<template>
    <div class="product-card h-100" @click="goToDetail">
    <div class="card border-0 h-100 overflow-hidden" style="cursor: pointer; border-radius: 4px;">
      <!-- Image -->
      <div class="position-relative overflow-hidden" style="padding-top: 125%; background: var(--color-light);">
        <img
          :src="product.images[0]"
          :alt="product.name"
          class="product-img position-absolute top-0 start-0 w-100 h-100"
          style="object-fit: cover; transition: transform 0.5s ease;"
          loading="lazy"
        />
        <!-- Hover image -->
        <img
          v-if="product.images[1]"
          :src="product.images[1]"
          :alt="product.name"
          class="product-img-hover position-absolute top-0 start-0 w-100 h-100"
          style="object-fit: cover; opacity: 0; transition: opacity 0.4s ease;"
          loading="lazy"
        />
        <!-- Badge -->
        <span v-if="product.badge" class="position-absolute top-0 start-0 m-2 badge-custom" :class="badgeClass">
          {{ product.badge }}
        </span>
        <!-- Wishlist btn -->
        <button
          class="wishlist-btn position-absolute top-0 end-0 m-2 btn btn-sm rounded-circle"
          :class="isWishlisted ? 'btn-dark' : 'btn-light'"
          @click.stop="store.toggleWishlist(product)"
          :title="isWishlisted ? '위시리스트 제거' : '위시리스트 추가'"
          style="width: 36px; height: 36px; display: flex; align-items: center; justify-content: center;"
        >
          <i class="bi" :class="isWishlisted ? 'bi-heart-fill' : 'bi-heart'" style="font-size: 14px;"></i>
        </button>
        <!-- Quick add overlay -->
        <div class="quick-add position-absolute bottom-0 start-0 end-0 p-2" style="background: rgba(255,255,255,0.95); transform: translateY(100%); transition: transform 0.3s ease;">
          <button class="btn btn-dark w-100 btn-sm" @click.stop="quickAdd">
            <i class="bi bi-bag-plus me-1"></i> 빠른 추가
          </button>
        </div>
      </div>

      <!-- Info -->
      <div class="card-body px-0 py-3">
        <p class="text-muted mb-1" style="font-size: 11px; letter-spacing: 1px; text-transform: uppercase;">
          {{ categoryName }}
        </p>
        <h6 class="card-title mb-1" style="font-family: var(--font-body); font-weight: 500; font-size: 15px;">
          {{ product.name }}
        </h6>
        <!-- Rating -->
        <div class="d-flex align-items-center gap-1 mb-2">
          <div>
            <i v-for="n in 5" :key="n" class="bi"
               :class="n <= Math.round(product.rating) ? 'bi-star-fill text-warning' : 'bi-star text-warning'"
               style="font-size: 11px;"></i>
          </div>
          <span class="text-muted" style="font-size: 12px;">({{ product.reviewCount }})</span>
        </div>
        <!-- Price -->
        <div class="d-flex align-items-center gap-2">
          <span class="fw-600" style="font-size: 16px;">{{ formatPrice(product.price) }}</span>
          <span v-if="product.price < product.originalPrice" class="text-muted text-decoration-line-through" style="font-size: 13px;">
            {{ formatPrice(product.originalPrice) }}
          </span>
          <span v-if="discountRate > 0" class="text-danger" style="font-size: 12px; font-weight: 600;">
            {{ discountRate }}%
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useShopStore } from '@/store/shop'

const props = defineProps({
  product: { type: Object, required: true }
})

const store = useShopStore()
const router = useRouter()

const isWishlisted = computed(() => store.isInWishlist(props.product.id))
const categoryName = computed(() => {
  const cat = store.categories.find(c => c.id === props.product.category)
  return cat ? cat.name : ''
})
const discountRate = computed(() => {
  if (props.product.price >= props.product.originalPrice) return 0
  return Math.round((1 - props.product.price / props.product.originalPrice) * 100)
})
const badgeClass = computed(() => ({
  'badge-sale': props.product.badge === 'SALE',
  'badge-new': props.product.badge === 'NEW',
  'badge-best': props.product.badge === 'BEST'
}))

function formatPrice(v) {
  return v.toLocaleString('ko-KR') + '원'
}
function goToDetail() {
  router.push(`/products/${props.product.id}`)
}
function quickAdd() {
  store.addToCart(props.product, 1, props.product.colors[0], props.product.sizes[0])
}
</script>

<style scoped>
.product-card:hover .product-img { transform: scale(1.04); }
.product-card:hover .product-img-hover { opacity: 1 !important; }
.product-card:hover .quick-add { transform: translateY(0) !important; }
.wishlist-btn { opacity: 0; transition: opacity 0.2s; }
.product-card:hover .wishlist-btn { opacity: 1; }
</style>