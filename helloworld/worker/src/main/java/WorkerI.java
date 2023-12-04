import java.util.ArrayList;

import com.zeroc.Ice.Current;

public class WorkerI implements Demo.Worker {

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

}
