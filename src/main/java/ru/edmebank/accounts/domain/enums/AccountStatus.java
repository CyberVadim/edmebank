package ru.edmebank.accounts.domain.enums;

public enum AccountStatus {
    ACTIVE("active"),
    CLOSED("closed"),
    BLOCKED("blocked"),
    FROZEN("frozen");

    private final String value;

    AccountStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static AccountStatus fromString(String status) {
        for (AccountStatus accountStatus : AccountStatus.values()) {
            if (accountStatus.value.equalsIgnoreCase(status)) {
                return accountStatus;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}