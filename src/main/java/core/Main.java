package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static final int taskQueueSize = 1000;
    public static final BlockingQueue<String> readerTaskQueue = new ArrayBlockingQueue<>(taskQueueSize);
    public static final BlockingQueue<Event> counterTaskQueue = new ArrayBlockingQueue<>(taskQueueSize);

    private static final int readerPoolSize = 10;
    private static final int counterPoolSize = 10;

    private static ExecutorService readers;
    private static ExecutorService counters;

    public static void main(String[] args) throws Exception {

        readers = new ThreadPoolExecutor(readerPoolSize, readerPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(taskQueueSize));

        counters = new ThreadPoolExecutor(counterPoolSize, counterPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(taskQueueSize));

        // input reading thread
        new Thread(() -> {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    String line = br.readLine();
                    boolean taskAdded = readerTaskQueue.offer(line);
                    // if we are producing too fast, just dropping new messages
                } catch (IOException e) {}
            }
        }).start();

        // readers thread
        new Thread(() -> {
            List<Future<Event>> results = new ArrayList<>();
            while (true) {
                try {
                    String task = readerTaskQueue.take();
                    while (true) {
                        try {
                            results.add(readers.submit(() -> JsonReader.handle(task)));
                            while (!results.isEmpty() && results.get(0).isDone()) {
                                Event event = results.remove(0).get();
                                if (event == null) { continue; } // skipping corrupted jsons
                                counterTaskQueue.offer(event); // dropping events which didn't fit in
                            }
                        } catch (RejectedExecutionException e) {
                            continue; // keep submitting task until work is done
                        }
                        break;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    return;
                }
            }
        }).start();

        // events thread
        new Thread(() -> {
            while (true) {
                try {
                    Event task = counterTaskQueue.take();
                    while (true) {
                        try {
                            counters.execute(() -> EventCounter.handle(task));
                        } catch (RejectedExecutionException e) {
                            continue; // keep submitting task until work is done
                        }
                        break;
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        }).start();

        Server.start();
    }

}
