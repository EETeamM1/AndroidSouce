package devicepolicymanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import android.widget.Toast;

import com.transility.tim.android.LoginActivity;
import com.transility.tim.android.MasterPasswordActivity;
import com.transility.tim.android.Utilities.TransiltiyInvntoryAppSharedPref;
import com.transility.tim.android.Utilities.Utility;

public class MyDeviceAdminReciver extends DeviceAdminReceiver {




    @Override
    public void onDisabled(Context context, Intent intent) {


        openMasterPasswordScreen(context);


    }

    @Override
    public void onEnabled(Context context, Intent intent) {

        openLoginActivity(context);

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

                    openLoginActivity(context);
                } else {
                    if (TransiltiyInvntoryAppSharedPref.getWasLoginScreenVisible(context)) {

                        openLoginActivity(context);

                    } else {
                        Utility.appendLog("Boot Got Completed and in previous shut down User was having a valid session. ");
                        Utility.logError(MyDeviceAdminReciver.class.getSimpleName(), "Inside Login Screen invisible loop");
                        TransiltiyInvntoryAppSharedPref.setWasLoginScreenVisible(context, false);
                        reEnableAlarm(context);
                    }
                }

            } else if (TransiltiyInvntoryAppSharedPref.isMasterPasswordScreenVisible(context)) {
                openMasterPasswordScreen(context);
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


    /**
     * Function to reinitialise the next alarm based on the current user session.
     * @param context exiting context of the application.
     */
    private void reEnableAlarm(Context context) {
        int timeoutPeriod = TransiltiyInvntoryAppSharedPref.getSessionTimeout(context);
        if (TransiltiyInvntoryAppSharedPref.getDeviceLastShutdownTime(context) == 0) {
            reintialiseAlramWithGivenTimePeriod(context,System.currentTimeMillis(),timeoutPeriod*60*1000);
        } else {
            long elapsedTime = System.currentTimeMillis() - TransiltiyInvntoryAppSharedPref.getDeviceLastShutdownTime(context);
            reintialiseAlramWithGivenTimePeriod(context,System.currentTimeMillis() + elapsedTime,timeoutPeriod*60*1000);
        }
    }

    /**
     * Reinitialize the alarm with given time period.
     * @param context Existing context of the application.
     * @param timeForFirstCall Device Time at which Alarm should first go On
     * @param timeForSubiquentCall Interval in which Alarm should be called.
     */
    private void reintialiseAlramWithGivenTimePeriod(Context context,long timeForFirstCall,long timeForSubiquentCall){
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SessionTimeOutReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.cancel(alarmIntent);
        Toast.makeText(context, "Inside Last Shut Down Time loop " +timeForFirstCall, Toast.LENGTH_LONG).show();
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, timeForFirstCall
                , timeForSubiquentCall, alarmIntent);
        Utility.appendLog("Boot Got Completed the device will show login screen after " + timeForSubiquentCall + " min");
        Utility.logError(context.getClass().getSimpleName(), "Alarm Time>>>>" + System.currentTimeMillis() + (timeForSubiquentCall));

    }
    /**
     * Open the Login Activity.
     */
    private void openLoginActivity(Context context){
        Utility.appendLog("Boot Got Completed and in previous shut down Login Mode was enabled.");
        Utility.logError(MyDeviceAdminReciver.class.getSimpleName(), "Inside data base check loop");
        Utility.cancelCurrentPendingIntent(context);
        Intent loginIntent = new Intent(context, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(loginIntent);
    }

    /**
     * Open master password screen.
     * @param context Pass the current context of the application.
     */
    private void openMasterPasswordScreen(Context context){
        Utility.cancelCurrentPendingIntent(context);
        Utility.clearPrefrences();
        Intent masterPasswordIntent = new Intent(context, MasterPasswordActivity.class);
        masterPasswordIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(masterPasswordIntent);
    }
}
