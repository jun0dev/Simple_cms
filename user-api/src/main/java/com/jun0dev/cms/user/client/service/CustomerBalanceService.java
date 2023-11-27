package com.jun0dev.cms.user.client.service;

import com.jun0dev.cms.user.client.domain.cutomer.ChangeBalanceForm;
import com.jun0dev.cms.user.client.domain.model.CustomerBalanceHistory;
import com.jun0dev.cms.user.client.exception.CustomException;
import com.jun0dev.cms.user.client.exception.ErrorCode;
import com.jun0dev.cms.user.client.repository.CustomerBalanceHistoryRepository;
import com.jun0dev.cms.user.client.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerBalanceService {
    private final CustomerBalanceHistoryRepository customerBalanceHistoryRepository;
    private final CustomerRepository customerRepository;

    @Transactional(noRollbackFor = {CustomException.class})
    public CustomerBalanceHistory changeBalance(Long customerId, ChangeBalanceForm form) throws CustomException {
        CustomerBalanceHistory customerBalanceHistory =
                customerBalanceHistoryRepository.findFirstByCustomer_IdOrderByIdDesc(customerId)
                        .orElse(CustomerBalanceHistory.builder()
                                .changeMoney(0)
                                .currentMoney(0)
                                .customer(customerRepository.findById(customerId)
                                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER)))
                                .build());
        if (customerBalanceHistory.getCurrentMoney() + form.getMoney() < 0) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_BALANCE);
        }
        customerBalanceHistory = customerBalanceHistory.builder()
                .changeMoney(form.getMoney())
                .currentMoney(customerBalanceHistory.getCurrentMoney()+form.getMoney())
                .description(form.getMessage())
                .fromMessage(form.getFrom())
                .customer(customerBalanceHistory.getCustomer())
                .build();
        customerBalanceHistory.getCustomer().setBalance(customerBalanceHistory.getCurrentMoney());

        return customerBalanceHistoryRepository.save(customerBalanceHistory);
    }

}
