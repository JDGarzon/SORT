import com.zeroc.Ice.Current;

import Demo.BrokerPrx;
import Demo.MasterPrx;
import Demo.Task;
import Demo.WorkerPrx;
import sorter.ExternalSorter;
import java.util.ArrayList;

public class Master implements Demo.Master {

    com.zeroc.Ice.ObjectAdapter adapter;

    ArrayList<WorkerPrx> workers = new ArrayList<>();

    ExternalSorter sorter;

    public void setAdapter(com.zeroc.Ice.ObjectAdapter adapter) {
        this.adapter = adapter;
    }

    public Master() {
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
        return null;
    }

    @Override
    public void addPartialResult(Task d, Current current) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addPartialResult'");
    }

}
