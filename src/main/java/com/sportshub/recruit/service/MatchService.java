package com.sportshub.recruit.service;

import com.sportshub.recruit.domain.Match;
import com.sportshub.recruit.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {
    private final MatchRepository matchRepository;
    private final RestTemplate restTemplate;

    @Value("${notification.service.url:http://sportshub-notification:8085}")
    private String notificationServiceUrl;

    @Value("${team.service.url:http://sportshub-team:8083}")
    private String teamServiceUrl;

    public List<Match> findAll() {
        return matchRepository.findAll();
    }

    public Match findById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + id));
    }

    public List<Match> findByStatus(String status) {
        return matchRepository.findByStatus(status);
    }

    public List<Match> findByMatchDate(LocalDate matchDate) {
        return matchRepository.findByMatchDate(matchDate);
    }

    public List<Match> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return matchRepository.findByMatchDateBetween(startDate, endDate);
    }

    @Transactional
    public Match create(Match match) {
        return matchRepository.save(match);
    }

    @Transactional
    public Match update(Long id, Match updates) {
        Match existing = findById(id);
        String oldStatus = existing.getStatus();

        if (updates.getMatchDate() != null) existing.setMatchDate(updates.getMatchDate());
        if (updates.getMatchTime() != null) existing.setMatchTime(updates.getMatchTime());
        if (updates.getVenue() != null) existing.setVenue(updates.getVenue());
        if (updates.getVenueId() != null) existing.setVenueId(updates.getVenueId());
        if (updates.getVenueUrl() != null) existing.setVenueUrl(updates.getVenueUrl());
        if (updates.getHomeTeamId() != null) existing.setHomeTeamId(updates.getHomeTeamId());
        if (updates.getAwayTeamId() != null) existing.setAwayTeamId(updates.getAwayTeamId());
        if (updates.getHomeScore() != null) existing.setHomeScore(updates.getHomeScore());
        if (updates.getAwayScore() != null) existing.setAwayScore(updates.getAwayScore());
        if (updates.getStatus() != null) existing.setStatus(updates.getStatus());
        if (updates.getReferee() != null) existing.setReferee(updates.getReferee());
        if (updates.getWeather() != null) existing.setWeather(updates.getWeather());
        if (updates.getTemperature() != null) existing.setTemperature(updates.getTemperature());

        Match saved = matchRepository.save(existing);

        // 경기 취소 시 양팀 캡틴에게 알림 전송
        if ("CANCELLED".equals(saved.getStatus()) && !"CANCELLED".equals(oldStatus)) {
            String message = String.format("경기가 취소되었습니다. (%s)",
                    saved.getMatchDate() != null ? saved.getMatchDate().toString() : "일정 미정");

            // 양팀 캡틴에게 알림 전송 (TODO: 팀 전체 멤버에게 전송하도록 개선)
            notifyTeamCaptain(saved.getHomeTeamId(), message, saved.getId());
            notifyTeamCaptain(saved.getAwayTeamId(), message, saved.getId());
            log.info("Match cancellation notifications sent for match {}", id);
        }

        return saved;
    }

    @Transactional
    public void delete(Long id) {
        matchRepository.deleteById(id);
    }

    // 팀 캡틴에게 알림 전송
    private void notifyTeamCaptain(Long teamId, String message, Long matchId) {
        try {
            String url = teamServiceUrl + "/api/teams/" + teamId + "/members";
            List<Map<String, Object>> members = restTemplate.getForObject(url, List.class);

            if (members != null) {
                for (Map<String, Object> member : members) {
                    Map<String, Object> id = (Map<String, Object>) member.get("id");
                    String role = (String) member.get("roleInTeam");
                    Boolean isActive = (Boolean) member.get("isActive");

                    if ("CAPTAIN".equals(role) && Boolean.TRUE.equals(isActive) && id != null) {
                        Object profileIdObj = id.get("profileId");
                        if (profileIdObj != null) {
                            Long profileId = ((Number) profileIdObj).longValue();
                            sendNotification(profileId, "MATCH_CANCELLED", message, "MATCH", matchId);
                            log.info("Match cancellation notification sent to captain (profileId: {}) of team {}", profileId, teamId);
                        }
                        break; // 캡틴 한 명에게만 전송
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to notify team captain for team {}: {}", teamId, e.getMessage());
        }
    }

    // 알림 생성 헬퍼 메서드
    private void sendNotification(Long receiverProfileId, String type, String message, String relatedType, Long relatedId) {
        try {
            String url = notificationServiceUrl + "/api/notifications";
            Map<String, Object> request = new HashMap<>();
            request.put("receiverProfileId", receiverProfileId);
            request.put("receiverId", receiverProfileId);
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
        }
    }
}
