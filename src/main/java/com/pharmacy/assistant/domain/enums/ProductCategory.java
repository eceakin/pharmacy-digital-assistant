package com.pharmacy.assistant.domain.enums;

public enum ProductCategory {
    MEDICATION("İlaç"),
    MEDICAL_SUPPLY("Medikal Malzeme"),
    COSMETIC("Kozmetik"),
    SUPPLEMENT("Takviye Gıda"),
    PERSONAL_CARE("Kişisel Bakım"),
    OTHER("Diğer");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
