package com.jun0dev.cms.user.client.application;

import com.jun0dev.cms.user.client.domain.SignInForm;
import com.jun0dev.cms.user.client.domain.model.Customer;
import com.jun0dev.cms.user.client.domain.model.Seller;
import com.jun0dev.cms.user.client.exception.CustomException;
import com.jun0dev.cms.user.client.service.customer.CustomerService;
import com.jun0dev.cms.user.client.service.seller.SellerService;
import com.jun0dev.config.JwtAuthenticationProvider;
import com.jun0dev.domain.common.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.jun0dev.cms.user.client.exception.ErrorCode.LOGIN_CHECK_FAIL;

@Service
@RequiredArgsConstructor
public class SignInApplication {
    private final CustomerService customerService;
    private final SellerService sellerService;
    private final JwtAuthenticationProvider provider;
    public String customerLoginToken(SignInForm form) {
        Customer c = customerService.findValidCustomer(form.getEmail(), form.getPassword())
                .orElseThrow(() -> new CustomException(LOGIN_CHECK_FAIL));
        return provider.createToken(c.getEmail(), c.getId(), UserType.CUSTOMER);
    }
    public String sellerLoginToken(SignInForm form) {
        Seller s = sellerService.findValidSeller(form.getEmail(), form.getPassword())
                .orElseThrow(() -> new CustomException(LOGIN_CHECK_FAIL));
        return provider.createToken(s.getEmail(), s.getId(), UserType.SELLER);
    }


}
