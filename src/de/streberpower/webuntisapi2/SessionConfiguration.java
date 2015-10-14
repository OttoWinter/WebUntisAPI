package de.streberpower.webuntisapi2;

import com.google.gson.Gson;
import de.streberpower.webuntisapi2.WebUntisObjects.WebUntisCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by Streberpower on 10.10.2015.
 */
public class SessionConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SessionConfiguration.class);
    public WebUntisCredentials credentials;
    public String server;
    public String school;

    public SessionConfiguration(WebUntisCredentials credentials, String server, String school) {
        this.credentials = credentials;
        this.server = server;
        this.school = school;
    }

    public static SessionConfiguration fromFile(Gson gson, File file) {
        try {
            return gson.fromJson(new FileReader(file), SessionConfiguration.class);
        } catch (IOException e) {
            logger.error("Can't read SessionConfiguration", e);
        }
        return null;
    }

    public void writeToFile(File file, Gson gson) {
        PrintWriter writer = null;
        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            gson.toJson(this, writer);
            writer.flush();
        } catch (IOException e) {
            logger.error("Can't create SessionConfiguration file", e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
