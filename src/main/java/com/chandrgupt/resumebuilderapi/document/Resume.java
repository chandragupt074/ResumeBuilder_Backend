package com.chandrgupt.resumebuilderapi.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "resume")
public class Resume {
    @Id
    @JsonProperty("_id")
    private String id;
    private String userId;

    private String title;

    private String thumbnailLink;

    private String template;
    private ProfileInfo profileInfo;
    private ContactInfo contactInfo;
    private List<WorkExperience> workExperience;
    private List<Education> education;
    private List<Skill> skills;
    private List<Project> projects;
    private List<Certification> certifications;
    private List<Language> languages;
    private List<String> interests;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;



    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Template{
        private String theme;
        private List<String> colorPalette;

    }

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class ProfileInfo{
        private String profilePreviewUrl;
        private String fullname;
        private String designation;
        private String summary;
    }
    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class ContactInfo{
        private String email;
        private String phone;
        private String location;
        private String linkedIn;
        private String github;
        private String website;
    }

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class WorkExperience{
        private String company;
        private String role;
        private String startDate;
        private String endDate;
        private String description;

    }

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Education{
        private String degree;
        private String institution;
        private String startDate;
        private String endDate;

    }

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Skill{
        private String name;
        private Integer progress;
    }

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Project{
        private String title;
        private String description;
        private String github;
        private String liveDemo;
    }

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Certification{
        private String title;
        private String issuer;
        private String year;

    }

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Language{
        private String name;
        private String progress;
    }

}
