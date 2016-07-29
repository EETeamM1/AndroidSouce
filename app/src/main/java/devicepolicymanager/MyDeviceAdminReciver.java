package devicepolicymanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.transility.tim.android.LoginActivity;
import com.transility.tim.android.MasterPasswordActivity;
import com.transility.tim.android.Utilities.TransiltiyInvntoryAppSharedPref;
import com.transility.tim.android.Utilities.Utility;

public class MyDeviceAdminReciver extends DeviceAdminReceiver {
    public static String MAHEVENT = "action.com.app.tranisity.android";

    private static WindowManager windowManager;
    private static LinearLayout wrapperView;
    private static Button submitBtn;

    @Override
    public void onDisabled(Context context, Intent intent) {


        Utility.cancelCurrentPendingIntent(context);
        Utility.clearPrefrences();
        Intent intent1 = new Intent(context, MasterPasswordActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);


    }

    @Override
    public void onEnabled(Context context, Intent intent) {

        Utility.cancelCurrentPendingIntent(context);
        Intent intent1 = new Intent(context, LoginActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);

    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        // Overwrided method
    }


    @Override
    public void onPasswordExpiring(Context context, Intent intent) {
        // This would require API 11 an above
        //Over writted method
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        // Overwritted methods
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        //Overwritted methods
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);
        Utility.logError(MyDeviceAdminReciver.class.getSimpleName(), "onReceive");

        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Toast.makeText(context, "My Device Boot Completed", Toast.LENGTH_LONG).show();
            DevicePolicyManager truitonDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

            if (truitonDevicePolicyManager.isAdminActive(new ComponentName(context, MyDeviceAdminReciver.class))) {
                if (TextUtils.isEmpty(TransiltiyInvntoryAppSharedPref.getSessionToken(context))) {

                    Utility.appendLog("Boot Got Completed and in previous shut down Login Mode was enabled.");
                    Utility.logError(MyDeviceAdminReciver.class.getSimpleName(), "Inside data base check loop");
                    Utility.cancelCurrentPendingIntent(context);
                    Intent intent1 = new Intent(context, LoginActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);
                } else {
                    if (TransiltiyInvntoryAppSharedPref.getWasLoginScreenVisible(context)) {

                        Utility.appendLog("Boot Got Completed and in previous shut down Login Mode was enabled.");
                        Utility.logError(MyDeviceAdminReciver.class.getSimpleName(), "Inside Login Screen Visible loop");
                        Utility.cancelCurrentPendingIntent(context);
                        Intent intent1 = new Intent(context, LoginActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);

                    } else {
                        Utility.appendLog("Boot Got Completed and in previous shut down User was having a valid session. ");
                        Utility.logError(MyDeviceAdminReciver.class.getSimpleName(), "Inside Login Screen invisible loop");
                        TransiltiyInvntoryAppSharedPref.setWasLoginScreenVisible(context, false);
                        reEnableAlarm(context);
                    }
                }

            } else if (TransiltiyInvntoryAppSharedPref.isMasterPasswordScreenVisible(context)) {
                Intent intent1 = new Intent(context, MasterPasswordActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }
        } else if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {

            if (!TransiltiyInvntoryAppSharedPref.getWasLoginScreenVisible(context)) {
                Utility.appendLog("Device Got Shut down and this time user might be having valid login session.");
                Utility.logError(MyDeviceAdminReciver.class.getSimpleName(), "Inside Action Shut Donw Screen invisible loop");

                TransiltiyInvntoryAppSharedPref.setDeviceLastShutdownTime(context, System.currentTimeMillis());
            } else {
                Utility.appendLog("Device Got Shut down and this time user was on login screen.");
                Utility.logError(MyDeviceAdminReciver.class.getSimpleName(), "Inside Action Shut Donw Screen Visible loop");
                TransiltiyInvntoryAppSharedPref.setDeviceLastShutdownTime(context, 0);
            }


        }
    }


    private void reEnableAlarm(Context context) {

        int timeoutPeriod = TransiltiyInvntoryAppSharedPref.getSessionTimeout(context);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SessionTimeOutReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.cancel(alarmIntent);

        if (TransiltiyInvntoryAppSharedPref.getDeviceLastShutdownTime(context) == 0) {
            Toast.makeText(context, "Inside Last Shut Down Time loop " + System.currentTimeMillis() + (timeoutPeriod * 60 * 1000), Toast.LENGTH_LONG).show();
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (timeoutPeriod * 60 * 1000)
                    , timeoutPeriod * 60 * 1000, alarmIntent);
            Utility.appendLog("Boot Got Completed the device will show login screen after " + timeoutPeriod + " min");
            Utility.logError(context.getClass().getSimpleName(), "Alarm Time>>>>" + System.currentTimeMillis() + (timeoutPeriod * 60 * 1000));
        } else {
            long elapsedTime = System.currentTimeMillis() - TransiltiyInvntoryAppSharedPref.getDeviceLastShutdownTime(context);
            Toast.makeText(context, "Inside Last Shut Down Time loop " + System.currentTimeMillis() + (elapsedTime), Toast.LENGTH_LONG).show();
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + elapsedTime
                    , timeoutPeriod * 60 * 1000, alarmIntent);
            Utility.appendLog("Boot Got Completed the device will show login screen after " + (elapsedTime / 60000) + " min");
            Utility.logError(context.getClass().getSimpleName(), "Alarm Time>>>>" + System.currentTimeMillis() + (elapsedTime));

        }


    }
}
