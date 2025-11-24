package com.sportshub.recruit.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "recruit_posts")
@Getter
@Setter
@NoArgsConstructor
public class RecruitPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "writer_profile_id", nullable = false)
    private Long writerProfileId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String region;

    @Column(name = "sub_region")
    private String subRegion;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "match_date")
    private LocalDate matchDate;

    @Column(name = "game_time")
    private LocalTime gameTime;

    private String category;
    private String targetType;
    private String status;

    // 모집 조건 필드
    @Column(name = "required_personnel")
    private Integer requiredPersonnel;

    @Column(name = "preferred_positions")
    private String preferredPositions;

    @Column(name = "age_group")
    private String ageGroup;

    @Column(name = "skill_level")
    private String skillLevel;

    // 경기 장소 (네이버 지도로 검색 가능)
    @Column(name = "field_location")
    private String fieldLocation;

    // 경기 매치 관련 필드
    @Column(name = "match_type")
    private String matchType; // 친선경기, 연습경기, 리그전, 토너먼트

    @Column(name = "team_size")
    private String teamSize; // 5vs5, 6vs6, 7vs7, 8vs8, 11vs11

    @Column(name = "field_type")
    private String fieldType; // 잔디구장, 인조잔디, 풋살장

    private Integer cost; // 참가비

    @Column(name = "parking_available")
    private Boolean parkingAvailable; // 주차 가능 여부

    @Column(name = "shower_facilities")
    private Boolean showerFacilities; // 샤워 시설 여부

    // 팀 모집 관련 필드
    @Column(name = "activity_days")
    private String activityDays; // 주 활동 요일

    @Column(name = "activity_time")
    private String activityTime; // 주 활동 시간대

    // 추가 공통 필드
    @Column(name = "min_players")
    private Integer minPlayers; // 최소 인원

    @Column(name = "max_players")
    private Integer maxPlayers; // 최대 인원

    @Column(name = "match_rules", columnDefinition = "TEXT")
    private String matchRules; // 경기 규칙 및 특이사항
}

