package de.streberpower.webuntisapi2.WebUntisObjects;

/**
 * Created by Streberpower on 10.10.2015.
 */
public class WebUntisCredentials {
    public String username;
    public String password;

    public WebUntisCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public WebUntisCredentials() {
    }
}
