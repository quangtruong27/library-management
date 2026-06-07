package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPaymentDetailRepository extends JpaRepository<PaymentDetail, Long> {
	List<PaymentDetail> findByFineId(Long fineId);
}
