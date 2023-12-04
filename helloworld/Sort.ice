module Demo
{

    ["java:serializable:java.util.ArrayList"] sequence<byte> Data;

    class Task{
        Data data;
        string id;
    }

    interface Printer
    {
        string printString(string s);
    }

    interface CallbackReceiver
    {
        void callback(string response);
    }

    interface CallbackSender
    {
        void initiateCallback(CallbackReceiver* proxy,string request);
        void shutdown();
    }

    interface Master
    {
        string sort(string inputFile, string outputFile);
        void launchWorkers(int n);
        idempotent Task getTask();
        idempotent void addPartialResult(Task d);

    }

    interface Worker{
        void execute();
        idempotent void getData(Data g);
        void shutdown();
        void setMaster(Master* master);
        
    }

    interface Broker{
        void registerWorker(Worker* worker,CallbackReceiver* callbackReceiver);
        void unregisterWorker(Worker* worker);
        void registerMaster(Master* master,CallbackReceiver* callbackReceiver);
        void unregisterMaster(Master* master);
        Worker* getWorker();
        Master* getMaster();
    }
    
    enum State{
        FREE,
        BUSY
    }

    

}
    