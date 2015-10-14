package de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Streberpower on 10.10.2015.
 */
public class WebUntisTime extends GregorianCalendar implements JsonSerializer<WebUntisTime>, JsonDeserializer<WebUntisTime> {
    private static final Logger logger = LoggerFactory.getLogger(WebUntisDate.class);

    @Override
    public WebUntisTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        WebUntisTime a = (WebUntisTime) this.clone();
        try {
            //a.setTime(dateFormat.parse(json.getAsString()));
            String s = json.getAsString();
            int offset = 4 - s.length();
            a.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.substring(0, 2 - offset)));
            a.set(Calendar.MINUTE, Integer.parseInt(s.substring(2 - offset)));
            return a;
        } catch (NumberFormatException e) {
            logger.error("Wasn't able to deserialize " + json.getAsString(), e);
        }
        return null;
    }

    public String format(String s){
        return String.format("%d" + s + "%02d", get(Calendar.HOUR_OF_DAY), get(Calendar.MINUTE));
    }

    @Override
    public JsonElement serialize(WebUntisTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(Integer.parseInt(src.format("")));
    }

    @Override
    public long getTimeInMillis() {
        return get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000 + get(Calendar.MINUTE) * 60 * 1000;
    }
}