package de.streberpower.webuntisapi2.WebUntisObjects;

/**
 * Created by Streberpower on 10.10.2015.
 */
public class User {
    public String sessionId;
    public int personType;
    public int personId;
    public int klasseId;

    public User(String sessionId, int personType, int personId, int klasseId) {
        this.sessionId = sessionId;
        this.personType = personType;
        this.personId = personId;
        this.klasseId = klasseId;
    }

    public User() {
    }
}
