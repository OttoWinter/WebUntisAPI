package de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes;

import com.google.gson.*;
import javafx.scene.paint.Color;

import java.lang.reflect.Type;

/**
 * Created by Streberpower on 10.10.2015.
 */
public class WebUntisColor implements JsonDeserializer<Color>, JsonSerializer<Color> {

    @Override
    public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String c = json.getAsString();
        if(!c.startsWith("#"))
            c = "#" + c;
        return Color.web(c);
    }

    @Override
    public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
        String s =  String.format( "#%02X%02X%02X%02X",
                (int)( src.getRed() * 255 ),
                (int)( src.getGreen() * 255 ),
                (int)( src.getBlue() * 255 ) ,
                (int)(src.getOpacity() * 255));

        return new JsonPrimitive(s);
    }
}
