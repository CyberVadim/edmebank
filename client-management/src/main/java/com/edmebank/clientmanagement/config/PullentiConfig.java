package com.edmebank.clientmanagement.config;

import com.pullenti.address.AddressService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Configuration
@Slf4j
public class PullentiConfig {
    
    @Value("${pullenti.gar.download.enabled:true}")
    private boolean downloadEnabled;
    
    @Value("client-management/src/main/resources/Pullenti")
    private String garIndexBasePath;
    
    private static final String GAR77_URL = "http://garfias.ru/assets/docs/Gar77.zip";
    private static final String GAR77_FOLDER = "Gar77";
    
    @PostConstruct
    public void initPullenti() throws Exception {
        Path garIndexPath = Paths.get(garIndexBasePath, GAR77_FOLDER);
        if (!Files.exists(garIndexPath)) {
            if (downloadEnabled) {
                log.info("GAR index not found. Downloading from {}", GAR77_URL);
                downloadAndUnzipGar77(Paths.get(garIndexBasePath));
            } else {
                log.info("GAR index not found and download is disabled. Pullenti will run without GAR index.");
                AddressService.initialize();
                return;
            }
        }
        AddressService.initialize();
        AddressService.setGarIndexPath(garIndexPath.toString());
    }
    
    private void downloadAndUnzipGar77(Path targetBaseDir) throws IOException {
        Files.createDirectories(targetBaseDir);
        Path zipPath = targetBaseDir.resolve("Gar77.zip");
        Path tmpExtractDir = targetBaseDir.resolve("tmpGar77");
        log.info("Downloading GAR77.zip...");
        try (InputStream in = URI.create(GAR77_URL).toURL().openStream()) {
            Files.copy(in, zipPath, StandardCopyOption.REPLACE_EXISTING);
        }
        log.info("Unzipping GAR77.zip...");
        Files.createDirectories(tmpExtractDir);
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath.toFile()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path newFile = tmpExtractDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(newFile);
                } else {
                    Files.createDirectories(newFile.getParent());
                    try (OutputStream out = Files.newOutputStream(newFile)) {
                        zis.transferTo(out);
                    }
                }
                zis.closeEntry();
            }
        }
        // Move Gar77 from tmpExtractDir to targetBaseDir
        Path extractedGar77 = tmpExtractDir.resolve(GAR77_FOLDER);
        Path finalGar77 = targetBaseDir.resolve(GAR77_FOLDER);
        if (Files.exists(finalGar77)) {
            // Clean up if already exists
            deleteRecursively(finalGar77);
        }
        Files.move(extractedGar77, finalGar77, StandardCopyOption.REPLACE_EXISTING);
        deleteRecursively(tmpExtractDir);
        Files.deleteIfExists(zipPath);
        log.info("GAR77.zip download and extraction complete.");
    }
    
    private void deleteRecursively(Path path) throws IOException {
        if (Files.notExists(path)) return;
        try (var walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException e) {
                            log.warn("Failed to delete {}", p, e);
                        }
                    });
        }
    }
}