package com.sportshub.recruit.service;

import com.sportshub.recruit.domain.Match;
import com.sportshub.recruit.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {
    private final MatchRepository matchRepository;

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

        return matchRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        matchRepository.deleteById(id);
    }
}
