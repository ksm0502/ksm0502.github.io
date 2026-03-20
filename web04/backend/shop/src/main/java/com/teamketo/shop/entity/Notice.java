package com.teamketo.shop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private int hits;

    private String author;

    private String attachment;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void incrementHits() {
        this.hits++;
    }
    public void update(String title, String content, String author, String attachment) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.attachment = attachment;
    }
}
