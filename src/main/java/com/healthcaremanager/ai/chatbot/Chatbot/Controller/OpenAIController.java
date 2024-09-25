package com.healthcaremanager.ai.chatbot.Chatbot.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthcaremanager.ai.chatbot.Chatbot.Service.OpenAIService;

@RestController
@RequestMapping("/api/openai")
public class OpenAIController {

    @Autowired
    private OpenAIService openAIService;

    // 處理健康建議的 POST 請求
    @PostMapping("/health-advice")
    public Map<String, Object> getHealthAdvice() {
        // 調用 Service 層，並返回 OpenAI API 的響應
        return openAIService.getHealthAdvice();
    }

    // 處理一般問題的 POST 請求
    @PostMapping("/ask")
    public Map<String, Object> askHealthQuestion(@RequestBody Map<String, String> request) {
        // 從請求中獲取用戶的問題
        String userQuestion = request.get("question");

        // 調用服務層方法，傳遞用戶問題並獲取回應
        return openAIService.handleGeneralQuestions(userQuestion);
    }
}


