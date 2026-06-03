package com.eyc.key.modules.kyc.service;

import com.eyc.key.modules.kyc.entity.KycDocument;
import com.eyc.key.modules.kyc.enums.DocumentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    public KycDocument saveFile(MultipartFile file, UUID submissionId , DocumentType documentType) throws IOException {
        validateFile(file);

        // Tạo đường dãn file lưu
        String subDir = buildSubDir(submissionId);
        Path dirPath = Paths.get(uploadDir, subDir);
        System.out.println("filepath" + dirPath);
        Files.createDirectories(dirPath);

        // tạo tên duy nhất
        String extension = getExtension(file.getOriginalFilename());
        String storedFileName = documentType.name() + "_" + UUID.randomUUID() + "." + extension;
        Path filePath = dirPath.resolve(storedFileName);
        System.out.println(filePath);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String checkSum = calculateChecksum(file.getBytes());
        System.out.println("checkSum " + checkSum);

        return KycDocument.builder()
                .documentType(documentType)
                .fileName(file.getOriginalFilename())
                .storedPath(filePath.toString())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .checksum(checkSum)
                .build();
    }

    private void validateFile(MultipartFile file){
        if (file.isEmpty()){
            throw new RuntimeException("File không được để trống");
        }

        if(file.getSize() > maxFileSizeMb * MB) {
            throw new RuntimeException("File Vượt quá " + maxFileSizeMb + "MB" + MB);
        }

        String contentType = file.getContentType();
        if(contentType == null ||
        !contentType.equals("image/jpeg") && !contentType.equals("image/png")){
            throw new RuntimeException("Sai Định dạng file");
        }
    }

    private String buildSubDir(UUID submissionId){
        LocalDate now = LocalDate.now();
        return String.format("%d/%02d/%s",now.getYear() , now.getMonthValue(), submissionId);
    }

    private String getExtension(String filename){
        if(filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1 ).toLowerCase();
    }

    private String calculateChecksum(byte[] data){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            System.out.println("hash" + hash);
            return HexFormat.of().formatHex(hash);
        }catch (Exception e){
            return "unknow";
        }
    }
}
