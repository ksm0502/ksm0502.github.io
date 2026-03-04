<template>
  <div>
    <!-- Hero Banner Carousel -->
    <section class="hero-section">
      <div id="heroBannerCarousel" class="carousel slide carousel-fade" data-bs-ride="carousel" data-bs-interval="5000">
        <div class="carousel-inner">
          <div
            v-for="(banner, idx) in store.banners"
            :key="banner.id"
            class="carousel-item"
            :class="{ active: idx === 0 }"
          >
            <div class="hero-slide position-relative" style="height: 88vh; min-height: 500px;">
              <img :src="banner.image" :alt="banner.title" class="w-100 h-100" style="object-fit: cover;" />
              <div class="position-absolute top-0 start-0 w-100 h-100" style="background: linear-gradient(to right, rgba(0,0,0,0.5) 0%, rgba(0,0,0,0.1) 70%);"></div>
              <div class="position-absolute top-50 translate-middle-y text-white px-4 px-lg-5" style="max-width: 600px; left: 5%;">
                <p class="text-uppercase mb-2" style="letter-spacing: 3px; font-size: 13px; opacity: 0.8;">{{ banner.subtitle }}</p>
                <h1 class="font-display mb-3" style="font-size: clamp(2.5rem, 6vw, 5rem); line-height: 1.1;">{{ banner.title }}</h1>
                <p class="mb-4" style="opacity: 0.85; font-size: 16px; max-width: 400px;">{{ banner.description }}</p>
                <RouterLink :to="`/products?category=${banner.category}`" class="btn btn-light px-4 py-2" style="font-family: var(--font-body); font-weight: 500; letter-spacing: 0.5px;">
                  {{ banner.cta }} <i class="bi bi-arrow-right ms-2"></i>
                </RouterLink>
              </div>
            </div>
          </div>
        </div>
        <button class="carousel-control-prev" type="button" data-bs-target="#heroBannerCarousel" data-bs-slide="prev">
          <span class="carousel-control-prev-icon"></span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#heroBannerCarousel" data-bs-slide="next">
          <span class="carousel-control-next-icon"></span>
        </button>
        <div class="carousel-indicators">
          <button v-for="(_, idx) in store.banners" :key="idx" type="button"
            data-bs-target="#heroBannerCarousel" :data-bs-slide-to="idx"
            :class="{ active: idx === 0 }"></button>
        </div>
      </div>
    </section>

    <!-- Category Quick Links -->
    <section class="py-5 bg-light-cream">
      <div class="container">
        <div class="row g-3">
          <div v-for="cat in mainCategories" :key="cat.id" class="col-6 col-md-3">
            <RouterLink :to="`/products?category=${cat.id}`" class="cat-link d-block text-center py-4 px-2 bg-white" style="border-radius: 4px; transition: var(--transition);">
              <i class="bi fs-1 mb-2 d-block" :class="catIcon(cat.id)" style="color: var(--color-accent);"></i>
              <span style="font-size: 14px; font-weight: 500; letter-spacing: 0.5px;">{{ cat.name }}</span>
            </RouterLink>
          </div>
        </div>
      </div>
    </section>

    <!-- New Arrivals -->
    <section class="py-5">
      <div class="container">
        <div class="d-flex justify-content-between align-items-end mb-4">
          <div>
            <p class="text-muted text-uppercase mb-1" style="font-size: 12px; letter-spacing: 2px;">신상품</p>
            <h2 class="font-display mb-0" style="font-size: 2rem;">New Arrivals</h2>
          </div>
          <RouterLink to="/products?badge=NEW" class="btn btn-outline-dark btn-sm">전체 보기 <i class="bi bi-arrow-right"></i></RouterLink>
        </div>
        <div class="row g-3 g-lg-4">
          <div v-for="product in newProducts" :key="product.id" class="col-6 col-md-4 col-lg-3 fade-in-up">
            <ProductCard :product="product" />
          </div>
        </div>
      </div>
    </section>

    <!-- Promo Banner -->
    <section class="py-5 bg-dark text-white text-center">
      <div class="container">
        <p class="text-uppercase mb-2" style="letter-spacing: 3px; font-size: 12px; color: var(--color-accent);">Special Offer</p>
        <h2 class="font-display mb-3" style="font-size: 2.5rem;">시즌 세일 최대 30% OFF</h2>
        <p class="text-white-50 mb-4">한정 기간만 제공되는 특별 할인 혜택. 지금 바로 쇼핑하세요.</p>
        <RouterLink to="/products?badge=SALE" class="btn px-5 py-2" style="background: var(--color-accent); color: #fff; font-weight: 500;">
          세일 상품 보기
        </RouterLink>
      </div>
    </section>

    <!-- Best Sellers -->
    <section class="py-5">
      <div class="container">
        <div class="d-flex justify-content-between align-items-end mb-4">
          <div>
            <p class="text-muted text-uppercase mb-1" style="font-size: 12px; letter-spacing: 2px;">베스트셀러</p>
            <h2 class="font-display mb-0" style="font-size: 2rem;">Best Sellers</h2>
          </div>
          <RouterLink to="/products?badge=BEST" class="btn btn-outline-dark btn-sm">전체 보기 <i class="bi bi-arrow-right"></i></RouterLink>
        </div>
        <div class="row g-3 g-lg-4">
          <div v-for="product in bestProducts" :key="product.id" class="col-6 col-md-4 col-lg-3">
            <ProductCard :product="product" />
          </div>
        </div>
      </div>
    </section>

    <!-- Features -->
    <section class="py-5 bg-light-cream">
      <div class="container">
        <div class="row g-4 text-center">
          <div v-for="feature in features" :key="feature.title" class="col-6 col-md-3">
            <i class="bi fs-2 mb-3 d-block" :class="feature.icon" style="color: var(--color-accent);"></i>
            <h6 class="fw-600 mb-1" style="font-size: 14px;">{{ feature.title }}</h6>
            <p class="text-muted mb-0" style="font-size: 13px;">{{ feature.desc }}</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useShopStore } from '@/store/shop'
import ProductCard from '@/components/ProductCard.vue'

const store = useShopStore()

onMounted(() => {
  store.fetchData()
})

const mainCategories = computed(() => store.categories.filter(c => c.id !== 'all'))
const newProducts = computed(() => store.products.filter(p => p.badge === 'NEW').slice(0, 4))
const bestProducts = computed(() => store.products.filter(p => p.badge === 'BEST').slice(0, 4))

const features = [
  { icon: 'bi-truck', title: '무료 배송', desc: '5만원 이상 구매시 무료 배송' },
  { icon: 'bi-arrow-return-left', title: '30일 반품', desc: '조건없는 30일 무료 반품' },
  { icon: 'bi-shield-check', title: '안전 결제', desc: '안전한 암호화 결제 시스템' },
  { icon: 'bi-headset', title: '고객 지원', desc: '24/7 전문 고객 지원 서비스' }
]

function catIcon(id) {
  const icons = { clothing: 'bi-bag', accessories: 'bi-gem', shoes: 'bi-bootstrap', bags: 'bi-handbag' }
  return icons[id] || 'bi-grid'
}
</script>

<style scoped>
.cat-link:hover {
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  transform: translateY(-3px);
}
</style>