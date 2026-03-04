<template>
  <div class="py-5 text-center">
    <div class="container" style="max-width: 480px;">
      <div class="mb-4">
        <div class="d-inline-flex align-items-center justify-content-center rounded-circle mb-4"
          style="width: 80px; height: 80px; background: var(--color-light);">
          <i class="bi bi-check-lg fs-1" style="color: var(--color-accent);"></i>
        </div>
        <h2 class="font-display mb-2">주문 완료!</h2>
        <p class="text-muted mb-1">주문이 성공적으로 접수되었습니다.</p>
        <p class="text-muted" style="font-size: 14px;">주문 번호: <strong>{{ orderNum }}</strong></p>
      </div>

      <div class="p-4 bg-light-cream text-start mb-4" style="border-radius: 4px;">
        <h6 class="fw-600 mb-3">주문 안내</h6>
        <ul class="list-unstyled mb-0" style="font-size: 14px;">
          <li class="d-flex align-items-start gap-2 mb-2">
            <i class="bi bi-check-circle-fill mt-1" style="color: var(--color-accent); font-size: 14px;"></i>
            <span>입력하신 이메일로 주문 확인서가 발송됩니다.</span>
          </li>
          <li class="d-flex align-items-start gap-2 mb-2">
            <i class="bi bi-check-circle-fill mt-1" style="color: var(--color-accent); font-size: 14px;"></i>
            <span>오늘 주문하신 상품은 내일 배송 예정입니다.</span>
          </li>
          <li class="d-flex align-items-start gap-2">
            <i class="bi bi-check-circle-fill mt-1" style="color: var(--color-accent); font-size: 14px;"></i>
            <span>배송 시작 시 문자로 안내 드립니다.</span>
          </li>
        </ul>
      </div>

      <div class="d-flex gap-3">
        <RouterLink to="/" class="btn btn-outline-dark flex-grow-1 py-2">홈으로</RouterLink>
        <RouterLink to="/products" class="btn btn-dark flex-grow-1 py-2">쇼핑 계속하기</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const orderNum = ref('LUXE-' + Math.random().toString(36).substr(2, 9).toUpperCase())
</script>