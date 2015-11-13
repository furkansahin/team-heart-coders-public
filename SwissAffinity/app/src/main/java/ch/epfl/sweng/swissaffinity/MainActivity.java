package ch.epfl.sweng.swissaffinity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;

import ch.epfl.sweng.swissaffinity.db.userDBAdapter;
import ch.epfl.sweng.swissaffinity.events.Event;
import ch.epfl.sweng.swissaffinity.gui.EventExpandableListAdapter;
import ch.epfl.sweng.swissaffinity.utilities.network.DefaultNetworkProvider;
import ch.epfl.sweng.swissaffinity.utilities.network.events.EventClient;
import ch.epfl.sweng.swissaffinity.utilities.network.events.EventClientException;
import ch.epfl.sweng.swissaffinity.utilities.network.events.NetworkEventClient;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT = "ch.epfl.sweng.swissaffinity.event";
    public static final String SAVED_DATA = "ch.epfl.sweng.swissaffinity.saved_data";
    public static final String SHARED_PREF = "ch.epfl.sweng.swissaffinity.shared_pref";
    public static final String USERNAME = "user_name";
    public static final String USERID = "user_id";

    private userDBAdapter mDbHelper;

    public static String email;
    public static String userName;

    public static boolean REGISTERED = false;

    public static final String SERVER_URL = "http://www.beecreative.ch";

    private static EventClient EVENT_CLIENT;

    private EventExpandableListAdapter mListAdapter;
    private SharedPreferences sharedPreferences;


    public static EventClient getEventClient() {
        if (EVENT_CLIENT == null) {
            setEventClient(new NetworkEventClient(SERVER_URL, new DefaultNetworkProvider()));
        }
        return EVENT_CLIENT;
    }

    public static void setEventClient(EventClient eventClient) {
        EVENT_CLIENT = eventClient;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EVENT_CLIENT = getEventClient();
        sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        mListAdapter = new EventExpandableListAdapter(this);
        mDbHelper = new userDBAdapter(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        String userName = sharedPreferences.getString(USERNAME, null);
        String userId = sharedPreferences.getString(USERID, null);
        String welcomeText = getString(R.string.welcome_not_registered_text);
        if (userName == null) {
            REGISTERED = false;
        } else {
            REGISTERED = true;
            welcomeText = String.format(getString(R.string.welcome_registered_text), userName);
        }
        ((TextView) findViewById(R.id.mainWelcomeText)).setText(welcomeText);
        createData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createData() {
        if (isNetworkConnected(this)) {
            if (mListAdapter.getGroupCount() == 0) {
                new DownloadEventsTask().execute();
                mDbHelper.open();
                try {
                    fillData();
                } catch (SQLException e) {
                    // TODO: handle exception here
                }
                mDbHelper.close();
            }
        } else {
            Toast.makeText(MainActivity.this, "No Network", Toast.LENGTH_LONG).show();
        }
    }

    private void fillData() throws SQLException {
        Cursor dataCursor = mDbHelper.fetchAllData();
        if (dataCursor != null && dataCursor.getCount() > 2) {
            dataCursor.moveToFirst();
            TextView view = (TextView) findViewById(R.id.mainWelcomeText);
            userName = dataCursor.getString(dataCursor.getColumnIndex(mDbHelper.KEY_USER_NAME));
            email = dataCursor.getString(dataCursor.getColumnIndex(mDbHelper.KEY_EMAIL));
            Log.v("DataBase", userName + " :: " + email);
        }
    }


    private class DownloadEventsTask extends AsyncTask<Void, Void, List<Event>> {

        @Override
        protected List<Event> doInBackground(Void... args) {
            List<Event> result = null;
            try {
                result = EVENT_CLIENT.fetchAll();
            } catch (EventClientException e) {
                // TODO: check for an error handling
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<Event> result) {
            fillEvents(result);
        }
    }


    private void fillEvents(List<Event> result) {
        String myEvents = getResources().getString(R.string.my_events);
        String upcomingEvents = getResources().getString(R.string.upcoming_events);
        if (REGISTERED) {
            mListAdapter.addGroup(myEvents);
            // TODO : add the events the user is registered to.
        }
        mListAdapter.addGroup(upcomingEvents);
        if (result != null) {
            for (Event e : result) {
                mListAdapter.addChild(upcomingEvents, e);
            }
        }
        final ExpandableListView listView =
                (ExpandableListView) findViewById(R.id.mainEventListView);
        listView.setAdapter(mListAdapter);
        listView.setOnChildClickListener(
                new OnChildClickListener() {
                    @Override
                    public boolean onChildClick(
                            ExpandableListView parent,
                            View v,
                            int groupPosition,
                            int childPosition,
                            long id)
                    {
                        Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                        Event event = (Event) mListAdapter.getChild(groupPosition, childPosition);
                        intent.putExtra(EXTRA_EVENT, event);
                        startActivity(intent);
                        return true;
                    }
                });
        if (mListAdapter.getChildrenCount(0) == 0) {
            listView.expandGroup(1);
        } else {
            listView.expandGroup(0);
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();
        return network != null && network.isConnected();
    }
}
