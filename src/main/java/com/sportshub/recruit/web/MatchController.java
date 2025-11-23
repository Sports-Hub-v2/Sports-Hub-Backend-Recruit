package com.sportshub.recruit.web;

import com.sportshub.recruit.domain.Match;
import com.sportshub.recruit.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {
    private final MatchService matchService;

    @GetMapping
    public List<Match> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate matchDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        if (status != null) {
            return matchService.findByStatus(status);
        }
        if (matchDate != null) {
            return matchService.findByMatchDate(matchDate);
        }
        if (startDate != null && endDate != null) {
            return matchService.findByDateRange(startDate, endDate);
        }
        return matchService.findAll();
    }

    @GetMapping("/{id}")
    public Match get(@PathVariable Long id) {
        return matchService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Match create(@RequestBody Match match) {
        return matchService.create(match);
    }

    @PatchMapping("/{id}")
    public Match update(@PathVariable Long id, @RequestBody Match updates) {
        return matchService.update(id, updates);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        matchService.delete(id);
    }

    @PostMapping("/{id}/cancel")
    public Match cancel(@PathVariable Long id, @RequestBody(required = false) String reason) {
        Match updates = new Match();
        updates.setStatus("CANCELLED");
        return matchService.update(id, updates);
    }

    @PostMapping("/{id}/complete")
    public Match complete(
            @PathVariable Long id,
            @RequestParam Integer homeScore,
            @RequestParam Integer awayScore
    ) {
        Match updates = new Match();
        updates.setStatus("COMPLETED");
        updates.setHomeScore(homeScore);
        updates.setAwayScore(awayScore);
        return matchService.update(id, updates);
    }
}
