package com.pharmacy.assistant.domain.enums;

public enum AdministrationRoute {
    ORAL("Ağızdan"),
    SUBLINGUAL("Dil Altı"),
    INTRAVENOUS("Damar İçi"),
    INTRAMUSCULAR("Kas İçi"),
    SUBCUTANEOUS("Deri Altı"),
    TOPICAL("Cilt Üzerine"),
    INHALATION("Solunum Yolu"),
    OPHTHALMIC("Göz İçi"),
    OTIC("Kulak İçi"),
    NASAL("Burun İçi"),
    RECTAL("Rektal"),
    VAGINAL("Vajinal");

    private final String displayName;

    AdministrationRoute(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
