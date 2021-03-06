package ch.epfl.sweng.swissaffinity.utilities.parsers;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import ch.epfl.sweng.swissaffinity.DataForTesting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by sahinfurkan on 18/11/15.
 */
public class SafeJSONObjectTest {

    JSONObject json;
    SafeJSONObject jsonFromString;
    SafeJSONObject jsonFromJson;

    @Before
    public void setUp() throws JSONException {
        json = DataForTesting.createJSONEvent();
        jsonFromString = new SafeJSONObject(json.toString());
        jsonFromJson = new SafeJSONObject(json);
    }

    @Test
    public void identityTest() throws JSONException {
        assertEquals(jsonFromString.toString(), json.toString());
        assertEquals(jsonFromJson.toString(), json.toString());
    }

    @Test
    public void noExceptionTest() throws JSONException {
        jsonFromString.get("me", null);
        jsonFromJson.get("him", null);
    }

}
