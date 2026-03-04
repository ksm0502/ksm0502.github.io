<template>
  <header class="sticky-top bg-white border-bottom" style="z-index: 1030;">
    <!-- Top bar -->
    <div class="bg-dark text-white text-center py-1" style="font-size: 12px; letter-spacing: 1px;">
      무료 배송 · 30일 무료 반품 · 안전 결제
    </div>

    <!-- Main nav -->
    <nav class="navbar navbar-expand-lg px-3 px-lg-5" style="height: 72px;">
      <RouterLink class="navbar-brand font-display fw-bold fs-4" to="/" style="letter-spacing: -0.5px;">
        LUXE
      </RouterLink>

      <button class="navbar-toggler border-0 shadow-none" type="button" @click="mobileOpen = !mobileOpen">
        <i class="bi bi-list fs-4"></i>
      </button>

      <div :class="['collapse navbar-collapse', { show: mobileOpen }]">
        <ul class="navbar-nav me-auto gap-lg-2 ms-lg-4">
          <li class="nav-item">
            <RouterLink class="nav-link fw-500" to="/" @click="mobileOpen = false">홈</RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link fw-500" to="/products" @click="mobileOpen = false">전체 상품</RouterLink>
          </li>
          <li v-for="cat in mainCategories" :key="cat.id" class="nav-item">
            <RouterLink class="nav-link fw-500" :to="`/products?category=${cat.id}`" @click="mobileOpen = false">
              {{ cat.name }}
            </RouterLink>
          </li>
        </ul>

        <!-- Search -->
        <div class="d-flex align-items-center gap-2 my-2 my-lg-0">
          <div class="input-group" style="max-width: 220px;">
            <input
              v-model="searchQuery"
              type="text"
              class="form-control form-control-sm border-0 bg-light"
              placeholder="검색..."
              @keyup.enter="doSearch"
              style="font-family: var(--font-body);"
            />
            <button class="btn btn-sm btn-light border-0" @click="doSearch">
              <i class="bi bi-search"></i>
            </button>
          </div>

          <!-- Wishlist -->
          <RouterLink to="/wishlist" class="btn btn-sm btn-light border-0 position-relative" @click="mobileOpen = false">
            <i class="bi bi-heart fs-5"></i>
            <span v-if="store.wishlistCount > 0" class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-dark" style="font-size: 10px;">
              {{ store.wishlistCount }}
            </span>
          </RouterLink>

          <!-- Cart -->
          <RouterLink to="/cart" class="btn btn-sm btn-dark position-relative px-3" @click="mobileOpen = false">
            <i class="bi bi-bag me-1"></i>
            <span class="d-none d-lg-inline">장바구니</span>
            <span v-if="store.cartCount > 0" class="position-absolute top-0 start-100 translate-middle badge rounded-pill" style="background: var(--color-accent); font-size: 10px;">
              {{ store.cartCount }}
            </span>
          </RouterLink>
        </div>
      </div>
    </nav>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useShopStore } from '@/store/shop'

const store = useShopStore()
const router = useRouter()
const searchQuery = ref('')
const mobileOpen = ref(false)

const mainCategories = computed(() => store.categories.filter(c => c.id !== 'all'))

function doSearch() {
  if (searchQuery.value.trim()) {
    router.push(`/products?search=${encodeURIComponent(searchQuery.value.trim())}`)
    searchQuery.value = ''
    mobileOpen.value = false
  }
}
</script>

<style scoped>
.nav-link {
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--color-text);
  transition: color 0.2s;
}
.nav-link:hover, .router-link-active.nav-link {
  color: var(--color-accent);
}
</style>