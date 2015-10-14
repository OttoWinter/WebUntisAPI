package de.streberpower.webuntisapi2.WebUntisObjects;

import com.google.gson.annotations.SerializedName;
import de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes.WebUntisDate;
import de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes.WebUntisTime;

import java.util.List;

/**
 * Created by Streberpower on 10.10.2015.
 */
public class DayTimeGridUnits {
    public DayTimeGridDays day;
    public List<TimeGridUnit> timeUnits;

    public enum DayTimeGridDays {
        @SerializedName("1")SUNDAY,
        @SerializedName("2")MONDAY,
        @SerializedName("3")TUESDAY,
        @SerializedName("4")WEDNESDAY,
        @SerializedName("5")THURSDAY,
        @SerializedName("6")FRIDAY,
        @SerializedName("7")SATURDAY;

        public static DayTimeGridDays fromCalendarInt(int i){
            switch (i){
                case 1: return SUNDAY;
                case 2: return MONDAY;
                case 3: return TUESDAY;
                case 4: return WEDNESDAY;
                case 5: return THURSDAY;
                case 6: return FRIDAY;
                case 7: return SATURDAY;
                default: return null;
            }
        }
    }
    public WebUntisTime getDayStart() {
        WebUntisTime lowestTime = null;
        for(TimeGridUnit unit : timeUnits){
            if(lowestTime == null || unit.startTime.before(lowestTime)){
                lowestTime = unit.startTime;
            }
        }
        assert lowestTime != null;
        return (WebUntisTime) lowestTime.clone();
    }
    public WebUntisTime getDayEnd() {
        WebUntisTime highestTime = null;
        for(TimeGridUnit unit : timeUnits){
            if(highestTime == null || unit.endTime.after(highestTime)){
                highestTime = unit.startTime;
            }
        }
        assert highestTime != null;
        return (WebUntisTime) highestTime.clone();
    }
}
