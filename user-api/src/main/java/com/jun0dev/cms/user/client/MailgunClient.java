package com.jun0dev.cms.user.client;

import com.jun0dev.cms.user.client.mailgun.SendMailForm;
import feign.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "mailgun", url = "https://api.mailgun.net/v3/")
@Qualifier("mailgun")
public interface MailgunClient {
    @PostMapping("sandbox619ee2f9a63240b8a17033e45ff4c65f.mailgun.org/messages")
    Response sendEmail(@SpringQueryMap SendMailForm form);

}