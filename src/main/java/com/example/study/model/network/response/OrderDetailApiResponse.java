package com.example.study.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailApiResponse {

    private Long id;

    private String status;

    private LocalDateTime arrivalDate;

    private Integer Quantity;

    private BigDecimal totalPrice;

    private Long itemId;

    private Long orderHistory;

    
}
