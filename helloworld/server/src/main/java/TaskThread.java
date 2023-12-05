import java.util.ArrayList;

import Demo.Task;
import sorter.Spliter;

public class TaskThread extends Thread {
    
    Task task;
    Spliter spliter;
    Master master;
    int actualBucket;
    int maxBuckets;
    String inputFile;

    public TaskThread(String in, Spliter spliter, Master master){
        this.spliter = spliter;
        this.master = master;
        inputFile = in;
    }

    @Override
    public void run(){
        while(!master.getIsFinished()){
            ArrayList<String> bucket;
            actualBucket = master.getNumBucket();
            maxBuckets = master.getMaxBuckets();
            master.incrementBucket();
            bucket = spliter.split(inputFile, actualBucket, maxBuckets);
            Task task = new Task();
            task.data = bucket;
            task.id = actualBucket+"";
            master.addTask(task);
        }
        
    }
}
