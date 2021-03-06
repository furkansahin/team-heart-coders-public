package ch.epfl.sweng.swissaffinity.end_to_end;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.swissaffinity.MainActivity;
import ch.epfl.sweng.swissaffinity.utilities.Location;
import ch.epfl.sweng.swissaffinity.utilities.network.DefaultNetworkProvider;
import ch.epfl.sweng.swissaffinity.utilities.network.NetworkProvider;
import ch.epfl.sweng.swissaffinity.utilities.network.users.NetworkUserClient;
import ch.epfl.sweng.swissaffinity.utilities.network.users.UserClient;
import ch.epfl.sweng.swissaffinity.utilities.network.users.UserClientException;
import ch.epfl.sweng.swissaffinity.utilities.parsers.LocationParser;
import ch.epfl.sweng.swissaffinity.utilities.parsers.ParserException;
import ch.epfl.sweng.swissaffinity.utilities.parsers.SafeJSONObject;

import static ch.epfl.sweng.swissaffinity.utilities.network.ServerTags.EMAIL;
import static ch.epfl.sweng.swissaffinity.utilities.network.ServerTags.ENABLED;
import static ch.epfl.sweng.swissaffinity.utilities.network.ServerTags.FIRST_NAME;
import static ch.epfl.sweng.swissaffinity.utilities.network.ServerTags.GENDER;
import static ch.epfl.sweng.swissaffinity.utilities.network.ServerTags.LAST_NAME;
import static ch.epfl.sweng.swissaffinity.utilities.network.ServerTags.LOCKED;
import static ch.epfl.sweng.swissaffinity.utilities.network.ServerTags.USERNAME;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by Joel on 11/19/2015.
 * Modified by Dario on 09.12.2015
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateUserTest {
    private UserClient mUserClient;
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void setUp() {
        mActivityRule.getActivity();
        NetworkProvider networkProvider = new DefaultNetworkProvider();
        mUserClient = new NetworkUserClient(NetworkProvider.SERVER_URL, networkProvider);
    }

    @Test
    public void postMalformedUser() {
        JSONObject jsonUser = new JSONObject();
        try {
            jsonUser = prefilledJSon();
            jsonUser.put(GENDER.get(), "maleo");//test incorrect gender format
            jsonUser.put("birthDate", "18-02-1993");//test incorrect bday format
        } catch (JSONException e){
            fail(e.getMessage());
        }
        try{
            mUserClient.postUser(jsonUser);
        } catch (UserClientException e){
            String expectedMessage = "Validation Failed: Choose a valid gender This value is not valid. ";
            assertEquals(expectedMessage,e.getMessage());
        }
    }

    @Test
    public void userNameEmailTaken() {
        JSONObject jsonUser = new JSONObject();
        try {
            jsonUser = prefilledJSon();
            jsonUser.put(EMAIL.get(), "dario.anongba@epfl.ch");//email should be taken
            jsonUser.put(USERNAME.get(), "Admin"); //username should be taken
        } catch (JSONException e){
            fail(e.getMessage());
        }
        try{
            mUserClient.postUser(jsonUser);
        } catch (UserClientException e){
            String expectedMessage = "Validation Failed: The email is already used The username is already used ";
            // make explicit the server response, it does not come with \s.
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    public void postUserTest() {
        List<Location> locationsOfInterest = new ArrayList<>();
        locationsOfInterest.add(new Location(2, "Genève"));
        locationsOfInterest.add(new Location(3, "Lausanne"));
        locationsOfInterest.add(new Location(4, "Fribourg"));
        locationsOfInterest.add(new Location(6, "Zürich"));
        locationsOfInterest.add(new Location(7, "Berne"));
        locationsOfInterest.add(new Location(8, "Bulle"));
        try {
            JSONObject jsonUser = prefilledJSon();
            JSONObject responseJSON = mUserClient.postUser(jsonUser);

            List<Location> areasOfInterest = new ArrayList<>();
            JSONArray areas = responseJSON.getJSONArray("locations_of_interest");
            for (int i = 0; i < areas.length(); i++) {
                JSONObject jsonArea = areas.getJSONObject(i);
                Location location = new LocationParser().parse(new SafeJSONObject(jsonArea));
                areasOfInterest.add(location);
            }
            String fb_id = responseJSON.getString("facebook_id");
            assertEquals(responseJSON.getString(EMAIL.get()), "testpostuser@gmail.com");
            assertEquals(responseJSON.getString(USERNAME.get()), "TestPostUser");
            assertEquals(responseJSON.getString(FIRST_NAME.get()), "Test");
            assertEquals(responseJSON.getString(LAST_NAME.get()), "Post");
            assertEquals(responseJSON.getString(GENDER.get()), "male");
            assertEquals(
                    responseJSON.getString("birth_date"),
                    "1993-02-18T00:00:00+0100");
            assertEquals(fb_id, "666");
            assertEquals(responseJSON.getBoolean(LOCKED.get()), false);
            assertEquals(responseJSON.getBoolean(ENABLED.get()), false);
            assertTrue(
                    "Unexpected locations of preference",
                    new GetUserTest.CollectionComparator<Location>().compare(
                            locationsOfInterest,
                            areasOfInterest));
            mUserClient.deleteUser("TestPostUser");
        } catch (UserClientException | ParserException | JSONException e) {
            fail(e.getMessage());
        }
    }
    @Ignore
    private JSONObject prefilledJSon() throws JSONException{
        JSONObject jsonUser = new JSONObject();
        jsonUser.put(EMAIL.get(), "testpostuser@gmail.com");
        jsonUser.put(USERNAME.get(), "TestPostUser");
        jsonUser.put("firstName", "Test");
        jsonUser.put("lastName", "Post");
        jsonUser.put(GENDER.get(), "male");
        jsonUser.put("birthDate", "18/02/1993");
        jsonUser.put("facebookId", "666");
        jsonUser.put("plainPassword", "testpassword");
        return jsonUser;
    }
}
