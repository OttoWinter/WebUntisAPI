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
public class Student implements JsonDeserializer<Student>{
    public static List<Student> cache;
    public int id;
    public String key;
    public String name;
    public String foreName;
    public String longName;
    public String gender;

    @Override
    public Student deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int id = json.getAsJsonObject().get("id").getAsInt();
        for (Student aCache : cache) {
            if (id == aCache.id) {
                return aCache;
            }
        }
        return null;
    }


    public static void populateCache(URL url, String sessionId, Gson serializer, Gson deserializer) throws WebUntisConnectionException, WebUntisParseException {
        Request request = new Request("getStudents");
        Type resultType = new TypeToken<WebUntisResult<List<Student>>>(){}.getType();
        WebUntisResult<List<Student>> result = Request.send(url, sessionId, serializer.toJson(request), resultType, deserializer);
        assert result != null;
        cache = result.result;
    }
}
