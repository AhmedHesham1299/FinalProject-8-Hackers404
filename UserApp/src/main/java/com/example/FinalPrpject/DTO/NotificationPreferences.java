package com.example.FinalPrpject.DTO;

public class NotificationPreferences {
    private boolean isPushEnabled;
    private boolean isEmailEnabled;

    public NotificationPreferences() {}

    public NotificationPreferences(boolean isPushEnabled, boolean isEmailEnabled) {
        this.isPushEnabled = isPushEnabled;
        this.isEmailEnabled = isEmailEnabled;
    }

    public boolean isPushEnabled() {
        return isPushEnabled;
    }

    public void setPushEnabled(boolean isPushEnabled) {
        this.isPushEnabled = isPushEnabled;
    }

    public boolean isEmailEnabled() {
        return isEmailEnabled;
    }

    public void setEmailEnabled(boolean isEmailEnabled) {
        this.isEmailEnabled = isEmailEnabled;
    }
}
