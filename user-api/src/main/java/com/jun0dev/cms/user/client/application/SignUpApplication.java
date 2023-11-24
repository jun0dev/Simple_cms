package com.jun0dev.cms.user.client.application;

import com.jun0dev.cms.user.client.MailgunClient;
import com.jun0dev.cms.user.client.domain.SignUpForm;
import com.jun0dev.cms.user.client.domain.model.Customer;
import com.jun0dev.cms.user.client.exception.CustomException;
import com.jun0dev.cms.user.client.exception.ErrorCode;
import com.jun0dev.cms.user.client.mailgun.SendMailForm;
import com.jun0dev.cms.user.client.service.SignUpCustomerService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SignUpApplication {
    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;

    public void customerVerify(String email, String code) {
        signUpCustomerService.verifyEmail(email, code);
    }
    public String customerSignUp(SignUpForm form) {
        if (signUpCustomerService.isEmailExist(form.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_REGISTER_USER);
        } else {
            Customer c = signUpCustomerService.signUp(form);
            LocalDateTime now = LocalDateTime.now();

            String code = getRandomCode();

            SendMailForm sendMailForm = SendMailForm.builder()
                    .from("jjunyeong4361@gmail.com")
                    .to(c.getEmail())
                    .subject("verification Email!")
                    .text(getVerificationMailBody(c.getEmail(), c.getName(), code))
                    .build();

            mailgunClient.sendEmail(sendMailForm);
            signUpCustomerService.changeCustomerEmailValidateEmail(c.getId(), code);
            return "회원가입에 성공하였습니다.";
        }
    }

    private String getRandomCode() {
        return RandomStringUtils.random(10, true, true);
    }

    private String getVerificationMailBody(String email, String name, String code) {
        StringBuilder builder = new StringBuilder();
        return builder.append("Hello").append(name).append("! Please Click Link for verification.\n\n")
                .append("http://localhost:8082/signup/verify/customer?email=")
                .append(email)
                .append("&code=")
                .append(code).toString();
    }
}
