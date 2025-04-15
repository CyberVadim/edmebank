package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.util.PassportOcrResult;

import java.io.File;

public interface OcrService {
    PassportOcrResult extractText(File imageFile);
}

