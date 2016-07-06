package devicepolicymanager;

import android.app.IntentService;
import android.content.Intent;

import com.transility.tim.android.Utilities.RestResponseShowFeedbackInterface;
import com.transility.tim.android.http.RESTResponse;

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

    @Override
    protected void onHandleIntent(Intent intent) {


        /**
         * This will be 
         */
        while (!isOperationCompleted){



        }

    }

    private RestResponseShowFeedbackInterface restResponseShowFeedbackInterface=new RestResponseShowFeedbackInterface() {
        @Override
        public void onSuccessOfBackGroundOperation(RESTResponse reposeJson) {
            isOperationCompleted=true;

        }

        @Override
        public void onErrorInBackgroundOperation(RESTResponse reposeJson) {

            isOperationCompleted=true;

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


}
