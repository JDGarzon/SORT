module Demo
{
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

    interface Sorter
    {
        string sort(string inputFile, string outputFile);
        void launchWorkers(int n);
    }

    interface Worker{
    
    }

    interface Broker{
        void registerWorker(Worker* worker,CallbackReceiver* callbackReceiver);
        void unregisterWorker(Worker* worker);
        void registerSorter(Sorter* sorter,CallbackReceiver* callbackReceiver);
        void unregisterSorter(Sorter* sorter);
        Worker* getWorker();
    }

    
    enum State{
        FREE,
        BUSY
    }
}
    