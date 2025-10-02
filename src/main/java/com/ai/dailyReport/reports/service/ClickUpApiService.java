package com.ai.dailyReport.reports.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClickUpApiService {
    
    private final RestTemplate restTemplate;
    
    private static final String CLICKUP_API_BASE_URL = "https://api.clickup.com/api/v2";
    private static final String CLICKUP_API_TOKEN = "pk_288766577_XT482X09ML6NWAW6I0O987LQSO8QMTZJ";
    
    public void processClickUpLink(String link) {
        try {
            // 1. 링크에서 task_id와 team_id 파싱
            String[] linkParts = link.split("/");
            if (linkParts.length < 2) {
                log.error("Invalid link format: {}", link);
                return;
            }
            
            String taskId = linkParts[linkParts.length - 1];  // 마지막 부분
            String teamId = linkParts[linkParts.length - 2];  // 뒤에서 두번째
            
            log.info("Parsed task_id: {}, team_id: {}", taskId, teamId);
            
            // 2. ClickUp API 호출
            String apiUrl = String.format("%s/task/%s?custom_task_ids=true&team_id=%s&include_subtasks=false&include_markdown_description=false",
                    CLICKUP_API_BASE_URL, taskId, teamId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", CLICKUP_API_TOKEN);
            headers.set("accept", "application/json");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl, 
                    HttpMethod.GET, 
                    entity, 
                    String.class
            );
            
            // 3. 응답 콘솔 로그 출력
            log.info("ClickUp API Response Status: {}", response.getStatusCode());
            log.info("ClickUp API Response Body: {}", response.getBody());
            
        } catch (Exception e) {
            log.error("Error calling ClickUp API for link: {}", link, e);
        }
    }
}
