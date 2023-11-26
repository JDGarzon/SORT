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
    }

 

}
    