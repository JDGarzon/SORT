import java.util.Hashtable;

import Demo.BrokerPrx;
import Demo.CallbackReceiverPrx;
import Demo.CallbackSenderPrx;
import Demo.SorterPrx;

public class Server {
    private static int requestCount = 0;
    private static int responsesCount = 0;
    protected static Hashtable<String, CallbackReceiverPrx> users = new Hashtable<>();

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
            callback.add(callBackObject, com.zeroc.Ice.Util.stringToIdentity("CallbackReceiver"));
            com.zeroc.Ice.ObjectAdapter sorter = communicator.createObjectAdapter("Sorter.Server");

            BrokerPrx broker = Demo.BrokerPrx.checkedCast(
                    communicator.propertyToProxy("Broker.Proxy")).ice_twoway().ice_secure(false);

            com.zeroc.Ice.Object sorterObject = new Sorter();
            sorter.add(sorterObject, com.zeroc.Ice.Util.stringToIdentity("Sorter"));
            CallbackSenderPrx sender = Demo.CallbackSenderPrx.checkedCast(
                    communicator.propertyToProxy("CallbackSender.Proxy")).ice_twoway().ice_secure(false);
            if (sender == null) {
                throw new Error("Invalid proxy");
            }
            CallbackReceiverPrx receiver = CallbackReceiverPrx.uncheckedCast(adapter.createProxy(
                    com.zeroc.Ice.Util.stringToIdentity("callbackReceiver")));
            SorterPrx sorterPrx = SorterPrx.uncheckedCast(adapter.createProxy(
                    com.zeroc.Ice.Util.stringToIdentity("Sorter")));
            adapter.activate();
            callback.activate();
            sorter.activate();
            broker.registerSorter(sorterPrx, receiver);

            communicator.waitForShutdown();
        }
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

}