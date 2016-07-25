package devicepolicymanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.transility.tim.android.Utilities.Utility;

/**
 * Receiver which is called when the device time outs after periods downloaded from server.
 * Created By ambesh.kukreja
 */
public class SessionTimeOutReciever extends BroadcastReceiver {
    public SessionTimeOutReciever() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Utility.logError(SessionTimeOutReciever.class.getSimpleName(), "On Recive of SessionTImeoutReciver");
        Intent logoutServiceIntent = new Intent(context, LogoutServiceClient.class);
        context.startService(logoutServiceIntent);

    }
}
