package sorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BucketSorting {

    public ArrayList<String> sort(ArrayList<String> in) {
        try {
            ArrayList<ArrayList<String>> buckets = new ArrayList<ArrayList<String>>();
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            ArrayList<Future<Void>> futures = new ArrayList<>();
            int bucketsAmount = Runtime.getRuntime().availableProcessors();

            buckets = split(in, bucketsAmount);

            for (ArrayList<String> bucket : buckets) {
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
            return merge(buckets);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<ArrayList<String>> split(ArrayList<String> initialBucket, int bucketsAmount) {

        ArrayList<ArrayList<String>> buckets = new ArrayList<>(bucketsAmount);
        for (int i = 0; i < bucketsAmount; i++) {
            buckets.add(new ArrayList<>());
        }

        for(String element:initialBucket){
            int elementValue = element.charAt(1) - '0';
            int bucketIndex = determinateBucket(elementValue, bucketsAmount);
            buckets.get(bucketIndex).add(element);
        }
        return buckets;
    }

    public int determinateBucket(int stringValue, int bucketsAmount) {
        return (int) Math.floor((bucketsAmount * stringValue) / 75);
    }

    public ArrayList<String> merge(ArrayList<ArrayList<String>> buckets) {
        ArrayList<String> result = new ArrayList<>();
        for (ArrayList<String> bucket : buckets) {
            for (String element : bucket) {
                result.add(element);
            }
        }
        return result;
    }
}