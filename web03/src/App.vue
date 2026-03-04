<script setup>
import { onMounted } from 'vue';
import { useShopStore } from './store/shop'
import AppHeader from './components/AppHeader.vue'
import AppFooter from './components/AppFooter.vue'
import AppToast from './components/AppToast.vue'
</script>

<template>
  <div>
    <AppHeader />
    <main style="min-height: 80vh">
      <RouterView v-slot="{ Component }">
        <Transition name="fade" mode="out-in">
          <component :is="Component"/>
        </Transition>
      </RouterView>
    </main>
    <AppFooter />
    <AppToast />
  </div>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.25s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>