import com.zeroc.Ice.Current;

import sorter.ExternalSorter;

public class Sorter implements Demo.Sorter {

    ExternalSorter sorter;

    public Sorter() {
        super();
        sorter = new ExternalSorter();
    }

    @Override
    public String sort(String inputFile, String outputFile, Current current) {
        try {
            sorter.sort("server\\src\\main\\java\\data\\" + inputFile, "server\\src\\main\\java\\data\\" + outputFile);
        } catch (Exception e) {
            e.printStackTrace();
            return "Archivo no ordenado";
        }
        return "Archivo ordenado exitosamente";
    }

}
