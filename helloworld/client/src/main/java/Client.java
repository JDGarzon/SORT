import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

import Demo.CallbackReceiverPrx;
import Demo.CallbackSenderPrx;
import Demo.SorterPrx;

public class Client {
    static Long timeStart;
    static Long timeEnd;
    static Long solitudes = 0L;
    static Long responses = 0L;

    public static void main(String[] args) {
        java.util.List<String> extraArgs = new java.util.ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "config.client",
                extraArgs)) {
            // com.zeroc.Ice.ObjectPrx base =
            // communicator.stringToProxy("SimplePrinter:default -p 10000");
            communicator.getProperties().setProperty("Ice.Default.Package", "com.zeroc.demos.Ice.callback");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> communicator.destroy()));
            /*
             * Demo.PrinterPrx twoway = Demo.PrinterPrx.checkedCast(
             * communicator.propertyToProxy("Printer.Proxy")).ice_twoway().ice_secure(false)
             * ;
             * 
             * // Demo.PrinterPrx printer = Demo.PrinterPrx.checkedCast(base);
             * Demo.PrinterPrx printer = twoway.ice_twoway();
             * if (printer == null) {
             * throw new Error("Invalid proxy");
             * }
             */

            CallbackSenderPrx sender = Demo.CallbackSenderPrx.checkedCast(
                    communicator.propertyToProxy("CallbackSender.Proxy")).ice_twoway().ice_secure(false);
            if (sender == null) {
                throw new Error("Invalid proxy");
            }
            String toPrint = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Callback.Client");
            adapter.add(new CallbackReceiverI(), com.zeroc.Ice.Util.stringToIdentity("callbackReceiver"));
            adapter.activate();

            CallbackReceiverPrx receiver = CallbackReceiverPrx.uncheckedCast(adapter.createProxy(
                    com.zeroc.Ice.Util.stringToIdentity("callbackReceiver")));
            communicator.getProperties().setProperty("Ice.Default.Package", "com.zeroc.demos.Ice.sorter");
            com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy("Sorter:default -p 10001");

            Demo.SorterPrx twoway = Demo.SorterPrx.checkedCast(base);

            SorterPrx sorter = twoway.ice_twoway();
            if (sorter == null) {
                throw new Error("Invalid proxy");
            }

            while (!toPrint.equalsIgnoreCase("exit")) {

                try {
                    // InetAddress localHost = InetAddress.getLocalHost();
                    // String hostname = localHost.getHostName();

                    // String username = System.getProperty("user.name");
                    System.out.println("Ingrese el nombre del archivo a ordenar");
                    String fileName = reader.readLine();
                    System.out.println("Ingrese el nombre del archivo de salida");
                    String outputFile = reader.readLine();
                    toPrint = "reader.readLine()";
                    timeStart = System.nanoTime();
                    solitudes++;
                    // String prompt = username + "@" + hostname + ":" + toPrint;
                    String response = sorter.sort(fileName, outputFile);
                    System.out.println(response);
                    /*
                     * String response = printer.printString(prompt);
                     * 
                     * timeEnd = System.nanoTime();
                     * if (response != null) {
                     * responses++;
                     * }
                     * Long timeToResonse = timeEnd - timeStart;
                     * double latencia = (double) timeToResonse / 10_000_000;
                     * System.out.println(response + "\n" + "Latencia final:" + latencia +
                     * "\n" + "Solicitudes:" + solitudes + "\n" +
                     * "Respuestas:" + responses);
                     */
                } catch (IOException e) {

                    e.printStackTrace();
                }

            }
            sender.shutdown();
        }
    }

}