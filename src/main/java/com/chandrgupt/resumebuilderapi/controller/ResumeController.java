package com.chandrgupt.resumebuilderapi.controller;


import com.chandrgupt.resumebuilderapi.document.Resume;
import com.chandrgupt.resumebuilderapi.dto.CreateResumeRequest;
import com.chandrgupt.resumebuilderapi.service.AuthService;
import com.chandrgupt.resumebuilderapi.service.FileUploadService;
import com.chandrgupt.resumebuilderapi.service.ResumeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.chandrgupt.resumebuilderapi.util.AppConstants.*;

@RestController
       @RequiredArgsConstructor
       @Slf4j
       @RequestMapping(RESUME)
       public class ResumeController {
       private final ResumeService resumeService;
       private final AuthService authService;
       private final FileUploadService fileUploadService;

       @PostMapping
       public ResponseEntity<?> createResume(@Valid @RequestBody CreateResumeRequest request, Authentication authentication){
              Resume newResume = resumeService.createResume(request,authentication.getPrincipal());

              return ResponseEntity.status(HttpStatus.CREATED).body(newResume);


       }

       @GetMapping
       public ResponseEntity<?> getUserResume(Authentication authentication){
              List<Resume> resumes= resumeService.getUserResumes(authentication.getPrincipal());
              return ResponseEntity.ok(resumes);

       }

       @GetMapping(ID)
       public ResponseEntity<?>getResumeById(@PathVariable String id,Authentication authentication){
              Resume existingResume = resumeService.getResumeById(id,authentication.getPrincipal());

              return ResponseEntity.ok(existingResume);

       }

       @PutMapping(ID)
       public ResponseEntity<?> updateResume(@PathVariable String id, @RequestBody Resume updatedData,Authentication authentication){
              Resume updatedResume = resumeService.updateResume(id,updatedData,authentication.getPrincipal());
              return ResponseEntity.ok(updatedResume);
       }

       @PutMapping(UPLOAD_IMAGES)
              public ResponseEntity<?>uploadResumeImage
               (@PathVariable String id , @RequestPart(value="thumbnail", required = false)
               MultipartFile thumbnail, @RequestPart(value = "profileImage",required = false)
               MultipartFile profileImage, HttpServletRequest request,Authentication authentication) throws IOException {

             Map<String,String> response=fileUploadService.uploadResumeImage(id,authentication.getPrincipal(),thumbnail,profileImage);
               return ResponseEntity.ok(response);

       }

       @DeleteMapping(ID)
              public ResponseEntity<?> deleteResume(@PathVariable String id,Authentication authentication){
              resumeService.deleteResume(id,authentication.getPrincipal());

              return ResponseEntity.ok(Map.of("message","Resume deleted Successfully"));

       }

}
