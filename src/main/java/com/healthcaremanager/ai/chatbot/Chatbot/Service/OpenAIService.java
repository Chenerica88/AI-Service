package com.healthcaremanager.ai.chatbot.Chatbot.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    // 保留健康建議功能
    @SuppressWarnings("unchecked")
    public Map<String, Object> getHealthAdvice() {
        Map<String, Object> responseJson = new HashMap<>();
        try {
            String apiURL = "https://api.openai.com/v1/chat/completions";
            String healthData = "{\\\"systolic\\\": 135, \\\"diastolic\\\": 85, \\\"heart_rate\\\": 78, \\\"date\\\": \\\"2024-09-01\\\"}";
            String requestBody = "{\n" +
                    "  \"model\": \"gpt-4o\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"system\", \"content\": \"你是一位健康管理助理，你的任務是根據用戶的健康數據提供健康建議。請使用繁體中文回應，只回答健康相關問題。如果用戶問非健康問題，請回應‘我無法回答你的問題’。\"},\n" +
                    "    {\"role\": \"user\", \"content\": \"這是我最近的血壓讀數： " + healthData
                    + "。請問我的健康狀況如何？是否需要就醫？建議的運動時間是多少？\"}\n" +
                    "  ]\n" +
                    "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiURL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 將回應轉換為 JSON 格式並返回
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> responseMap = mapper.readValue(response.body(), Map.class);
    
            // 取得 choices 列表，並從中取得 answer
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            String responseContent = (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");
    
            // 只保留 answer 的結果
            responseJson.put("answer", responseContent);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseJson;
    }

    // 新增處理非健康問題的功能
    @SuppressWarnings("unchecked")
    public Map<String, Object> handleGeneralQuestions(String question) {
        Map<String, Object> responseJson = new HashMap<>();
        try {
            String apiURL = "https://api.openai.com/v1/chat/completions";
            String requestBody = "{\n" +
                    "  \"model\": \"gpt-4o\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"system\", \"content\": \"你只能回答健康相關問題，對於其他問題請回應‘我無法回答你的問題’。\"},\n" +
                    "    {\"role\": \"user\", \"content\": \"" + question + "\"}\n" +
                    "  ]\n" +
                    "}";
    
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiURL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();
    
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
            // 使用 Jackson 來解析 JSON
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> responseMap = mapper.readValue(response.body(), Map.class);
    
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            String responseContent = (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");
    
            responseJson.put("answer", responseContent);
    
        } catch (Exception e) {
            e.printStackTrace();
            responseJson.put("answer", "無法處理請求");
        }
        return responseJson;  
    }
    
}