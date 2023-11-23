package com.jun0dev.cms.user.client.service;

import com.jun0dev.cms.user.client.domain.SignUpForm;
import com.jun0dev.cms.user.client.domain.model.Customer;
import com.jun0dev.cms.user.client.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

    private final CustomerRepository customerRepository;

    public Customer signUp(SignUpForm form) {
        return customerRepository.save(Customer.from(form));
    }
}
