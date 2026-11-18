package com.teamketo.shop.service;

import com.teamketo.shop.entity.Member;
import com.teamketo.shop.entity.Order;
import com.teamketo.shop.entity.OrderStatus;
import com.teamketo.shop.entity.Product;
import com.teamketo.shop.entity.Role;
import com.teamketo.shop.repository.MemberRepository;
import com.teamketo.shop.repository.OrderRepository;
import com.teamketo.shop.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class OrderInventoryTests {

    @Autowired private OrderService orderService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ProductRepository productRepository;

    private final List<Long> memberIds = new ArrayList<>();
    private final List<Long> productIds = new ArrayList<>();

    @AfterEach
    void removeFixtures() {
        for (Long memberId : memberIds) {
            orderRepository.deleteAll(orderRepository.findByMemberIdOrderByCreatedAtDesc(memberId));
        }
        productRepository.deleteAllById(productIds);
        memberRepository.deleteAllById(memberIds);
    }

    @Test
    void concurrentOrdersCannotBothBuyTheLastUnit() throws Exception {
        Long first = createMember();
        Long second = createMember();
        Long productId = createProduct(1);

        List<HttpStatus> results = concurrently(
                () -> tryOrder(first, productId),
                () -> tryOrder(second, productId));

        assertThat(results).containsExactlyInAnyOrder(HttpStatus.OK, HttpStatus.CONFLICT);
        assertThat(stock(productId)).isZero();
        assertThat(orderRepository.findByMemberIdOrderByCreatedAtDesc(first).size()
                + orderRepository.findByMemberIdOrderByCreatedAtDesc(second).size()).isEqualTo(1);
    }

    @Test
    void concurrentMultiProductOrdersUseAConsistentLockOrder() throws Exception {
        Long first = createMember();
        Long second = createMember();
        Long productA = createProduct(10);
        Long productB = createProduct(10);

        List<Long> orders = concurrently(
                () -> placeOrder(first, List.of(productA, productB), List.of(2, 3)).getId(),
                () -> placeOrder(second, List.of(productB, productA), List.of(1, 4)).getId());

        assertThat(orders).doesNotHaveDuplicates().allMatch(orderRepository::existsById);
        assertThat(stock(productA)).isEqualTo(4);
        assertThat(stock(productB)).isEqualTo(6);
    }

    @Test
    void customerAndAdminCancellationRestoreStockOnce() throws Exception {
        Long customer = createMember();
        Long productId = createProduct(10);
        Long orderId = placeOrder(customer, List.of(productId, productId), List.of(1, 2)).getId();
        assertThat(stock(productId)).isEqualTo(7);

        concurrently(
                () -> {
                    orderService.cancelOrder(orderId, customer);
                    return orderId;
                },
                () -> {
                    orderService.changeOrderStatus(orderId, OrderStatus.CANCELLED);
                    return orderId;
                });
        orderService.cancelOrder(orderId, customer);
        orderService.changeOrderStatus(orderId, OrderStatus.CANCELLED);

        assertThat(stock(productId)).isEqualTo(10);
        assertThat(orderRepository.findById(orderId).orElseThrow().getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThatThrownBy(() -> orderService.changeOrderStatus(orderId, OrderStatus.READY))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void checkoutAndCancellationBothApplyTheirStockChanges() throws Exception {
        Long first = createMember();
        Long second = createMember();
        Long productId = createProduct(10);
        Long existing = placeOrder(first, List.of(productId), List.of(2)).getId();

        concurrently(
                () -> {
                    orderService.cancelOrder(existing, first);
                    return existing;
                },
                () -> placeOrder(second, List.of(productId), List.of(3)).getId());

        assertThat(stock(productId)).isEqualTo(7);
        assertThat(orderRepository.findById(existing).orElseThrow().getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(orderRepository.findByMemberIdOrderByCreatedAtDesc(second)).hasSize(1);
    }

    @Test
    void shippedOrderCannotBeCancelledByCustomerOrAdmin() {
        Long customer = createMember();
        Long productId = createProduct(10);
        Long orderId = placeOrder(customer, List.of(productId), List.of(2)).getId();
        orderService.changeOrderStatus(orderId, OrderStatus.SHIPPING);

        assertThatThrownBy(() -> orderService.cancelOrder(orderId, customer))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        assertThatThrownBy(() -> orderService.changeOrderStatus(orderId, OrderStatus.CANCELLED))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        assertThat(stock(productId)).isEqualTo(8);
        assertThat(orderRepository.findById(orderId).orElseThrow().getStatus()).isEqualTo(OrderStatus.SHIPPING);
    }

    @Test
    void insufficientStockOnLaterLineRollsBackEarlierStockChange() {
        Long customer = createMember();
        Long first = createProduct(10);
        Long second = createProduct(1);

        assertThatThrownBy(() -> placeOrder(customer, List.of(first, second), List.of(2, 2)))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        assertThat(stock(first)).isEqualTo(10);
        assertThat(stock(second)).isEqualTo(1);
        assertThat(orderRepository.findByMemberIdOrderByCreatedAtDesc(customer)).isEmpty();
    }

    private Long createMember() {
        Member member = memberRepository.save(Member.builder().name("Inventory test")
                .email(UUID.randomUUID() + "@example.test")
                .password("unused-by-service-test").role(Role.USER).build());
        memberIds.add(member.getId());
        return member.getId();
    }

    private Long createProduct(int stock) {
        Product product = productRepository.save(Product.builder().name("Inventory test product")
                .price(10_000).stock(stock).categoryId(1L).build());
        productIds.add(product.getId());
        return product.getId();
    }

    private int stock(Long id) {
        return productRepository.findById(id).orElseThrow().getStock();
    }

    private Order placeOrder(Long memberId, List<Long> products, List<Integer> quantities) {
        return orderService.createOrder(memberId, products, quantities, "QA address", "QA customer", "010-0000-0000");
    }

    private HttpStatus tryOrder(Long memberId, Long productId) {
        try {
            placeOrder(memberId, List.of(productId), List.of(1));
            return HttpStatus.OK;
        } catch (ResponseStatusException error) {
            if (error.getStatusCode().equals(HttpStatus.CONFLICT)) {
                return HttpStatus.CONFLICT;
            }
            throw error;
        }
    }

    private static <T> List<T> concurrently(Callable<T> first, Callable<T> second) throws Exception {
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var firstResult = executor.submit(startTogether(first, ready, start));
            var secondResult = executor.submit(startTogether(second, ready, start));
            if (!ready.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Both order workers must be ready");
            }
            start.countDown();
            return List.of(firstResult.get(15, TimeUnit.SECONDS), secondResult.get(15, TimeUnit.SECONDS));
        }
    }

    private static <T> Callable<T> startTogether(Callable<T> task, CountDownLatch ready, CountDownLatch start) {
        return () -> {
            ready.countDown();
            if (!start.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Order workers were not started");
            }
            return task.call();
        };
    }
}
