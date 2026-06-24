package com.chandrgupt.resumebuilderapi.repository;

import com.chandrgupt.resumebuilderapi.document.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment,String> {

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);
    List<Payment> findByUserIdOrderByCreatedAtDesc(String UserId);
    List<Payment> findByStatus(String Status);

}
