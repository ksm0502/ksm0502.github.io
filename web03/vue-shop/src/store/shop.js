import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useShopStore = defineStore('shop', () => {
  // State
  const products = ref([])
  const categories = ref([])
  const banners = ref([])
  const reviews = ref([])
  const cart = ref([])
  const wishlist = ref([])
  const loading = ref(false)
  const toast = ref({ show: false, message: '', type: 'success' })

  // Load data from JSON
  async function fetchData() {
    loading.value = true
    try {
      const res = await fetch('/products.json')
      const data = await res.json()
      products.value = data.products
      categories.value = data.categories
      banners.value = data.banners
      reviews.value = data.reviews
    } catch (e) {
      console.error('Failed to load data:', e)
    } finally {
      loading.value = false
    }
  }

  // Computed
  const cartCount = computed(() => cart.value.reduce((sum, i) => sum + i.qty, 0))
  const cartTotal = computed(() => cart.value.reduce((sum, i) => sum + i.price * i.qty, 0))
  const wishlistCount = computed(() => wishlist.value.length)

  function getProductById(id) {
    return products.value.find(p => p.id === Number(id))
  }

  function getProductsByCategory(categoryId) {
    if (categoryId === 'all') return products.value
    return products.value.filter(p => p.category === categoryId)
  }

  function getReviewsByProductId(productId) {
    return reviews.value.filter(r => r.productId === Number(productId))
  }

  // Cart actions
  function addToCart(product, qty = 1, selectedColor = null, selectedSize = null) {
    const key = `${product.id}-${selectedColor}-${selectedSize}`
    const existing = cart.value.find(i => i.key === key)
    if (existing) {
      existing.qty += qty
    } else {
      cart.value.push({
        key,
        id: product.id,
        name: product.name,
        price: product.price,
        image: product.images[0],
        color: selectedColor,
        size: selectedSize,
        qty
      })
    }
    showToast(`"${product.name}"이 장바구니에 추가되었습니다.`, 'success')
  }

  function removeFromCart(key) {
    cart.value = cart.value.filter(i => i.key !== key)
  }

  function updateCartQty(key, delta) {
    const item = cart.value.find(i => i.key === key)
    if (item) {
      item.qty = Math.max(1, item.qty + delta)
    }
  }

  function clearCart() {
    cart.value = []
  }

  // Wishlist actions
  function toggleWishlist(product) {
    const idx = wishlist.value.findIndex(i => i.id === product.id)
    if (idx > -1) {
      wishlist.value.splice(idx, 1)
      showToast('위시리스트에서 제거되었습니다.', 'info')
    } else {
      wishlist.value.push({
        id: product.id,
        name: product.name,
        price: product.price,
        originalPrice: product.originalPrice,
        image: product.images[0],
        badge: product.badge,
        rating: product.rating
      })
      showToast('위시리스트에 추가되었습니다.', 'success')
    }
  }

  function isInWishlist(productId) {
    return wishlist.value.some(i => i.id === productId)
  }

  // Toast
  function showToast(message, type = 'success') {
    toast.value = { show: true, message, type }
    setTimeout(() => { toast.value.show = false }, 3000)
  }

  return {
    products, categories, banners, reviews,
    cart, wishlist, loading, toast,
    cartCount, cartTotal, wishlistCount,
    fetchData, getProductById, getProductsByCategory, getReviewsByProductId,
    addToCart, removeFromCart, updateCartQty, clearCart,
    toggleWishlist, isInWishlist, showToast
  }
})