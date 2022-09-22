package com.hanghae.greenstep.kakaoAPI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.hanghae.greenstep.kakaoAPI.Dto.CustomFieldDto;
import com.hanghae.greenstep.kakaoAPI.Dto.PushContentDto;
import com.hanghae.greenstep.kakaoAPI.Dto.PushTokenDto;
import com.hanghae.greenstep.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hanghae.greenstep.kakaoAPI.PushStatus.ALL;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoPushAlertService {

    @Value("${kakao_admin_key}")
    String adminKey;


    public void requestPushToken(Member member) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","KakaoAK "+ adminKey);
        headers.add("Content-type", "application/x-www-form-urlencoded");
        String pushToken = FcmService.getAccessToken().substring(0,235);
        String deviceId = UUID.randomUUID().toString();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("uuid", member.getKakaoId().toString());
        body.add("device_id", deviceId);
        body.add("push_type", "fcm");
        body.add("push_token", pushToken);
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, Object>> kakaoPushTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/push/register",
                HttpMethod.POST,
                kakaoPushTokenRequest,
                String.class
        );
        String responseBody = response.getBody();
        log.info(responseBody);
        log.info("푸시토큰 발급 완료");
        member.updatePushStatus(ALL);
    }

    @Transactional
    public void sendPushAlert(Member member, PushContentDto pushContentDto) throws JsonProcessingException {
        log.info(member.getKakaoId().toString());
//        PushTokenDto pushTokenDto = getPushToken(member);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","KakaoAK "+ adminKey);
        headers.add("Content-type", "application/x-www-form-urlencoded");

        log.info("알러트 생성");
        List<String> uuids = new ArrayList<>();
        uuids.add(member.getKakaoId().toString());

        JSONObject pushMsgDto = makeJson(pushContentDto);

        MultiValueMap<String, Object> pushContentBody = new LinkedMultiValueMap<>();
        pushContentBody.add("uuids", uuids);
        pushContentBody.add("push_message", pushMsgDto);
        log.info(pushContentBody.toString());
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, Object>> kakaoPushTokenRequest =
                new HttpEntity<>(pushContentBody, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/push/send",
                HttpMethod.POST,
                kakaoPushTokenRequest,
                String.class
        );
        log.info("통과");
        log.info("response: " + response);
    }

//    @Transactional
//    public void deletePushToken(Member member) throws JsonProcessingException {
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization","KakaoAK "+ adminKey);
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        PushTokenDto pushTokenDto = getPushToken(member);
//
//        MultiValueMap<String, Object> tokenDeleteBody = new LinkedMultiValueMap<>();
//        tokenDeleteBody.add("uuid", member.getKakaoId());
//        tokenDeleteBody.add("device_id", pushTokenDto.getDeviceId());
//        tokenDeleteBody.add("push_type", pushTokenDto.getPushType());
//
//        // HTTP 요청 보내기
//        HttpEntity<MultiValueMap<String, Object>> kakaoPushTokenDelete =
//                new HttpEntity<>(tokenDeleteBody, headers);
//        RestTemplate requestDeleteRT = new RestTemplate();
//        ResponseEntity<String> responseDeleteRT = requestDeleteRT.exchange(
//                "https://kapi.kakao.com/v2/push/deregister",
//                HttpMethod.POST,
//                kakaoPushTokenDelete,
//                String.class
//        );
//        log.info(responseDeleteRT.getBody());
//    }
//
//    public PushTokenDto getPushToken(Member member) throws JsonProcessingException {
//        log.info("푸시토큰 가져오기");
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization","KakaoAK "+ adminKey);
//
//        MultiValueMap<String, String> tokenCheckBody = new LinkedMultiValueMap<>();
//        tokenCheckBody.add("uuid", member.getKakaoId().toString());
//
//        // HTTP 요청 보내기
//        HttpEntity<MultiValueMap<String, String>> kakaoPushTokenRequest =
//                new HttpEntity<>(tokenCheckBody, headers);
//        log.info(tokenCheckBody+"    "+ headers);
//        RestTemplate rt = new RestTemplate();
//        ResponseEntity<String> response = rt.exchange(
//                "https://kapi.kakao.com/v2/push/tokens",
//                HttpMethod.POST,
//                kakaoPushTokenRequest,
//                String.class
//        );
//
//        String responseBody = response.getBody();
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = objectMapper.readTree(responseBody);
//        log.info(jsonNode.asText());
//        String uuid = jsonNode.get("uuid").asText();
//        String deviceId = jsonNode.get("device_id").asText();
//        String pushToken = jsonNode.get("push_token").asText();
//        String pushType = jsonNode.get("push_type").asText();
//        Long createdAt = jsonNode.get("created_at").asLong();
//        Long updatedAt = jsonNode.get("updated_at").asLong();
//
//        return PushTokenDto.builder()
//                .uuid(uuid)
//                .deviceId(deviceId)
//                .pushToken(pushToken)
//                .pushType(pushType)
//                .createdAt(createdAt)
//                .updatedAt(updatedAt)
//                .build();
//    }

    public JSONObject makeJson(PushContentDto pushContentDto){
        JSONObject pushJson = new JSONObject();
        JSONObject forFcm = new JSONObject();
        JSONObject customField = new JSONObject(pushContentDto);
//        forFcm.put("dryRun","true");
        forFcm.put("custom_field", customField);
        pushJson.put("for_fcm", forFcm);

        return pushJson;
    }
}
