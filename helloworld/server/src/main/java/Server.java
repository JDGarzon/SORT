import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import Demo.BrokerPrx;
import Demo.CallbackReceiverPrx;
import Demo.CallbackSenderPrx;
import Demo.SorterPrx;

public class Server {
    private static int requestCount = 0;
    private static int responsesCount = 0;
    protected static Hashtable<String, CallbackReceiverPrx> users = new Hashtable<>();
    static BrokerPrx broker;
    static SorterPrx sorter;

    public static void main(String[] args) {
        java.util.List<String> extraArgs = new java.util.ArrayList<String>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "config.server",
                extraArgs)) {
            communicator.getProperties().setProperty("Ice.Default.Package", "com.zeroc.demos.Ice.callback");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> communicator.destroy()));
            if (!extraArgs.isEmpty()) {
                System.err.println("too many arguments");
                for (String v : extraArgs) {
                    System.out.println(v);
                }
            }
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Printer");
            com.zeroc.Ice.Object object = new PrinterI();
            adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("SimplePrinter"));
            com.zeroc.Ice.ObjectAdapter callback = communicator.createObjectAdapter("Callback.Server");
            com.zeroc.Ice.Object callBackObject = new CallbackReceiverI();
            adapter.add(callBackObject, com.zeroc.Ice.Util.stringToIdentity("CallbackReceiver"));
            com.zeroc.Ice.ObjectAdapter sorterAdapter = communicator.createObjectAdapter("Sorter.Server");

            BrokerPrx broker = Demo.BrokerPrx.checkedCast(
                    communicator.propertyToProxy("Broker.Proxy")).ice_twoway().ice_secure(false);
            setBroker(broker);
            com.zeroc.Ice.Object sorterObject = new Sorter();
            adapter.add(sorterObject, com.zeroc.Ice.Util.stringToIdentity("Sorter"));
            CallbackSenderPrx sender = Demo.CallbackSenderPrx.checkedCast(
                    communicator.propertyToProxy("CallbackSender.Proxy")).ice_twoway().ice_secure(false);
            if (sender == null) {
                throw new Error("Invalid proxy");
            }
            CallbackReceiverPrx receiver = CallbackReceiverPrx.uncheckedCast(adapter.createProxy(
                    com.zeroc.Ice.Util.stringToIdentity("callbackReceiver")));
            SorterPrx sorterPrx = SorterPrx.uncheckedCast(adapter.createProxy(
                    com.zeroc.Ice.Util.stringToIdentity("Sorter")));

            setSorter(sorterPrx);
            adapter.activate();
            broker.registerSorter(sorterPrx, receiver);
            launchWorkers();
            communicator.waitForShutdown();

        }
    }

    public static void launchWorkers() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Ingrese el numero de workers a lanzar");
        int n = 0;
        try {
            String input = reader.readLine();
            n = Integer.parseInt(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sorter.launchWorkers(n);
        System.out.println("Workers lanzados");
    }

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

    public static void setBroker(BrokerPrx brokerPrx) {
        broker = brokerPrx;
    }

    public static BrokerPrx getBroker() {
        return broker;
    }

    public static void setSorter(SorterPrx sorterPrx) {
        sorter = sorterPrx;
    }

}