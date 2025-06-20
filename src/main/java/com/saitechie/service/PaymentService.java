package com.saitechie.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saitechie.dto.OrderDTO;
import com.saitechie.dto.UserDTO;
import com.saitechie.entity.Payment;
import com.saitechie.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
@Slf4j
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private RestTemplate restTemplate;

    String URL = "http://localhost:9090/users";

    @KafkaListener(topics = "order_topic")
    public Payment paymentProcessing(String orderDetailsJSONString) {
        try {
            OrderDTO orderDTO = new ObjectMapper().readValue(orderDetailsJSONString, OrderDTO.class);
            log.info("Payment Processing Initiated for the userId "+orderDTO.getUserId());
            Payment payment = Payment.builder()
                    .paymentMode(orderDTO.getPaymentMode())
                    .orderId(orderDTO.getOrderId())
                    .amount(orderDTO.getPrice())
                    .paidDate(new Date())
                    .userId(orderDTO.getUserId())
                    .build();
            if (orderDTO.getPaymentMode().equals("COD")) {
                payment.setPaymentStatus("PENDING");
                //No need to check from the Bank whether the available balance is present or not to process the order
            } else {
                //PaymentMode is Either Debit or Credit or UPI, so conenct with UserService and check available balance
                //if available balance is present grater than the order price then process the order and make payment
                //Will use synchronous mechanism
                UserDTO userDTO = restTemplate.getForObject(URL + "/" + payment.getUserId(), UserDTO.class);
                if (userDTO.getAvailableAmount() < payment.getAmount()) {
                    throw new RuntimeException(" Insufficient Funds !.. ");
                } else {
                    payment.setPaymentStatus("PAID");
                    restTemplate.put(URL + "/" + payment.getUserId() + "/" + payment.getAmount(), null);
                    log.info("Successfully Payment Completed for the User ID "+payment.getUserId());
                }
            }
            return paymentRepository.save(payment);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Payment getPaymentDetailsByOrderId(String orderId){
        return paymentRepository.findByOrderId(orderId);
    }
}
