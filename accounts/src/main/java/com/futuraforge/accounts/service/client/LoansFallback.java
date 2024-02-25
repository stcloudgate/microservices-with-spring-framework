package com.futuraforge.accounts.service.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.futuraforge.accounts.dto.LoansDto;

@Component
public class LoansFallback implements LoansFeignClient{

	@Override
	public ResponseEntity<LoansDto> fetchLoanDetails(String correlationId, String mobileNumber) {
		// in real life situations, you can raise exceptions, generate log entries and trigger email notifications
		// OR even implement retry logic
		return null;
	}

}
