package com.microservice.api_gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.api_gateway.UserService;
import com.microservice.api_gateway.dto.AuthenticationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public static class Config {
    }

    public AuthenticationFilter(UserService userService, ObjectMapper objectMapper) {
        super(Config.class);
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    Mono<Void> unauthenticated(ServerHttpResponse response) {
        AuthenticationResponse authenticationResponse = AuthenticationResponse
                .builder()
                .error("Unauthenticated")
                .statusCode(1041)
                .build();

        String body = null;

        try{
            body = objectMapper.writeValueAsString(authenticationResponse);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            if(path.startsWith("/auth")){
                return chain.filter(exchange);
            }

            // Get token form authorization header
            List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
            if(authHeader == null || authHeader.isEmpty()){
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.get(0).substring(7);

            return userService.isValid(token)
                    .flatMap(authenticationResponseApiResponse -> {
                        log.info(authenticationResponseApiResponse.getData().toString() +" filter exchange");
                        if (authenticationResponseApiResponse.getData().isValid()) {/*
                            switch (authenticationResponse.getData().getRole()) {
                                case "admin":
                                    if (path.startsWith("/list-project")) {
                                        return chain.filter(exchange);
                                    } else
                                          return unauthenticated(exchange.getResponse());
                                case "user":
                                    if (path.startsWith("/home")) {
                                        return chain.filter(exchange);
                                    } else
                                        return unauthenticated(exchange.getResponse());
                                case "manager":
                                    if (path.startsWith("/manager")) {
                                        return chain.filter(exchange);
                                    } else
                                        return unauthenticated(exchange.getResponse());
                                case "hr":
                                    if (path.startsWith("/hr")) {
                                        return chain.filter(exchange);
                                    } else
                                        return unauthenticated(exchange.getResponse());
                            } */
                            return chain.filter(exchange);
                        }else{
                            return unauthenticated(exchange.getResponse());
                        }
                    })
                    .onErrorResume(e->{
                        return unauthenticated(exchange.getResponse());
                    });
        };
    }
}
