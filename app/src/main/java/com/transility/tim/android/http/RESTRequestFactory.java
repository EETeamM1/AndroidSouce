package com.transility.tim.android.http;

import android.content.Context;
import android.os.Bundle;

import com.transility.tim.android.Constants;
import com.transility.tim.android.http.RESTRequest.Method;

import java.util.List;
import java.util.Map;

/**
 * This class creates and dispatches REST requests and makes sure the correct RESTResponseHandlers are called
 * back upon the server's response.
 * Created by Himanshu Bapna
 */
public class RESTRequestFactory {

    static RESTRequestFactory DISPATCHER_FACTORY = null;
    private Context appContext;

    protected RESTRequestFactory(android.content.Context context) {
        appContext = context.getApplicationContext();
    }

    /**
     * @return A HTTPRequestDispatcher factory.
     */
    public static synchronized RESTRequestFactory factory(android.content.Context context) {
        if (DISPATCHER_FACTORY == null) {
            DISPATCHER_FACTORY = new RESTRequestFactory(context);
        }
        return DISPATCHER_FACTORY;
    }

    /**
     * Replaces the {@link Constants#NAME_URI_PATH} with the specified path-string.
     */
    public static String appendPath(String uri, String path) {
        if (uri == null || uri.length() == 0) {
            return uri;
        }

        return uri.replaceFirst(Constants.REGEX_URI_PATH, path);
    }

    /**
     * Dispatch a HTTP request to one of the given response handlers.
     *
     * @param forContext The context for which this request is dispatched.
     * @param method     The HTTP request's HTTP-method.
     * @param uri        The HTTP request's fully qualified URI.
     * @param body       Body of the request (e.g. post-parameters, xml, etc).
     * @param handlers   The response handlers.
     * @param userData   User data that can be fetched again when the dispatch returns a {@link RESTResponse}.
     * @return A ResponseFetcher that will handle the HTTP request as soon as it can.
     */
    public static ResponseFetcher dispatch(Context forContext, Method method, String uri, Object body, Map<String, Object> queryParams, List<? extends RESTResponseHandler> handlers, Bundle userData) {
        return factory(forContext).doDispatch(forContext, method, uri, body, queryParams, handlers, userData);
    }

    /**
     * Dispatch a HTTP request to one of the given response handlers.
     *
     * @param forContext The context for which this request is dispatched.
     * @param method     The HTTP request's HTTP-method.
     * @param uri        The HTTP request's fully qualified URI.
     * @param body       Body of the request (e.g. post-parameters, xml, etc).
     * @param handlers   The response handlers.
     * @param userData   User data that can be fetched again when the dispatch returns a {@link RESTResponse}
     * @return A ResponseFetcher that will handle the HTTP request as soon as it can.
     */
    protected ResponseFetcher doDispatch(Context forContext, Method method, String uri, Object body, Map<String, Object> queryParams, List<? extends RESTResponseHandler> handlers, Bundle userData) {
        final ResponseFetcher fetcher = new ResponseFetcher(forContext, create(method, uri, body, queryParams), handlers, userData);
        fetcher.execute();
        return fetcher;
    }


    /**
     * Creates an actual HTTPRequestDispatcher for the given request parameters.
     * (Override this method for mocking it when testing)
     *
     * @param method HTTP Method
     * @param uri    URI of the request.
     * @param body   POST/PUT body (if any). Can be null
     * @return An actual HTTPRequest
     */
    protected RESTRequest create(Method method, String uri, Object body, Map<String, Object> queryParams) {
        // This method's implementation may not get called during functional JUnit test,
        // since these tests probably mock this class and provide their own implementation.

        return new RESTRequest(method, uri, body, queryParams);

    }
}
