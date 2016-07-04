package com.transility.tim.android.Utilities;

import com.transility.tim.android.http.RESTResponse;

/**
 * Interface whose concrete implementation will show feedback to user based on the response returned by User.
 * Created by ambesh.kukreja on 6/8/2016.
 */
public interface RestResponseShowFeedbackInterface {

    /***
     * Show proper feedback when the API response Returns success.
     * @param reposeJson
     */
    void onSucces(RESTResponse reposeJson);

    /***
     * Show proper feedback when the API response Returns error.
     * @param reposeJson
     */
    void onError(RESTResponse reposeJson);
}
