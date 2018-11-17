package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class EventCounter {

    public static final ConcurrentHashMap<String, ArrayList<ConcurrentHashMap<String, Long>>> threadsData = new ConcurrentHashMap<>();

    public static void handle(Event input) {
        ArrayList<ConcurrentHashMap<String, Long>> threadData = threadsData.get(Thread.currentThread().getName());
        if (threadData == null) {
            threadData = new ArrayList<>();
            threadsData.put(Thread.currentThread().getName(), threadData);
            if (threadData.isEmpty()) {
                threadData.add(new ConcurrentHashMap<>());
                threadData.add(new ConcurrentHashMap<>());
            }
        }

        ConcurrentHashMap<String, Long> events = threadData.get(0);
        ConcurrentHashMap<String, Long> words = threadData.get(1);

        events.putIfAbsent(input.type, 0L);
        words.putIfAbsent(input.data, 0L);

        events.put(input.type, events.get(input.type) + 1);
        words.put(input.data, words.get(input.data) + 1);
    }
}
