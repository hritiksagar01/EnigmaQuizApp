package com.sagar.question_service.controller;

import com.sagar.question_service.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")

@RequestMapping("/leaderboard")
public class LeaderboardController {
    @Autowired
    LeaderboardService leaderboardService;
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadLeaderboardbyPlayed() {
        return leaderboardService.generateLeaderboardExcelPLayed();
    }
    @GetMapping("/download1")
    public ResponseEntity<byte[]> downloadLeaderboard() {
        return leaderboardService.generateLeaderboardExcel();
    }
}
