package com.transility.tim.android.http;

import android.util.Log;

import com.transility.tim.android.Constants;
import com.transility.tim.android.Utilities.Utility;
import com.transility.tim.android.http.RESTResponse.Status;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.Map;

/**
 * This class send a HttpURLConnection and handle response of it.
 * @author Himanshu Bapna
 */
public class RESTRequest {

    static final int CONNECTION_TIMEOUT = 10000;

    public enum Method{
        POST,
        GET
    }

    public enum MediaType{
        APPLICATION_JSON("application/json");

        private final String type;
        MediaType(String type) {
            this.type = type;
        }

        public String toString(){
            return type;
        }
    }

    public Method method;
    public String uri;
    public Object body;
    public Map<String,Object> queryParams;

    /**
     * Creates a new request for a HTTP call.
     * @param method The request method. Required.
     * @param uri The (relative) request URI. Required.
     * @param body If the method is POST or PUT, then this can be the contents of the request. Can be null.
     * @param queryParams A map defining the query-parameters of the request. Can be null.
     */
    protected RESTRequest(Method method, String uri, Object body,
                          Map<String, Object> queryParams) {
        this.method = method;
        this.uri    = uri;
        this.body   = body;
        this.queryParams  = queryParams;
    }

    /**
     * This method does the actual request. Note that this method will be called on a background thread.
     */
    protected RESTResponse dispatch() {
        if (uri == null){
            return null;
        }

        RESTResponse response = null;

        if (uri.endsWith(RESTResponseHandler.NULL_URI)) {
            response = new RESTResponse(RESTResponse.Status.CONNECTION_ERROR_OTHER, null, this);
        }
        else  {

            final String encodedURI = fillOutParameters(uri, queryParams);

            HttpURLConnection connection;

            InputStream inputStream = null;
            try {
                Log.i(Constants.LOGTAG, "Request(" + method + ") made. Uri=" + encodedURI);

                URL url = new URL(encodedURI);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setReadTimeout(CONNECTION_TIMEOUT);
                connection.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON.toString());

                if (Method.GET.equals(method)) {
                    connection.setRequestMethod("GET");
                }
                else if (Method.POST.equals(method)) {
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setUseCaches(false);

                    if (body != null && body instanceof String) {
                        //Send request
                        PrintWriter out = new PrintWriter(connection.getOutputStream());
                        out.print(body);
                        out.close();
                    }
                }
                else {
                    connection.setRequestMethod("GET");
                }

                Status status = Status.CONNECTOR_ERROR_INTERNAL;
                if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                    status = Status.SUCCESS_OK;
                }else if(connection.getResponseCode()==HttpURLConnection.HTTP_UNAUTHORIZED){
                    status = Status.CLIENT_ERROR_UNAUTHORIZED;
                }
                else if (connection.getResponseCode()==HttpURLConnection.HTTP_BAD_REQUEST){
                    status = Status.CLIENT_ERROR;
                }

                Utility.logError("Rest Request status>>>",connection.getResponseCode()+"");
                inputStream =  connection.getResponseCode()==HttpURLConnection.HTTP_OK ? connection.getInputStream(): connection.getErrorStream();
                response = new RESTResponse(status, inputStream, this);
            }
            catch (IOException re) {
                response = new RESTResponse(Status.CONNECTOR_ERROR_INTERNAL, null, this);
                String text = response.getText();
                if (text != null && text.length() > 0){
                    Log.w(Constants.LOGTAG, "ResourceException occurred with response:\n"+text);
                }

            }
            catch (Throwable e) {
                Log.e(Constants.LOGTAG, "Error occurred.", e);
                response = new RESTResponse(Status.CONNECTOR_ERROR_INTERNAL, inputStream, this);
            }
            finally {
                body = null;
            }
        }
        return response;
    }


    public String fillOutParameters(String uri, Map<String, Object> queryParams) {
        String newUri = uri;

        newUri = newUri+queryParams(queryParams);

        newUri = newUri.replaceFirst("\\?&", "?");
        return newUri;
    }

    public String queryParams(Map<String,Object> queryParamsMap) {
        StringBuffer strBufQP = new StringBuffer();
        if (queryParamsMap != null && queryParamsMap.size() > 0) {
            for (String queryParamName : queryParamsMap.keySet()) {
                Object queryParamValue = queryParamsMap.get(queryParamName);
                appendQueryParams(queryParamName, queryParamValue, strBufQP);
            }
        }
        return strBufQP.toString();
    }

    private void appendQueryParams(String paramName, Object paramValue, StringBuffer strBuf) {
        if (paramValue == null) {
            strBuf.append('&').append(urlEncode(paramName)).append('=');
            return;
        }

        strBuf.append('&').append(urlEncode(paramName)).append('=').append(urlEncode(paramValue.toString()));
    }

    public String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        }
        catch (Exception e) {
            return string;
        }
    }

}
