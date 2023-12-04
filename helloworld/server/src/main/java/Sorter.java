import com.zeroc.Ice.Current;

import Demo.BrokerPrx;
import Demo.Data;
import Demo.WorkerPrx;
import sorter.ExternalSorter;
import java.util.ArrayList;

public class Sorter implements Demo.Sorter {

    ArrayList<WorkerPrx> workers = new ArrayList<>();

    ExternalSorter sorter;

    public Sorter() {
        super();
        sorter = new ExternalSorter();
    }

    @Override
    public String sort(String inputFile, String outputFile, Current current) {
        try {
            sorter.sort("server\\src\\main\\java\\data\\" + inputFile, "server\\src\\main\\java\\data\\" + outputFile);
        } catch (Exception e) {
            e.printStackTrace();
            return "Archivo no ordenado";
        }
        return "Archivo ordenado exitosamente";
    }

    @Override
    public void launchWorkers(int n, Current current) {
        BrokerPrx broker = Server.getBroker();
        for (int i = 0; i < n; i++) {
            WorkerPrx worker = broker.getWorker();
            Data data = new Data();
            data.data = new ArrayList<>();
            ArrayList<String> list = data.data;
            list.add("1");
            list.add("2");
            list.add("3");
            list.add("4");
            list.add("5");
            worker.getData(data);
            System.out.println("" + worker);
            if (worker != null) {
                workers.add(worker);
            }
        }
        System.out.println("" + workers.size());
    }

}
