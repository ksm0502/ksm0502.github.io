package com.teamketo.shop.repository;

import com.teamketo.shop.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
