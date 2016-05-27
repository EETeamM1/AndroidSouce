package com.transility.tim.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;


import android.content.Context;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;


import com.transility.tim.android.http.RESTRequest;
import com.transility.tim.android.http.RESTRequestFactory;
import com.transility.tim.android.http.RESTResponse;
import com.transility.tim.android.http.RESTResponseHandler;
import com.transility.tim.android.http.ResponseFetcher;
import com.transility.tim.android.http.RESTRequest.Method;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends FragmentActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private WindowManager winManager;
    private RelativeLayout wrapperView;
    Button mEmailSignInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the login form.

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams( WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        winManager = ((WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE));

        wrapperView = new RelativeLayout(this);
        wrapperView.setBackgroundColor(this.getResources().getColor(R.color.backWhite));
        View activityView= View.inflate(this, R.layout.activity_login, this.wrapperView);
        setContentView(activityView);

        this.winManager.addView(wrapperView, localLayoutParams);


        mEmailSignInButton = (Button) activityView.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mPasswordView = (EditText)activityView.findViewById(R.id.password);

        mEmailView = (EditText) activityView.findViewById(R.id.email);
        mLoginFormView = activityView.findViewById(R.id.login_form);
        mProgressView = activityView.findViewById(R.id.login_progress);

//        List<RESTResponseHandler> handlers = Arrays.asList( okhandler);
//        RESTRequestFactory.dispatch(this,Method.POST, "http://impetus8.int.kronos.com/wfc/bridge/rest/logon", "<Logon username=\"mgr\" password=\"kronites\" appversion=\"1.0\"></Logon>", null, null,handlers ,null);
    }

//  RESTResponseHandler okhandler =  new RESTResponseHandler() {
//      @Override
//      public void handleResponseInBackground(Context context, Class<? extends Context> forContextType, RESTResponse response) {
//          Log.i("ok handler", "handleResponseInBackground");
//      }
//
//      @Override
//      public void handleResponseInUI(Context context, Class<? extends Context> forContextType, RESTResponse response) {
//          Log.i("ok handler  ", "handleResponseInUI");
//      }
//
//      @Override
//      public void handleCancelledRequest(Context context, Class<? extends Context> forContextType, RESTRequest request) {
//
//      }
//
//      @Override
//      public boolean matchesExpectedStatus(RESTResponse.Status status) {
//          return status.isSuccess();
//      }
//  };
//

    MyTask myTask;
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        myTask=new MyTask(this);
        myTask.execute();
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    class MyTask extends  AsyncTask<Void,Void,Void>{

        private Activity activity;
        private ProgressDialog progressDialog;
        public MyTask(Activity activity){
            this.activity=activity;
            progressDialog = new ProgressDialog(activity.getApplicationContext());
            progressDialog.setMessage("Iski Bara Baju Chal Gaya ghata top");


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.print("Inside On pre Execute>>");


        }


        @Override
        protected Void doInBackground(Void... params) {
            System.out.print("Inside do in background");
            for (int i=0;i<1000;i++){

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            System.out.println("On Post Execute");

            finish();

        }
    }

    @Override
    public void onBackPressed() {
        System.out.println("My Activity onBackPressed");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Activity Destroyed");
        mPasswordView.setText("Badiya Kam Ho gaya");
        winManager.removeView(wrapperView);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);


    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

