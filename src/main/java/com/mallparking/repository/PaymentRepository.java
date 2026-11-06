package com.mallparking.repository;
import com.mallparking.entity.Payment;
import com.mallparking.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByParkingSessionId(Long sessionId);
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);
    List<Payment> findByPaymentTimeBetween(LocalDateTime start, LocalDateTime end);
}
