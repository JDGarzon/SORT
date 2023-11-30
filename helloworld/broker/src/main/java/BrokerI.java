import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import com.zeroc.Ice.Current;

import Demo.CallbackReceiverPrx;
import Demo.SorterPrx;
import Demo.WorkerPrx;
import Demo.Broker;

public class BrokerI implements Broker {
    private static int requestCount = 0;
    private static int responsesCount = 0;
    protected static Hashtable<String, CallbackReceiverPrx> users = new Hashtable<>();
    private Map<String, Pair<WorkerPrx, CallbackReceiverPrx>> workers = new Hashtable<>();
    private Pair<SorterPrx, CallbackReceiverPrx> sorter = null;

    public static void showStatistics() {
        System.out.println("Numero de solicitudes:" + requestCount);
        System.out.println("Numero de respuestas:" + responsesCount);
    }

    public static synchronized void incrementRequestCount() {
        requestCount++;
    }

    // Método para obtener el contador de solicitudes
    public static synchronized int getRequestCount() {
        return requestCount;
    }

    public static synchronized void incrementResponsesCount() {
        responsesCount++;
    }

    // Método para obtener el contador de solicitudes
    public static synchronized int getResponsesCount() {
        return responsesCount;
    }

    public static void registerUser(String hostName, CallbackReceiverPrx proxi) {
        users.put(hostName, proxi);
    }

    public static CallbackReceiverPrx getUserEndpoint(String hostName) {
        return users.get(hostName);
    }

    @Override
    public void registerWorker(WorkerPrx worker, CallbackReceiverPrx callbackReceiver, Current current) {
        Pair<WorkerPrx, CallbackReceiverPrx> pair = new Pair<>(worker, callbackReceiver);
        int id = workers.size();
        workers.put("" + id, pair);
        System.out.println("Worker registrado");
    }

    @Override
    public void unregisterWorker(WorkerPrx worker, Current current) {
        Set<String> keys = workers.keySet();
        for (String key : keys) {
            Pair<WorkerPrx, CallbackReceiverPrx> pair = workers.get(key);
            if (pair.getFirst().equals(worker)) {
                workers.remove(key);
                break;
            }
        }
    }

    @Override
    public void registerSorter(SorterPrx sorter, CallbackReceiverPrx callbackReceiver, Current current) {

        this.sorter = new Pair<>(sorter, callbackReceiver);
        System.out.println("Sorter registrado");
    }

    @Override
    public void unregisterSorter(SorterPrx sorter, Current current) {
        if (this.sorter.getFirst().equals(sorter)) {
            this.sorter = null;
        }
    }

}