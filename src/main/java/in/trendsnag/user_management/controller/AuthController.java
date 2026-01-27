package in.trendsnag.user_management.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.trendsnag.user_management.dto.UserLoginRequest;
import in.trendsnag.user_management.dto.UserLoginResponse;
import in.trendsnag.user_management.security.JwtUtil;
import in.trendsnag.user_management.service.LoginService;
import in.trendsnag.user_management.model.User;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginService loginService;
    private final JwtUtil jwtUtil;

    public AuthController(LoginService loginService, JwtUtil jwtUtil) {
        this.loginService = loginService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody UserLoginRequest request) {

        // 1. Validate identifier + password
        User user = loginService.login(request);

        // 2. Generate JWT
        String token = jwtUtil.generateToken(user.getEmail());

        // 3. Return response DTO
        return new UserLoginResponse(
                user.getUsername(),
                user.getEmail(),
                user.getRole().toString(),
                token
        );
    }
}
