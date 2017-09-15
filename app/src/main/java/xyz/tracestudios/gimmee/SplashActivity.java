package xyz.tracestudios.gimmee;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private boolean createdFirebaseAccount;
    private boolean isNewUser;

    private CallbackManager callbackManager;
    private LinearLayout loginBaseForm;
    private LinearLayout loginRegistrationForm;
    private LinearLayout loginEmailForm;
    private LinearLayout loginEmailForgottenForm;

    private TextInputLayout loginRegistrationEmailWrapper;
    private TextInputLayout loginRegistrationPasswordWrapper;
    private TextInputLayout loginEmailEmailWrapper;
    private TextInputLayout loginEmailPasswordWrapper;
    private TextInputLayout loginEmailForgottenEmailWrapper;
    private Button emailLogin;
    private Button fbLogin;

    private TextView createAcc;

    Button registerBtn;

    EditText registrationPassword;
    EditText registrationEmail;
    EditText loginPassword;
    EditText loginEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_splash);

        callbackManager = CallbackManager.Factory.create();

        isNewUser = false;

        createdFirebaseAccount = false;

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null && !isNewUser) {
                    // User is signed in
                    Log.d("LOGGEDIN", "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(SplashActivity.this, "Welcome to Gimmee!",
                            Toast.LENGTH_SHORT).show();

                    Intent main = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(main);
                    finish();

                } else if (user != null && isNewUser) {
                    // User is signed in
                    Log.d("LOGGEDIN", "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(SplashActivity.this, "Welcome to Gimmee!",
                            Toast.LENGTH_SHORT).show();

                    Intent setup = new Intent(SplashActivity.this, ProfileSetupActivity.class);
                        setup.putExtra("fromRegister", true);
                    startActivity(setup);
                    finish();

                } else{
                    // User is signed out
                    Log.d("LOGGEDOUT", "onAuthStateChanged:signed_out");
                    Toast.makeText(SplashActivity.this, "Hey! Seems you're not signed in.",
                            Toast.LENGTH_SHORT).show();
                }
                // ...
            }
        };

        loginBaseForm = (LinearLayout) findViewById(R.id.login_base_form);
        loginRegistrationForm = (LinearLayout) findViewById(R.id.login_registration_form);
        loginEmailForm = (LinearLayout) findViewById(R.id.login_email_form);
        loginEmailForgottenForm = (LinearLayout) findViewById(R.id.login_email_forgotten_form);

        prepareLoginFormNavigation();
        prepareInputBoxes();
        prepareActionButtons();
    }

    private void setVisibilityOfRegistrationForm(boolean setVisible) {
        if (setVisible) {
            loginBaseForm.setVisibility(View.INVISIBLE);
            loginRegistrationForm.setVisibility(View.VISIBLE);
        } else {
            loginBaseForm.setVisibility(View.VISIBLE);
            loginRegistrationForm.setVisibility(View.INVISIBLE);
            hideSoftKeyboard();
        }
    }

    private void setVisibilityOfEmailForm(boolean setVisible) {
        if (setVisible) {
            loginBaseForm.setVisibility(View.INVISIBLE);
            loginEmailForm.setVisibility(View.VISIBLE);
        } else {
            loginBaseForm.setVisibility(View.VISIBLE);
            loginEmailForm.setVisibility(View.INVISIBLE);
            hideSoftKeyboard();
        }
    }

    private void setVisibilityOfEmailForgottenForm(boolean setVisible) {
        if (setVisible) {
            loginEmailForm.setVisibility(View.INVISIBLE);
            loginEmailForgottenForm.setVisibility(View.VISIBLE);
        } else {
            loginEmailForm.setVisibility(View.VISIBLE);
            loginEmailForgottenForm.setVisibility(View.INVISIBLE);
        }
        hideSoftKeyboard();
    }

    private void prepareInputBoxes() {
        // Registration form
        loginRegistrationEmailWrapper = (TextInputLayout) findViewById(R.id.login_registration_email_wrapper);
            registrationEmail = loginRegistrationEmailWrapper.getEditText();
        loginRegistrationPasswordWrapper = (TextInputLayout) findViewById(R.id.login_registration_password_wrapper);
            registrationPassword = loginRegistrationPasswordWrapper.getEditText();

        // Login email form
        loginEmailEmailWrapper = (TextInputLayout) findViewById(R.id.login_email_email_wrapper);
            loginEmail = loginEmailEmailWrapper.getEditText();

        loginEmailPasswordWrapper = (TextInputLayout) findViewById(R.id.login_email_password_wrapper);
            loginPassword = loginEmailPasswordWrapper.getEditText();

    }

    private void prepareLoginFormNavigation() {
        // Login email
        Button loginFormEmailButton = (Button) findViewById(R.id.login_form_email_btn);
        loginFormEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibilityOfEmailForm(true);
            }
        });
        ImageButton closeEmailBtn = (ImageButton) findViewById(R.id.login_email_close_button);
        closeEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Slow to display ripple effect
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setVisibilityOfEmailForm(false);
                    }
                }, 200);
            }
        });

        // Registration
        TextView loginFormRegistrationButton = (TextView) findViewById(R.id.login_form_registration_btn);
        loginFormRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibilityOfRegistrationForm(true);
            }
        });
        ImageButton closeRegistrationBtn = (ImageButton) findViewById(R.id.login_registration_close_button);
        closeRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Slow to display ripple effect
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setVisibilityOfRegistrationForm(false);
                    }
                }, 200);
            }
        });

        // Email forgotten password
        TextView loginEmailFormForgottenButton = (TextView) findViewById(R.id.login_email_forgotten_password);
        loginEmailFormForgottenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibilityOfEmailForgottenForm(true);
            }
        });
        ImageButton closeEmailForgottenFormBtn = (ImageButton) findViewById(R.id.login_email_forgotten_back_button);
        closeEmailForgottenFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Slow to display ripple effect
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setVisibilityOfEmailForgottenForm(false);
                    }
                }, 200);
            }
        });
    }

    private void prepareActionButtons() {
        TextView loginBaseHelp = (TextView) findViewById(R.id.login_form_skip);
        loginBaseHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              TODO: implement help screen
            }
        });

        // FB login
        fbLogin = (Button) findViewById(R.id.login_form_facebook);
        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeFacebookLogin();
            }
        });

        emailLogin = (Button) findViewById(R.id.login_email_confirm);
        emailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithEmail();
            }
        });

        registerBtn = (Button) findViewById(R.id.login_registration_confirm);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });

        Button resetPassword = (Button) findViewById(R.id.login_email_forgotten_confirm);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeResetPassword();
            }
        });
    }

    private void invokeFacebookLogin() {

    }

    private void registerNewUser() {

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailTxt = registrationEmail.getText().toString();
                String passwordTxt = registrationPassword.getText().toString();

                System.out.println("e:" + emailTxt + " - pass: " + passwordTxt);

                createFirebaseAccountEmail(emailTxt, passwordTxt);
            }
        });
    }

    private void loginWithEmail() {
        String emailTxt = loginEmail.getText().toString();
        String passwordTxt = loginPassword.getText().toString();

        System.out.println("LOGIN // e:" + emailTxt + " - pass: " + passwordTxt);

            loginFirebaseAccountEmail(emailTxt, passwordTxt);
    }

    private void invokeResetPassword() {
        /*EditText emailForgottenPasswordEmail = loginEmailForgottenEmailWrapper.getEditText();
        if (emailForgottenPasswordEmail == null || emailForgottenPasswordEmail.getText().toString().equalsIgnoreCase("")) {
            loginEmailForgottenEmailWrapper.setErrorEnabled(true);
            loginEmailForgottenEmailWrapper.setError("This field is required");
        } else {
            loginEmailForgottenEmailWrapper.setErrorEnabled(false);
            resetPassword(emailForgottenPasswordEmail);
        }*/
    }

    private void resetPassword(EditText emailOfForgottenPassword) {
       //TODO: implement forgot password stuff
    }

    private void loginFirebaseAccountEmail(final String mEmail, final String mPassword){

        final ProgressDialog loginProgDialog = new ProgressDialog(SplashActivity.this,
                R.style.MaterialTheme);
        loginProgDialog.setIndeterminate(true);
        loginProgDialog.setMessage("Logging In...");
        loginProgDialog.show();

        emailLogin.setEnabled(false);

        firebaseAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("whatevvs2", "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                            if (mEmail.isEmpty())
                                registrationEmail.setError("Please input your email");
                            else if (mPassword.isEmpty())
                                loginEmailPasswordWrapper.setError("Please input your password");

                            loginProgDialog.dismiss();
                            emailLogin.setEnabled(true);

                            Log.w("whatevvs", "signInWithEmail", task.getException());
                            Toast.makeText(SplashActivity.this, "Please check your email and password and try again",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void createFirebaseAccountEmail(final String mEmail, final String mPassword){

        final ProgressDialog registerProgDialog = new ProgressDialog(SplashActivity.this,
                R.style.MaterialTheme);
        registerProgDialog.setIndeterminate(true);
        registerProgDialog.setMessage("Creating User...");
        registerProgDialog.show();

        registerBtn.setEnabled(false);

        isNewUser = true;

        firebaseAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("someTAGGGG", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            if (!validateEmail(mEmail))
                                registrationEmail.setError("Email is invalid.");
                            else if (!validatePassword(mPassword))
                                registrationPassword.setError("Password must be greater than 8 characters.");

                            registerProgDialog.dismiss();
                            registerBtn.setEnabled(true);

                            Toast.makeText(SplashActivity.this, "Authentication failed. Please check everything and try again.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                        createdFirebaseAccount = true;
                    }
                });
    }

    private void hideSoftKeyboard() {
        if (this.getCurrentFocus()!= null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }

    private boolean validateEmail(String email){
        if (email.contains("@") && email.contains("."))
            return true;
        else
            return false;
    }

    private boolean validatePassword(String password){
        return password.length() > 8;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
