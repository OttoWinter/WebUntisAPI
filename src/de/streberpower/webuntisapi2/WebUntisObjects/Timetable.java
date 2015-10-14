package de.streberpower.webuntisapi2.WebUntisObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Streberpower on 10.10.2015.
 */
public class Timetable {
    public List<SchoolDay> days = new ArrayList<>();
    public List<DayTimeGridUnits> timeGridUnits = new ArrayList<>();

    public boolean hasDayInTimeGrid(DayTimeGridUnits.DayTimeGridDays day){
        for (DayTimeGridUnits c : timeGridUnits){
            if(c.day == day) return true;
        }
        return false;
    }
}
