package com.teamketo.shop.entity;

public enum OrderStatus {
    ORDER_COMPLETE,   // 주문완료
    READY,            // 배송준비
    SHIPPING,         // 배송중
    DELIVERED,        // 배송완료
    CANCELLED         // 취소
}
