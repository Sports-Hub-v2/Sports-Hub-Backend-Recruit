package com.sportshub.recruit.service;

import com.sportshub.recruit.domain.RecruitPost;
import com.sportshub.recruit.repository.RecruitApplicationRepository;
import com.sportshub.recruit.repository.RecruitPostRepository;
import com.sportshub.recruit.web.dto.RecruitDtos.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitService {
    private final RecruitPostRepository recruitPostRepository;
    private final RecruitApplicationRepository recruitApplicationRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public RecruitPost create(RecruitPost p) {
        if (p.getStatus() == null || p.getStatus().isBlank()) p.setStatus("OPEN");
        return recruitPostRepository.save(p);
    }

    @Transactional(readOnly = true)
    public RecruitPost get(Long id) {
        return recruitPostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found"));
    }

    @Transactional(readOnly = true)
    public PostResponse getWithStats(Long id) {
        RecruitPost post = get(id);
        Long acceptedCount = recruitApplicationRepository.countAcceptedByPostId(post.getId());
        PostResponse response = new PostResponse(post, acceptedCount);
        // 프로필 이름 조회
        if (post.getWriterProfileId() != null) {
            String authorName = getProfileName(post.getWriterProfileId());
            response.setAuthorName(authorName);
        }
        // 팀 이름 조회
        if (post.getTeamId() != null) {
            String teamName = getTeamName(post.getTeamId());
            response.setTeamName(teamName);
        }
        return response;
    }

    private String getProfileName(Long profileId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT name FROM profiles WHERE id = ?",
                String.class,
                profileId
            );
        } catch (Exception e) {
            return null;
        }
    }

    private String getTeamName(Long teamId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT team_name FROM teams WHERE id = ?",
                String.class,
                teamId
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public RecruitPost update(Long id, RecruitPost patch) {
        RecruitPost p = get(id);
        if (patch.getTitle() != null) p.setTitle(patch.getTitle());
        if (patch.getContent() != null) p.setContent(patch.getContent());
        if (patch.getRegion() != null) p.setRegion(patch.getRegion());
        if (patch.getSubRegion() != null) p.setSubRegion(patch.getSubRegion());
        if (patch.getImageUrl() != null) p.setImageUrl(patch.getImageUrl());
        if (patch.getMatchDate() != null) p.setMatchDate(patch.getMatchDate());
        if (patch.getGameTime() != null) p.setGameTime(patch.getGameTime());
        if (patch.getCategory() != null) p.setCategory(patch.getCategory());
        if (patch.getTargetType() != null) p.setTargetType(patch.getTargetType());
        if (patch.getStatus() != null) p.setStatus(patch.getStatus());
        if (patch.getTeamId() != null) p.setTeamId(patch.getTeamId());
        if (patch.getWriterProfileId() != null) p.setWriterProfileId(patch.getWriterProfileId());
        if (patch.getRequiredPersonnel() != null) p.setRequiredPersonnel(patch.getRequiredPersonnel());
        if (patch.getPreferredPositions() != null) p.setPreferredPositions(patch.getPreferredPositions());
        if (patch.getAgeGroup() != null) p.setAgeGroup(patch.getAgeGroup());
        if (patch.getSkillLevel() != null) p.setSkillLevel(patch.getSkillLevel());
        if (patch.getFieldLocation() != null) p.setFieldLocation(patch.getFieldLocation());
        // 새로 추가된 필드들
        if (patch.getMatchType() != null) p.setMatchType(patch.getMatchType());
        if (patch.getTeamSize() != null) p.setTeamSize(patch.getTeamSize());
        if (patch.getFieldType() != null) p.setFieldType(patch.getFieldType());
        if (patch.getCost() != null) p.setCost(patch.getCost());
        if (patch.getParkingAvailable() != null) p.setParkingAvailable(patch.getParkingAvailable());
        if (patch.getShowerFacilities() != null) p.setShowerFacilities(patch.getShowerFacilities());
        if (patch.getActivityDays() != null) p.setActivityDays(patch.getActivityDays());
        if (patch.getActivityTime() != null) p.setActivityTime(patch.getActivityTime());
        if (patch.getMinPlayers() != null) p.setMinPlayers(patch.getMinPlayers());
        if (patch.getMaxPlayers() != null) p.setMaxPlayers(patch.getMaxPlayers());
        if (patch.getMatchRules() != null) p.setMatchRules(patch.getMatchRules());
        return recruitPostRepository.save(p);
    }

    @Transactional
    public void delete(Long id) {
        if (!recruitPostRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found");
        }
        recruitPostRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<RecruitPost> list(Long teamId, Long writerProfileId, String status, String category) {
        if (teamId != null) return recruitPostRepository.findByTeamId(teamId);
        if (writerProfileId != null) return recruitPostRepository.findByWriterProfileId(writerProfileId);
        if (status != null && !status.isBlank()) return recruitPostRepository.findByStatus(status);
        if (category != null && !category.isBlank()) return recruitPostRepository.findByCategory(category);
        return recruitPostRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> listWithStats(Long teamId, Long writerProfileId, String status, String category) {
        List<RecruitPost> posts = list(teamId, writerProfileId, status, category);
        return posts.stream()
                .map(post -> {
                    Long acceptedCount = recruitApplicationRepository.countAcceptedByPostId(post.getId());
                    PostResponse response = new PostResponse(post, acceptedCount);
                    // 프로필 이름 조회
                    if (post.getWriterProfileId() != null) {
                        String authorName = getProfileName(post.getWriterProfileId());
                        response.setAuthorName(authorName);
                    }
                    // 팀 이름 조회
                    if (post.getTeamId() != null) {
                        String teamName = getTeamName(post.getTeamId());
                        response.setTeamName(teamName);
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }
}
