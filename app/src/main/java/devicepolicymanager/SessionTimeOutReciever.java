package devicepolicymanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Receiver which is called when the device time outs after periods downloaded from server.
 */
public class SessionTimeOutReciever extends BroadcastReceiver {
    public SessionTimeOutReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
