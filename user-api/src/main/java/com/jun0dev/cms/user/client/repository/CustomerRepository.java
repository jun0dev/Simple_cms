package com.jun0dev.cms.user.client.repository;

import com.jun0dev.cms.user.client.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}