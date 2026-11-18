package com.teamketo.shop.dto;

import com.teamketo.shop.entity.Review;
import java.time.LocalDateTime;

public record PublicReviewResponse(Long id, Integer rating, String content, LocalDateTime createdAt,
                                   Author member) {

    public PublicReviewResponse(Review review) {
        this(review.getId(), review.getRating(), review.getContent(), review.getCreatedAt(),
                new Author(review.getMember().getName()));
    }

    public record Author(String name) {
    }
}
