package com.jun0dev.cms.user.client.service.customer;

import com.jun0dev.cms.user.client.domain.model.Customer;
import com.jun0dev.cms.user.client.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Optional<Customer> findByIdAndEmail(Long id, String email) {
        return customerRepository.findById(id)
                .stream().filter(customer -> customer.getEmail().equals(email)).findFirst();
    }
    public Optional<Customer> findValidCustomer(String email, String password) {
        return customerRepository.findByEmail(email).stream()
                .filter(customer -> customer.getPassword().equals(password) && customer.getVerify()
                ).findFirst();

    }
}
