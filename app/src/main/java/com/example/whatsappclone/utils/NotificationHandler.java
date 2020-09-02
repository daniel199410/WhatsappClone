package com.example.whatsappclone.utils;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationHandler {

    public static void sendNotification(String message, String heading, String notificationKey, String chatId) {
        try {
            JSONObject notificationContent = new JSONObject(String.format("{" +
                    "'contents': {'en':'%s'}, " +
                    "'include_player_ids': ['" + "%s" + "']," +
                    "'headings':{'en':'%s'}," +
                    "'data':{'chatId': %s}}", message, notificationKey, heading, chatId));
            OneSignal.postNotification(notificationContent, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
