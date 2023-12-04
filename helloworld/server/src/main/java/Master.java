import com.zeroc.Ice.Current;

import Demo.BrokerPrx;
import Demo.MasterPrx;
import Demo.Task;
import Demo.WorkerPrx;
import sorter.BucketSorting;
import sorter.ExternalSorter;
import sorter.Spliter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Queue;

public class Master implements Demo.Master {

    com.zeroc.Ice.ObjectAdapter adapter;

    ArrayList<WorkerPrx> workers = new ArrayList<>();

    BucketSorting sorter;
    Spliter spliter;
    TaskThread[] threads = new TaskThread[2];
    String out;
    String in;
    int numBucket;
    Queue tasks;
    boolean isFinished;
    int resultsSubmited;

    public void setAdapter(com.zeroc.Ice.ObjectAdapter adapter) {
        this.adapter = adapter;
    }

    public Master() {
        super();
        sorter = new BucketSorting();
        spliter = new Spliter();
        tasks = new Queue<Task>();
    }

    @Override
    public String sort(String inputFile, String outputFile, Current current) {
        setIsFinished(false);
        setResultsSubmited(0);
        setNumBucket(0);
        out = "server\\src\\main\\java\\data\\" + outputFile;
        in = "server\\src\\main\\java\\data\\" + inputFile;
        try {
            ArrayList<ArrayList<String>> buckets;
            
            if(!willBeDistributed(inputFile)){
                sorter.sort(in, out);
            }else{
                for(TaskThread thread:threads){
                    thread = new TaskThread(in, spliter, this);
                    thread.start();
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Archivo no ordenado";
        }
        return "Archivo ordenado exitosamente";
    }

    public boolean willBeDistributed(String inputFile){
        long umbral = 209715200L;
        Path path = Paths.get("server\\src\\main\\java\\data\\" + inputFile);

        try {
            long size = Files.size(path);
            System.out.println("El tamaño del archivo es: " + size + " bytes");

            if(size>umbral)
                return true;
            else
                return false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void launchWorkers(int n, Current current) {
        BrokerPrx broker = Server.getBroker();
        MasterPrx sorterPrx = MasterPrx.uncheckedCast(adapter.createProxy(
                com.zeroc.Ice.Util.stringToIdentity("Sorter")));
        for (int i = 0; i < n; i++) {
            WorkerPrx worker = broker.getWorker();
            ArrayList<String> list = new ArrayList<>();
            list.add("1");
            list.add("2");
            list.add("3");
            list.add("4");
            list.add("5");
            worker.getData(list);

            System.out.println("" + worker);
            if (worker != null) {
                workers.add(worker);
                worker.setMaster(sorterPrx);
                worker.execute();
            }
        }
        System.out.println("" + workers.size());
    }

    @Override
    public Task getTask(Current current) {
        if(tasks.isEmpty()){
            return null;
        }else{
            return tasks.poll();
        }
    }

    @Override
    public void addPartialResult(Task d, Current current) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("block" + d.id))) {
            ArrayList<String> data = (ArrayList<String>) d.data;
            for (String s:data) {
                writer.write(s);
                writer.newLine();
            }
        }
        setResultsSubmited(getResultsSubmited() + 1);
        if (getResultsSubmited() == getMaxBuckets()){
            setIsFinished(true);
            merge();
        }
    }

    public void merge(){
        int totalBlocks = 0;
        while (new File("block" + totalBlocks).exists()) {
            totalBlocks++;
        }

        BufferedReader[] blockReaders = new BufferedReader[totalBlocks];
        for (int i = 0; i < totalBlocks; i++) {
            blockReaders[i] = new BufferedReader(new FileReader("block" + i));
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(out))) {
            for(int i=0;i<blockReaders.length;i++){
                String line = blockReaders[i].readLine();
                while (line != null) {
                    writer.write(line);
                    writer.newLine();
                    line = blockReaders[i].readLine();
                }

            }
        }
    }

    public void incrementBucket() {
        numBucket++;
    }

    public void setNumBucket(int numBucket){
        this.numBucket = numBucket;
    }

    public int getNumBucket(){
        return numBucket;
    }

    public int getMaxBuckets(){
        return workers.size();
    }

    public int getResultsSubmited(){
        return resultsSubmited;
    }

    public void setResultsSubmited(int resultsSubmited){
        this.resultsSubmited = resultsSubmited;
    }

    public void addTask(Task task){
        tasks.add(task);
    }

    public void setIsFinished(boolean isFinished){
        this.isFinished = isFinished;
    }

    public boolean getIsFinished(){
        return isFinished;
    }

}
