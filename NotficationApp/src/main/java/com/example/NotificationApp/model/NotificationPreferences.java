package com.example.NotificationApp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationPreferences {
    private boolean isEmailEnabled;



    private boolean isPushEnabled;

    public NotificationPreferences(boolean isEmailEnabled, boolean isPushEnabled) {
        this.isEmailEnabled = isEmailEnabled;
        this.isPushEnabled = isPushEnabled;
    }

    public boolean isPushEnabled() {
        return isPushEnabled;
    }

    public void setPushEnabled(boolean pushEnabled) {
        isPushEnabled = pushEnabled;
    }

    public boolean isEmailEnabled() {
        return isEmailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        isEmailEnabled = emailEnabled;
    }
}
