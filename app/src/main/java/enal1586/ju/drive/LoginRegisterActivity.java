package enal1586.ju.drive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class LoginRegisterActivity extends AppCompatActivity {
    //Initialize variable.
    private Button LogInGoogle;
    private Button LoginButton;
    private Button RegisterButton;
    private TextView RegisterLink;
    private TextView Status;
    private EditText Email;
    private EditText Password;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private SignInButton signin;
    GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 9191;
    private FirebaseAuth.AuthStateListener listener;
    private List<AuthUI.IdpConfig> providers;
    FirebaseDatabase database;
    DatabaseReference driverInfoRef;
    //  GoogleApiClient mGoogleApiClient;
    // variables

    Switch aSwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
      //  signin = findViewById(R.id.sig_in_button);
        setContentView(R.layout.activity_driver_login_register);

        //DB connect.

        mAuth = FirebaseAuth.getInstance();


        //connect the variable.
        LoginButton = (Button) findViewById(R.id.driver_login_btn);
        RegisterButton = (Button) findViewById(R.id.driver_register_btn);
        RegisterLink = (TextView) findViewById(R.id.driver_register_link);
        Status = (TextView) findViewById(R.id.driver_status);
        Email = (EditText) findViewById(R.id.email_driver);
        Password = (EditText) findViewById(R.id.password_driver);
        loadingBar = new ProgressDialog(this);
        LogInGoogle = (Button) findViewById(R.id.btn_google_sign_in);
        //registration button visible/invisible.
        RegisterButton.setVisibility(View.INVISIBLE);
        RegisterButton.setEnabled(false);

        //register link visible/invisible.
        RegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setVisibility(View.INVISIBLE);
                RegisterLink.setVisibility(View.INVISIBLE);
                LogInGoogle.setVisibility(View.INVISIBLE);
                Status.setText("Register");
                RegisterButton.setVisibility(View.VISIBLE);
                RegisterButton.setEnabled(true);
            }
        });
        //registration button actions.
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString();
                String password = Password.getText().toString();
                RegisterDriver(email, password);
            }

        });
        //login button actions.
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Email.getText().toString();
                String password = Password.getText().toString();
                SignInDriver(email, password);
            }
        });


        aSwitch = (Switch) findViewById(R.id.switch1);   //Using Swich btn to chose Driver or Rider Navi Drawer

        createRequest();


        findViewById(R.id.btn_google_sign_in).setOnClickListener(view -> signIn());

    }









    //check mai/pass if is empty then log in to DB.
    private void SignInDriver(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginRegisterActivity.this, "Please write your Email...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginRegisterActivity.this, "Please write your Password...", Toast.LENGTH_SHORT).show();
        } else {
            //loading message show when Login user to DB.
            loadingBar.setTitle("Please wait :");
            loadingBar.setMessage("While system is performing processing on your data...");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginRegisterActivity.this, "Driver Login Successfully... ", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        loadingBar.dismiss();


                        // Start next activity
                        if (aSwitch.isChecked() == true) {
                            Toast.makeText(getBaseContext(), "DRIVER", Toast.LENGTH_SHORT).show();
                            Intent registerIntent = new Intent(LoginRegisterActivity.this, DriversMapActivity.class);
                            startActivity(registerIntent);
                        } else {
                            Toast.makeText(getBaseContext(), "RIDER", Toast.LENGTH_SHORT).show();
                            Intent registerIntent = new Intent(LoginRegisterActivity.this, CustomersMapActivity.class);
                            startActivity(registerIntent);
                        }

                        //log in to map activity.
                       // Intent intent = new Intent(DriverLoginRegisterActivity.this, DriversMapActivity.class);
                        //startActivity(intent);
                    } else {
                        Toast.makeText(LoginRegisterActivity.this, "Please Try Again. Error Occurred, while Login.. ", Toast.LENGTH_SHORT).show();

                        loadingBar.dismiss();

                    }
                }
            });
        }
    }


    private void RegisterDriver(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginRegisterActivity.this, "Please write your Email...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginRegisterActivity.this, "Please write your Password...", Toast.LENGTH_SHORT).show();
        } else {
            //loading message show when register new user to DB.
            loadingBar.setTitle("Please wait :");
            loadingBar.setMessage("While system is performing processing on your data...");
            loadingBar.show();

            //Register new user to DB.
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginRegisterActivity.this, "Customer Register Successfully... ", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        loadingBar.dismiss();

                        // Start next activity
                        if (aSwitch.isChecked() == true) {
                            Toast.makeText(getBaseContext(), "DRIVER", Toast.LENGTH_SHORT).show();
                            Intent registerIntent = new Intent(LoginRegisterActivity.this, DriversMapActivity.class);
                            startActivity(registerIntent);
                        } else {
                            Toast.makeText(getBaseContext(), "RIDER", Toast.LENGTH_SHORT).show();
                            Intent registerIntent = new Intent(LoginRegisterActivity.this, CustomersMapActivity.class);
                            startActivity(registerIntent);
                        }


                        //start map activity.
                        //Intent intent = new Intent(DriverLoginRegisterActivity.this, DriversMapActivity.class);
                       // startActivity(intent);
                    } else {
                        Toast.makeText(LoginRegisterActivity.this, "Please Try Again. Error Occurred, while registering... ", Toast.LENGTH_SHORT).show();

                        loadingBar.dismiss();
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
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
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
                            loadingBar.dismiss();
                            // Start next activity
                            if (aSwitch.isChecked() == true) {
                                Toast.makeText(getBaseContext(), "DRIVER", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent registerIntent = new Intent(LoginRegisterActivity.this, DriversMapActivity.class);
                                startActivity(registerIntent);

                            } else {
                                Toast.makeText(getBaseContext(), "RIDER", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent registerIntent = new Intent(LoginRegisterActivity.this, CustomersMapActivity.class);
                                startActivity(registerIntent);
                            }
                            // Intent intent = new Intent(getApplicationContext(),DriversMapActivity.class);
                            //startActivity(intent);


                        } else {
                            Toast.makeText(LoginRegisterActivity.this, "Sorry auth failed.", Toast.LENGTH_SHORT).show();


                        }


                        // ...

                    }
                });
    }



}





