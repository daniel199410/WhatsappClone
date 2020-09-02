package com.example.whatsappclone.utils;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class SendNotification {

    public SendNotification(String message, String heading, String notificationKey) {
        notificationKey = "1a494c27-d466-4188-9694-36c4beb302f7";
        try {
            JSONObject notificationContent = new JSONObject(String.format("{" +
                    "'contents': {'en':'%s'}, " +
                    "'include_player_ids': ['" + "%s" + "']," +
                    "'headings':{'en':'%s'}}", message, notificationKey, heading));
            OneSignal.postNotification(notificationContent, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
