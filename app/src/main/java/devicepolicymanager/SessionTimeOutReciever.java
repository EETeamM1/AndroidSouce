package devicepolicymanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.transility.tim.android.LoginActivity;

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
        Intent intent1=new Intent(context, LoginActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);

    }
}
