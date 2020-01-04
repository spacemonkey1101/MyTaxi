package com.example.tweety;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class StarterActivity extends Application {

        @Override
        public void onCreate() {
            super.onCreate();

            // Enable Local Datastore.
            Parse.enableLocalDatastore(this);

            // Add your initialization code here
            Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                    .applicationId("80b62a610f51a5e03e41a9d1fad27590c9b87200")
                    .clientKey("402541a9f6e31b4bdc71a983626168fad40df71d")
                    .server("http://18.208.162.155:80/parse/")
                    .build()
            );



            //ParseUser.enableAutomaticUser();



            ParseACL defaultACL = new ParseACL();
            defaultACL.setPublicReadAccess(true);
            defaultACL.setPublicWriteAccess(true);
            ParseACL.setDefaultACL(defaultACL, true);

        }
    }


