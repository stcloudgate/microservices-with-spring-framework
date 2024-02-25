package com.futuraforge.accounts;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.futuraforge.accounts.dto.AccountsContactInfoDto;

@SpringBootApplication
@EnableFeignClients //enables feign clients related functionality
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@EnableConfigurationProperties(value = {AccountsContactInfoDto.class})
@OpenAPIDefinition(
		info = @Info(
				title = "Accounts microservice REST API Documentation",
				description = "FFC Accounts microservice REST API Documentation",
				version = "v1",
				contact = @Contact(
						name = "Sanjeev Tiwari",
						email = "tutor@futuraforge.com",
						url = "https://www.futuraforge.com"
				),
				license = @License(
						name = "Apache 2.0",
						url = "https://www.futuraforge.com"
				)
		),
		externalDocs = @ExternalDocumentation(
				description =  "FFC Accounts microservice REST API Documentation",
				url = "https://www.futuraforge.com/swagger-ui.html"
		)
)
public class AccountsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsApplication.class, args);
	}

}
