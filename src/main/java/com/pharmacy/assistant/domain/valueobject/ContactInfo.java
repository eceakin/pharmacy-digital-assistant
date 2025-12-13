package com.pharmacy.assistant.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfo {
    private String phoneNumber;
    private String email;
    private String address;

    public boolean hasPhoneNumber() {
        return phoneNumber != null && !phoneNumber.isEmpty();
    }

    public boolean hasEmail() {
        return email != null && !email.isEmpty();
    }
}