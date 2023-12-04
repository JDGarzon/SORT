import java.util.ArrayList;

import com.zeroc.Ice.Current;

import Demo.MasterPrx;

public class WorkerI implements Demo.Worker {

    MasterPrx master;

    @Override
    public void shutdown(Current current) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'shutdown'");
    }

    @Override
    public void getData(ArrayList g, Current current) {
        ArrayList<String> list = (ArrayList<String>) g;
        for (String s : list) {
            System.out.println(s);
        }
    }

    @Override
    public void setMaster(MasterPrx master, Current current) {
        this.master = master;
        System.out.println("Master seteado" + master);
    }

}
