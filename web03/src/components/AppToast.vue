<template>
  <Teleport to="body">
    <div class="toast-container position-fixed bottom-0 end-0 p-3">
      <Transition name="slide-up">
        <div
          v-if="store.toast.show"
          class="toast show align-items-center border-0 shadow-lg"
          :class="toastClass"
          role="alert"
        >
          <div class="d-flex align-items-center p-3 gap-2">
            <i class="bi" :class="iconClass" style="font-size: 18px;"></i>
            <div class="toast-body p-0" style="font-family: var(--font-body); font-size: 14px;">
              {{ store.toast.message }}
            </div>
          </div>
        </div>
      </Transition>
    </div>
  </Teleport>
</template>

<script setup>
import { computed } from 'vue'
import { useShopStore } from '@/store/shop'

const store = useShopStore()

const toastClass = computed(() => ({
  'bg-dark text-white': store.toast.type === 'success',
  'bg-info text-white': store.toast.type === 'info',
  'bg-danger text-white': store.toast.type === 'error'
}))

const iconClass = computed(() => ({
  'bi-check-circle-fill': store.toast.type === 'success',
  'bi-info-circle-fill': store.toast.type === 'info',
  'bi-exclamation-circle-fill': store.toast.type === 'error'
}))
</script>

<style scoped>
.slide-up-enter-active, .slide-up-leave-active { transition: all 0.3s ease; }
.slide-up-enter-from, .slide-up-leave-to { opacity: 0; transform: translateY(20px); }
</style>