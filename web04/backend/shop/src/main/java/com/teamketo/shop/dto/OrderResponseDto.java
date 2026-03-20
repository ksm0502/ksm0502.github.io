package com.teamketo.shop.dto;

import com.teamketo.shop.entity.Order;
import com.teamketo.shop.entity.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderResponseDto {

    private Long id;
    private Integer totalPrice;
    private OrderStatus status;
    private String deliveryAddress;
    private String deliveryReceiver;
    private String deliveryPhone;
    private LocalDateTime createdAt;
    private String memberName;
    private String memberEmail;
    private List<OrderItemDto> orderItems;

    public OrderResponseDto(Order order) {
        this.id = order.getId();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus();
        this.deliveryAddress = order.getDeliveryAddress();
        this.deliveryReceiver = order.getDeliveryReceiver();
        this.deliveryPhone = order.getDeliveryPhone();
        this.createdAt = order.getCreatedAt();
        this.memberName = order.getMember().getName();
        this.memberEmail = order.getMember().getEmail();
        this.orderItems = order.getOrderItems().stream()
                .map(OrderItemDto::new)
                .collect(Collectors.toList());
    }

    @Getter
    public static class OrderItemDto {
        private Long id;
        private String productName;
        private Integer quantity;
        private Integer price;

        public OrderItemDto(com.teamketo.shop.entity.OrderItem item) {
            this.id = item.getId();
            this.productName = item.getProduct().getName();
            this.quantity = item.getQuantity();
            this.price = item.getPrice();
        }
    }
}
