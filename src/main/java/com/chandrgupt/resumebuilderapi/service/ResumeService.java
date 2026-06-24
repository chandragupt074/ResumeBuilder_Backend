package com.chandrgupt.resumebuilderapi.service;

import com.chandrgupt.resumebuilderapi.document.Resume;
import com.chandrgupt.resumebuilderapi.dto.AuthResponse;
import com.chandrgupt.resumebuilderapi.dto.CreateResumeRequest;
import com.chandrgupt.resumebuilderapi.repository.ResumeRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final AuthService authService;

    public Resume createResume(CreateResumeRequest request,Object principalObject){
        Resume newResume = new Resume();

        AuthResponse response=authService.getProfile(principalObject);

        newResume.setUserId(response.getId());
        newResume.setTitle(request.getTitle());


        setDefaultResumeData(newResume);

       return  resumeRepository.save(newResume);
    }

    private void setDefaultResumeData(Resume newResume){
        newResume.setProfileInfo(new Resume.ProfileInfo());
        newResume.setContactInfo(new Resume.ContactInfo());
        newResume.setWorkExperience(new ArrayList<>());
        newResume.setEducation(new ArrayList<>());
        newResume.setSkills(new ArrayList<>());
        newResume.setProjects(new ArrayList<>());
        newResume.setCertifications(new ArrayList<>());
        newResume.setLanguages(new ArrayList<>());
        newResume.setInterests(new ArrayList<>());
    }

    public List<Resume>getUserResumes(Object principal){
        AuthResponse response = authService.getProfile(principal);

        List<Resume>resumes = resumeRepository.findByUserIdOrderByUpdatedAtDesc(response.getId());
        return resumes;

    }

    public Resume getResumeById(String resumeId,Object principal){
        AuthResponse response = authService.getProfile(principal);

     Resume existingResume= resumeRepository.findByUserIdAndId(response.getId(),resumeId)
        .orElseThrow(()->new RuntimeException("Resume not found"));

     return existingResume;
    }

    public Resume updateResume(String resumeId,Resume updatedData,Object principal){
       AuthResponse response = authService.getProfile(principal);

      Resume existingResume=resumeRepository.findByUserIdAndId(response.getId(),resumeId)
              .orElseThrow(()->new RuntimeException("Resume not found"));

        existingResume.setTitle(updatedData.getTitle());
        existingResume.setThumbnailLink(updatedData.getThumbnailLink());
        existingResume.setTemplate(updatedData.getTemplate());
        existingResume.setProfileInfo(updatedData.getProfileInfo());
        existingResume.setContactInfo(updatedData.getContactInfo());
        existingResume.setWorkExperience(updatedData.getWorkExperience());
        existingResume.setEducation(updatedData.getEducation());
        existingResume.setSkills(updatedData.getSkills());
        existingResume.setProjects(updatedData.getProjects());
        existingResume.setCertifications(updatedData.getCertifications());
        existingResume.setLanguages(updatedData.getLanguages());
        existingResume.setInterests(updatedData.getInterests());

        resumeRepository.save(existingResume);

        return existingResume;

    }

    public void deleteResume(String resumeId, Object principal){
         AuthResponse response = authService.getProfile(principal);

       Resume existingResume=resumeRepository.findByUserIdAndId(response.getId(), resumeId)
                 .orElseThrow(()->new RuntimeException("Resume not found"));

       resumeRepository.delete(existingResume);

    }



}
