package core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonReader {

    private static final Pattern checker = Pattern.compile(".*\"event_type\".*\"(.*)\".*\"data\".*?\"(.*?)\".*");

    public static Event handle(String input) {
        // can be parsed/validated correctly via some lib (Gson, Jackson) but to keep it simple, checking some keypoints
        Matcher matcher = checker.matcher(input);
        if (!matcher.matches()) { return null; }
        String eventType = matcher.group(1);
        String data = matcher.group(2);
        return new Event(eventType, data);
    }
}
