package com.shopwell.api.repository;

import com.shopwell.api.model.entity.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaystackPaymentRepository extends JpaRepository<PaymentDetail, Long> {
}
