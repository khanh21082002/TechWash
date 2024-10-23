package com.example.techwash.Notification;

import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class NotificationService {
    private static final String FCM_API_URL = "https://fcm.googleapis.com/v1/projects/techwash-56d5a/messages:send";
    private static final String firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging";

    public static void saveFCMToken (String userid){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.d("FCM Token", "Token: " + token);
                // Lưu token vào Firestore
                saveFcmTokenToDatabase(userid, token);
            }
        });
    }

    private static void saveFcmTokenToDatabase(String uid, String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("fcmToken", token);

        db.collection("Tokens").document(uid)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "FCM token saved successfully."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error saving FCM token", e));
    }



    public static void sendNotification(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Tokens").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String token = documentSnapshot.getString("fcmToken");
                        sendFCM(token);
                    } else {
                        Log.w("Firestore", "No such document");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error getting FCM token", e);
                });
    }

    public static void sendFCM(String token) {
        new Thread(() -> {
            try {
                URL url = new URL(FCM_API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + getAccessToken());
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Xây dựng nội dung JSON gửi đi
                JSONObject message = new JSONObject();
                JSONObject notification = new JSONObject();
                notification.put("title", "Tech Wash Auto");
                notification.put("body", "Bạn có một lịch hẹn mới");


                JSONObject payload = new JSONObject();
                payload.put("token", token);
                payload.put("notification", notification);

                message.put("message", payload);

                // Gửi yêu cầu
                OutputStream os = conn.getOutputStream();
                os.write(message.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("Notification sent successfully.");
                } else {
                    System.out.println("Failed to send notification, response code: " + responseCode);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static String getAccessToken() {
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"techwash-56d5a\",\n" +
                    "  \"private_key_id\": \"7dd127cab4ae24a0f4226746872045085758713d\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDbcdLi4VH43tWj\\nP1zVhznRNwvMl3FO7cXDLSSiZZgkW54K2UmTnmH5r7R6Eg3xCs3Ozff2E4wlkK09\\nEAqcZaAkaLfsZy4SuevwLsbfSsa3cJtXLGBtGjEHOBSgVlBB/BdmQaYOF8zlgjy/\\nXQ0k9suzEjHlwI2XiD9VYbeptFSfTgTF0lJyQ5+Hds683gyIipqYTQ2dvVZzOPLe\\nIOsYaCeIh6FaD97aKXz1kYTZbeHQg948L9+NXH7k1ZznJ/4kGF+A6Ng+R/HPT/eK\\nNWfbuuIngSUE0a5CiGuApflgPyzkjYqMrWXm4aQoXYvMXtJx7C7DOb+CyCgFsLIa\\nxrat2VitAgMBAAECggEAB/bVEwPfcDtSIJcbnOpH+GZZiMbUR2uMul0oj7ZFM8iZ\\nb0Zci717S88LK0T9eYqNVpyWC7nA49og127+G5f6nGeqiStjei2CORjRgegaVy0A\\njkTfiQYi0mLAKOOJDSQDi2QFjUxi1M+Jud48LGPCl9Cou/BlkyLQPZ6Kxl9zlMZ0\\nWaWJ2A/vrKLwNrbBjSix1kQkGscz6fgQccEw14Pg0IUthdNuKAvk+IO7vQbVTPsI\\neu9Z+SdRb4kVBS4YLXhFL2t3nfc+/bXGJLHUiFg86gt9RYkn20gaNS2beobfzG1k\\nr16UhhVyBRpBb3P1rXd/V7zm8MxfAmW1JmrcSs/+wQKBgQD+VgMDH9s1E+ShhpaU\\nQz3vvIbmGSGZsyHd7ZAAMRSBqH7iYczzm//a8Vrc7Rs+p2l/2tMezCVqa9CKaZQq\\nWzdcCozOpUbdl52itYcydtbwN31Lu0d8mzG/cnsytIzCUkN/uB/L/DhMU0QyBsSi\\nM7OR58jW7WKq+u7Duj6vnq8UjQKBgQDc4V9SKc0GdF25AhTa0N7G2MNDIk7sCR67\\noCa6t3YN6UqOwDDWvdx1rq3GrXVrsmdvxCG6MFAKbM1CdzcCz/z7GboMkJHmo/+g\\nSIxHx7hhaaX16hQ9+/DavKgYZuICB4hLbzCsaVipoHAY2RhDkhm+QFGRmKVHyifa\\nPJTd7PYcoQKBgQDOBxz/jfHECG5fzqfySJpGtwDMKrWGTY70uTm6fgevlK/ccP9W\\npBtZ38QMTWKRmYoj0T7Opu70S4wuY6JrwWKSA9XWnBpQ7T38DXMMlSo+g6TTEU2y\\nigF9pZ5ZD9sqmvsk3iGb26A7bARFRihJgPpJCmsueDRCb221TgzztOb9DQKBgQCJ\\nOcuKtEWYWPtRUaW7PEDrsQe+0bXOluvtDVaCcjB5/BJCFc1B3Lr2sgtsf5+PxUrV\\n+TgrAJD5qRbs0DvxF17IcfdQjxDWBdVusyPUBJTckFJ3MXJaAAhnHQYiAuku8ZcP\\npojK1LgsymxaQ6X7dO1hcXw7xGYK7+FKiJDEP8+3gQKBgGnsYbXhv5O35Ja9HOei\\nfPPaZK4Rmb5hQqYYc03ETIVEuu2Qo7JApY/VM2QzIw+EjsjS2xtbi3BreehDDhEo\\nNv70wgIy3TR3Bjw1NKId6OwhT7GnjggsAXGGLlS54ZCjWfIdHrtfE7xL6ty28r9R\\naHEazVfU2/2I6Uki5BHyldzq\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-zub7j@techwash-56d5a.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"100708006461173223282\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-zub7j%40techwash-56d5a.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}";
            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream).createScoped(firebaseMessagingScope);
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (Exception e) {
            Log.e("AccessToken", "getAccessToken: " + e.getLocalizedMessage());
            return null;
        }
    }
}
