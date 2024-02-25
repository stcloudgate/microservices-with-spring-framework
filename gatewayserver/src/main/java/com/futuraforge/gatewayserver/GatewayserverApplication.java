package com.futuraforge.gatewayserver;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import reactor.core.publisher.Mono;


@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

	@Bean
	public RouteLocator ffcBankRouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
		
		return routeLocatorBuilder.routes()
				.route(p->p
					.path("/ffcbank/accounts/**")
					.filters(f->f.rewritePath("/ffcbank/accounts/(?<segment>.*)", "/${segment}") // segment is a variable - that can be any controller path
								 .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()) //built in filter to add a header
								 .circuitBreaker(config -> config.setName("accountCircuitBreaker") //implements circuit breaker pattern, inbuilt filter -resilience-example-1
								 .setFallbackUri("forward:/contactSupport"))) // tells the circuit-breaker that whenever an exception happens, invoke the fallbackuri method and forward the request to fallback url
					.uri("lb://ACCOUNTS")) // path where to forward the request. this is the endpoint managed by eureka. lb indicates that eureka acts as load balancer
				.route(p->p
						.path("/ffcbank/loans/**")
						.filters(f->f.rewritePath("/ffcbank/loans/(?<segment>.*)", "/${segment}")
						 			 .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
						 			 .retry(retryConfig -> retryConfig.setRetries(3)  //Set the retry by using lambda function that uses retry configs. resilience-example-2
						 					 						  .setMethods(org.springframework.http.HttpMethod.GET)
						 					 						  .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))) // use back-off for retries.						 					 						  
						.uri("lb://LOANS"))
				.route(p->p
						.path("/ffcbank/cards/**")
						.filters(f->f.rewritePath("/ffcbank/cards/(?<segment>.*)", "/${segment}")
			 			 			 .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())	
									 .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter()) // to implement the rate limiter pattern - resilience-example-3.
									 .setKeyResolver(userKeyResolver()))) // to implement the rate limiter pattern - resilience-example-3.
						.uri("lb://CARDS")).build();
	}
	
	// Bean to define the configurations for resilience4j pattern- circuit-breaker.
	// the configuration uses timelimiter pattern to define - how long to wait for applications response.
	@Bean
	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
		
		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id) //define the resilience4j configurations object and
				.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults()) //load the circuit-breaker config from default resilience4j configurations
				.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build()).build()); // change the timout property and build the 		
	}
	
	
	// to implement the rate limiter pattern
	@Bean
	public RedisRateLimiter redisRateLimiter() {
			return new RedisRateLimiter(1,1,1);
	}

	// to implement the rate limiter pattern
	@Bean
	KeyResolver userKeyResolver() {
		return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user"))
				.defaultIfEmpty("anonymous");		
	}
}


















