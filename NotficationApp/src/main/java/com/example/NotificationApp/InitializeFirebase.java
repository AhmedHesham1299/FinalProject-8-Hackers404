//package com.example.NotificationApp;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//
//@Component
//public class InitializeFirebase {
//
//    @Value("${firebase.config.path}")
//    private String firebaseConfigPath;
//
//
//    @PostConstruct
//    public void init() throws IOException {
//        if (FirebaseApp.getApps().isEmpty()) {
//            FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);
//
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .build();
//
//            FirebaseApp.initializeApp(options);
//        }
//    }
//}
