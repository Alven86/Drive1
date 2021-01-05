package enal1586.ju.drive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    Thread thread = new Thread()
    {
        @Override
        public void run()
        {
            //start the main screen for som secondes then continue to welcome screen.
            try
            {
                sleep(8000);
            }

            catch(Exception e)
            {
                e.printStackTrace();
            }

            finally
            {
                Intent mainIntent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                startActivity(mainIntent);
            }
        }
    };
        thread.start();

    }

    @Override
    protected void onPause()
    {
        super.onPause();

        finish();
    }
}
