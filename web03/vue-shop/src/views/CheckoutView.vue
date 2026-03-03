<template>
  <div class="py-5">
    <div class="container">
      <h1 class="font-display mb-2">결제하기</h1>
      <!-- Steps -->
      <div class="d-flex align-items-center gap-2 mb-5" style="font-size: 13px; color: var(--color-muted);">
        <span class="fw-500 text-dark">1. 배송 정보</span>
        <i class="bi bi-chevron-right"></i>
        <span :class="step >= 2 ? 'fw-500 text-dark' : ''">2. 결제 수단</span>
        <i class="bi bi-chevron-right"></i>
        <span :class="step >= 3 ? 'fw-500 text-dark' : ''">3. 확인 및 결제</span>
      </div>

      <div v-if="store.cart.length === 0" class="text-center py-5">
        <p class="text-muted mb-3">장바구니가 비어있습니다.</p>
        <RouterLink to="/products" class="btn btn-dark">쇼핑하기</RouterLink>
      </div>

      <div v-else class="row g-4">
        <!-- Form -->
        <div class="col-lg-7">
          <!-- Step 1: Shipping -->
          <div v-if="step === 1">
            <h5 class="font-display mb-4">배송 정보</h5>
            <div class="row g-3">
              <div class="col-6">
                <label class="form-label small fw-500">이름</label>
                <input v-model="form.name" type="text" class="form-control" placeholder="홍길동" />
              </div>
              <div class="col-6">
                <label class="form-label small fw-500">전화번호</label>
                <input v-model="form.phone" type="tel" class="form-control" placeholder="010-0000-0000" />
              </div>
              <div class="col-12">
                <label class="form-label small fw-500">이메일</label>
                <input v-model="form.email" type="email" class="form-control" placeholder="example@email.com" />
              </div>
              <div class="col-4">
                <label class="form-label small fw-500">우편번호</label>
                <input v-model="form.zipcode" type="text" class="form-control" placeholder="12345" />
              </div>
              <div class="col-8">
                <label class="form-label small fw-500">주소</label>
                <input v-model="form.address" type="text" class="form-control" placeholder="서울시 강남구" />
              </div>
              <div class="col-12">
                <label class="form-label small fw-500">상세 주소</label>
                <input v-model="form.addressDetail" type="text" class="form-control" placeholder="상세 주소 입력" />
              </div>
              <div class="col-12">
                <label class="form-label small fw-500">배송 메모</label>
                <select v-model="form.memo" class="form-select">
                  <option value="">배송 메모 선택 (선택)</option>
                  <option>문 앞에 놓아주세요</option>
                  <option>경비실에 맡겨주세요</option>
                  <option>배송 전 연락 바랍니다</option>
                  <option>직접 입력</option>
                </select>
              </div>
            </div>
            <button class="btn btn-dark w-100 py-2 mt-4" @click="nextStep">
              다음 단계 <i class="bi bi-arrow-right ms-1"></i>
            </button>
          </div>

          <!-- Step 2: Payment -->
          <div v-if="step === 2">
            <h5 class="font-display mb-4">결제 수단</h5>
            <div class="d-flex flex-column gap-2 mb-4">
              <label v-for="method in paymentMethods" :key="method.value" class="d-flex align-items-center gap-3 p-3 border" :class="{ 'border-dark': form.payment === method.value }" style="border-radius: 4px; cursor: pointer;">
                <input type="radio" v-model="form.payment" :value="method.value" class="form-check-input m-0" />
                <i class="bi fs-5" :class="method.icon"></i>
                <span style="font-size: 14px;">{{ method.label }}</span>
              </label>
            </div>
            <!-- Card inputs -->
            <div v-if="form.payment === 'card'" class="row g-3 mb-4">
              <div class="col-12">
                <label class="form-label small fw-500">카드 번호</label>
                <input type="text" class="form-control" placeholder="0000 0000 0000 0000" />
              </div>
              <div class="col-6">
                <label class="form-label small fw-500">유효 기간</label>
                <input type="text" class="form-control" placeholder="MM/YY" />
              </div>
              <div class="col-6">
                <label class="form-label small fw-500">CVC</label>
                <input type="text" class="form-control" placeholder="000" />
              </div>
            </div>
            <div class="d-flex gap-2">
              <button class="btn btn-outline-dark py-2 px-4" @click="step = 1">
                <i class="bi bi-arrow-left me-1"></i> 이전
              </button>
              <button class="btn btn-dark flex-grow-1 py-2" @click="nextStep">
                다음 단계 <i class="bi bi-arrow-right ms-1"></i>
              </button>
            </div>
          </div>

          <!-- Step 3: Confirm -->
          <div v-if="step === 3">
            <h5 class="font-display mb-4">주문 확인</h5>
            <div class="p-3 bg-light-cream mb-4" style="border-radius: 4px; font-size: 14px;">
              <p class="fw-500 mb-1">배송지</p>
              <p class="text-muted mb-0">{{ form.name }} · {{ form.phone }}</p>
              <p class="text-muted mb-0">{{ form.address }} {{ form.addressDetail }}</p>
            </div>
            <div class="d-flex flex-column gap-2 mb-4">
              <div v-for="item in store.cart" :key="item.key" class="d-flex align-items-center gap-3">
                <img :src="item.image" :alt="item.name" style="width: 60px; height: 70px; object-fit: cover; border-radius: 4px;" />
                <div class="flex-grow-1">
                  <p class="fw-500 mb-0" style="font-size: 14px;">{{ item.name }}</p>
                  <p class="text-muted mb-0" style="font-size: 12px;">{{ item.size }} / 수량: {{ item.qty }}</p>
                </div>
                <span class="fw-500">{{ (item.price * item.qty).toLocaleString() }}원</span>
              </div>
            </div>
            <div class="d-flex gap-2">
              <button class="btn btn-outline-dark py-2 px-4" @click="step = 2">
                <i class="bi bi-arrow-left me-1"></i> 이전
              </button>
              <button class="btn btn-dark flex-grow-1 py-2" @click="placeOrder">
                <i class="bi bi-lock me-1"></i> 결제 완료 ({{ totalWithShipping.toLocaleString() }}원)
              </button>
            </div>
          </div>
        </div>

        <!-- Order Summary -->
        <div class="col-lg-5">
          <div class="p-4 bg-light-cream" style="border-radius: 4px; position: sticky; top: 90px;">
            <h5 class="font-display mb-3">주문 상품</h5>
            <div class="d-flex flex-column gap-2 mb-3" style="max-height: 300px; overflow-y: auto;">
              <div v-for="item in store.cart" :key="item.key" class="d-flex align-items-center gap-2">
                <img :src="item.image" :alt="item.name" style="width: 50px; height: 60px; object-fit: cover; border-radius: 4px;" />
                <div class="flex-grow-1">
                  <p class="mb-0" style="font-size: 13px; font-weight: 500;">{{ item.name }}</p>
                  <p class="text-muted mb-0" style="font-size: 12px;">{{ item.size }} · x{{ item.qty }}</p>
                </div>
                <span style="font-size: 13px; font-weight: 600;">{{ (item.price * item.qty).toLocaleString() }}원</span>
              </div>
            </div>
            <hr />
            <div class="d-flex justify-content-between mb-2" style="font-size: 14px;">
              <span>상품 금액</span><span>{{ store.cartTotal.toLocaleString() }}원</span>
            </div>
            <div class="d-flex justify-content-between mb-2" style="font-size: 14px;">
              <span>배송비</span><span :class="shippingFree ? 'text-success' : ''">{{ shippingFree ? '무료' : '3,000원' }}</span>
            </div>
            <hr />
            <div class="d-flex justify-content-between fw-bold fs-5">
              <span>총 결제금액</span><span>{{ totalWithShipping.toLocaleString() }}원</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useShopStore } from '@/store/shop'

const store = useShopStore()
const router = useRouter()
const step = ref(1)

const form = ref({
  name: '', phone: '', email: '', zipcode: '', address: '', addressDetail: '', memo: '', payment: 'card'
})

const paymentMethods = [
  { value: 'card', label: '신용카드 / 체크카드', icon: 'bi-credit-card' },
  { value: 'kakao', label: '카카오페이', icon: 'bi-phone' },
  { value: 'naver', label: '네이버페이', icon: 'bi-n-circle' },
  { value: 'bank', label: '계좌이체', icon: 'bi-bank' }
]

const shippingFree = computed(() => store.cartTotal >= 50000)
const totalWithShipping = computed(() => store.cartTotal + (shippingFree.value ? 0 : 3000))

function nextStep() {
  if (step.value === 1 && (!form.value.name || !form.value.phone || !form.value.address)) {
    store.showToast('배송 정보를 모두 입력해주세요.', 'error'); return
  }
  step.value++
}

function placeOrder() {
  store.clearCart()
  router.push('/order-complete')
}
</script>