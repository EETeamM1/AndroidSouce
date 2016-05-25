package com.transility.tim.android.http;

import android.content.Context;
import android.util.Log;

import com.transility.tim.android.Constants;
import com.transility.tim.android.http.RESTResponse.Status;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author Himanshu Bapna
 */
public class RESTRequest {

    static final int CONNECTION_TIMEOUT = 20000;

    public enum Method{
        POST,
        GET;
    }

    public enum MediaType{
        APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
        APPLICATION_JSON("application/json"),
        APPLICATION_OCTET_STREAM("application/octet-stream"),
        APPLICATION_XML("application/xml"),
        MULTIPART_FORM_DATA("multipart/form-data"),
        TEXT_PLAIN("text/plain"),
        TEXT_XML("text/xml");

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
    public List<String> matrixParams;
    public Map<String,Object> queryParams;
    boolean checkForCookies;

    /**
     * Creates a new request for a HTTP call.
     * @param method The request method. Required.
     * @param uri The (relative) request URI. Required.
     * @param body If the method is POST or PUT, then this can be the contents of the request. Can be null.
     * @param matrixParams Matrix parameters part of the URI (as semicolon separated values). Can be null.
     * @param queryParams A map defining the query-parameters of the request. Can be null.
     */
    protected RESTRequest(Method method, String uri, Object body,
                          List<String> matrixParams,
                          Map<String,Object> queryParams) {
        this.method = method;
        this.uri    = uri;
        this.body   = body;
        this.matrixParams = matrixParams;
        this.queryParams  = queryParams;
    }

    /**
     * This method does the actual request. Note that this method will be called on a background thread.
     * You could override this method for JUnit tests to avoid client-to-server communication.
     */
    protected RESTResponse dispatch(Context context) {
        if (uri == null){
            return null;
        }

        RESTResponse response = null;

        if (uri.endsWith(RESTResponseHandler.NULL_URI)) {
            response = new RESTResponse(RESTResponse.Status.CONNECTION_ERROR_OTHER, null, this);
        }
        else  {

            final String encodedURI = fillOutParameters(uri, matrixParams, queryParams);



            HttpsURLConnection connection = null;

            InputStream inputStream = null;
            try {
                Log.i(Constants.LOGTAG, "Request(" + method + ") made. Uri=" + encodedURI);

                URL url = new URL(encodedURI);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setReadTimeout(CONNECTION_TIMEOUT);


                if (Method.GET.equals(method)) {
                    connection.setRequestMethod("GET");
                }
                else if (Method.POST.equals(method)) {
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setUseCaches(false);

                    if (body != null && body instanceof String) {
                        DataOutputStream doStream = new DataOutputStream(connection.getOutputStream());
                        doStream.writeUTF(body.toString());
                        doStream.flush ();
                        doStream.close();
                    }
                }
                else {
                    connection.setRequestMethod("GET");
                }

                connection.connect();

                response = new RESTResponse(
                        connection.getResponseCode()==HttpURLConnection.HTTP_OK?Status.SUCCESS_OK:Status.CONNECTOR_ERROR_INTERNAL,
                        inputStream,
                        this);

//		        HTTPRequestFactory.getProtocolHelper(context).storeResponseCookies(resource);
            }
            catch (IOException re) {
                response = new RESTResponse(Status.CONNECTOR_ERROR_INTERNAL, inputStream, this);

                String text = response.getText();

//                Log.w(Constants.LOGTAG, "ResourceException occurred with HTTP-status="+ (inputStream!=null?connection.getResponseCode().  :"Unknown"));
                if (text != null && text.length() > 0){
                    Log.w(Constants.LOGTAG, "ResourceException occurred with response:\n"+text);
                }

            }
//            catch (InterruptedException ie) {
//                Log.i(Constants.LOGTAG, "HTTPRequest was interrupted.");
//                response = new RESTResponse(RESTResponseHandler.STATUS_USER_CANCELLED_AUTH, null /*"User cancelled request"*/, this);
//            }
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


    public static  String fillOutParameters(String uri, List<String> matrixParams, Map<String,Object> queryParams) {
        String newUri = uri;

        newUri = newUri.replaceFirst(Constants.REGEX_MATRIX_PARAMS, matrixParams(matrixParams));

        newUri = newUri.replaceAll(Constants.REGEG_QUERY_PARAMS, queryParams(queryParams));

        newUri = newUri.replaceFirst("\\?&", "?");
        return newUri;
    }

    public static String matrixParams(List<String> matrixParams) {
        StringBuffer strBufMP = new StringBuffer();
        if (matrixParams != null && matrixParams.size() > 0) {
            for (String matrixParam : matrixParams) {
                if (strBufMP.length() == 0) {
                    strBufMP.append(urlEncode(matrixParam));
                }
                else {
                    strBufMP.append(';').append(urlEncode(matrixParam));
                }
            }
        }
        return strBufMP.toString();
    }

    public static String queryParams(Map<String,Object> queryParamsMap) {
        StringBuffer strBufQP = new StringBuffer();
        if (queryParamsMap != null && queryParamsMap.size() > 0) {
            for (String queryParamName : queryParamsMap.keySet()) {
                Object queryParamValue = queryParamsMap.get(queryParamName);
                appendQueryParams(queryParamName, queryParamValue, strBufQP);
            }
        }
        return strBufQP.toString();
    }

    private static void appendQueryParams(String paramName, Object paramValue, StringBuffer strBuf) {
        if (paramValue == null) {
            strBuf.append('&').append(urlEncode(paramName)).append('=');
            return;
        }

        if (paramValue.getClass().isArray()) {
            Object[] array = (Object[])paramValue;
            if (array.length == 0) {
                appendQueryParams(paramName, null, strBuf);
                return;
            }
            for (Object value : array) {
                appendQueryParams(paramName, value, strBuf);
            }
            return;
        }

        if (paramValue instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>)paramValue;
            if (collection.isEmpty()) {
                appendQueryParams(paramName, null, strBuf);
                return;
            }
            for (Object value : collection) {
                appendQueryParams(paramName, value, strBuf);
            }
            return;
        }

        strBuf.append('&').append(urlEncode(paramName)).append('=').append(urlEncode(paramValue.toString()));
    }

    public static String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        }
        catch (Exception e) {
            return string;
        }
    }

}
