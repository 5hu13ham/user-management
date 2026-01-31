package in.trendsnag.user_management.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.trendsnag.user_management.dto.UserLoginRequest;
import in.trendsnag.user_management.dto.UserLoginResponse;
import in.trendsnag.user_management.dto.UserRegisterRequest;
import in.trendsnag.user_management.service.LoginService;
import in.trendsnag.user_management.service.RegisterService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginService loginService;

    private final RegisterService registerService;
    
    

    public AuthController(LoginService loginService, RegisterService registerService) {
		this.loginService = loginService;
		this.registerService = registerService;
	}
    
    
	@PostMapping("/register")
    public ResponseEntity<UserLoginResponse> register(
            @Valid @RequestBody UserRegisterRequest request) {

        UserLoginResponse response = registerService.register(request);
        return ResponseEntity.ok(response);
        
	}

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {


        UserLoginResponse response = loginService.login(request);


        return ResponseEntity.ok(response);
        
    }
}
