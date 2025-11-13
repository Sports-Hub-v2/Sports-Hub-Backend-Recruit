package com.sportshub.recruit.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

public class RecruitDtos {
    @Data
    public static class PostCreateRequest {
        @NotNull
        private Long teamId;
        @NotNull
        private Long writerProfileId;
        @NotBlank
        private String title;
        private String content;
        private String region;
        private String imageUrl;
        private LocalDate matchDate;
        private String category;
        private String targetType;
        private String status;
    }

    @Data
    public static class PostUpdateRequest {
        private Long teamId;
        private Long writerProfileId;
        private String title;
        private String content;
        private String region;
        private String imageUrl;
        private LocalDate matchDate;
        private String category;
        private String targetType;
        private String status;
    }

    @Data
    public static class ApplicationCreateRequest {
        @NotNull
        private Long applicantProfileId;
        private String description;
        private String status;
    }

    @Data
    public static class ApplicationUpdateStatusRequest {
        @NotBlank
        private String status;
    }

    @Data
    public static class PostResponse {
        private Long id;
        private Long teamId;
        private Long writerProfileId;
        private String title;
        private String content;
        private String region;
        private String imageUrl;
        private LocalDate matchDate;
        private String category;
        private String targetType;
        private String status;
        private java.time.LocalDateTime createdAt;
        private Long acceptedCount;

        public PostResponse(com.sportshub.recruit.domain.RecruitPost post, Long acceptedCount) {
            this.id = post.getId();
            this.teamId = post.getTeamId();
            this.writerProfileId = post.getWriterProfileId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.region = post.getRegion();
            this.imageUrl = post.getImageUrl();
            this.matchDate = post.getMatchDate();
            this.category = post.getCategory();
            this.targetType = post.getTargetType();
            this.status = post.getStatus();
            this.createdAt = post.getCreatedAt();
            this.acceptedCount = acceptedCount;
        }
    }
}

