package com.transility.tim.android.http;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.transility.tim.android.Constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This implements an AsyncTask that issues REST requests
 * and dispatches the REST responses to the correct response-handlers.
 * This class should only be used by the RESTRequestFactory class.
 * @auther Himanshu Bapna
 * */
public class ResponseFetcher  extends AsyncTask<Void,RESTResponse,RESTResponse> {
    private static int ID_GENERATOR = 1;
    private static Map<String, ResponseFetcher> activeFetchers = new HashMap<String, ResponseFetcher>();

    private final String id;
    private final String originalUri;
    private final Class<? extends Context> forContextType;
    private final RESTRequest request;

    private Context appContext;
    private List<? extends RESTResponseHandler> responseHandler;
    private Bundle userData;

    ResponseFetcher(Context forContext, RESTRequest request, RESTResponseHandler httpRespHandler) {
        this(forContext, request, Arrays.asList(httpRespHandler), null);
    }

    ResponseFetcher(Context forContext, RESTRequest request, List<? extends RESTResponseHandler> handlers,
                    Bundle userData) {
        this.id = Integer.toString(ID_GENERATOR++);
        this.appContext = forContext.getApplicationContext();
        this.forContextType = forContext.getClass();
        this.originalUri = request.uri;
        this.request = request;
        this.responseHandler = handlers;
        this.userData = userData;

        activeFetchers.put(this.id, this);
        Log.i(Constants.LOGTAG, "ResponseFetcher-" + id + " created for " + originalUri);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i(Constants.LOGTAG, "ResponseFetcher-" + id + " about to start.");
    }

    @Override
    protected RESTResponse doInBackground(Void... params) {
        Context context;
        synchronized (this) {
            context = appContext;
        }

        Log.i(Constants.LOGTAG, "ResponseFetcher-" + id + " starts.");
        RESTResponse result = null;

            if (result != null) {
                result.release();
            }

            if (!isCancelled()) {
                final long start = System.currentTimeMillis();
                try {
                    result = request.dispatch();
                } catch (Throwable t) {
                    result = new RESTResponse(com.transility.tim.android.http.RESTResponse.Status.CONNECTOR_ERROR_INTERNAL,null, request);
                }
                Log.v(Constants.LOGTAG, "Server response took " + (System.currentTimeMillis() - start) + " millisecs.");
            } else {
                result = null;

            }


        if (result != null) {
            result.userData = this.userData;
        }

        if (result != null && isCancelled()) {
            result.release();
            result.userData = null;
            return null;
        }

        List<? extends RESTResponseHandler> handlers;
        synchronized (this) {
            handlers = responseHandler;
        }

        if (result != null && handlers != null) {
            for (RESTResponseHandler handler : handlers) {
                if (!result.isEmpty() && handler.matchesExpectedStatus(result.status)) {
                    try {
                        handler.handleResponseInBackground(context, forContextType, result);
                    } catch (Exception e) {
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
        if (appContext == null) {
            return;
        }

        try {
            if (response != null && responseHandler != null && !isCancelled()) {
                for (RESTResponseHandler handler : responseHandler) {
                    if (!response.isEmpty() && handler.matchesExpectedStatus(response.status)) {
                        try {
                            handler.handleResponseInUI(appContext, forContextType, response);
                        } catch (Exception e) {
                            Log.e(Constants.LOGTAG, "doInBackground at Async Task .", e);
                            return;
                        }
                    }
                }
            }
        } finally {
            synchronized (this) {
                appContext = null;
            }

            if (response != null) {
                if (responseHandler != null) {
                    response.release();
                }

                response.userData = null;
            }

            synchronized (this) {
                responseHandler = null;
                activeFetchers.remove(id);
                userData = null;
            }

            Log.i(Constants.LOGTAG, "ResponseFetcher-" + id + " has finished.");
        }
    }

    @Override
    protected void onProgressUpdate(RESTResponse... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (appContext == null) {
            return;
        }

        try {
            if (responseHandler != null) {
                for (RESTResponseHandler handler : responseHandler) {
                    handler.handleCancelledRequest(appContext, forContextType, request);
                }
            }
        } finally {
            synchronized (this) {
                appContext = null;
                responseHandler = null;
                activeFetchers.remove(id);
                userData = null;
            }
            Log.i(Constants.LOGTAG, "ResponseFetcher-" + id + " has been cancelled");
        }
    }
}
