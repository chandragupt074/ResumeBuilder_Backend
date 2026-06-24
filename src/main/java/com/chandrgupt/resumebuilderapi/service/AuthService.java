package com.chandrgupt.resumebuilderapi.service;


import com.chandrgupt.resumebuilderapi.document.User;
import com.chandrgupt.resumebuilderapi.dto.AuthResponse;
import com.chandrgupt.resumebuilderapi.dto.LoginRequest;
import com.chandrgupt.resumebuilderapi.dto.RegisterRequest;
import com.chandrgupt.resumebuilderapi.exception.ResourceExistException;
import com.chandrgupt.resumebuilderapi.repository.UserRepository;
import com.chandrgupt.resumebuilderapi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
//import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;



    @Value("${app.base.url:http://localhost:8081}")
    private String appBaseUrl;


    public AuthResponse register(RegisterRequest request){
           log.info("Inside AuthService: register() {}" , request);

           if(userRepository.existsByEmail(request.getEmail())){
               throw new ResourceExistException("User already exist with this email");
           }

      User newUser =toDocument(request);

           userRepository.save(newUser);
           sendVerificationEmail(newUser);

          return toResponse(newUser);

    }

    private void sendVerificationEmail(User newUser) {
        log.info("Inside Authservice - sendVerificationEmail():{}", newUser);
        try{
            String link =appBaseUrl+"/api/auth/verify-email?token="+newUser.getVerificationToken();
            String html =
                    "<div style='font-family:sans-serif'>" +

                            "<h2>Verify your email</h2>" +

                            "<p>Hi " + newUser.getName() +
                            ", please confirm your email to activate your account.</p>" +

                            "<p>" +
                            "<a href='" + link + "' " +
                            "style='display:inline-block;" +
                            "padding:10px 16px;" +
                            "background:#6366f1;" +
                            "color:#fff;" +
                            "border-radius:6px;" +
                            "text-decoration:none;'>" +
                            "Verify Email" +
                            "</a>" +
                            "</p>" +

                            "<p>Or copy this link: " + link + "</p>" +

                            "<p>This link expires in 24 hours.</p>" +

                            "</div>";

            emailService.sendHtmlEmail(newUser.getEmail(),"Verify your email",html);
        }catch(Exception e){
            log.error("Exception occured at sendVerificationEmail(): {}" , e.getMessage());
            throw new RuntimeException("failed to send verification email:"+e.getMessage());
        }

    }

    private AuthResponse toResponse(User newUser){
        return AuthResponse.builder()
                .id(newUser.getId())
                .name(newUser.getName())
                .email(newUser.getEmail())
                .profileImageUrl(newUser.getProfileImageUrl())
                .emailVerification(newUser.isEmailVerified())
                .subscriptionPlan(newUser.getSubscriptionPlan())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .build();
    }

    private User toDocument(RegisterRequest request){
       return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profileImageUrl(request.getProfileImageUrl())
                .subscriptionPlan("Basic")
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(LocalDateTime.now().plusHours(24))
                .build();
    }

    public void verifyEmail(String token){
        log.info("Inside AuthService: verifyEmail():{}",token);
     User user =userRepository.findByVerificationToken(token)
                .orElseThrow(()->new RuntimeException("Invalid or expired varification token"));
     if(user.getVerificationExpires()!=null && user.getVerificationExpires().isBefore(LocalDateTime.now())){
         throw new RuntimeException("Verification token has expired. Please Request new one");
     }
     user.setEmailVerified(true);
     user.setVerificationToken(null);
     user.setVerificationExpires(null);
     userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request){
      User existingUser=  userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new UsernameNotFoundException("Invalid email or password"));

      if(!passwordEncoder.matches(request.getPassword(),existingUser.getPassword())){
          throw new UsernameNotFoundException("Invalid email or password");

      }

      if(!existingUser.isEmailVerified()){
          throw new RuntimeException("Please verify your email before logging in");
      }

      String token = jwtUtil.generateToken(existingUser.getId());

      AuthResponse response = toResponse(existingUser);
      response.setToken(token);
      return response;

    }

    public void resendVerification(String email){
        User user =userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));

        if(user.isEmailVerified()){
            throw new RuntimeException("Email is already verified.");
        }

        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerificationExpires(LocalDateTime.now().plusHours(24));


        userRepository.save(user);

        sendVerificationEmail(user);

    }
    public AuthResponse getProfile(Object principalObject){
        User existingUser = (User) principalObject;
        return toResponse(existingUser);
    }


}
