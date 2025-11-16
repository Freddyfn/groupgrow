package com.groupgrow.groupgrow.controller;

import com.groupgrow.groupgrow.dto.AiQuestionRequest;
import com.groupgrow.groupgrow.dto.AiResponse;
import com.groupgrow.groupgrow.service.GeminiAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    @Autowired
    private GeminiAiService geminiAiService;

    @PostMapping("/ask/group/{groupId}")
    public ResponseEntity<AiResponse> askGroupQuestion(
            @PathVariable Long groupId,
            @RequestBody AiQuestionRequest request) {
        try {
            System.out.println("=== AI CONTROLLER ===");
            System.out.println("GroupId: " + groupId);
            System.out.println("Question: " + request.getQuestion());
            
            String answer = geminiAiService.askGro(groupId, request.getQuestion());
            return ResponseEntity.ok(new AiResponse(answer));
        } catch (Exception e) {
            System.err.println("ERROR en AiController: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new AiResponse("Error: " + e.getMessage()));
        }
    }
}
