package com.microservice.api_gateway;

import com.microservice.api_gateway.dto.ApiResponse;
import com.microservice.api_gateway.dto.AuthenticationRequest;
import com.microservice.api_gateway.dto.AuthenticationResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j

public class UserService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public Mono<ApiResponse<AuthenticationResponse>> isValid(String token) {
        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8088/auth/isValid")
                .bodyValue(token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<AuthenticationResponse>>() {})
                ;
    }
}