package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Stats {

    public static String getStats() {
        HashMap<String, Long> wordsResult = new HashMap<>();
        HashMap<String, Long> eventsResult = new HashMap<>();
        for (ArrayList<ConcurrentHashMap<String, Long>> data : EventCounter.threadsData.values()) {
            ConcurrentHashMap<String, Long> events = data.get(0);
            ConcurrentHashMap<String, Long> words = data.get(1);

            for (Map.Entry<String, Long> e : events.entrySet()) {
                eventsResult.putIfAbsent(e.getKey(), 0L);
                eventsResult.put(e.getKey(), eventsResult.get(e.getKey()) + e.getValue());
            }

            for (Map.Entry<String, Long> e : words.entrySet()) {
                wordsResult.putIfAbsent(e.getKey(), 0L);
                wordsResult.put(e.getKey(), wordsResult.get(e.getKey()) + e.getValue());
            }
        }

        StringBuilder result = new StringBuilder();
        result.append("{\"events\":{");

        for (Map.Entry<String, Long> e : eventsResult.entrySet()) {
            result.append("\"").append(e.getKey()).append("\"").append(":").append(e.getValue()).append(",");
        }

        result.append("}, \"data\":{");
        for (Map.Entry<String, Long> e : wordsResult.entrySet()) {
            result.append("\"").append(e.getKey()).append("\"").append(":").append(e.getValue()).append(",");
        }
        result.append("}}");
        return result.toString().replaceAll(",}", "}");
    }
}
