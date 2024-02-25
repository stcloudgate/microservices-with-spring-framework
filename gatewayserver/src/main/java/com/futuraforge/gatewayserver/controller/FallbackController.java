package com.futuraforge.gatewayserver.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class FallbackController {
	
	// since the gateway server is based-on spring reactive - we need to make use of same for implementing any functionality.
	// the keyword Mono belongs to spring reactive world.
	@RequestMapping("/contactSupport")
	public Mono<String> contactSupport() {
		return Mono.just("An error occured while calling the microservice.\n please try after sometime OR contact support teams!!!");
		/*
		 * in real-life scenario - process a transaction roll-back, 
		 * generate logs entries and trigger email notifications. The actual project situation may demand heavy backoff process 
		 * that can be implemented using additional beans/components or database operations OR even external APIs to compensation 
		 * flows in partner systems  
		 * 
		 */
	}

}
