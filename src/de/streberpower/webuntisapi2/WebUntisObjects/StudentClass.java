package de.streberpower.webuntisapi2.WebUntisObjects;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import de.streberpower.webuntisapi2.Request;
import de.streberpower.webuntisapi2.WebUntisConnectionException;
import de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes.WebUntisResult;
import de.streberpower.webuntisapi2.WebUntisParseException;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

/**
 * Created by Streberpower on 10.10.2015.
 */
public class StudentClass implements JsonDeserializer<StudentClass> {
    public static List<StudentClass> cache;
    public String name;
    public int id;
    public String longName;
    public boolean active;

    @Override
    public StudentClass deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int id = json.getAsJsonObject().get("id").getAsInt();
        for (StudentClass aCache : cache) {
            if (id == aCache.id) {
                return aCache;
            }
        }
        return null;
    }

    public static void populateCache(URL url, String sessionId, Gson serializer, Gson deserializer) throws WebUntisConnectionException, WebUntisParseException {
        Request request = new Request("getKlassen");
        Type resultType = new TypeToken<WebUntisResult<List<StudentClass>>>(){}.getType();
        WebUntisResult<List<StudentClass>> result = Request.send(url, sessionId, serializer.toJson(request), resultType, deserializer);
        assert result != null;
        cache = result.result;
    }
}
