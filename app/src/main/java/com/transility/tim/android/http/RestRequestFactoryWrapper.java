package com.transility.tim.android.http;

import android.content.Context;

import com.transility.tim.android.Utilities.RestResponseShowFeedbackInterface;
import com.transility.tim.android.Utilities.Utility;

import java.util.Arrays;
import java.util.List;

/**
 * Customized Wrapper class to interact with Rest Framework.
 * Created by ambesh.kukreja on 6/8/2016.
 */
public class RestRequestFactoryWrapper {


    private RestResponseShowFeedbackInterface restResponseShowFeedbackInterface;
    /**
     * Handler called when the Resquest has executed Successfully.
     */
    RESTResponseHandler okhandler = new RESTResponseHandler() {
        @Override
        public void handleResponseInBackground(Context context, Class<? extends Context> forContextType, RESTResponse response) {
            restResponseShowFeedbackInterface.onSuccessOfBackGroundOperation(response);
            Utility.logError(this.getClass().getSimpleName(), "handleResponseInBackground");

        }

        @Override
        public void handleResponseInUI(Context context, Class<? extends Context> forContextType, RESTResponse response) {
            Utility.logError(this.getClass().getSimpleName(), "handleResponseInUI");

            restResponseShowFeedbackInterface.onSuccessInForeGroundOperation(response);
        }

        @Override
        public void handleCancelledRequest(Context context, Class<? extends Context> forContextType, RESTRequest request) {
            Utility.logError(this.getClass().getSimpleName(), "handleCancelledRequest");

        }

        @Override
        public boolean matchesExpectedStatus(RESTResponse.Status status) {
            return status.isSuccess();
        }
    };
    /**
     * Handler called when the Resquest executed with errors.
     */
    RESTResponseHandler errorHandler = new RESTResponseHandler() {
        @Override
        public void handleResponseInBackground(Context context, Class<? extends Context> forContextType, RESTResponse response) {
            Utility.logError(this.getClass().getSimpleName(), "handleResponseInBackground");
            restResponseShowFeedbackInterface.onErrorInBackgroundOperation(response);

        }

        @Override
        public void handleResponseInUI(Context context, Class<? extends Context> forContextType, RESTResponse response) {
            Utility.logError(this.getClass().getSimpleName(), "handleResponseInUI");
            restResponseShowFeedbackInterface.onErrorInForeGroundOperation(response);
        }

        @Override
        public void handleCancelledRequest(Context context, Class<? extends Context> forContextType, RESTRequest request) {
            Utility.logError(this.getClass().getSimpleName(), "handleCancelledRequest");

        }

        @Override
        public boolean matchesExpectedStatus(RESTResponse.Status status) {
            return status.isError();
        }
    };
    private Context context;

    /**
     * Creates a object for this wrapper class.
     *
     * @param context
     * @param restResponseShowFeedbackInterface
     */
    public RestRequestFactoryWrapper(Context context, RestResponseShowFeedbackInterface restResponseShowFeedbackInterface) {

        this.restResponseShowFeedbackInterface = restResponseShowFeedbackInterface;
        this.context = context;
    }

    /**
     * Create and intiate a Rest Request to the RequestURL passed.
     *
     * @param requestUrl
     * @param requestJson
     */
    public void callHttpRestRequest(String requestUrl, String requestJson, RESTRequest.Method method) {
        List<RESTResponseHandler> handlers = Arrays.asList(okhandler, errorHandler);
        RESTRequestFactory.dispatch(context, method, requestUrl, requestJson, null, handlers, null);
    }
}
