package com.saitechie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private Integer id;
    private String name;
    private String category;
    private double price;
    private Date purchasedDate;
    private String orderId;
    private Integer userId;
    private String paymentMode;
}
