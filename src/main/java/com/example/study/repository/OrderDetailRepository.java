package com.example.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.study.model.Entity.OrderDetail;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
