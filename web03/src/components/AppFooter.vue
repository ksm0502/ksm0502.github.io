<template>
  <footer class="bg-dark text-white pt-5 pb-3 mt-5">
    <div class="container">
      <div class="row g-4 mb-4">
        <div class="col-lg-4">
          <h3 class="font-display fs-2 mb-3">LUXE</h3>
          <p class="text-white-50 mb-3" style="font-size: 14px; line-height: 1.8;">
            트렌드를 선도하는 프리미엄 패션 브랜드.<br>
            당신의 스타일을 완성하는 특별한 경험.
          </p>
          <div class="d-flex gap-3">
            <a href="#" class="text-white-50 hover-accent"><i class="bi bi-instagram fs-5"></i></a>
            <a href="#" class="text-white-50 hover-accent"><i class="bi bi-facebook fs-5"></i></a>
            <a href="#" class="text-white-50 hover-accent"><i class="bi bi-pinterest fs-5"></i></a>
            <a href="#" class="text-white-50 hover-accent"><i class="bi bi-youtube fs-5"></i></a>
          </div>
        </div>
        <div class="col-6 col-lg-2">
          <h6 class="text-uppercase mb-3" style="letter-spacing: 1px; font-size: 12px; color: var(--color-accent);">쇼핑</h6>
          <ul class="list-unstyled">
            <li v-for="cat in store.categories" :key="cat.id" class="mb-2">
              <RouterLink :to="`/products?category=${cat.id}`" class="text-white-50 footer-link">
                {{ cat.name }}
              </RouterLink>
            </li>
          </ul>
        </div>
        <div class="col-6 col-lg-2">
          <h6 class="text-uppercase mb-3" style="letter-spacing: 1px; font-size: 12px; color: var(--color-accent);">고객 서비스</h6>
          <ul class="list-unstyled">
            <li v-for="item in ['배송 안내', '반품/교환', '사이즈 가이드', '자주 묻는 질문', '1:1 문의']" :key="item" class="mb-2">
              <a href="#" class="text-white-50 footer-link">{{ item }}</a>
            </li>
          </ul>
        </div>
        <div class="col-lg-4">
          <h6 class="text-uppercase mb-3" style="letter-spacing: 1px; font-size: 12px; color: var(--color-accent);">뉴스레터 구독</h6>
          <p class="text-white-50 mb-3" style="font-size: 13px;">새로운 컬렉션과 특별 할인 혜택을 가장 먼저 받아보세요.</p>
          <div class="input-group">
            <input type="email" class="form-control bg-white-10 border-secondary text-white" placeholder="이메일 주소" style="background: rgba(255,255,255,0.1);" />
            <button class="btn" style="background: var(--color-accent); color: #fff; font-size: 13px;">구독</button>
          </div>
        </div>
      </div>
      <hr class="border-secondary" />
      <div class="d-flex flex-column flex-md-row justify-content-between align-items-center">
        <p class="text-white-50 mb-0" style="font-size: 12px;">© 2026 LUXE. All rights reserved.</p>
        <div class="d-flex gap-3 mt-2 mt-md-0">
          <a href="#" class="text-white-50 footer-link" style="font-size: 12px;">개인정보처리방침</a>
          <a href="#" class="text-white-50 footer-link" style="font-size: 12px;">이용약관</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { useShopStore } from '@/store/shop'
const store = useShopStore()
</script>

<style scoped>
.footer-link { text-decoration: none; font-size: 14px; transition: color 0.2s; }
.footer-link:hover { color: var(--color-accent) !important; }
.hover-accent { transition: color 0.2s; }
.hover-accent:hover { color: var(--color-accent) !important; }
</style>