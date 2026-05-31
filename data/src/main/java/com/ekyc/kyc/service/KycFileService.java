package com.ekyc.kyc.service;

import com.ekyc.kyc.entity.KycDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.UUID;

@Service
@Slf4j
public class KycFileService {

    @Value("${kyc.file.upload-dir:uploads/kyc}")
    private String uploadDir;

    @Value("${kyc.file.max-size-mb:5}")
    private long maxFileSizeMb;

    private static final long MB = 1024 * 1024;

    public KycDocument saveFile(MultipartFile file,
                                UUID submissionId,
                                KycDocument.DocumentType documentType) throws IOException {
        // Validate
        validateFile(file);

        // Tạo path theo cấu trúc: uploads/kyc/2024/01/submissionId/FRONT_uuid.jpg
        String subDir = buildSubDir(submissionId);
        Path dirPath = Paths.get(uploadDir, subDir);
        Files.createDirectories(dirPath);

        // Tên file unique
        String extension = getExtension(file.getOriginalFilename());
        String storedFileName = documentType.name() + "_" + UUID.randomUUID() + "." + extension;
        Path filePath = dirPath.resolve(storedFileName);

        // Lưu file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Tính checksum
        String checksum = calculateChecksum(file.getBytes());

        log.info("Saved KYC file: {}", filePath);

        return KycDocument.builder()
                .documentType(documentType)
                .fileName(file.getOriginalFilename())
                .storedPath(filePath.toString())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .checksum(checksum)
                .build();
    }

    public void deleteFile(String storedPath) {
        try {
            Files.deleteIfExists(Paths.get(storedPath));
            log.info("Deleted KYC file: {}", storedPath);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", storedPath, e);
        }
    }

    public byte[] readFile(String storedPath) throws IOException {
        Path path = Paths.get(storedPath);
        if (!Files.exists(path)) {
            throw new RuntimeException("File không tồn tại: " + storedPath);
        }
        return Files.readAllBytes(path);
    }

    // ---- Helpers ----

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File không được trống");
        }
        if (file.getSize() > maxFileSizeMb * MB) {
            throw new RuntimeException("File vượt quá " + maxFileSizeMb + "MB");
        }
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new RuntimeException("Chỉ chấp nhận file JPG hoặc PNG");
        }
    }

    private String buildSubDir(UUID submissionId) {
        LocalDate now = LocalDate.now();
        return String.format("%d/%02d/%s", now.getYear(), now.getMonthValue(), submissionId);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    private String calculateChecksum(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            return "unknown";
        }
    }
}
