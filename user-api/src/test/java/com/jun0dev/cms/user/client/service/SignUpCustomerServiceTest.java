package com.jun0dev.cms.user.client.service;

import com.jun0dev.cms.user.client.domain.SignUpForm;
import com.jun0dev.cms.user.client.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.time.LocalDate;

@SpringBootTest
class SignUpCustomerServiceTest {

    @Autowired
    private SignUpCustomerService service;
    @Test
    void signUp() {
        // given
        // when
        SignUpForm form = SignUpForm.builder()
                .name("name")
                .birth(LocalDate.now())
                .email("abcd@gmail.com")
                .password("1")
                .phone("01012341234")
                .build();
        Customer c = service.signUp(form);
        // then
        Assert.isTrue(service.signUp(form).getId()!=null);
    }
}