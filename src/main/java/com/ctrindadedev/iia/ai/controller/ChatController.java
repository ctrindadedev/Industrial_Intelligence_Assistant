package com.ctrindadedev.iia.ai.controller;

import com.ctrindadedev.iia.ai.service.TurnoChatbotService;
import com.ctrindadedev.iia.core.dto.PsoeDtos.PerguntaRequest;
import com.ctrindadedev.iia.core.dto.PsoeDtos.RespostaChat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final TurnoChatbotService turnoChatbotService;

    public ChatController(TurnoChatbotService turnoChatbotService) {
        this.turnoChatbotService = turnoChatbotService;
    }

    @PostMapping
    public ResponseEntity<RespostaChat> perguntar(@RequestBody PerguntaRequest request) {
        var resposta = turnoChatbotService.perguntar(request.conversationId(), request.mensagem());
        return ResponseEntity.ok(new RespostaChat(resposta));
    }
}
