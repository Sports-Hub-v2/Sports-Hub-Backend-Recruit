package com.sportshub.recruit.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "recruit_applications")
@Getter
@Setter
@NoArgsConstructor
public class RecruitApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "applicant_profile_id", nullable = false)
    private Long applicantProfileId;

    @Column(name = "applicant_team_id")
    private Long applicantTeamId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String status;

    @Column(name = "application_date", nullable = false, updatable = false)
    private LocalDateTime applicationDate;

    @PrePersist
    protected void onCreate() {
        if (applicationDate == null) {
            applicationDate = LocalDateTime.now();
        }
    }
}

