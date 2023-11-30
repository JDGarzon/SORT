import com.zeroc.Ice.Current;

import Demo.BrokerPrx;
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
            System.out.println("" + worker);
            if (worker != null) {
                workers.add(worker);
            }
        }
        System.out.println("" + workers.size());
    }

}
