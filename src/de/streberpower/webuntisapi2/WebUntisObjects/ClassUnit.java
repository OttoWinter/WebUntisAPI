package de.streberpower.webuntisapi2.WebUntisObjects;

import com.google.gson.annotations.SerializedName;
import de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes.WebUntisDate;
import de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes.WebUntisTime;

import java.util.List;

/**
 * Created by Streberpower on 10.10.2015.
 */
public class ClassUnit {
    public WebUntisDate date;
    @SerializedName("kl")
    public List<StudentClass> classes;
    @SerializedName("te")
    public List<Teacher> teachers;
    @SerializedName("ro")
    public List<Room> rooms;
    @SerializedName("su")
    public List<Subject> subjects;

    public int id;
    public WebUntisTime startTime;
    public WebUntisTime endTime;
    public ClassUnitCode code = ClassUnitCode.NONE;
    public String statFlags = "@"; // Purpose?
    public ClassUnitType type = ClassUnitType.NORMAL_LESSON;

    // Source: http://python-webuntis.readthedocs.org/en/latest/objects.html

    public enum ClassUnitCode {
        @SerializedName("") NONE,
        @SerializedName("cancelled") CANCELLED,
        @SerializedName("irregular") IRREGULAR
    }

    public enum ClassUnitType {
        @SerializedName("ls") NORMAL_LESSON,
        @SerializedName("oh") OFFICE_HOUR,
        @SerializedName("sb") STANDBY,
        @SerializedName("bs") BREAK_SUPERVISION,
        @SerializedName("ex") EXAMINATION
    }
}
