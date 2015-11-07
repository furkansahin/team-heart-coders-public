package ch.epfl.sweng.swissaffinity.utilities.network;

import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.sweng.swissaffinity.utilities.network.events.SpeedDatingEventParser;

import static ch.epfl.sweng.swissaffinity.utilities.network.Parsable.TAGS.SPEED_DATING_TYPE;
import static ch.epfl.sweng.swissaffinity.utilities.network.Parsable.TAGS.TYPE;

public class ParserFactory {

    public static Parsable parserFor(JSONObject jsonObject) throws ParserException {
        try {
            String type = jsonObject.getString(TYPE.get());
            if (type.equals(SPEED_DATING_TYPE.get())) {
                return new SpeedDatingEventParser();
            }
        } catch (JSONException e) {
            throw new ParserException(e);
        }
        throw new ParserException("Unknown type");
    }
}
