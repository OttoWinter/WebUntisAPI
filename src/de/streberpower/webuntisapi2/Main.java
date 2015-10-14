package de.streberpower.webuntisapi2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes.WebUntisDate;
import de.streberpower.webuntisapi2.WebUntisObjects.Timetable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.Calendar;

public class Main {
    public static final String SESSION_CONFIGURATION_FILE = "session_conf.json";
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws WebUntisConnectionException, WebUntisParseException {
        logger.info("Program started!");
        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithModifiers(Modifier.STATIC).create();

        File file = new File(SESSION_CONFIGURATION_FILE);
        SessionConfiguration conf = SessionConfiguration.fromFile(gson, file);

        Session session = new Session(conf);
        session.login();
        WebUntisDate from = new WebUntisDate();
        WebUntisDate to = new WebUntisDate();
        to.add(Calendar.DAY_OF_YEAR, 7);
        Timetable t = session.getTimetable(from, to);
        logger.debug(session.gson.toJson(t));
        session.logout();
    }
}
