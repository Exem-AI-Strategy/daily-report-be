package com.ai.dailyReport.reports.service;

import com.ai.dailyReport.reports.dto.ClickUpTaskDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;
    
    private static final String CLICKUP_API_BASE_URL = "https://api.clickup.com/api/v2";
    private static final String CLICKUP_API_TOKEN = "pk_288766577_XT482X09ML6NWAW6I0O987LQSO8QMTZJ";
    
    public void processClickUpLink(String link) {
        try {
            // 1. 링크에서 task_id와 team_id 파싱
            String[] linkParts = link.split("/");
            if (linkParts.length < 5) {
                log.error("Invalid link format: {}", link);
                return;
            }
            
            String taskId = linkParts[linkParts.length - 1];  // 마지막 부분
            
            // 2. 링크 형태에 따라 API URL 구성
            String apiUrl;
            if (linkParts.length == 5) {
                // 첫 번째 형태: /t/task_id
                apiUrl = String.format("%s/task/%s?include_subtasks=false&include_markdown_description=false",
                        CLICKUP_API_BASE_URL, taskId);
                log.info("Parsed task_id: {} (no team_id)", taskId);
            } else if (linkParts.length == 6) {
                // 두 번째 형태: /t/team_id/task_id
                String teamId = linkParts[linkParts.length - 2];  // 뒤에서 두번째
                apiUrl = String.format("%s/task/%s?custom_task_ids=true&team_id=%s&include_subtasks=false&include_markdown_description=false",
                        CLICKUP_API_BASE_URL, taskId, teamId);
                log.info("Parsed task_id: {}, team_id: {}", taskId, teamId);
            } else {
                log.error("Invalid link format: {}", link);
                return;
            }
            
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
            log.info("ClickUp API Response Status: {}", response.getStatusCode());
            
        } catch (Exception e) {
            log.error("Error calling ClickUp API for link: {}", link, e);
        }
    }
    
    // ClickUp API 호출하여 데이터를 파싱하여 반환하는 메서드
    public ClickUpTaskDto getClickUpTaskData(String link) {
        try {
            // 1. 링크에서 task_id와 team_id 파싱
            String[] linkParts = link.split("/");
            if (linkParts.length < 5) {
                log.error("Invalid link format: {}", link);
                return ClickUpTaskDto.createError("INVALID_LINK", "잘못된 링크 형식입니다.");
            }
            
            String taskId = linkParts[linkParts.length - 1];  // 마지막 부분
            
            // 2. 링크 형태에 따라 API URL 구성
            String apiUrl;
            if (linkParts.length == 5) {
                // 첫 번째 형태: /t/task_id
                apiUrl = String.format("%s/task/%s?include_subtasks=false&include_markdown_description=false",
                        CLICKUP_API_BASE_URL, taskId);
                log.info("Parsed task_id: {} (no team_id)", taskId);
            } else if (linkParts.length == 6) {
                // 두 번째 형태: /t/team_id/task_id
                String teamId = linkParts[linkParts.length - 2];  // 뒤에서 두번째
                apiUrl = String.format("%s/task/%s?custom_task_ids=true&team_id=%s&include_subtasks=false&include_markdown_description=false",
                        CLICKUP_API_BASE_URL, taskId, teamId);
                log.info("Parsed task_id: {}, team_id: {}", taskId, teamId);
            } else {
                log.error("Invalid link format: {}", link);
                return ClickUpTaskDto.createError("INVALID_LINK", "지원하지 않는 링크 형식입니다.");
            }
            
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
            
            // 3. 응답을 ClickUpTaskDto로 파싱
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("ClickUp API Response Status: {}", response.getStatusCode());
                
                // JSON 응답을 ClickUpTaskDto로 변환
                ClickUpTaskDto taskDto = objectMapper.readValue(response.getBody(), ClickUpTaskDto.class);
                return taskDto;
            } else {
                log.error("ClickUp API call failed with status: {}", response.getStatusCode());
                return ClickUpTaskDto.createError("API_ERROR", "ClickUp API 호출 실패: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Error calling ClickUp API for link: {}", link, e);
            return ClickUpTaskDto.createError("EXCEPTION", "ClickUp API 호출 중 오류 발생: " + e.getMessage());
        }
    }
}
