package com.example.tweety;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Feed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        setTitle("Your Feed");
        final ListView listView = findViewById(R.id.feedListView);

        // our data cant be a simple array list this time
        final List<Map<String, String>> tweet = new ArrayList<Map<String, String>>();

        for (int i = 1 ; i<=5 ;i++)
        {
            Map<String , String> tweetInfo = new HashMap<String, String>();

            tweetInfo.put("content" , "Tweet Content :" + Integer.toString(i));

            tweetInfo.put("username" , "User " + Integer.toString(i));

            tweet.add(tweetInfo);

            // tweet list contain 5 item that themselves contain username and some content
        }
        //get actual data from the parse server using a parse query
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tweet"); // get query on the tweet class
        query.whereContainedIn("username" , ParseUser.getCurrentUser().getList("Following")); // get the required usernames
        query.orderByDescending("createdAt");//order the list by the automatic date
        query.setLimit(20);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null)
                {
                    if (objects.size() > 0)
                    {
                        List<Map<String, String>> tweetData = new ArrayList<Map<String, String>>();

                        for (ParseObject tweet : objects)
                        {

                                Map<String , String> tweetInfo2 = new HashMap<String, String>();

                                tweetInfo2.put("content" , tweet.getString("tweet"));

                                tweetInfo2.put("username" , tweet.getString("username"));

                                tweetData.add(tweetInfo2);

                            }


                        //Attach tweet list to our listview
                        SimpleAdapter simpleAdapter = new SimpleAdapter(Feed.this , tweetData, android.R.layout.simple_list_item_2 , new String[] { "content" ,"username"} ,  new int[] {android.R.id.text1 , android.R.id.text2});
                        //the string array in the parameter is going to contain the objects from where we can access the data
                        // and then we tell then whenre we want the information to go....that is the 1st item should go to position 1 ....etc

                        //set the listview to the simpleadapter
                        listView.setAdapter(simpleAdapter);
                        }
                    }
                }

        });

    }
}
