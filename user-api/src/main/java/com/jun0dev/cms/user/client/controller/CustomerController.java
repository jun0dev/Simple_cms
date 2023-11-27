package com.jun0dev.cms.user.client.controller;

import com.jun0dev.cms.user.client.domain.cutomer.ChangeBalanceForm;
import com.jun0dev.cms.user.client.domain.cutomer.CustomerDto;
import com.jun0dev.cms.user.client.domain.model.Customer;
import com.jun0dev.cms.user.client.exception.CustomException;
import com.jun0dev.cms.user.client.exception.ErrorCode;
import com.jun0dev.cms.user.client.service.CustomerBalanceService;
import com.jun0dev.cms.user.client.service.CustomerService;
import com.jun0dev.config.JwtAuthenticationProvider;
import com.jun0dev.domain.common.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final JwtAuthenticationProvider provider;
    private final CustomerService customerService;
    private final CustomerBalanceService customerBalanceService;
    @GetMapping("/getInfo")
    public ResponseEntity<CustomerDto> getInfo(@RequestHeader(name="X-AUTH-TOKEN") String token) {
        UserVo vo = provider.getUserVo(token);
        Customer c = customerService.findByIdAndEmail(vo.getId(), vo.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER));
        return ResponseEntity.ok(CustomerDto.from(c));
    }


    @PostMapping("/balance")
    public ResponseEntity<Integer> changeBalance(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                                 @RequestBody ChangeBalanceForm form) {
        UserVo vo = provider.getUserVo(token);

        return ResponseEntity.ok(customerBalanceService.changeBalance(vo.getId(), form).getCurrentMoney());
    }

}
