package com.sportshub.recruit.repository;

import com.sportshub.recruit.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByStatus(String status);
    List<Match> findByMatchDate(LocalDate matchDate);
    List<Match> findByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId);
    List<Match> findByMatchDateBetween(LocalDate startDate, LocalDate endDate);
}
