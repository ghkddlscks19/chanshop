package com.chanshop.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(400, "C001", "잘못된 입력값입니다"),
    METHOD_NOT_ALLOWED(405, "C002", "허용되지 않는 메서드입니다"),
    INTERNAL_SERVER_ERROR(500, "C003", "서버 오류가 발생했습니다"),
    INVALID_TYPE_VALUE(400, "C004", "잘못된 타입입니다"),
    HANDLE_ACCESS_DENIED(403, "C005", "접근이 거부되었습니다"),

    // User
    USER_NOT_FOUND(404, "U001", "사용자를 찾을 수 없습니다"),
    DUPLICATE_EMAIL(409, "U002", "이미 존재하는 이메일입니다"),
    INVALID_PASSWORD(400, "U003", "비밀번호가 일치하지 않습니다"),
    UNAUTHORIZED_USER(401, "U004", "인증되지 않은 사용자입니다"),

    // Product
    PRODUCT_NOT_FOUND(404, "P001", "상품을 찾을 수 없습니다"),
    OUT_OF_STOCK(400, "P002", "재고가 부족합니다"),

    // Order
    ORDER_NOT_FOUND(404, "O001", "주문을 찾을 수 없습니다"),
    INVALID_ORDER_STATUS(400, "O002", "유효하지 않은 주문 상태입니다"),

    // Cart
    CART_NOT_FOUND(404, "CT001", "장바구니를 찾을 수 없습니다"),
    CART_ITEM_NOT_FOUND(404, "CT002", "장바구니 항목을 찾을 수 없습니다"),

    // Review
    REVIEW_NOT_FOUND(404, "R001", "리뷰를 찾을 수 없습니다"),
    REVIEW_ALREADY_EXISTS(409, "R002", "이미 리뷰가 존재합니다"),

    // Wishlist
    WISHLIST_NOT_FOUND(404, "W001", "위시리스트를 찾을 수 없습니다"),
    WISHLIST_ITEM_ALREADY_EXISTS(409, "W002", "이미 위시리스트에 존재하는 상품입니다");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
