package com.transility.tim.android.http;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.transility.tim.android.Constants;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther Himanshu Bapna
 * */
public class ResponseFetcher  extends AsyncTask<Void,RESTResponse,RESTResponse>
        /*implements JUnitHTTPResponseFetcher*/ {
    private static int ID_GENERATOR = 1;
    private static Map<String,ResponseFetcher> activeFetchers = new HashMap<String,ResponseFetcher>();
    private static Map<String,ResponseFetcher> fetchersWaitingForAuthorization = new HashMap<String,ResponseFetcher>();

    public static ResponseFetcher getActiveResponseFecther(String fetcherId) {
        return activeFetchers.get(fetcherId);
    }

    public static void cancelRequests(Collection<String> fetcherIds) {
        for (String fetcherId : fetcherIds) {
            ResponseFetcher fetcher = activeFetchers.get(fetcherId);
            if (fetcher != null) {
                Log.i(Constants.LOGTAG, "ResponseFetcher-" + fetcher.id + " received a cancellation.");
                fetcher.cancel(true);
            }
        }
    }

    public static void cancelAllRequests() {
        for (ResponseFetcher fetcher : activeFetchers.values()) {
            if (fetcher != null) {
                Log.i(Constants.LOGTAG, "ResponseFetcher-"+fetcher.id+" received a cancellation.");
                fetcher.cancel(true);
            }
        }
    }

    public static void setIsAuthorized(String fetcherId, boolean isAuthorized) {
        ResponseFetcher fetcher = getResponseFetcherWaitingForAuthorization(fetcherId);
        if (fetcher != null) {
            fetcher.setIsAuthorized(isAuthorized);
        }
    }

    public static void retryRequest(String fetcherId, boolean retry) {
        ResponseFetcher fetcher = getResponseFetcherWaitingForAuthorization(fetcherId);
        if (fetcher != null) {
            fetcher.retryRequest(retry);
        }
    }

    private static ResponseFetcher getResponseFetcherWaitingForAuthorization(String id) {
        synchronized(fetchersWaitingForAuthorization) {
            return fetchersWaitingForAuthorization.get(id);
        }
    }

    private final String id;
    private final String originalUri;
    private final Class<? extends Context> forContextType;
    private final RESTRequest  request;

    private Context appContext;
    private List<? extends RESTResponseHandler> responseHandler;
    private Bundle userData;

    private boolean showLogonWhenUnAuthorized;
    private boolean waitForAuthorizedAnswer;
    private boolean tryRequestAgain;
    private com.transility.tim.android.http.RESTResponse.Status authStatus;

    ResponseFetcher(Context forContext, RESTRequest request, RESTResponseHandler httpRespHandler) {
        this(forContext, request, Arrays.asList(httpRespHandler), null, false);
    }

    ResponseFetcher(Context forContext, RESTRequest request, List<? extends RESTResponseHandler> handlers,
                    Bundle userData, boolean dontHandleUnauthorized) {
        this.id                 = Integer.toString(ID_GENERATOR++);
        this.appContext         = forContext.getApplicationContext();
        this.forContextType     = forContext.getClass();
        this.originalUri        = request.uri;
        this.request            = request;
        this.responseHandler    = handlers;
        this.userData           = userData;

        this.showLogonWhenUnAuthorized = !dontHandleUnauthorized;
//        this.request.uri = IntellicusMobilePreferences.checkUri(appContext, originalUri);;
        this.request.checkForCookies = this.showLogonWhenUnAuthorized;

        activeFetchers.put(this.id, this);
        Log.i(Constants.LOGTAG, "ResponseFetcher-"+id+" created for "+originalUri);
    }

    public void updateRequestURI(String serverURI){
//        this.request.uri = IntellicusMobilePreferences.checkUri(appContext, serverURI, originalUri);;
    }

    public String getID() {
        return id;
    }

    public void setIsAuthorized(boolean isAuthorized) {
        setIsAuthorized(isAuthorized, null);
    }

    public void retryRequest(boolean retry) {
        synchronized(this) {
            tryRequestAgain = retry;
            authStatus = retry ? null : RESTResponseHandler.STATUS_USER_CANCELLED_AUTH;

            waitForAuthorizedAnswer = false;
            this.notifyAll();
        }
    }

    private void setIsAuthorized(boolean isAuthorized, com.transility.tim.android.http.RESTResponse.Status status) {
        synchronized(this) {
            tryRequestAgain = isAuthorized;
            showLogonWhenUnAuthorized = false;
            if (status == null){
                authStatus = isAuthorized ? com.transility.tim.android.http.RESTResponse.Status.SUCCESS_OK : RESTResponseHandler.STATUS_USER_CANCELLED_AUTH;
            }
            else{
                authStatus = status;
            }

            waitForAuthorizedAnswer = false;

            this.notifyAll();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i(Constants.LOGTAG, "ResponseFetcher-"+id+" about to start.");
    }

    @Override
    protected RESTResponse doInBackground(Void... params) {
        Context context;
        synchronized(this) {
            context = appContext;
        }

        Log.i(Constants.LOGTAG, "ResponseFetcher-"+id+" starts.");
        RESTResponse result = null;
        do {
            if (result != null) {
                result.release();
                result = null;
            }

            if (!isCancelled()) {
                final long start = System.currentTimeMillis();
                try {
                    result = request.dispatch(context);
                }
                catch (Throwable t) {
                    result = new RESTResponse(com.transility.tim.android.http.RESTResponse.Status.CONNECTOR_ERROR_INTERNAL, null, request);
                }
                Log.v(Constants.LOGTAG, "Server response took "+(System.currentTimeMillis()-start)+" millisecs.");
            }
            else {
                result = null;
                break;
            }
        } while (mustTryAgainForFailedAuthorization(result));

        if (result != null) {
            result.userData = this.userData;
        }

        synchronized(fetchersWaitingForAuthorization) {
            fetchersWaitingForAuthorization.remove(id);
        }

        if (result != null && isCancelled()) {
            result.release();
            result.userData = null;
            return null;
        }

        List<? extends RESTResponseHandler> handlers;
        synchronized(this) {
            handlers = responseHandler;
        }

        if (result != null && handlers != null) {
            for (RESTResponseHandler handler : handlers) {
                if (!result.isEmpty() && handler.matchesExpectedStatus(result.status)) {
                    try {
                        handler.handleResponseInBackground(context, forContextType, result);
                    }
                    catch (Exception e) {
                        Log.e(Constants.LOGTAG, "doInBackground at Async Task .", e);
                        result.release();
                        result.userData = null;
                        return null;
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(RESTResponse response) {
        super.onPostExecute(response);
        if (appContext == null){
            return;
        }

        try {
            if (response != null && responseHandler != null && !isCancelled()) {
                for (RESTResponseHandler handler : responseHandler) {
                    if (!response.isEmpty() && handler.matchesExpectedStatus(response.status)) {
                        try {
                            handler.handleResponseInUI(appContext, forContextType, response);
                        }
                        catch (Exception e) {
                            Log.e(Constants.LOGTAG, "doInBackground at Async Task .", e);
                            return;
                        }
                    }
                }
            }
        }
        finally {
            synchronized(this) {
                appContext = null;
            }

            if (response != null) {
                if (responseHandler != null){
                    response.release();
                }

                response.userData = null;
            }

            synchronized(this) {
                responseHandler = null;
                activeFetchers.remove(id);
                userData = null;
            }

            Log.i(Constants.LOGTAG, "ResponseFetcher-"+id+" has finished.");
        }
    }

    @Override
    protected void onProgressUpdate(RESTResponse... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (appContext == null){
            return;
        }

        try {
            if (responseHandler != null) {
                for (RESTResponseHandler handler : responseHandler) {
                    handler.handleCancelledRequest(appContext, forContextType, request);
                }
            }
        }
        finally {
            synchronized(this) {
                appContext = null;
                responseHandler = null;
                activeFetchers.remove(id);
                userData = null;
            }
            Log.i(Constants.LOGTAG, "ResponseFetcher-"+id+" has been cancelled");
        }
    }

    private boolean mustTryAgainForFailedAuthorization(RESTResponse result) {
        if (showLogonWhenUnAuthorized && result != null && result.status.isError()) {
            Log.w(Constants.LOGTAG, "ResponseFetcher-"+id+" has a recoverable failure:\n  status="+result.status);

            tryRequestAgain = false;
            waitForAuthorizedAnswer = true;
            authStatus = null;

            synchronized(fetchersWaitingForAuthorization) {
                fetchersWaitingForAuthorization.put(id, this);
            }

            publishProgress(result);

            synchronized(this) {
                try {
                    while (waitForAuthorizedAnswer) {
                        if (!isCancelled()){
                            this.wait(500);
                        }
                    }
                    if (authStatus != null){
                        result.status = authStatus;
                    }
                } catch (InterruptedException e) {
                    tryRequestAgain = false;
                }

                request.uri = originalUri;

                synchronized(fetchersWaitingForAuthorization) {
                    fetchersWaitingForAuthorization.remove(id);
                }

                Log.w(Constants.LOGTAG, "ResponseFetcher-"+id+" has recovered from failure: "+tryRequestAgain);
                return tryRequestAgain;
            }
        }
        else {
            return false;
        }
    }

//    @Override
//    public void setJUnitListener(Listener listener) {
//    }
}
