package sorter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BucketSorting {

    static Long timeStart;
    static Long timeEnd;

    public static void sort(String inputFile, String outputFile, int bucketsAmount) {
        try {
            List<List<String>> buckets = new ArrayList<List<String>>();
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            List<Future<Void>> futures = new ArrayList<>();

            buckets = split(inputFile, bucketsAmount);

            for (List<String> bucket : buckets) {
                Callable<Void> sortTask = () -> {
                    Collections.sort(bucket);
                    return null;
                };
                futures.add(executor.submit(sortTask));
            }

            for (Future<Void> future : futures) {
                future.get();
            }

            executor.shutdown();
            merge(outputFile, buckets);
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static List<List<String>> split(String inputFile, int bucketsAmount) throws IOException {
        Path inputPath = FileSystems.getDefault().getPath(inputFile);

        List<List<String>> buckets = new ArrayList<>(bucketsAmount);
        for (int i = 0; i < bucketsAmount; i++) {
            buckets.add(new ArrayList<>());
        }

        try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int lineValue = line.charAt(0) - '0';
                int bucketIndex = determinateBucket(lineValue, bucketsAmount);
                buckets.get(bucketIndex).add(line);
            }
        }
        return buckets;
    }

    public static int determinateBucket(int stringValue, int bucketsAmount) {
        return (int) Math.floor((bucketsAmount * stringValue) / 75);
    }

    public static void merge(String outputFile, List<List<String>> buckets) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            for (List<String> bucket : buckets) {
                for (String element : bucket) {
                    writer.write(element);
                    writer.newLine();
                }
            }

        }
    }
}