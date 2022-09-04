package com.hanghae.greenstep.rank;

import com.hanghae.greenstep.member.MemberRepository;
import com.hanghae.greenstep.shared.Message;
import com.hanghae.greenstep.submitMission.SubmitMission;
import com.hanghae.greenstep.submitMission.SubmitMissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RankService {
    private final MemberRepository memberRepository;
    private final SubmitMissionRepository submitMissionRepository;

    @Transactional
    public ResponseEntity<?> getRankMissions() {
        List<SubmitMission> memberRankList = submitMissionRepository.findRankMissions();
        return new ResponseEntity<>(Message.success(memberRankList), HttpStatus.OK);
    }
}
