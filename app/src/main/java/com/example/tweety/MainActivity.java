package com.example.tweety;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelStoreOwner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {

    //set the redirect when the user is logged in
    public void redirectUser()
    {
        if (ParseUser.getCurrentUser() != null)
        {
            Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
            startActivity(intent);
        }
    }
    //method for login and signup

    public void signupLogin(View view)
    {
        final EditText username = findViewById(R.id.usernameEditText);
        final EditText password =findViewById(R.id.passwordEditText);

        //then we try to login
        //we give it the username the password and a login callback
        ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e == null) // no exception
                {
                    Log.i("info","Logged In");
                    redirectUser();
                }
                else{
                    //we will sign the user up
                    ParseUser parseUser = new ParseUser();
                    parseUser.setUsername(username.getText().toString());
                    parseUser.setPassword(password.getText().toString());

                    //signupCallback

                    parseUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e== null)
                            {
                                Log.i("info" ,"signed up");
                                redirectUser();

                            }
                            else
                                Toast.makeText(MainActivity.this, e.getMessage().substring(e.getMessage().indexOf(' ')), Toast.LENGTH_SHORT).show();
                                //everything after e.getMessage() is there to create a message without the java exception
                        }
                    });
                }

            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //give the title
        setTitle("Twitter: Login");
        redirectUser();
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }
}
