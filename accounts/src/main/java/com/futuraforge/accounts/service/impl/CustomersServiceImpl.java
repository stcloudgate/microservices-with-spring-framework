package com.futuraforge.accounts.service.impl;

import com.futuraforge.accounts.dto.AccountsDto;
import com.futuraforge.accounts.dto.CardsDto;
import com.futuraforge.accounts.dto.CustomerDetailsDto;
import com.futuraforge.accounts.dto.LoansDto;
import com.futuraforge.accounts.entity.Accounts;
import com.futuraforge.accounts.entity.Customer;
import com.futuraforge.accounts.exception.ResourceNotFoundException;
import com.futuraforge.accounts.mapper.AccountsMapper;
import com.futuraforge.accounts.mapper.CustomerMapper;
import com.futuraforge.accounts.repository.AccountsRepository;
import com.futuraforge.accounts.repository.CustomerRepository;
import com.futuraforge.accounts.service.ICustomersService;
import com.futuraforge.accounts.service.client.CardsFeignClient;
import com.futuraforge.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomersServiceImpl implements ICustomersService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    /**
     * @param mobileNumber - Input Mobile Number
     * @return Customer Details based on a given mobileNumber
     */
    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {
    	
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        // since accounts is the top-level api, its circuit-breaker is implemented in the api-gateway server. 
        //here we just need to set the dto class. 
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        // with circuit-breaker implementation for the feign-client, the fall back may return null if the circuit-break detects a failure. 
        // hence we need to handle the null return value
        if(null != loansDtoResponseEntity) {
            customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());        	
        }
        
        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);
        // with circuit-breaker implementation for the feign-client, the fall back may return null if the circuit-break detects a failure. 
        // hence we need to handle the null return value
        if(null != cardsDtoResponseEntity) {
            customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());        	
        }

        return customerDetailsDto;

    }
}