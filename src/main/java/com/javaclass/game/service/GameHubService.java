package com.javaclass.game.service;

import com.javaclass.game.dto.HubInteractionResponse;
import org.springframework.stereotype.Service;

@Service
public class GameHubService {

    public HubInteractionResponse saveBed(Long playerId) {
        return HubInteractionResponse.builder()
            .action("BED_SAVE")
            .allowed(true)
            .message("saved")
            .build();
    }

    public HubInteractionResponse validatePortal(Long playerId) {
        return HubInteractionResponse.builder()
            .action("PORTAL_VALIDATE")
            .allowed(true)
            .message("allowed")
            .build();
    }

    public HubInteractionResponse openStorage(Long playerId) {
        return HubInteractionResponse.builder()
            .action("STORAGE_OPEN")
            .allowed(true)
            .message("allowed")
            .build();
    }
}
