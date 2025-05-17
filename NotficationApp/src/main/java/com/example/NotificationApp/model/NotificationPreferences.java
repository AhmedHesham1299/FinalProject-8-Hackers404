package com.example.NotificationApp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferences {
    private boolean isEmailEnabled;
    private boolean isPushEnabled;
}
