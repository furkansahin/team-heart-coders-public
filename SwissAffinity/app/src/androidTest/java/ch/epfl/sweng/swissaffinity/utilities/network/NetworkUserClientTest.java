package ch.epfl.sweng.swissaffinity.utilities.network;

import android.test.suitebuilder.annotation.LargeTest;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ch.epfl.sweng.swissaffinity.DataForTesting;
import ch.epfl.sweng.swissaffinity.events.Event;
import ch.epfl.sweng.swissaffinity.users.User;
import ch.epfl.sweng.swissaffinity.utilities.Location;
import ch.epfl.sweng.swissaffinity.utilities.network.users.NetworkUserClient;
import ch.epfl.sweng.swissaffinity.utilities.network.users.UserClientException;
import ch.epfl.sweng.swissaffinity.utilities.parsers.ParserException;
import ch.epfl.sweng.swissaffinity.utilities.parsers.SafeJSONObject;
import ch.epfl.sweng.swissaffinity.utilities.parsers.user.UserParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@LargeTest
public class NetworkUserClientTest {

    private String mockServerURL;
    private NetworkProvider mockNetworkProvider;
    private NetworkUserClient networkUserClient;

    private UserParser userParser = new UserParser();
    private User testUser;
    private User returnedUser;

    private Collection<Location> areasOfInterest;
    private List<Event> eventsAttended;

    @Before
    public void setup() throws IOException, JSONException, ParserException {
        testUser = DataForTesting.createUser();
        areasOfInterest = DataForTesting.LOCATIONS;
        eventsAttended = new ArrayList<>();

        mockServerURL = "http://beecreative.ch";
        mockNetworkProvider = mock(DefaultNetworkProvider.class);
        networkUserClient = new NetworkUserClient(mockServerURL, mockNetworkProvider);

        when(mockNetworkProvider.getContent(anyString())).thenReturn(DataForTesting.userJSONcontent);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testIllegalURL() {
        networkUserClient = new NetworkUserClient(null, mockNetworkProvider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullNetworkProvider() {
        networkUserClient = new NetworkUserClient(mockServerURL, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullJSONObjectPostUser() throws UserClientException {
        networkUserClient.postUser(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullJSONObjectDeleteUser() throws UserClientException {
        networkUserClient.deleteUser(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullUsernameRegisterUser() throws UserClientException {
        networkUserClient.registerUser(null, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeEventIdRegisterUser() throws UserClientException {
        networkUserClient.registerUser("testUsername", -100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeRegistrationIdUnregisterUser() throws UserClientException {
        networkUserClient.unregisterUser(-100);
    }

    @Test
    public void testFetchByUsername() throws UserClientException, IOException {
        returnedUser = networkUserClient.fetchByUsername("testUsername");

        assertEquals(testUser.getId(), returnedUser.getId());
        assertEquals(testUser.getFacebookId(), returnedUser.getFacebookId());

        assertEquals(testUser.getUsername(), returnedUser.getUsername());
        assertEquals(testUser.getFirstName(), returnedUser.getFirstName());
        assertEquals(testUser.getLastName(), returnedUser.getLastName());

        assertEquals(testUser.getHomePhone(), returnedUser.getHomePhone());
        assertEquals(testUser.getMobilePhone(), returnedUser.getMobilePhone());
        assertEquals(testUser.getEmail(), returnedUser.getEmail());
        assertEquals(testUser.getAddress(), returnedUser.getAddress());
        assertEquals(testUser.getProfession(), returnedUser.getProfession());

        assertEquals(testUser.getLocked(), returnedUser.getLocked());
        assertEquals(testUser.getEnabled(), returnedUser.getEnabled());

        assertEquals(testUser.getGender(), returnedUser.getGender());
        assertEquals(testUser.getBirthDate(), returnedUser.getBirthDate());


        Collection<Location> coll1 = testUser.getAreasOfInterest();
        Collection<Location> coll2 = returnedUser.getAreasOfInterest();
        coll1.removeAll(coll2);
        assertTrue(coll1.isEmpty());


        for (int i = 0; i < returnedUser.getEventsAttended().size(); i++) {
            Iterator<Event> iterator3 = testUser.getEventsAttended().iterator();
            Iterator<Event> iterator4 = returnedUser.getEventsAttended().iterator();

            if (iterator3.hasNext() && iterator4.hasNext()) {
                assertEquals(iterator3.next(), iterator4.next());
            }
        }
    }

    @Test
    public void testFetchByFacebookID() throws UserClientException, IOException {
        returnedUser = networkUserClient.fetchByFacebookID("1");

        assertEquals(testUser.getId(), returnedUser.getId());
        assertEquals(testUser.getFacebookId(), returnedUser.getFacebookId());

        assertEquals(testUser.getUsername(), returnedUser.getUsername());
        assertEquals(testUser.getFirstName(), returnedUser.getFirstName());
        assertEquals(testUser.getLastName(), returnedUser.getLastName());

        assertEquals(testUser.getHomePhone(), returnedUser.getHomePhone());
        assertEquals(testUser.getMobilePhone(), returnedUser.getMobilePhone());
        assertEquals(testUser.getEmail(), returnedUser.getEmail());
        assertEquals(testUser.getAddress(), returnedUser.getAddress());
        assertEquals(testUser.getProfession(), returnedUser.getProfession());

        assertEquals(testUser.getLocked(), returnedUser.getLocked());
        assertEquals(testUser.getEnabled(), returnedUser.getEnabled());

        assertEquals(testUser.getGender(), returnedUser.getGender());
        assertEquals(testUser.getBirthDate(), returnedUser.getBirthDate());

        Collection<Location> coll1 = testUser.getAreasOfInterest();
        Collection<Location> coll2 = returnedUser.getAreasOfInterest();

        coll1.removeAll(coll2);
        assertTrue(coll1.isEmpty());

        for (int i = 0; i < returnedUser.getEventsAttended().size(); i++) {
            Iterator<Event> iterator3 = testUser.getEventsAttended().iterator();
            Iterator<Event> iterator4 = returnedUser.getEventsAttended().iterator();

            if (iterator3.hasNext() && iterator4.hasNext()) {
                assertEquals(iterator3.next(), iterator4.next());
            }
        }
    }

    @Test
    public void testPostUser() throws
        UserClientException,
        IllegalArgumentException,
        IOException,
        JSONException,
        ParserException
    {
        when(mockNetworkProvider.postContent(anyString(), any(JSONObject.class))).thenReturn(
            DataForTesting.userJSONcontent);

        String
            s =
            networkUserClient.postUser(new JSONObject(DataForTesting.userJSONcontent)).toString();
        returnedUser = userParser.parse(new SafeJSONObject(s));

        assertEquals(testUser.getId(), returnedUser.getId());
        assertEquals(testUser.getFacebookId(), returnedUser.getFacebookId());

        assertEquals(testUser.getUsername(), returnedUser.getUsername());
        assertEquals(testUser.getFirstName(), returnedUser.getFirstName());
        assertEquals(testUser.getLastName(), returnedUser.getLastName());

        assertEquals(testUser.getHomePhone(), returnedUser.getHomePhone());
        assertEquals(testUser.getMobilePhone(), returnedUser.getMobilePhone());
        assertEquals(testUser.getEmail(), returnedUser.getEmail());
        assertEquals(testUser.getAddress(), returnedUser.getAddress());
        assertEquals(testUser.getProfession(), returnedUser.getProfession());

        assertEquals(testUser.getLocked(), returnedUser.getLocked());
        assertEquals(testUser.getEnabled(), returnedUser.getEnabled());

        assertEquals(testUser.getGender(), returnedUser.getGender());
        assertEquals(testUser.getBirthDate(), returnedUser.getBirthDate());

        Collection<Location> coll1 = testUser.getAreasOfInterest();
        Collection<Location> coll2 = returnedUser.getAreasOfInterest();

        coll1.removeAll(coll2);
        assertTrue(coll1.isEmpty());

        for (int i = 0; i < returnedUser.getEventsAttended().size(); i++) {
            Iterator<Event> iterator3 = testUser.getEventsAttended().iterator();
            Iterator<Event> iterator4 = returnedUser.getEventsAttended().iterator();

            if (iterator3.hasNext() && iterator4.hasNext()) {
                assertEquals(iterator3.next(), iterator4.next());
            }
        }
    }

    @Test
    public void testDeleteUser() throws UserClientException, IOException {
        when(mockNetworkProvider.deleteContent(anyString())).thenReturn("");
        try {
            networkUserClient.deleteUser("testUsername");
        } catch (UserClientException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRegisterUser() throws UserClientException, IOException {
        when(mockNetworkProvider.postContent(anyString(), any(JSONObject.class)))
            .thenReturn("");
        try {
            networkUserClient.registerUser("testUsername", 100);
        } catch (UserClientException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUnregisterUser() throws UserClientException, IOException {
        //Server sends empty String by default when successful
        when(mockNetworkProvider.deleteContent(anyString())).thenReturn("");
        try {
            networkUserClient.unregisterUser(100);

        } catch (UserClientException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUnregisterUser2() throws UserClientException, IOException {
        try {
            networkUserClient.unregisterUser(100);
        } catch (UserClientException e) {
            //SUCCESS
        }
    }

    @Test
    public void testDeleteUser2() throws UserClientException, IOException {
        try {
            networkUserClient.deleteUser("jeSuisPasLa");
        } catch (UserClientException e) {
            //SUCCESS
        }
    }
}
