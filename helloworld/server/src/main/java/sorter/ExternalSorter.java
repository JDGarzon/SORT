package sorter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.*;

public class ExternalSorter {

    private static int blockSize = 1000;
    static Long timeStart;
    static Long timeEnd;

    public static void sort(String inputFile, String outputFile) {
        try {
            splitAndSort(inputFile);
            mergeSortedBlocks(outputFile);
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void splitAndSort(String inputFile) throws IOException, InterruptedException, ExecutionException {
        Path inputPath = FileSystems.getDefault().getPath(inputFile);

        try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)) {
            int blockNumber = 0;
            String line;

            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            List<Future<Void>> futures = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                final int[] bytesRead = { 1 }; // Using an array to make it effectively final

                String[] block = new String[blockSize];
                block[0] = line;

                while (bytesRead[0] < blockSize && (line = reader.readLine()) != null) {
                    block[bytesRead[0]++] = line;
                }

                int finalBlockNumber = blockNumber;
                Callable<Void> sortTask = () -> {
                    Arrays.sort(block, 0, bytesRead[0]);
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter("block" + finalBlockNumber))) {
                        for (int i = 0; i < bytesRead[0]; i++) {
                            writer.write(block[i]);
                            writer.newLine();
                        }
                        return null;
                    }

                };

                futures.add(executor.submit(sortTask));
                blockNumber++;
            }

            // Esperar a que todas las tareas de ordenación terminen
            for (Future<Void> future : futures) {
                future.get();
            }

            executor.shutdown();
        }
    }

    private static void mergeSortedBlocks(String outputFile) throws IOException {
        int totalBlocks = 0;
        while (new File("block" + totalBlocks).exists()) {
            totalBlocks++;
        }

        BufferedReader[] blockReaders = new BufferedReader[totalBlocks];
        for (int i = 0; i < totalBlocks; i++) {
            blockReaders[i] = new BufferedReader(new FileReader("block" + i));
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            PriorityQueue<String> priorityQueue = new PriorityQueue<>(totalBlocks, (a, b) -> a.compareTo(b));

            // Inicializar la cola de prioridad con el primer elemento de cada bloque
            for (int i = 0; i < totalBlocks; i++) {
                String line = blockReaders[i].readLine();
                if (line != null) {
                    priorityQueue.add(line + "," + i); // También almacenar el índice del bloque
                }
            }

            // Fusionar los bloques hasta que la cola de prioridad esté vacía
            while (!priorityQueue.isEmpty()) {
                String[] data = priorityQueue.poll().split(",");
                int blockIndex = Integer.parseInt(data[1]);

                writer.write(data[0]);
                writer.newLine();

                String line = blockReaders[blockIndex].readLine();
                if (line != null) {
                    priorityQueue.add(line + "," + blockIndex);
                }
            }
        }

        // Cerrar los lectores y eliminar los archivos temporales
        for (int i = 0; i < totalBlocks; i++) {
            blockReaders[i].close();
            new File("block" + i).delete();
        }
    }

}
