package cordova.plugin.smsport;
import android.Manifest;
import android.app.Activity;
import android.content.IntentFilter;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import android.content.Intent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.telephony.SmsManager;
/**
 * This class echoes a string called from JavaScript.
 */
public class smsport extends CordovaPlugin {
public final String READ_SMS="ecoutersms";
public final String SEND_SMS="sendsms";
private RecepteurSMS recepteurSMS = null;
private boolean isReceiving = false;
    private CallbackContext callbackReceive;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        if (action.equals("ecoutersms")) {
            String port = args.getString(0);
            this.EcouteReceptionSMS(port, callbackContext);
            return true;
        }
        if (action.equals("sendsms")) {
            String telephone = args.getString(0);
            String text = args.getString(1);
            int port = args.optInt(2);
            this.envoyerSMS(telephone,text,port,callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Veuillez saisir une chaine.");
        }
    }
    public void EcouteReceptionSMS(String port,CallbackContext callbackContext) {
        if (this.isReceiving) {
            /*PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(false);
            this.callbackReceive.sendPluginResult(pluginResult);
*/
           //S callbackContext.error("Pas sms à venir");
        }
        this.isReceiving = true;

        if (this.recepteurSMS == null) {
            this.recepteurSMS = new RecepteurSMS();
            IntentFilter intent = new IntentFilter("android.intent.action.DATA_SMS_RECEIVED");
           // IntentFilter intent = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            intent.setPriority(10000);
            intent.addDataScheme("sms");
            intent.addDataAuthority("*",port);
            this.cordova.getActivity().registerReceiver(this.recepteurSMS, intent);
        }

        this.recepteurSMS.startReceiving(callbackContext);


        this.callbackReceive = callbackContext;
    }
    public void  envoyerSMS(String telephone, String message, int port,CallbackContext callbackContext) {
        short p=(short) port;
        try{
            SmsManager smsManager=SmsManager.getDefault();
            smsManager.sendDataMessage(telephone,null,p,message.getBytes(),null,null);

            // this.afficheToast(message);
            // Toast.makeText(this.cordova.getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();

           // callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "Message envoyé avec succès !"));
            callbackContext.success("message envoyé");
        }
        catch (Exception e){
            callbackContext.error("erreur envoi sms "+e.getMessage());

            //callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Erreur d'envoi du message"));
        }
    }
}
