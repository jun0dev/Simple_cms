package com.jun0dev.cms.user.client.controller;

import com.jun0dev.cms.user.client.application.SignUpApplication;
import com.jun0dev.cms.user.client.domain.SignUpForm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpApplication signUpApplication;

    @PostMapping("/customer")
    public ResponseEntity<String> customerSignUp(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(signUpApplication.customerSignUp(form));
    }

    @GetMapping("/customer/verify")
    public ResponseEntity<String> verifyCustomer(@RequestParam("email") String email,@RequestParam("code") String code) {
        signUpApplication.customerVerify(email,code);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }

    @PostMapping("/seller")
    public ResponseEntity<String> sellerSignUp(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(signUpApplication.sellerSignUp(form));
    }

    @GetMapping("/seller/verify")
    public ResponseEntity<String> verifySeller(@RequestParam("email") String email,@RequestParam("code") String code) {
        signUpApplication.sellerVerify(email,code);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }
}
