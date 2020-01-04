package com.example.tweety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {


    ArrayList<String> users = new ArrayList<>();//a list of users whom we can follow

    ArrayAdapter arrayAdapter ;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.tweetmenu , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.tweet)
        {
            //use an alert for tweet
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Send a tweet");

            final EditText tweetContentEditText = new EditText(this);

            builder.setView(tweetContentEditText);
            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("info" , tweetContentEditText.getText().toString());

                    //we have to send this tweet via Parse
                    ParseObject tweet = new ParseObject("Tweet");
                    tweet.put("username" ,ParseUser.getCurrentUser().getUsername());
                    tweet.put("tweet" , tweetContentEditText.getText().toString());
                    tweet.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                        if (e == null)
                            Toast.makeText(Main2Activity.this, "Succesfully Tweeted", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(Main2Activity.this, "Tweet failed", Toast.LENGTH_SHORT).show();

                        }

                    });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        else if(item.getItemId() == R.id.logout)
        {
            ParseUser.logOut();
            Intent intent = new Intent(getApplicationContext() , MainActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.viewFeed)
        {
            Intent intent = new Intent(getApplicationContext() , Feed.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setTitle("User List");

        //check if the user has a list of followings
        if(ParseUser.getCurrentUser().get("Following") == null)
        {
            //if the current user has no following then we will create a list for them
            List<String> emptylist = new ArrayList<>();
            ParseUser.getCurrentUser().put("Following" , emptylist);
        }

        //set up our listview

        final ListView listView =findViewById(R.id.listview);

        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);//allows user to choose items on the list
        // AbsListView.CHOICE_MODE_MULTIPLE allows us to choose multiple items from the list

        //setup our array adapter
        arrayAdapter = new ArrayAdapter(this , android.R.layout.simple_list_item_checked ,users);

        //assign our arraylist to the array adapter
        listView.setAdapter(arrayAdapter);

        //allow us to do smething hen each individual item is selected or deselected
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get the row that was tapped
                CheckedTextView checkedTextView = (CheckedTextView) view;

                if(checkedTextView.isChecked())
                {
                    Log.i("info" , "row is checked");
                    //getting the current Following to the list of already following
                    ParseUser.getCurrentUser().add("Following" , users.get(position));
                    //Save in Background
                    ParseUser.getCurrentUser().saveInBackground();

                }
                else
                {
                    Log.i("info" , "Row is not checked");

                    //removing the current unfollowing from the list of already following

                    ParseUser.getCurrentUser().getList("Following").remove(users.get(position));
                    List newList = ParseUser.getCurrentUser().getList("Following");
                    ParseUser.getCurrentUser().remove("Following" );
                    ParseUser.getCurrentUser().put("Following" , newList);
                    //Save in Background
                    ParseUser.getCurrentUser().saveInBackground();


                }
            }
        });
        users.clear(); // emptying the array list

        //querying our database of users in parse server
        //getiing the data from the database and storing it in the arrayList
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereNotEqualTo("username" , ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if( e ==  null)
                {
                    if(objects.size() > 0)
                    {
                        for (ParseUser user : objects)
                        {
                            users.add(user.getUsername());
                        }
                        arrayAdapter.notifyDataSetChanged();

                        //making already followers tick at start
                        for(String username : users)
                        {
                            if (ParseUser.getCurrentUser().getList("Following").contains(username)){
                                //if the list of followings have the names of the users ,we will check those
                                listView.setItemChecked(users.indexOf(username) , true); // true = checked and false = unchecked

                            }
                        }
                    }
                }
                //so if I have 4 people A,B,C,D and I am logged in as A
                //then I would see B,C and D
            }
        });
    }
}
