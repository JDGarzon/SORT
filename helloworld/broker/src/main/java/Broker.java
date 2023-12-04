public class Broker {

    public static void main(String[] args) {
        java.util.List<String> extraArgs = new java.util.ArrayList<String>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "config.broker",
                extraArgs)) {
            communicator.getProperties().setProperty("Ice.Default.Package", "com.zeroc.demos.Ice.callback");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> communicator.destroy()));
            if (!extraArgs.isEmpty()) {
                System.err.println("too many arguments");
                for (String v : extraArgs) {
                    System.out.println(v);
                }
            }
            com.zeroc.Ice.ObjectAdapter callback = communicator.createObjectAdapter("Callback.Broker");
            com.zeroc.Ice.Object callBackObject = new CallbackSenderI();
            callback.add(callBackObject, com.zeroc.Ice.Util.stringToIdentity("CallbackSender"));
            callback.activate();
            com.zeroc.Ice.ObjectAdapter broker = communicator.createObjectAdapter("Broker");
            com.zeroc.Ice.Object brokerObject = new BrokerI();
            broker.add(brokerObject, com.zeroc.Ice.Util.stringToIdentity("Broker"));
            broker.activate();
            communicator.waitForShutdown();
        }
    }

}
