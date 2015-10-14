package de.streberpower.webuntisapi2.WebUntisObjects;

import de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes.WebUntisDate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Streberpower on 10.10.2015.
 */
public class SchoolDay {
    public WebUntisDate date;
    public List<ClassUnit> units = new ArrayList<>();
    public Holiday holiday;
}
