package devicepolicymanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.transility.tim.android.InventoryManagment;
import com.transility.tim.android.LoginActivity;
import com.transility.tim.android.MasterPasswordScreen;

import com.transility.tim.android.Utilities.TransiltiyInvntoryAppSharedPref;
import com.transility.tim.android.Utilities.Utility;

import java.util.zip.Inflater;

public class MyDeviceAdminReciver extends DeviceAdminReceiver {
    public static String MAHEVENT="action.com.app.tranisity.android";

    private static WindowManager  windowManager;
    private static LinearLayout wrapperView;
    private static  Button submitBtn;

    @Override
    public void onDisabled(Context context, Intent intent) {

        cancelCurrentPendingIntent(context);
        ((InventoryManagment)context.getApplicationContext()).getInventoryDatabasemanager().getEmployeeDataTable()
                .deleteEmployeeInfoFromDatabase(((InventoryManagment)context.getApplicationContext()).getSqliteDatabase());
        Intent intent1=new Intent(context, MasterPasswordScreen.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);


    }

    @Override
    public void onEnabled(Context context, Intent intent) {

        cancelCurrentPendingIntent(context);
        Intent intent1=new Intent(context, LoginActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);

    }







private View.OnClickListener onClickListener=new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        windowManager.removeViewImmediate(wrapperView);
    }
};
    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        Utility.logError(MyDeviceAdminReciver.class.getSimpleName(),"onPasswordChanged");

        DevicePolicyManager localDPM = (DevicePolicyManager) context
                .getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName localComponent = new ComponentName(context,
                MyDeviceAdminReciver.class);
        localDPM.setPasswordExpirationTimeout(localComponent, 0L);
    }


    @Override
    public void onPasswordExpiring(Context context, Intent intent) {
        // This would require API 11 an above
        Utility.logError(MyDeviceAdminReciver.class.getSimpleName(),"onPasswordExpiring");

        DevicePolicyManager localDPM = (DevicePolicyManager) context
                .getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName localComponent = new ComponentName(context,
                MyDeviceAdminReciver.class);
        long expr = localDPM.getPasswordExpiration(localComponent);
        long delta = expr - System.currentTimeMillis();
        boolean expired = delta < 0L;
        if (expired) {

            localDPM.setPasswordExpirationTimeout(localComponent, 10000L);
            Intent passwordChangeIntent = new Intent(
                    DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
            passwordChangeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(passwordChangeIntent);
        }
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        Utility.logError(MyDeviceAdminReciver.class.getSimpleName(),"onPasswordFailed");
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        Utility.logError(MyDeviceAdminReciver.class.getSimpleName(),"onPasswordSucceeded");
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);
        Utility.logError(MyDeviceAdminReciver.class.getSimpleName(),"onReceive");

        if (intent.getAction()!=null&&intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            if (((InventoryManagment)context.getApplicationContext()).getInventoryDatabasemanager()
                    .getEmployeeDataTable().getEmployeeTableRowCount(((InventoryManagment)context.getApplicationContext()).getSqliteDatabase())==0){

                cancelCurrentPendingIntent(context);
                Intent intent1=new Intent(context, LoginActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }
            else {
                if (TransiltiyInvntoryAppSharedPref.getWasLoginScreenVisible(context)){

                    cancelCurrentPendingIntent(context);
                    Intent intent1=new Intent(context, LoginActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);

                }
                else {
                    TransiltiyInvntoryAppSharedPref.setWasLoginScreenVisible(context,false);
                }
            }

        }
        else  if (intent.getAction()!=null&&intent.getAction().equals(Intent.ACTION_SHUTDOWN)){

            if (!TransiltiyInvntoryAppSharedPref.getWasLoginScreenVisible(context)){
                TransiltiyInvntoryAppSharedPref.setWasLoginScreenVisible(context,false);
            }


        }
    }


    /**
     * Cancels the current Pending Intent when the device is restarted.
     * @param context
     */
    private void cancelCurrentPendingIntent(Context context){
        AlarmManager alarmManager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SessionTimeOutReciever.class);
        PendingIntent  alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.cancel(alarmIntent);

        }
}
