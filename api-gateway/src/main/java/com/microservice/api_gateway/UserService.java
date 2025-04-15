package com.microservice.api_gateway;

import com.microservice.api_gateway.dto.ApiResponse;
import com.microservice.api_gateway.dto.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private WebClient.Builder webClientBuilder;

    public Mono<ApiResponse<AuthenticationResponse>> isValid(String token){
        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8088/autj/isValid")
                .bodyValue(token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<AuthenticationResponse>>() {

                });
    }
}
