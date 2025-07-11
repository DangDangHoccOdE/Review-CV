package com.microservice.user;

import com.microservice.user.dto.AuthenticationRequest;
import com.microservice.user.dto.AuthenticationResponse;
import com.microservice.user.exception.CustomException;
import com.microservice.user.exception.Error;
import com.microservice.user.model.Role;
import com.microservice.user.model.User;
import com.microservice.user.repository.UserRepository;
import com.microservice.user.security.OurUserDetailsService;
import com.microservice.user.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private OurUserDetailsService ourUserDetailsService;

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public AuthenticationResponse signUp(AuthenticationRequest registrationRequest) {
        String rawPassword = registrationRequest.getPassword();
        log.info("Original password: " + rawPassword);

        String encodedPassword = passwordEncoder.encode(rawPassword);
        log.info("Encoded password: " + encodedPassword);

        log.info("Sign up Auth Service");

        String email = registrationRequest.getEmail().trim().toLowerCase();

        // Kiểm tra xem email đã tồn tại chưa
        if (emailExists(email)) {
            return AuthenticationResponse.builder()
                    .message("Email already exists")
                    .statusCode(409)
                    .isVaild(false)
                    .build();
        }
        String role="";
        if(registrationRequest.getRole()==null){
            role="user";
        } else {
            role=registrationRequest.getRole();
        }

        User users = User.builder()
                .id(getGenerationId())
                .name(registrationRequest.getName())
                .email(registrationRequest.getEmail())
                .idEmployee(registrationRequest.getIdEmployee())
                .role(Role.valueOf(role))
                .isActive(true)
                .build();
        if (registrationRequest.getIdEmployee() != null) {
            users.setIdEmployee(registrationRequest.getIdEmployee());
        }

        users.setPassword(encodedPassword);
        log.info("User object before save: " + users);

        User userResult = userRepository.save(users);
        log.info("User object after save: " + userResult);

        log.info("pass new: " + users.getPassword());
        log.info("pass: " + registrationRequest.getPassword());

        return AuthenticationResponse.builder()
                .user(userResult)
                .message("User Saved Successfully")
                .statusCode(200)
                .isVaild(true)
                .build();
    }

    public AuthenticationResponse signIn(AuthenticationRequest signinRequest) {
        String email = signinRequest.getEmail().trim().toLowerCase();

        // Kiểm tra xem email đã tồn tại chưa
        if (!emailExists(email)) {
            return AuthenticationResponse.builder()
                    .message("Email not found")
                    .statusCode(404)
                    .isVaild(false)
                    .build();
        }

        log.info("Sign in Auth Service");
        // Nếu email tồn tại thì kiểm tra mật khẩu
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, signinRequest.getPassword()));
        } catch (Exception e) {
            return AuthenticationResponse.builder()
                    .message("Invalid credentials")
                    .statusCode(401)
                    .isVaild(false)
                    .build();
        }
        var user = userRepository.findByEmail(email).orElseThrow();

        if(!user.isActive()){
            throw new CustomException(Error.USER_LOGIN);
        }
        var jwt = jwtTokenUtil.generateToken(user);
        var refreshToken = jwtTokenUtil.generateRefreshToken(new HashMap<>(), user);
        return AuthenticationResponse.builder()
                .statusCode(200)
                .token(jwt)
                .refreshToken(refreshToken)
                .expirationTime("24Hr")
                .message("Successfully Signed In")
                .role(user.getRole()+"")
                .user(user)
                .isVaild(true)
                .build();

    }

    public AuthenticationResponse refreshToken(AuthenticationRequest refreshTokenRequest) {

        log.info("Refresh Token Auth Service");
        AuthenticationResponse.AuthenticationResponseBuilder response = AuthenticationResponse.builder();
        String ourEmail = jwtTokenUtil.extractUsername(refreshTokenRequest.getToken());
        User users = userRepository.findByEmail(ourEmail).orElseThrow();

        var jwt = jwtTokenUtil.generateToken(users);
        response.statusCode(200)
                .token(jwt)
                .refreshToken(refreshTokenRequest.getToken())
                .expirationTime("24Hr")
                .message("Successfully Refreshed Token");

        return response.build();

    }

    public AuthenticationResponse isValid(String token) {
        String email = jwtTokenUtil.extractUsername(token);
        if (email != null) {
            UserDetails userDetails = ourUserDetailsService.loadUserByUsername(email);
            if (jwtTokenUtil.isTokenValid(token, userDetails)) {
                User user = userRepository.findByEmail(userDetails.getUsername()).get();
                return AuthenticationResponse.builder().isVaild(true).role(user.getRole().toString()).build();
            }
        }
        return AuthenticationResponse.builder().isVaild(true).build();

    }

    public Integer getGenerationId() {
        UUID uuid = UUID.randomUUID();
        // Use most significant bits and ensure it's within the integer range
        return (int) (uuid.getMostSignificantBits() & 0xFFFFFFFFL);
    }
}
