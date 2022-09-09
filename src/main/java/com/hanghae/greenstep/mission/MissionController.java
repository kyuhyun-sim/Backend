package com.hanghae.greenstep.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @GetMapping("/missions/daily-lists")
    public ResponseEntity<?> getDailyMissions(HttpServletRequest request){
        return missionService.getDailyMissions(request);
    }

    @GetMapping("/missions/weekly-lists")
    public ResponseEntity<?> getWeeklyMissions(HttpServletRequest request){
        return missionService.getWeeklyMissions(request);
    }

    @GetMapping("/missions/today-lists")
    public ResponseEntity<?> getTodayMission(HttpServletRequest request){
        return missionService.getTodayMission(request);
    }

//    @GetMapping("/missions/{missionId}")
//    public ResponseEntity<?> getMissionDetail(@PathVariable Long missionId, HttpServletRequest request) {
//        return missionService.getMissionDetail(missionId, request);
//    }

}
