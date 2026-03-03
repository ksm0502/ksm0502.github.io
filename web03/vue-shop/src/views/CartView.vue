<template>
  <div class="py-5">
    <div class="container">
      <h1 class="font-display mb-4">장바구니</h1>

      <div v-if="store.cart.length === 0" class="text-center py-5">
        <i class="bi bi-bag fs-1 text-muted mb-3 d-block"></i>
        <p class="text-muted mb-3">장바구니가 비어있습니다.</p>
        <RouterLink to="/products" class="btn btn-dark px-4">쇼핑 계속하기</RouterLink>
      </div>

      <div v-else class="row g-4">
        <!-- Cart Items -->
        <div class="col-lg-8">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <span class="text-muted" style="font-size: 14px;">총 {{ store.cartCount }}개 상품</span>
            <button class="btn btn-sm btn-link text-danger p-0" @click="store.clearCart()">전체 삭제</button>
          </div>

          <div class="d-flex flex-column gap-3">
            <div
              v-for="item in store.cart"
              :key="item.key"
              class="cart-item d-flex gap-3 p-3 bg-white border"
              style="border-radius: 4px;"
            >
              <RouterLink :to="`/products/${item.id}`" class="flex-shrink-0">
                <img :src="item.image" :alt="item.name" style="width: 100px; height: 120px; object-fit: cover; border-radius: 4px;" />
              </RouterLink>
              <div class="flex-grow-1">
                <div class="d-flex justify-content-between">
                  <RouterLink :to="`/products/${item.id}`" class="fw-500" style="font-size: 15px;">{{ item.name }}</RouterLink>
                  <button class="btn btn-sm btn-link text-muted p-0" @click="store.removeFromCart(item.key)">
                    <i class="bi bi-x-lg"></i>
                  </button>
                </div>
                <div class="text-muted mt-1 mb-2" style="font-size: 13px;">
                  <span v-if="item.color" class="me-2">
                    색상 <span class="d-inline-block rounded-circle border" :style="{background: item.color, width: '12px', height: '12px', verticalAlign: 'middle'}"></span>
                  </span>
                  <span v-if="item.size">사이즈: {{ item.size }}</span>
                </div>
                <div class="d-flex align-items-center justify-content-between">
                  <div class="d-flex align-items-center border" style="border-radius: 4px; width: fit-content;">
                    <button class="btn btn-sm px-2 py-1" @click="store.updateCartQty(item.key, -1)"><i class="bi bi-dash"></i></button>
                    <span class="px-3" style="min-width: 36px; text-align: center;">{{ item.qty }}</span>
                    <button class="btn btn-sm px-2 py-1" @click="store.updateCartQty(item.key, 1)"><i class="bi bi-plus"></i></button>
                  </div>
                  <span class="fw-bold">{{ (item.price * item.qty).toLocaleString() }}원</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Order Summary -->
        <div class="col-lg-4">
          <div class="p-4 bg-light-cream" style="border-radius: 4px; position: sticky; top: 90px;">
            <h5 class="font-display mb-4">주문 요약</h5>
            <div class="d-flex justify-content-between mb-2" style="font-size: 14px;">
              <span>상품 금액</span>
              <span>{{ store.cartTotal.toLocaleString() }}원</span>
            </div>
            <div class="d-flex justify-content-between mb-2" style="font-size: 14px;">
              <span>배송비</span>
              <span :class="shippingFree ? 'text-success' : ''">
                {{ shippingFree ? '무료' : '3,000원' }}
              </span>
            </div>
            <div v-if="!shippingFree" class="mb-2" style="font-size: 12px; color: var(--color-accent);">
              {{ (50000 - store.cartTotal).toLocaleString() }}원 더 구매시 무료 배송
            </div>
            <hr />
            <div class="d-flex justify-content-between fw-bold fs-5 mb-4">
              <span>총 결제금액</span>
              <span>{{ totalWithShipping.toLocaleString() }}원</span>
            </div>
            <RouterLink to="/checkout" class="btn btn-dark w-100 py-2 mb-2">
              구매하기 ({{ store.cartCount }}개)
            </RouterLink>
            <RouterLink to="/products" class="btn btn-outline-dark w-100 py-2" style="font-size: 14px;">
              쇼핑 계속하기
            </RouterLink>

            <!-- Payment icons -->
            <div class="d-flex justify-content-center gap-2 mt-3">
              <i class="bi bi-credit-card text-muted fs-5"></i>
              <i class="bi bi-phone text-muted fs-5"></i>
              <i class="bi bi-shield-check text-muted fs-5"></i>
            </div>
            <p class="text-center text-muted mt-2 mb-0" style="font-size: 11px;">안전 결제 보장</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useShopStore } from '@/store/shop'

const store = useShopStore()
const shippingFree = computed(() => store.cartTotal >= 50000)
const totalWithShipping = computed(() => store.cartTotal + (shippingFree.value ? 0 : 3000))
</script>