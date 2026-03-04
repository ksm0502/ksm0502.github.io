import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
//공유 자원
export const useShopStore = defineStore('shop', () => {
    //State(ref)
    const products = ref([])
    const categories = ref([])
    const banners = ref([])
    const reviews = ref([])
    const loading = ref(false)
    const cart = ref([])
    const wishlist = ref([])
    const toast = ref({show: false, message: '', type: 'success'})

    //products.json(Spring Boot API): async, await, fetch
    async function fetchData() {
        loading.value = true
        try {
            const res = await fetch(import.meta.env.BASE_URL + '/products.json')
            const data = await res.json()
            products.value = data.products 
            categories.value = data.categories
            banners.value = data.banners
            reviews.value = data.reviews
        } catch (e) {
            console.error("정보를 가져오지 못했습니다.", e)
        } finally {
            loading.value = false
        }
    }

    //사용자 동작에 의한 계산(computed)
    const cartCount = computed(() => cart.value.reduce((sum, i) => sum+i.qty, 0)) //총수량
    const cartTotal = computed(() => cart.value.reduce((sum, i) => sum+i.price*i.qty, 0)) //총금액
    const wishlistCount = computed(() => wishlist.value.length)

    function isInWishlist(productId) {
        return wishlist.value.some(item => item.id === productId)
    }

    function toggleWishlist(product) {
        const index = wishlist.value.findIndex(item => item.id === product.id)

        if (index > -1) {
            wishlist.value.splice(index, 1)
        } else {
            wishlist.value.push(product)
        }
    }

    return {
        products, categories, banners, reviews, loading, cart, wishlist, toast,
        cartCount, cartTotal, wishlistCount, isInWishlist, toggleWishlist,
        fetchData
    }
})