package com.eyc.key.modules.kyc.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class KycSubmitRequest {
    @NotBlank(message = "Họ tên không được trống")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Số CCCD không được trống")
    @Pattern(regexp = "^[0-9]{9,12}$", message = "Số CCCD không hợp lệ")
    private String identityNumber;

    @NotNull(message = "Ngày sinh không được trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Giới tính không được trống")
    private String gender;

    private String nationality = "Việt Nam";

    private String placeOfOrigin;

    private String placeOfResidence;

    @NotNull(message = "Ngày cấp không được trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate idIssueDate;

    @NotNull(message = "Ngày hết hạn không được trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate idExpiryDate;
}
