package com.futuraforge.accounts.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.futuraforge.accounts.dto.CardsDto;
import com.futuraforge.accounts.dto.LoansDto;

import jakarta.validation.constraints.Pattern;

@FeignClient(name="cards", fallback=CardsFallback.class)
public interface CardsFeignClient {
	
    @GetMapping(value = "/api/fetch", consumes = "application/json")
    public ResponseEntity<CardsDto> fetchCardDetails(
    									@RequestHeader("ffcbank-correlation-id") String correlationId, 
    									@RequestParam String mobileNumber);

}
