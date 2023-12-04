import java.util.ArrayList;

import com.zeroc.Ice.Current;

import Demo.MasterPrx;
import Demo.Task;

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

    @Override
    public void execute(Current current) {
        while (true) {
            if (master != null) {
                Task task = master.getTask();
                if (task != null) {
                    System.out.println("Tarea recibida");
                } else {
                    System.out.println("Tarea nula");
                }
            }
        }
    }

}
