package devicepolicymanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.transility.tim.android.MasterPasswordActivity;
import com.transility.tim.android.Utilities.TransiltiyInvntoryAppSharedPref;
import com.transility.tim.android.Utilities.Utility;

/**
 * This broadcast to launch Master Password screen from background.
 * Created by ambesh.kukreja on 8/10/2016.
 */
public class MasterPasswordScreenLauncherBroadcast extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
            Utility.logError(MasterPasswordScreenLauncherBroadcast.class.getSimpleName(),"MasterPasswordScreenLauncherBroadcast Launched");
           Utility.cancelCurrentAlarmToLaunchTheMasterPasswordScreen(context);
        if (!TransiltiyInvntoryAppSharedPref.getWasLoginScreenVisible(context)&&!TransiltiyInvntoryAppSharedPref.isMasterPasswordScreenVisible(context)){

            Utility.cancelCurrentAlarmToLaunchTheLoginScreen(context);

            Utility.clearPreviousSessionToken();
            Intent masterPasswordIntent = new Intent(context, MasterPasswordActivity.class);
            masterPasswordIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(masterPasswordIntent);

        }
        Utility.logError(MasterPasswordScreenLauncherBroadcast.class.getSimpleName(),"MasterPasswordScreenLauncherBroadcast completed");
    }
}
