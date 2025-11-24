package com.sportshub.recruit.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

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
        private String subRegion;
        private String imageUrl;
        private LocalDate matchDate;
        private LocalTime gameTime;
        private String category;
        private String targetType;
        private String status;
        // 모집 조건
        private Integer requiredPersonnel;
        private String preferredPositions;
        private String ageGroup;
        private String skillLevel;
        // 경기 장소
        private String fieldLocation;
        // 경기 매치 관련 필드
        private String matchType;
        private String teamSize;
        private String fieldType;
        private Integer cost;
        private Boolean parkingAvailable;
        private Boolean showerFacilities;
        // 팀 모집 관련 필드
        private String activityDays;
        private String activityTime;
        // 추가 공통 필드
        private Integer minPlayers;
        private Integer maxPlayers;
        private String matchRules;
    }

    @Data
    public static class PostUpdateRequest {
        private Long teamId;
        private Long writerProfileId;
        private String title;
        private String content;
        private String region;
        private String subRegion;
        private String imageUrl;
        private LocalDate matchDate;
        private LocalTime gameTime;
        private String category;
        private String targetType;
        private String status;
        // 모집 조건
        private Integer requiredPersonnel;
        private String preferredPositions;
        private String ageGroup;
        private String skillLevel;
        // 경기 장소
        private String fieldLocation;
        // 경기 매치 관련 필드
        private String matchType;
        private String teamSize;
        private String fieldType;
        private Integer cost;
        private Boolean parkingAvailable;
        private Boolean showerFacilities;
        // 팀 모집 관련 필드
        private String activityDays;
        private String activityTime;
        // 추가 공통 필드
        private Integer minPlayers;
        private Integer maxPlayers;
        private String matchRules;
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
        private String authorName;  // 작성자 이름
        private String teamName;    // 팀 이름
        private String title;
        private String content;
        private String region;
        private String subRegion;
        private String imageUrl;
        private LocalDate matchDate;
        private LocalTime gameTime;
        private String category;
        private String targetType;
        private String status;
        private java.time.LocalDateTime createdAt;
        private Long acceptedCount;

        // 모집 조건
        private Integer requiredPersonnel;
        private String preferredPositions;
        private String ageGroup;
        private String skillLevel;

        // 경기 장소
        private String fieldLocation;

        // 경기 매치 관련 필드
        private String matchType;
        private String teamSize;
        private String fieldType;
        private Integer cost;
        private Boolean parkingAvailable;
        private Boolean showerFacilities;

        // 팀 모집 관련 필드
        private String activityDays;
        private String activityTime;

        // 추가 공통 필드
        private Integer minPlayers;
        private Integer maxPlayers;
        private String matchRules;

        public PostResponse(com.sportshub.recruit.domain.RecruitPost post, Long acceptedCount) {
            this.id = post.getId();
            this.teamId = post.getTeamId();
            this.writerProfileId = post.getWriterProfileId();
            this.authorName = null;  // TODO: user-service에서 프로필 이름 조회
            this.teamName = null;    // TODO: team-service에서 팀 이름 조회
            this.title = post.getTitle();
            this.content = post.getContent();
            this.region = post.getRegion();
            this.subRegion = post.getSubRegion();
            this.imageUrl = post.getImageUrl();
            this.matchDate = post.getMatchDate();
            this.gameTime = post.getGameTime();
            this.category = post.getCategory();
            this.targetType = post.getTargetType();
            this.status = post.getStatus();
            this.createdAt = post.getCreatedAt();
            this.acceptedCount = acceptedCount;
            this.requiredPersonnel = post.getRequiredPersonnel();
            this.preferredPositions = post.getPreferredPositions();
            this.ageGroup = post.getAgeGroup();
            this.skillLevel = post.getSkillLevel();
            this.fieldLocation = post.getFieldLocation();
            this.matchType = post.getMatchType();
            this.teamSize = post.getTeamSize();
            this.fieldType = post.getFieldType();
            this.cost = post.getCost();
            this.parkingAvailable = post.getParkingAvailable();
            this.showerFacilities = post.getShowerFacilities();
            this.activityDays = post.getActivityDays();
            this.activityTime = post.getActivityTime();
            this.minPlayers = post.getMinPlayers();
            this.maxPlayers = post.getMaxPlayers();
            this.matchRules = post.getMatchRules();
        }
    }
}

