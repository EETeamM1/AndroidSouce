package com.transility.tim.android.http;

import android.content.Context;

import com.transility.tim.android.http.RESTResponse.Status;

/**
 * @author Himanshu Bapna
 */
public interface RESTResponseHandler {

    String NULL_URI = "/null";
    Status STATUS_HOST_NOT_FOUND = Status.CAN_NOT_FOUND_HOST;
    Status STATUS_USER_CANCELLED_AUTH = Status.USER_CANCELLED_AUTH;

    /**
     * This method will be called when a successful response is being returned from the server.
     * This method will be called on a background thread, the thread that issues the actual REST/HTTP request.
     *
     * @param context        The context in which the response will be handled (Application context).
     * @param forContextType The type of the context that issued the originating request (Activity context class).
     * @param response       The RESTResponse of the request:
     *                       <p/>
     *                       response.status The actual status (HTTP-status) of the response. This could be different than the returned
     *                       value of {@link #matchesExpectedStatus()}. This could be null, but that's very unlikely.
     *                       response.representation The response data/body. This could be null (e.g the response only contains headers).
     *                       response.method The HTTP method that was used to obtain the response.
     *                       response.uri The URI from which the response was obtained.
     */
    void handleResponseInBackground(Context context, Class<? extends Context> forContextType, RESTResponse response);

    /**
     * This method will be called when a successful response is being returned from the server.
     * This method will be called on the main GUI-thread.
     *
     * @param context        The context in which the response will be handled (Application context).
     * @param forContextType The type of the context that issued the originating request (Activity context class).
     * @param response       The RESTResponse of the request:
     *                       <p/>
     *                       response.status The actual status (HTTP-status) of the response. This could be different than the returned
     *                       value of {@link #matchesExpectedStatus()}. This could be null, but that's very unlikely.
     *                       response.representation The response data/body. This could be null (e.g the response only contains headers).
     *                       response.method The HTTP method that was used to obtain the response.
     *                       response.uri The URI from which the response was obtained.
     */
    void handleResponseInUI(Context context, Class<? extends Context> forContextType, RESTResponse response);

    /**
     * When the background REST-request was cancelled, this method is called.
     *
     * @param context        The context in which the response will be handled (Application context).
     * @param forContextType The type of the context that issued the originating request (Activity context class).
     * @param request        The request that was cancelled.
     */
    void handleCancelledRequest(Context context, Class<? extends Context> forContextType, RESTRequest request);

    /**
     * An implementing instance of this class must return a HTTP-Status code.
     * If a response's HTTP-status matches according to this method, this handler will
     * be chosen to handle the response: The system will call its {@link #handleResponse(Status, Representation, Method, String)} method.
     *
     * @param status The status to match on. It will never be null.
     * @return True if the provided status matches the expected status.
     */
    boolean matchesExpectedStatus(Status status);
}
