package com.edmebank.clientmanagement.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Getter
@Setter
public class ClientDocumentRequest {
    private List<MultipartFile> documents;
}
