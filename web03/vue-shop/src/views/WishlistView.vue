<template>
  <div class="py-5">
    <div class="container">
      <h1 class="font-display mb-4">위시리스트</h1>

      <div v-if="store.wishlist.length === 0" class="text-center py-5">
        <i class="bi bi-heart fs-1 text-muted mb-3 d-block"></i>
        <p class="text-muted mb-3">위시리스트가 비어있습니다.</p>
        <RouterLink to="/products" class="btn btn-dark px-4">상품 둘러보기</RouterLink>
      </div>

      <div v-else>
        <p class="text-muted mb-4">총 {{ store.wishlist.length }}개의 상품</p>
        <div class="row g-3 g-lg-4">
          <div v-for="item in store.wishlist" :key="item.id" class="col-6 col-md-4 col-lg-3">
            <div class="card border-0 h-100 position-relative" style="border-radius: 4px; overflow: hidden;">
              <RouterLink :to="`/products/${item.id}`">
                <div style="padding-top: 120%; position: relative; background: var(--color-light);">
                  <img :src="item.image" :alt="item.name" class="position-absolute top-0 start-0 w-100 h-100" style="object-fit: cover;" />
                  <span v-if="item.badge" class="position-absolute top-0 start-0 m-2 badge-custom" :class="badgeClass(item.badge)">{{ item.badge }}</span>
                </div>
              </RouterLink>
              <!-- Remove btn -->
              <button
                class="position-absolute top-0 end-0 m-2 btn btn-sm btn-light rounded-circle"
                style="width: 32px; height: 32px; display: flex; align-items: center; justify-content: center;"
                @click="store.toggleWishlist(item)"
              >
                <i class="bi bi-heart-fill text-danger" style="font-size: 12px;"></i>
              </button>
              <div class="card-body px-0 py-3">
                <RouterLink :to="`/products/${item.id}`" class="fw-500 d-block mb-1" style="font-size: 15px;">{{ item.name }}</RouterLink>
                <div class="d-flex align-items-center gap-2 mb-2">
                  <i v-for="n in 5" :key="n" class="bi text-warning"
                     :class="n <= Math.round(item.rating) ? 'bi-star-fill' : 'bi-star'" style="font-size: 11px;"></i>
                </div>
                <div class="d-flex align-items-center gap-2 mb-2">
                  <span class="fw-bold">{{ item.price.toLocaleString() }}원</span>
                  <span v-if="item.price < item.originalPrice" class="text-muted text-decoration-line-through" style="font-size: 12px;">{{ item.originalPrice.toLocaleString() }}원</span>
                </div>
                <button class="btn btn-dark btn-sm w-100" @click="moveToCart(item)">
                  <i class="bi bi-bag-plus me-1"></i> 장바구니 담기
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useShopStore } from '@/store/shop'

const store = useShopStore()

function badgeClass(badge) {
  return { 'badge-sale': badge === 'SALE', 'badge-new': badge === 'NEW', 'badge-best': badge === 'BEST' }
}

function moveToCart(item) {
  const product = store.getProductById(item.id)
  if (product) {
    store.addToCart(product, 1, product.colors[0], product.sizes[0])
  }
}
</script>