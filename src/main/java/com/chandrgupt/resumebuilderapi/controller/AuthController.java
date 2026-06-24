package com.chandrgupt.resumebuilderapi.controller;

import com.chandrgupt.resumebuilderapi.dto.AuthResponse;
import com.chandrgupt.resumebuilderapi.dto.LoginRequest;
import com.chandrgupt.resumebuilderapi.dto.RegisterRequest;
import com.chandrgupt.resumebuilderapi.service.AuthService;
import com.chandrgupt.resumebuilderapi.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static com.chandrgupt.resumebuilderapi.util.AppConstants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(AUTH_CONTROLLER)
public class AuthController {

    private final AuthService authService;
    private final FileUploadService fileUploadService;
    @PostMapping(REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
            log.info("Inside AuthController - register():{}",request);
           AuthResponse response= authService.register(request);
           log.info("Response from service: {}", response);
           return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
    @GetMapping(VERIFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@RequestParam String token){
        log.info("Inside AuthController-verifyEmail():{}",token);
        authService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Email verified successfully"));
    }

    @PostMapping(UPLOAD_PROFILE)
    public ResponseEntity<?> uploadImage(@RequestPart("image")MultipartFile file) throws IOException {
        log.info("Inside AuthController- uploadImage()");
       Map<String,String> response= fileUploadService.uploadSingleImage(file);
       return ResponseEntity.ok(response);
    }
    @PostMapping(LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
      AuthResponse response=authService.login(request);
      return ResponseEntity.ok(response);

    }

//    @GetMapping("/validate")
//    public String testValidationToken(){
//        return "Token validation is working";
//    }


    @PostMapping(RESEND_VERIFICATION)
    public ResponseEntity<?> resendVerification(@RequestBody Map<String,String>body){
        String email=body.get("email");

        if(Objects.isNull(email)){
            return ResponseEntity.badRequest().body(Map.of("message","Email is required"));
        }

        authService.resendVerification(email);


        return ResponseEntity.ok(Map.of("success", true, "message","Verification email sent"));

    }

    @GetMapping(PROFILE)
    public ResponseEntity<AuthResponse> getProfile(Authentication authentication){

        Object principalObject = authentication.getPrincipal();

        AuthResponse currentProfile = authService.getProfile(principalObject);

        return ResponseEntity.ok(currentProfile);
    }
}
