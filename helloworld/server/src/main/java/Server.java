import java.util.Hashtable;

import Demo.CallbackReceiverPrx;

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
            com.zeroc.Ice.Object callBackObject = new CallbackSenderI();
            callback.add(callBackObject, com.zeroc.Ice.Util.stringToIdentity("CallbackSender"));
            com.zeroc.Ice.ObjectAdapter sorter = communicator.createObjectAdapter("Sorter.Server");
            com.zeroc.Ice.Object sorterObject = new Sorter();
            sorter.add(sorterObject, com.zeroc.Ice.Util.stringToIdentity("Sorter"));
            adapter.activate();
            callback.activate();
            sorter.activate();
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