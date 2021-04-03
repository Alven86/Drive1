package enal1586.ju.drive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginRegisterActivity extends AppCompatActivity {
    //Initialize variable.
    private Button mLogInGoogle,mLoginButton,mRegisterButton,mBack;
    private TextView mRegisterLink,mStatus;
    private EditText mEmail,mPassword;
    private ProgressDialog mLoadingBar;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private final static int mRC_SIGN_IN = 9191;



    // variables

    Switch aSwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);



        //DB connect.

        mAuth = FirebaseAuth.getInstance();

        //connect the variable.
        mBack = (Button) findViewById(R.id.back);
        mLoginButton = (Button) findViewById(R.id.driver_login_btn);
        mRegisterButton = (Button) findViewById(R.id.driver_register_btn);
        mRegisterLink = (TextView) findViewById(R.id.driver_register_link);
        mStatus = (TextView) findViewById(R.id.driver_status);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mLoadingBar = new ProgressDialog(this);
        mLogInGoogle = (Button) findViewById(R.id.btn_google_sign_in);

        //registration button visible/invisible.
        mBack.setVisibility(View.INVISIBLE);
        mBack.setEnabled(false);
        mRegisterButton.setVisibility(View.INVISIBLE);
        mRegisterButton.setEnabled(false);

        //register link visible/invisible.
        mRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginButton.setVisibility(View.INVISIBLE);
                mRegisterLink.setVisibility(View.INVISIBLE);
                mLogInGoogle.setVisibility(View.INVISIBLE);
                mStatus.setText(R.string.register);
                mRegisterButton.setVisibility(View.VISIBLE);
                mRegisterButton.setEnabled(true);
                mBack.setVisibility(View.VISIBLE);
                mBack.setEnabled(true);
            }
        });
        //registration button actions.
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                RegisterDriver(email, password);
            }

        });

        mBack.setOnClickListener(v -> {

            recreate();
        });

        //login button actions.
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                SignInDriver(email, password);
            }
        });


        //Using Swich btn to chose Driver or Rider Navi Drawer
        aSwitch = (Switch) findViewById(R.id.switch1);

        createRequest();


        findViewById(R.id.btn_google_sign_in).setOnClickListener(view -> signIn());

    }



    //check mai/pass if is empty then log in to DB.
    private void SignInDriver(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginRegisterActivity.this, R.string.Please_write_your_Email , Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginRegisterActivity.this, R.string.Please_write_your_Password, Toast.LENGTH_SHORT).show();
        } else {
            //loading message show when Login user to DB.
            mLoadingBar.setTitle(getString(R.string.Please_wait));
            mLoadingBar.setMessage(getString(R.string.while_system_is_performing_processing_on_your_data));
            mLoadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginRegisterActivity.this, R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    mLoadingBar.dismiss();


                    // Start next activity
                    if (aSwitch.isChecked() == true) {
                        Toast.makeText(getBaseContext(), R.string.Driver, Toast.LENGTH_SHORT).show();
                        Intent registerIntent = new Intent(LoginRegisterActivity.this, DriversMapActivity.class);
                        startActivity(registerIntent);
                    } else {
                        Toast.makeText(getBaseContext(), R.string.Customer, Toast.LENGTH_SHORT).show();
                        Intent registerIntent = new Intent(LoginRegisterActivity.this, CustomersMapActivity.class);
                        startActivity(registerIntent);
                    }


                } else {
                    Toast.makeText(LoginRegisterActivity.this, R.string.Login_Error_Try_Again, Toast.LENGTH_SHORT).show();

                    mLoadingBar.dismiss();

                }
            });
        }
    }


    private void RegisterDriver(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginRegisterActivity.this, R.string.Please_write_your_Email, Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginRegisterActivity.this, R.string.Please_write_your_Password, Toast.LENGTH_SHORT).show();
        } else {
            //loading message show when register new user to DB.
            mLoadingBar.setTitle(getString(R.string.Please_wait));
            mLoadingBar.setMessage(getString(R.string.while_system_is_performing_processing_on_your_data));
            mLoadingBar.show();

            //Register new user to DB.
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginRegisterActivity.this, R.string.Customer_Register_Successfully, Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        mLoadingBar.dismiss();

                        // Start next activity
                        if (aSwitch.isChecked() == true) {
                            Toast.makeText(getBaseContext(), R.string.Driver, Toast.LENGTH_SHORT).show();
                            Intent registerIntent = new Intent(LoginRegisterActivity.this, DriversMapActivity.class);
                            startActivity(registerIntent);
                        } else {
                            Toast.makeText(getBaseContext(), R.string.Customer, Toast.LENGTH_SHORT).show();
                            Intent registerIntent = new Intent(LoginRegisterActivity.this, CustomersMapActivity.class);
                            startActivity(registerIntent);
                        }


                    } else {
                        Toast.makeText(LoginRegisterActivity.this, R.string.Registering_Error_Try_Again, Toast.LENGTH_SHORT).show();

                        mLoadingBar.dismiss();
                    }
                }
            });
        }
    }


    //google login

    private void createRequest() {


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, mRC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == mRC_SIGN_IN) {
            Task task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = (GoogleSignInAccount) task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            mLoadingBar.dismiss();
                            // Start next activity
                            if (aSwitch.isChecked() == true) {
                                Toast.makeText(getBaseContext(), R.string.Driver, Toast.LENGTH_SHORT).show();
                                mLoadingBar.dismiss();
                                Intent registerIntent = new Intent(LoginRegisterActivity.this, DriversMapActivity.class);
                                startActivity(registerIntent);

                            } else {
                                Toast.makeText(getBaseContext(), R.string.Customer, Toast.LENGTH_SHORT).show();
                                mLoadingBar.dismiss();
                                Intent registerIntent = new Intent(LoginRegisterActivity.this, CustomersMapActivity.class);
                                startActivity(registerIntent);
                            }



                        } else {
                            Toast.makeText(LoginRegisterActivity.this, R.string.Sorry_auth_failed, Toast.LENGTH_SHORT).show();


                        }


                    }
                });
    }



}





