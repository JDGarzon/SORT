//
// Copyright (c) ZeroC, Inc. All rights reserved.
//
import java.util.Set;
import java.util.regex.*;

import Demo.CallbackReceiverPrx;

public  class CallbackSenderI implements Demo.CallbackSender
{
    @Override
    public void initiateCallback(CallbackReceiverPrx proxy,String request, com.zeroc.Ice.Current current)
    {
        String toX = "to .*:"; 
        String listClients = "^list clients.*";
        String bc = "^bc .*";
        Pattern patternToX = Pattern.compile(toX);
        Pattern patternListClients = Pattern.compile(listClients);
        Pattern patternBc = Pattern.compile(bc);
        try
        {
            String[] partition=request.split(":");
            String hostname=partition[0].split("@")[1];
            BrokerI.registerUser(hostname,proxy);
            String response = partition[1];
            if(partition.length==3){
                response+=":"+partition[2];
            }
            System.out.println(response);
            if((patternToX.matcher(response).find())){
                proxy=BrokerI.users.get(hostname);
                response=partition[2];
                proxy.callback(response);
            }else if((patternListClients.matcher(response).find())){
                response=hostNames();
                proxy.callback(response);
            }else if((patternBc.matcher(response).find())){

                response=partition[1].split(" ")[1];
                Set<String> keys = BrokerI.users.keySet();
                for(String key: keys){
                    proxy=BrokerI.users.get(key);
                    proxy.callback(response);
                }

            }else{
                
                proxy.callback(response);
            }
            
        }
        catch(com.zeroc.Ice.LocalException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void shutdown(com.zeroc.Ice.Current current)
    {
        System.out.println("Shutting down...");
        try
        {
            current.adapter.getCommunicator().shutdown();
        }
        catch(com.zeroc.Ice.LocalException ex)
        {
            ex.printStackTrace();
        }
    }

    public String hostNames(){
        String out="";
        for (String hostname : BrokerI.users.keySet()) {
            out+=hostname+"\n";
        }
        return out;
    }

    
}
