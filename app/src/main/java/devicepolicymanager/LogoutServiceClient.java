package devicepolicymanager;

import android.app.IntentService;
import android.content.Intent;

import com.transility.tim.android.LoginActivity;
import com.transility.tim.android.R;
import com.transility.tim.android.Utilities.TransiltiyInvntoryAppSharedPref;
import com.transility.tim.android.Utilities.Utility;
import com.transility.tim.android.bean.Logout;
import com.transility.tim.android.http.RESTRequest;
import com.transility.tim.android.http.RestRequestFactoryWrapper;

/**
 * Created the service to perform logout operation in background.
 * Created by ambesh.kukreja on 7/6/2016.
 */
public class LogoutServiceClient extends IntentService {



    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LogoutServiceClient(String name) {
        super(name);

    }

    public LogoutServiceClient() {
        super(LogoutServiceClient.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        String sessionToken = TransiltiyInvntoryAppSharedPref.getSessionToken(this);
        String json = Logout.writeLogoutJson(sessionToken);
        String logoutRequest = getResources().getString(R.string.baseUrl) + getResources().getString(R.string.api_logout);
        Utility.appendLog("Logout Request=" + logoutRequest + " Json=" + json + " Request Type=" + RESTRequest.Method.POST);

        RestRequestFactoryWrapper restRequestFactoryWrapper = new RestRequestFactoryWrapper(this, null);
        restRequestFactoryWrapper.callHttpRestRequest(logoutRequest,null ,json, RESTRequest.Method.POST);

        Utility.cancelCurrentAlarmToLaunchTheLoginScreen(LogoutServiceClient.this);
        Utility.clearPreviousSessionToken();

        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginActivityIntent);
    }


}
