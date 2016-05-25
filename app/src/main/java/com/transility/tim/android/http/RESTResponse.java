package com.transility.tim.android.http;

import android.os.Bundle;
import android.util.Log;

import com.transility.tim.android.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * @author  Himanshu Bapna
 */
public class RESTResponse {
    public enum Status{

        SUCCESS_OK(HttpURLConnection.HTTP_OK),
        CONNECTOR_ERROR_INTERNAL(HttpURLConnection.HTTP_INTERNAL_ERROR),

        CONNECTION_ERROR_OTHER(5000),

        CAN_NOT_FOUND_HOST(1003),

        USER_CANCELLED_AUTH(1012),

        CLIENT_ERROR_UNAUTHORIZED(HttpURLConnection.HTTP_UNAUTHORIZED);

        private final int sc;
        Status(int scode){
            sc = scode;
        }
        public boolean isSuccess() {
            boolean result = false;
            if(sc>=200 && sc <=207){
                result = true;
            }
            return result;
        }
        public String getCode() {
            return ""+sc;
        }
        public boolean isError() {
            return isClientError() || isServerError()
                    || isConnectorError();
        }
        public boolean isConnectorError() {
            boolean result = false;
            if(sc>=1000 && sc <=1002){
                result = true;
            }
            return result;
        }
        /**
         * Indicates if the status is a server error status.
         *
         * @param code
         *            The code of the status.
         * @return True if the status is a server error status.
         */
        public boolean isServerError() {
            boolean result = false;
            if(sc>=500 && sc <=507){
                result = true;
            }
            return result;
        }

        public boolean isClientError() {
            boolean result = false;
            if(sc>=400 && sc <=424){
                result = true;
            }
            return result;
        }
    }

    public Status status;
    public InputStream value;
    public final RESTRequest originatingRequest;

    public Bundle userData;
    public RESTRequest.MediaType contentType;
    byte[] header = new byte[32];

    public RESTResponse(Status status, InputStream value, RESTRequest originatingRequest) {
        this.status = status;
        this.value = value;
        this.originatingRequest = originatingRequest;
        contentType = RESTRequest.MediaType.APPLICATION_JSON;//TODO hard code
    }

    public boolean isEmpty() {
        return status == null;
    }

    public void release() {
        if (value != null) {
            try {
                value = null;
            }
            catch (Exception e) {
                Log.e(Constants.LOGTAG, "Problem releasing response-representation.", e);
            }
        }
    }


    public String getText() {
        StringBuilder sb = new StringBuilder();
        try  {
            BufferedReader reader = new BufferedReader(new InputStreamReader( value,"utf-8"));

            String nextLine = "";
            while ((nextLine = reader.readLine()) != null) {
                sb.append(nextLine);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public Bundle getUserData() {
        return userData;
    }
}
