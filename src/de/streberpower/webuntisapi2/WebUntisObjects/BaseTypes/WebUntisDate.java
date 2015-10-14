package de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by Streberpower on 10.10.2015.
 */
public class WebUntisDate extends GregorianCalendar implements JsonSerializer<WebUntisDate>, JsonDeserializer<WebUntisDate> {
    private static final Logger logger = LoggerFactory.getLogger(WebUntisDate.class);
    private transient SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @Override
    public WebUntisDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        WebUntisDate a = (WebUntisDate) this.clone();
        dateFormat.setCalendar(a);
        try {
            a.setTime(dateFormat.parse(json.getAsString()));
            return a;
        } catch (ParseException e) {
            logger.error("Wasn't able to deserialize " + json.getAsString(), e);
        }
        return null;
    }

    @Override
    public JsonElement serialize(WebUntisDate src, Type typeOfSrc, JsonSerializationContext context) {
        src.dateFormat.setCalendar(src);
        return new JsonPrimitive(Integer.parseInt(dateFormat.format(src.getTime())));
    }
}
