package de.streberpower.webuntisapi2;

/**
 * Created by Streberpower on 10.10.2015.
 */
public class Util {
    public static String addIndent(String string){
        String[] lines = string.split("\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if(i != 0)
                sb.append("\t");
            sb.append(lines[i]);
            if(lines.length != 1 && i != lines.length - 1)
                sb.append("\n");
        }
        return sb.toString();
    }
}
