package com.edmebank.clientmanagement.dto;

import java.time.LocalDate;
import java.util.UUID;

public class PassportDto {
    private Long id;
    private UUID clientId;
    private String passportNumber;
    private LocalDate passportIssueDate;
    private String documentPath;
    private LocalDate uploadedAt;
}
