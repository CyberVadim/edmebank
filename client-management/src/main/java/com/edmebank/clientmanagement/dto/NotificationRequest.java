package com.edmebank.clientmanagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {
    private Long clientId;
    private String email;
    private String message;
    private String type;

    public NotificationRequest(Long clientId, String email, String message, String type) {
        this.clientId = clientId;
        this.email = email;
        this.message = message;
        this.type = type;
    }
}
