package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.dto.PassportDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface PassportService {
    PassportDto uploadPassport(UUID clientId, MultipartFile file);
}

