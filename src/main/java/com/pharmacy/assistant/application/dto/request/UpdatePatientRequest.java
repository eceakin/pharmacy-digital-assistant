package com.pharmacy.assistant.application.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePatientRequest {

    @NotBlank(message = "Ad alanı boş olamaz")
    private String firstName;

    @NotBlank(message = "Soyad alanı boş olamaz")
    private String lastName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Geçerli bir telefon numarası giriniz")
    private String phoneNumber;

    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;

    private String address;

    private Boolean smsConsentGiven;

    private String notes;
}