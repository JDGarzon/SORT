package sorter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Spliter {
    
    public ArrayList<String> split(String inputFile, int actualBucket, int maxBuckets) {
        Path inputPath = FileSystems.getDefault().getPath(inputFile);

        ArrayList<String> bucket = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int lineValue = line.charAt(0) - '0';
                int bucketIndex = determinateBucket(lineValue, maxBuckets);
                if(bucketIndex == actualBucket)
                    bucket.add(line);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bucket;
    }

    public int determinateBucket(int stringValue, int bucketsAmount) {
        return (int) Math.floor((bucketsAmount * stringValue) / 75);
    }

}
