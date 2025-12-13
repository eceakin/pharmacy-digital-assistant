package com.pharmacy.assistant.domain.model.patient;


import com.pharmacy.assistant.domain.enums.PatientStatus;
import com.pharmacy.assistant.domain.model.common.BaseEntity;
import com.pharmacy.assistant.domain.valueobject.ContactInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Patient extends BaseEntity {
    private String firstName;
    private String lastName;
    private ContactInfo contactInfo;
    private boolean smsConsentGiven;
    private PatientStatus status;
    private String notes;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean canReceiveSms() {
        return smsConsentGiven &&
                contactInfo != null &&
                contactInfo.hasPhoneNumber();
    }

    public boolean canReceiveEmail() {
        return contactInfo != null && contactInfo.hasEmail();
    }

    public void activate() {
        this.status = PatientStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = PatientStatus.INACTIVE;
    }
}