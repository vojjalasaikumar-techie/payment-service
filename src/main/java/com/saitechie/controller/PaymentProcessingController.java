package com.saitechie.controller;

import com.saitechie.entity.Payment;
import com.saitechie.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentProcessingController {

    @Autowired
    private PaymentService paymentService;


    @GetMapping("/{orderId}")
    public Payment getPayment(@PathVariable String orderId) {
        return paymentService.getPaymentDetailsByOrderId(orderId);
    }

}
