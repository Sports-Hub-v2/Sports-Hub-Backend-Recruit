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
}

