package de.streberpower.webuntisapi2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes.WebUntisColor;
import de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes.WebUntisDate;
import de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes.WebUntisResult;
import de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes.WebUntisTime;
import de.streberpower.webuntisapi2.WebUntisObjects.*;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Streberpower on 10.10.2015.
 */
public class Session {
    private static Logger logger = LoggerFactory.getLogger(Session.class);
    public URL url;
    public User user;
    public Gson gson;
    public Gson gsonCache;
    public WebUntisCredentials credentials;

    public Session(SessionConfiguration conf) {
        this.credentials = conf.credentials;
        try {
            this.url = new URL(String.format("https://%s.webuntis.com/WebUntis/jsonrpc.do?school=%s", conf.server, conf.school));
        } catch (MalformedURLException e) {
            logger.error("Can't create Session", e);
            return;
        }
        this.user = new User();
        logger.debug("Created Session with username=%s url=%s", this.credentials.username, this.url);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(WebUntisDate.class, new WebUntisDate());
        gsonBuilder.registerTypeAdapter(WebUntisTime.class, new WebUntisTime());
        gsonBuilder.registerTypeAdapter(Color.class, new WebUntisColor());
        gsonBuilder.registerTypeAdapter(Teacher.class, new Teacher());
        gsonBuilder.registerTypeAdapter(StudentClass.class, new StudentClass());
        gsonBuilder.registerTypeAdapter(Room.class, new Room());
        gsonBuilder.registerTypeAdapter(Subject.class, new Subject());
        gsonBuilder.registerTypeAdapter(Holiday.class, new Holiday());
        gsonBuilder.registerTypeAdapter(Student.class, new Student());
        gsonBuilder.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT);
        gsonBuilder.setPrettyPrinting();
        this.gson = gsonBuilder.create();
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(WebUntisDate.class, new WebUntisDate());
        gsonBuilder.registerTypeAdapter(WebUntisTime.class, new WebUntisTime());
        gsonBuilder.registerTypeAdapter(Color.class, new WebUntisColor());
        gsonBuilder.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT);
        gsonBuilder.setPrettyPrinting();
        this.gsonCache = gsonBuilder.create();
    }

    public void login() throws WebUntisConnectionException, WebUntisParseException {
        Request request = new Request("authenticate");
        request.params.put("user", credentials.username);
        request.params.put("password", credentials.password);

        Type resultType = new TypeToken<WebUntisResult<User>>() {
        }.getType();
        WebUntisResult<User> result = Request.send(url, user.sessionId, gson.toJson(request), resultType, gson);
        assert result != null;
        this.user = result.result;
    }

    public void logout() throws WebUntisConnectionException, WebUntisParseException {
        Request request = new Request("logout");
        Request.send(url, user.sessionId, gson.toJson(request));
    }

    public List<DayTimeGridUnits> getTimegridUnits() throws WebUntisConnectionException, WebUntisParseException {
        Request request = new Request("getTimegridUnits");
        Type resultType = new TypeToken<WebUntisResult<List<DayTimeGridUnits>>>() {
        }.getType();
        WebUntisResult<List<DayTimeGridUnits>> result = Request.send(url, user.sessionId, gson.toJson(request), resultType, gson);
        assert result != null;
        return result.result;
    }

    public Timetable getTimetable(WebUntisDate startDate, WebUntisDate endDate, int type) throws WebUntisConnectionException, WebUntisParseException {
        Timetable t = new Timetable();

        populateCaches();

        try {
            t.timeGridUnits = getTimegridUnits();
        } catch (WebUntisConnectionException | WebUntisParseException e) {
            logger.error("Can't get Timegrid Units! Trying without");
        }

        Request request = new Request("getTimetable");
        request.params.put("startDate", startDate);
        request.params.put("endDate", endDate);
        request.params.put("id", user.personId);
        request.params.put("type", type);

        Type resType = new TypeToken<WebUntisResult<List<ClassUnit>>>() {
        }.getType();
        WebUntisResult<List<ClassUnit>> result = Request.send(url, user.sessionId, gson.toJson(request), resType, gson);
        assert result != null;
        List<ClassUnit> units = result.result;
        WebUntisDate currentDate = (WebUntisDate) startDate.clone();
        do {
            SchoolDay currentDay = new SchoolDay();
            currentDay.date = (WebUntisDate) currentDate.clone();
            for (ClassUnit unit : units) {
                if (isSameDay(currentDate, unit.date)) {
                    currentDay.units.add(unit);
                }
            }
            if (Holiday.cache != null)
                for (Holiday holiday : Holiday.cache) {
                    if (startDate.compareTo(currentDate) >= 0 && endDate.compareTo(currentDate) <= 0) {
                        currentDay.holiday = holiday;
                    }
                }
            currentDate.add(Calendar.DAY_OF_YEAR, 1);
            t.days.add(currentDay);
        } while (currentDate.compareTo(endDate) <= 0);
        return t;
    }

    private void populateCaches() {
        try {
            Teacher.populateCache(url, user.sessionId, gson, gsonCache);
        } catch (WebUntisConnectionException | WebUntisParseException e) {
            logger.error("Can't get Teachers! Trying without");
        }
        try {
            StudentClass.populateCache(url, user.sessionId, gson, gsonCache);
        } catch (WebUntisConnectionException | WebUntisParseException e) {
            logger.error("Can't get Klassen! Trying without");
        }
        try {
            Room.populateCache(url, user.sessionId, gson, gsonCache);
        } catch (WebUntisConnectionException | WebUntisParseException e) {
            logger.error("Can't get Rooms! Trying without");
        }
        try {
            Subject.populateCache(url, user.sessionId, gson, gsonCache);
        } catch (WebUntisConnectionException | WebUntisParseException e) {
            logger.error("Can't get Subjects! Trying without");
        }
        try {
            Holiday.populateCache(url, user.sessionId, gson, gsonCache);
        } catch (WebUntisConnectionException | WebUntisParseException e) {
            logger.error("Can't get Holidays! Trying without");
        }
        try {
            Student.populateCache(url, user.sessionId, gson, gsonCache);
        } catch (WebUntisConnectionException | WebUntisParseException e) {
            logger.error("Can't get Students! Trying without");
        }
    }

    private boolean isSameDay(WebUntisDate currentDate, WebUntisDate date) {
        return currentDate.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                currentDate.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR);
    }

    public Timetable getTimetable(WebUntisDate startDate, WebUntisDate endDate) throws WebUntisConnectionException, WebUntisParseException {
        return getTimetable(startDate, endDate, user.personType);
    }
}
