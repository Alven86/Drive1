package enal1586.ju.drive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

//Initialize buttons.
    private Button DriverButton;
    private Button CustomerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        DriverButton = (Button) findViewById(R.id.driver_btn);
        CustomerButton = (Button) findViewById(R.id.customer_btn);
        //connect buttons to Activitys.
        DriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginRegisterDriver = new Intent(WelcomeActivity.this,DriverLoginRegisterActivity.class);
                startActivity(LoginRegisterDriver);
            }
        });
        CustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginRegisterCustomer = new Intent(WelcomeActivity.this,CustomerLoginRegisterActivity.class);
                startActivity(LoginRegisterCustomer);
            }
        });
    }
}
