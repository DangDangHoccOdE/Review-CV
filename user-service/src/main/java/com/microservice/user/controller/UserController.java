package com.microservice.user.controller;

import com.microservice.user.dto.ApiResponse;
import com.microservice.user.dto.AuthenticationRequest;
import com.microservice.user.dto.AuthenticationResponse;
import com.microservice.user.dto.UserDto;
import com.microservice.user.security.OurUserDetailService;
import com.microservice.user.services.impl.AuthService;
import com.microservice.user.services.inter.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/auth")
@RestController
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private OurUserDetailService ourUserDetailService;
    @Autowired
    private AuthService authService;

    @PostMapping("/isValid")
    public ApiResponse<AuthenticationResponse> isValid(@RequestBody String token){
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>(true,"",authService.isValid(token));
        log.info("log" + response.getData());
        return response;
    }

    @GetMapping("/ourUserDetailsService")
    public ResponseEntity<ApiResponse<UserDetails>> getUserDetails(@PathVariable String username){
        UserDetails userDetails = ourUserDetailService.loadUserByUsername(username);
        ApiResponse<UserDetails> response = new ApiResponse<>(true,"get userdetails successfully",userDetails);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> signup(@RequestBody AuthenticationRequest authenticationRequest) {
        AuthenticationResponse authenticationResponse = authService.signUp(authenticationRequest);
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>(true,"signup successfully",authenticationResponse);
        if(!authenticationResponse.isVaild()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> signIn(@RequestBody AuthenticationRequest authenticationRequest) {
        AuthenticationResponse authenticationResponse = authService.signIn(authenticationRequest);
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>(true,"signin successfully",authenticationResponse);
        if(!authenticationResponse.isVaild()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<UserDto>> update(@RequestParam String token, @RequestBody UserDto UserDto) {
        UserDto updatedUser = userService.update(token, UserDto);
        ApiResponse<UserDto> response = new ApiResponse<>(true, "User updated successfully", updatedUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findbyid")
    public ResponseEntity<ApiResponse<UserDto>> findById( @RequestParam Integer id) {
        ApiResponse<UserDto> response = new ApiResponse<>(true, "User retrieved successfully",
                userService.findById( id));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(
            @RequestBody AuthenticationRequest refreshTokenRequest) {
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>(true, "Refresh token successfully",
                authService.refreshToken(refreshTokenRequest));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/checkId")
    public ResponseEntity<ApiResponse<Boolean>> checkId(@RequestParam Integer id) {
        ApiResponse<Boolean> response = new ApiResponse<>(true, "Check user id successfully",
                userService.checkUser(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getCurrentUser")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        ApiResponse<UserDto> response = new ApiResponse<>(true, "Check user id successfully",
                userService.getCurrentUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers(@RequestParam String token) {
        List<UserDto> UserDtoS = userService.getAllUsers(token);
        ApiResponse<List<UserDto>> response = new ApiResponse<>(true, "All users retrieved successfully", UserDtoS);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getlistuserbyid")
    public ResponseEntity<ApiResponse<List<UserDto>>> getUserByIds(@RequestParam String token,
                                                                   @RequestParam List<Integer> ids) {
        List<UserDto> UserDtoS = userService.findUserByIds(token, ids);
        ApiResponse<List<UserDto>> response = new ApiResponse<>(true, "User retrieved successfully", UserDtoS);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/updateactive")
    public ResponseEntity<ApiResponse<UserDto>> updateActive(@RequestParam String token, @RequestBody UserDto UserDto) {
        UserDto updatedUser = userService.updateIsActive(token, UserDto.getId());
        ApiResponse<UserDto> response = new ApiResponse<>(true, "User updated successfully", updatedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<UserDto>> deleteUser(@RequestParam String token, @RequestParam Integer id) {
        UserDto deletedUser = userService.delete(token, id);
        ApiResponse<UserDto> response = new ApiResponse<>(true, "User deleted successfully", deletedUser);
        return ResponseEntity.ok(response);
    }
}
