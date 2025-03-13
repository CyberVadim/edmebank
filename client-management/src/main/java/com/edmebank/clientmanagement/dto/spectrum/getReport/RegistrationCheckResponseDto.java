package com.edmebank.clientmanagement.dto.spectrum.getReport;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RegistrationCheckResponseDto {
    private boolean canRegister;
    private List<String> passedChecks;
    private List<String> issues;
}

