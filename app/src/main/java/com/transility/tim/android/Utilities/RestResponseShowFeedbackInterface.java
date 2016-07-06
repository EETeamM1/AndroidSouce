package com.transility.tim.android.Utilities;

import com.transility.tim.android.http.RESTResponse;

/**
 * Interface whose concrete implementation will show feedback to user based on the response returned by User.
 * Created by ambesh.kukreja on 6/8/2016.
 */
public interface RestResponseShowFeedbackInterface {

    /***
     * Perform action when the background operation was successfully completed..
     * @param reposeJson
     */
    void onSuccessOfBackGroundOperation(RESTResponse reposeJson);

    /***
     * Perfrom action when the background operation was not successful.
     * @param reposeJson
     */
    void onErrorInBackgroundOperation(RESTResponse reposeJson);

    /**
     * Perfrom action when the foreground operation was successful.
     * @param restResponse
     */
    void onSuccessInForeGroundOperation(RESTResponse restResponse);

    /**
     * Perfrom Action when the foreground operation was not successful
     * @param restResponse
     */
    void onErrorInForeGroundOperation(RESTResponse restResponse);
}
