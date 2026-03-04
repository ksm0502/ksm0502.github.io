import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import ProductsView from '@/views/ProductsView.vue'
import ProductDetailView from '@/views/ProductDetailView.vue'
import CartView from '@/views/CartView.vue'
import WishlistView from '@/views/WishlistView.vue'
import CheckoutView from '@/views/CheckoutView.vue'
import OrderCompleteView from '@/views/OrderCompleteView.vue'

const routes = [
    { path: '/', name: 'home', component: HomeView },
    { path: '/products', name: 'products', component: ProductsView },
    { path: '/products/:id', name: 'product-detail', component: ProductDetailView },
    { path: '/cart', name: 'cart', component: CartView },
    { path: '/wishlist', name: 'wishlist', component: WishlistView },
    { path: '/checkout', name: 'checkout', component: CheckoutView },
    { path: '/order-complete', name: 'order-complete', component: OrderCompleteView }
]

const router = createRouter({
        history: createWebHistory(import.meta.env.BASE_URL),
        routes,
        scrollBehavior () {
            return { top: 0, behavior: 'smooth' }
        }
    }
)

export default router