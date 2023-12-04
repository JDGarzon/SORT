import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class PrinterI implements Demo.Printer {
    Long timeStart;
    Long timeEnd;

    public synchronized String printString(String s, com.zeroc.Ice.Current current) {
        Worker.incrementRequestCount();
        timeStart = System.nanoTime();
        String toPrint = execute(s);
        timeEnd = System.nanoTime();
        Long timeToResonse = timeEnd - timeStart;
        double latencia = (double) timeToResonse / 10_000_000;
        toPrint += "\n Latencia:" + latencia;

        return toPrint;
    }

    public static String execute(String s) {
        String toPrint = "";
        System.out.println(s);
        if (s.contains("listifs")) {
            toPrint = logicInterfaces();

            System.out.println(toPrint);
        } else if (s.contains("listports ")) {
            String[] ipAddress = s.split(" ");
            toPrint = getOpenPortsInfo(ipAddress[1]);
            System.out.println(toPrint);
        } else if (s.contains("!")) {
            String[] command = s.split("!");
            toPrint = executeSystemCommand(command[1]);
            System.out.println(toPrint);
        } else {
            try {
                String[] number = s.split(":");
                int n = Integer.parseInt(number[1]);
                List<Integer> list = calculatePrimeFactors(n);
                String numString = "";
                for (int num : list) {
                    numString += num + ",";
                }
                toPrint = numString.substring(0, numString.length() - 1);
                System.out.println(toPrint);
            } catch (Exception e) {

            }

        }
        return toPrint;
    }

    public static List<Integer> calculatePrimeFactors(int numero) {
        List<Integer> factoresPrimos = new ArrayList<>();

        while (numero % 2 == 0) {
            factoresPrimos.add(2);
            numero /= 2;
        }

        for (int i = 3; i <= Math.sqrt(numero); i += 2) {
            while (numero % i == 0) {
                factoresPrimos.add(i);
                numero /= i;
            }
        }

        if (numero > 1) {
            factoresPrimos.add(numero);
        }

        return factoresPrimos;
    }

    public static String logicInterfaces() {
        String response = "Interfaces lógicas configuradas:\n";
        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                response += "Interfaz: " + iface.getName() + "\n";
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return response;
    }

    public static String getOpenPortsInfo(String ipAddress) {
        System.out.println("Escaneando puertos abiertos en " + ipAddress + "...\n");

        String nmapCommand = "nmap -p 1-65535 " + ipAddress;

        try {
            Process process = Runtime.getRuntime().exec(nmapCommand);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            boolean flag = false;
            String ports = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Nmap", 0)) {
                    flag = false;
                }
                if (flag) {
                    ports += line + "\n";
                }
                if (line.startsWith("PORT", 0)) {
                    flag = true;
                }

            }

            process.waitFor();
            return ports;
        } catch (IOException | InterruptedException e) {
            return "ip no valida";
        }
    }

    public static String executeSystemCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            StringBuilder output = new StringBuilder();

            while ((line = processReader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();

            return output.toString();
        } catch (IOException | InterruptedException e) {
            return "Error al ejecutar el comando";
        }
    }

    public List<String> hostNames() {
        List<String> hostnames = new ArrayList<>();
        for (String hostname : Worker.users.keySet()) {
            System.out.println(hostname);
            hostnames.add(hostname);
        }
        return hostnames;
    }

    public String toX(String hostname, String message, com.zeroc.Ice.Current current) {

        return null;
    }
}