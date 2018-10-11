package cordova.plugin.smsport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class RecepteurSMS extends BroadcastReceiver {
    public static final String SMS_EXTRA_NAME = "pdus";
    private CallbackContext callbackReceive;
    private boolean isReceiving = true;

    // This broadcast boolean is used to continue or not the message broadcast
    // to the other BroadcastReceivers waiting for an incoming SMS (like the native SMS app)
    private boolean broadcast = false;


/*        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();

            String recMsgString = "";
            String fromAddress = "";
            SmsMessage recMsg = null;
            byte[] data = null;
            if (bundle != null) {
                Toast.makeText(cordova.getActivity().getWindow().getContext(), "bundle null",
                        Toast.LENGTH_SHORT).show();
                //---retrieve the SMS message received---
                Object[] pdus = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdus.length; i++) {
                    recMsg = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    try {
                        data = recMsg.getUserData();
                    } catch (Exception e) {
                        System.out.println("Error: " + e);
                    }
                    if (data != null) {
                        for (int index = 0; index < data.length; ++index) {
                            recMsgString += Character.toString((char) data[index]);
                        }
                    }

                    fromAddress = recMsg.getOriginatingAddress();
                }
                JSONObject jsonObj = new JSONObject();
                try {
                    jsonObj.put("expediteur", fromAddress);
                    jsonObj.put("corpsSms", recMsgString);
                    //  jsonObj.put("corpsSms1", sms.getDisplayMessageBody());
                    callbackReceive.success(jsonObj);

                } catch (Exception e) {
                    callbackReceive.success("Error: " + e.getMessage());

                    System.out.println("Error: " + e);
                }
            }


        }*/
   // }


    @Override
    public void onReceive(Context ctx, Intent intent) {

        // Get the SMS map from Intent
        Bundle extras = intent.getExtras();
        if (extras != null) {
            // Get received SMS Array
            Object[] smsExtra = (Object[]) extras.get(SMS_EXTRA_NAME);

            for (int i = 0; i < smsExtra.length; i++) {

                //*****************************************
                SmsMessage sms;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = intent.getExtras().getString("format");
                    sms = SmsMessage.createFromPdu((byte[]) smsExtra[i], format);
                } else
                    //noinspection deprecation
                    sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
                //µµµµµµµµµµµµµµµµµµµµµµµµµµµµµµµµµµµµµµµµµµ
               // SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
                if (this.isReceiving && this.callbackReceive != null) {
                    JSONObject jsonObj = new JSONObject();
                    try {
                        String recMsgString = "";
                        byte[] data = sms.getUserData();

                        if (data != null) {

                            for (int index = 0; index < data.length; ++index) {
                                recMsgString += Character.toString((char) data[index]);
                            }
                        }
                        jsonObj.put("expediteur", sms.getOriginatingAddress());
                        jsonObj.put("corpsSms",recMsgString);


                    } catch (Exception e) {
                        callbackReceive.success("Error: " + e.getMessage());

                        System.out.println("Error: " + e);
                    }
                    PluginResult result = new PluginResult(PluginResult.Status.OK, jsonObj);
                    result.setKeepCallback(true);
                    callbackReceive.sendPluginResult(result);
                    callbackReceive.success(jsonObj);
                }
            }

            // If the plugin is active and we don't want to broadcast to other receivers
            if (this.isReceiving && !broadcast) {
                this.abortBroadcast();
            }
        }
    }

    public void broadcast(boolean v) {
        this.broadcast = v;
    }

    public void startReceiving(CallbackContext ctx) {
        this.callbackReceive = ctx;
        this.isReceiving = true;
    }

    public void stopReceiving() {
        this.callbackReceive = null;
        this.isReceiving = false;
    }
}
