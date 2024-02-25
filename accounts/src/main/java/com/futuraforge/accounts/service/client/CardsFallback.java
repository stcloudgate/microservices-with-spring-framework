package com.futuraforge.accounts.service.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.futuraforge.accounts.dto.CardsDto;
import com.futuraforge.accounts.dto.LoansDto;

@Component
public class CardsFallback implements CardsFeignClient{

	@Override
	public ResponseEntity<CardsDto> fetchCardDetails(String correlationId, String mobileNumber) {
		// in real life situations, you can raise exceptions, generate log entries and trigger email notifications
		// OR even implement retry logic
		return null;
	}


}
