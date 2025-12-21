package com.sportshub.recruit.service;

import com.sportshub.recruit.domain.RecruitApplication;
import com.sportshub.recruit.domain.RecruitPost;
import com.sportshub.recruit.domain.Match;
import com.sportshub.recruit.repository.RecruitApplicationRepository;
import com.sportshub.recruit.repository.RecruitPostRepository;
import com.sportshub.recruit.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final RecruitApplicationRepository applicationRepository;
    private final RecruitPostRepository recruitPostRepository;
    private final MatchRepository matchRepository;
    private final RestTemplate restTemplate;

    @Value("${team.service.url:http://sportshub-team:8083}")
    private String teamServiceUrl;

    @Value("${notification.service.url:http://sportshub-notification:8085}")
    private String notificationServiceUrl;

    @Transactional
    public RecruitApplication apply(Long postId, RecruitApplication a) {
        // Check if post exists
        RecruitPost post = recruitPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found"));

        // For TEAM category, prevent duplicate applications from existing team members
        if ("TEAM".equals(post.getCategory())) {
            boolean isAlreadyMember = checkIfAlreadyTeamMember(post.getTeamId(), a.getApplicantProfileId());
            if (isAlreadyMember) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Already a member of this team");
            }

            // Also check if user already applied to this post
            boolean alreadyApplied = applicationRepository.findByPostId(postId).stream()
                    .anyMatch(app -> app.getApplicantProfileId().equals(a.getApplicantProfileId()));
            if (alreadyApplied) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Already applied to this post");
            }
        }

        // For MATCH category, verify applicant is captain of the team
        if ("MATCH".equals(post.getCategory()) && a.getApplicantTeamId() != null) {
            boolean isCaptain = checkIfTeamCaptain(a.getApplicantTeamId(), a.getApplicantProfileId());
            if (!isCaptain) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only team captain can apply for matches");
            }

            // Also check if team already applied to this post
            boolean teamAlreadyApplied = applicationRepository.findByPostId(postId).stream()
                    .anyMatch(app -> app.getApplicantTeamId() != null &&
                                    app.getApplicantTeamId().equals(a.getApplicantTeamId()));
            if (teamAlreadyApplied) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This team already applied to this match");
            }
        }

        a.setPostId(postId);
        if (a.getStatus() == null || a.getStatus().isBlank()) a.setStatus("PENDING");
        RecruitApplication saved = applicationRepository.save(a);

        // 모집글 작성자에게 새로운 신청 알림 전송
        String categoryName = "MERCENARY".equals(post.getCategory()) ? "용병" :
                              "TEAM".equals(post.getCategory()) ? "팀" : "경기";
        String message = String.format("회원님의 %s 모집글에 새로운 지원자가 있습니다.", categoryName);
        sendNotification(post.getWriterProfileId(), "NEW_APPLICATION", message, "RECRUIT_POST", postId);

        return saved;
    }

    private boolean checkIfAlreadyTeamMember(Long teamId, Long profileId) {
        try {
            String url = teamServiceUrl + "/api/teams/" + teamId + "/members";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("content")) {
                List<Map<String, Object>> members = (List<Map<String, Object>>) response.get("content");
                return members.stream()
                        .anyMatch(member -> {
                            Object pid = member.get("profileId");
                            return pid != null && pid.equals(profileId.intValue());
                        });
            }
            return false;
        } catch (Exception e) {
            log.warn("Failed to check team membership for profile {} in team {}: {}",
                    profileId, teamId, e.getMessage());
            return false;
        }
    }

    private boolean checkIfTeamCaptain(Long teamId, Long profileId) {
        try {
            String url = teamServiceUrl + "/api/teams/" + teamId + "/members";
            List<Map<String, Object>> members = restTemplate.getForObject(url, List.class);

            if (members != null) {
                return members.stream()
                        .anyMatch(member -> {
                            Map<String, Object> id = (Map<String, Object>) member.get("id");
                            String role = (String) member.get("roleInTeam");
                            Boolean isActive = (Boolean) member.get("isActive");

                            if (id != null && role != null && isActive != null) {
                                Object pid = id.get("profileId");
                                return pid != null &&
                                       pid.equals(profileId.intValue()) &&
                                       "CAPTAIN".equals(role) &&
                                       isActive;
                            }
                            return false;
                        });
            }
            return false;
        } catch (Exception e) {
            log.warn("Failed to check team captain for profile {} in team {}: {}",
                    profileId, teamId, e.getMessage());
            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<RecruitApplication> listByPost(Long postId) {
        return applicationRepository.findByPostId(postId);
    }

    @Transactional(readOnly = true)
    public List<RecruitApplication> listByApplicant(Long applicantProfileId) {
        return applicationRepository.findByApplicantProfileId(applicantProfileId);
    }

    @Transactional(readOnly = true)
    public List<RecruitApplication> listByTeamProfile(Long teamProfileId) {
        return applicationRepository.findByTeamProfile(teamProfileId);
    }

    @Transactional
    public RecruitApplication updateStatus(Long postId, Long applicationId, String status) {
        RecruitApplication a = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "application not found"));
        if (!a.getPostId().equals(postId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "application not found");

        a.setStatus(status);
        RecruitApplication saved = applicationRepository.save(a);

        // 신청자에게 알림 전송
        RecruitPost post = recruitPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found"));

        String categoryName = "MERCENARY".equals(post.getCategory()) ? "용병" :
                              "TEAM".equals(post.getCategory()) ? "팀" : "경기";

        if ("ACCEPTED".equals(status)) {
            String message = String.format("축하합니다! %s 신청이 승인되었습니다.", categoryName);
            sendNotification(a.getApplicantProfileId(), "APPLICATION_APPROVED", message, "RECRUIT_POST", postId);
        } else if ("REJECTED".equals(status)) {
            String message = String.format("죄송합니다. %s 신청이 거절되었습니다.", categoryName);
            sendNotification(a.getApplicantProfileId(), "APPLICATION_REJECTED", message, "RECRUIT_POST", postId);
        }

        // Auto-closure logic for TEAM category recruitment
        if ("ACCEPTED".equals(status)) {
            if ("TEAM".equals(post.getCategory()) && !"COMPLETED".equals(post.getStatus())) {
                // Count accepted applications
                long acceptedCount = applicationRepository.findByPostId(postId).stream()
                        .filter(app -> "ACCEPTED".equals(app.getStatus()))
                        .count();

                log.info("Post {}: accepted count = {}, required = {}", postId, acceptedCount, post.getRequiredPersonnel());

                // If quota is met, close recruitment and add members to team
                if (acceptedCount >= post.getRequiredPersonnel()) {
                    post.setStatus("COMPLETED");
                    recruitPostRepository.save(post);
                    log.info("Post {} auto-closed: quota met", postId);

                    // Batch add all accepted applicants to team
                    List<RecruitApplication> acceptedApps = applicationRepository.findByPostId(postId).stream()
                            .filter(app -> "ACCEPTED".equals(app.getStatus()))
                            .toList();

                    for (RecruitApplication app : acceptedApps) {
                        try {
                            addMemberToTeam(post.getTeamId(), app.getApplicantProfileId());
                        } catch (Exception e) {
                            log.error("Failed to add profile {} to team {}: {}",
                                    app.getApplicantProfileId(), post.getTeamId(), e.getMessage());
                        }
                    }
                    log.info("Added {} members to team {}", acceptedApps.size(), post.getTeamId());
                }
            }
            // Match creation logic for MATCH category
            else if ("MATCH".equals(post.getCategory()) && !"COMPLETED".equals(post.getStatus())) {
                // Create match between home team (post author's team) and away team (applicant's team)
                if (a.getApplicantTeamId() == null) {
                    log.warn("Application {} has no applicantTeamId, cannot create match", applicationId);
                } else {
                    Match match = createMatch(post, a);
                    log.info("Match {} created for post {}: Team {} vs Team {}",
                            match.getId(), postId, post.getTeamId(), a.getApplicantTeamId());

                    // 양팀에게 경기 확정 알림 전송
                    String matchMessage = String.format("경기가 확정되었습니다. %s, %s",
                            match.getMatchDate() != null ? match.getMatchDate().toString() : "일정 미정",
                            match.getMatchTime() != null ? match.getMatchTime() : "시간 미정");
                    // 홈팀과 원정팀의 캡틴이나 멤버들에게 알림 (간단히 팀별로 한 개씩만 전송)
                    // 실제로는 팀의 모든 멤버에게 보내야 하지만, 여기서는 신청자에게만 보냄
                    sendNotification(a.getApplicantProfileId(), "MATCH_CONFIRMED", matchMessage, "MATCH", match.getId());
                    sendNotification(post.getWriterProfileId(), "MATCH_CONFIRMED", matchMessage, "MATCH", match.getId());

                    // Link match and post
                    post.setMatchId(match.getId());
                    post.setStatus("COMPLETED");
                    recruitPostRepository.save(post);
                    log.info("Post {} completed and linked to match {}", postId, match.getId());

                    // Add match to both teams' schedules
                    try {
                        addMatchToTeamSchedule(post.getTeamId(), match.getId());
                        addMatchToTeamSchedule(a.getApplicantTeamId(), match.getId());
                        log.info("Match {} added to schedules of teams {} and {}",
                                match.getId(), post.getTeamId(), a.getApplicantTeamId());
                    } catch (Exception e) {
                        log.error("Failed to add match {} to team schedules: {}", match.getId(), e.getMessage());
                    }
                }
            }
        }

        return saved;
    }

    private void addMemberToTeam(Long teamId, Long profileId) {
        String url = teamServiceUrl + "/api/teams/" + teamId + "/members";
        Map<String, Object> request = new HashMap<>();
        request.put("profileId", profileId);
        request.put("roleInTeam", "MEMBER");

        restTemplate.postForEntity(url, request, Map.class);
        log.info("Added profile {} to team {} as MEMBER", profileId, teamId);
    }

    @Transactional
    public void delete(Long postId, Long applicationId) {
        RecruitApplication a = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT));
        if (!a.getPostId().equals(postId)) return;
        applicationRepository.delete(a);
    }

    private Match createMatch(RecruitPost post, RecruitApplication application) {
        Match match = new Match();
        match.setMatchDate(post.getMatchDate());
        match.setMatchTime(post.getGameTime());
        match.setVenue(post.getFieldLocation() != null ? post.getFieldLocation() : post.getRegion() + " " + post.getSubRegion());
        match.setHomeTeamId(post.getTeamId());
        match.setAwayTeamId(application.getApplicantTeamId());
        match.setStatus("SCHEDULED");
        match.setRecruitPostId(post.getId());

        return matchRepository.save(match);
    }

    private void addMatchToTeamSchedule(Long teamId, Long matchId) {
        String url = teamServiceUrl + "/api/teams/" + teamId + "/schedules";
        Map<String, Object> request = new HashMap<>();
        request.put("matchId", matchId);
        request.put("eventType", "MATCH");

        restTemplate.postForEntity(url, request, Map.class);
        log.info("Added match {} to team {} schedule", matchId, teamId);
    }

    // 알림 생성 헬퍼 메서드
    private void sendNotification(Long receiverProfileId, String type, String message, String relatedType, Long relatedId) {
        try {
            String url = notificationServiceUrl + "/api/notifications";
            Map<String, Object> request = new HashMap<>();
            request.put("receiverProfileId", receiverProfileId);
            request.put("receiverId", receiverProfileId); // receiverId도 같이 보냄
            request.put("type", type);
            request.put("message", message);
            if (relatedType != null) {
                request.put("relatedType", relatedType);
            }
            if (relatedId != null) {
                request.put("relatedId", relatedId);
            }

            restTemplate.postForEntity(url, request, Map.class);
            log.info("Notification sent to profile {}: {}", receiverProfileId, type);
        } catch (Exception e) {
            log.error("Failed to send notification to profile {}: {}", receiverProfileId, e.getMessage());
            // 알림 전송 실패해도 메인 로직은 계속 진행
        }
    }
}

