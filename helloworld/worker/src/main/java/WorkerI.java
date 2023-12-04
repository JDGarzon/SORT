import com.zeroc.Ice.Current;

import Demo.Data;

public class WorkerI implements Demo.Worker {

    @Override
    public void getData(Data g, Current current) {
        for (String s : g.data) {
            System.out.println(s);
        }
    }

    @Override
    public void shutdown(Current current) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'shutdown'");
    }

}
