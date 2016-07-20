package devicepolicymanager;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.transility.tim.android.InventoryDatabase.InventoryDatabaseManager;
import com.transility.tim.android.InventoryManagment;
import com.transility.tim.android.LoginActivity;
import com.transility.tim.android.R;
import com.transility.tim.android.Utilities.RestResponseShowFeedbackInterface;
import com.transility.tim.android.Utilities.Utility;
import com.transility.tim.android.bean.Logout;
import com.transility.tim.android.http.RESTRequest;
import com.transility.tim.android.http.RESTResponse;
import com.transility.tim.android.http.RestRequestFactoryWrapper;

/**
 * Created the service to perform logout operation in background.
 * Created by ambesh.kukreja on 7/6/2016.
 */
public class LogoutServiceClient extends IntentService{


    private boolean isOperationCompleted=false;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LogoutServiceClient(String name) {
        super(name);

    }

    public LogoutServiceClient(){
        super(LogoutServiceClient.class.getSimpleName());
    }
    @Override
    protected void onHandleIntent(Intent intent) {



        if (Utility.checkInternetConnection(LogoutServiceClient.this)){
            InventoryDatabaseManager inventoryDatabaseManager = ((InventoryManagment) LogoutServiceClient.this.getApplication()).getInventoryDatabasemanager();
            String sessionToken = inventoryDatabaseManager.getEmployeeDataTable().
                    getSessionToken(((InventoryManagment) LogoutServiceClient.this.getApplication()).getSqliteDatabase());
            String json = Logout.writeLogoutJson(sessionToken);
            String loginRequest = getResources().getString(R.string.baseUrl) + getResources().getString(R.string.api_logout);
            Utility.appendLog("Login Request="+loginRequest+" Json="+json+" Request Type="+RESTRequest.Method.POST);

            RestRequestFactoryWrapper   restRequestFactoryWrapper = new RestRequestFactoryWrapper(this, restResponseShowFeedbackInterface);
            restRequestFactoryWrapper.callHttpRestRequest(loginRequest, json, RESTRequest.Method.POST);
        }
        else{
            Utility.appendLog("Offline Logout");
            InventoryDatabaseManager inventoryDatabaseManager = ((InventoryManagment) LogoutServiceClient.this.getApplication()).getInventoryDatabasemanager();
            inventoryDatabaseManager.getEmployeeDataTable()
                    .deleteEmployeeInfoFromDatabase(((InventoryManagment) LogoutServiceClient.this.getApplication()).getSqliteDatabase());
            isOperationCompleted=true;
        }


        /**
         * This will hold the thread until logout service response is returned.
         */
        while (!isOperationCompleted){



        }

        Intent intent1=new Intent(this, LoginActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);

    }

    private RestResponseShowFeedbackInterface restResponseShowFeedbackInterface=new RestResponseShowFeedbackInterface() {
        @Override
        public void onSuccessOfBackGroundOperation(RESTResponse reposeJson) {

            Utility.logError(LogoutServiceClient.class.getSimpleName(),"Request Code>>"+reposeJson.status.getCode()+" Resposne Message>>"+reposeJson.getText());
            Utility.appendLog("Response Logout API="+reposeJson.getText());

            isOperationCompleted=true;
            InventoryDatabaseManager inventoryDatabaseManager = ((InventoryManagment) LogoutServiceClient.this.getApplication()).getInventoryDatabasemanager();
            inventoryDatabaseManager.getEmployeeDataTable()
                    .deleteEmployeeInfoFromDatabase(((InventoryManagment) LogoutServiceClient.this.getApplication()).getSqliteDatabase());
            Utility.cancelCurrentPendingIntent(LogoutServiceClient.this);

        }

        @Override
        public void onErrorInBackgroundOperation(RESTResponse reposeJson) {
            Utility.logError(LogoutServiceClient.class.getSimpleName(),"Request Code>>"+reposeJson.status.getCode()+" Resposne Message>>"+reposeJson.getText());
            Utility.appendLog("Response Logout API"+reposeJson.getText());

            isOperationCompleted=true;
            Utility.cancelCurrentPendingIntent(LogoutServiceClient.this);

        }

        @Override
        public void onSuccessInForeGroundOperation(RESTResponse restResponse) {
        // This will be empty since this componene tis not attached to any UI
        }

        @Override
        public void onErrorInForeGroundOperation(RESTResponse restResponse) {
            // This will be empty since this componene tis not attached to any UI
        }


    };

    /**
     * Canceles the current pending intent.
     * @param context
     */



}
