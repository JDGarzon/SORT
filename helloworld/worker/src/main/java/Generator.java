import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Generator {
    public static void main(String[] args) {
        // Solicitar al usuario la cantidad de elementos (n)
        Scanner scanner = new Scanner(System.in);
        int orden = 1000000;
        System.out.print("Ingrese la cantidad de elementos (n*" + orden + "): ");
        int n = scanner.nextInt();

        // Generar el nombre del archivo
        String nombreArchivo = "client\\src\\main\\java\\data\\caracteres_al_azar.txt";

        // Verificar si el archivo ya existe
        File archivo = new File(nombreArchivo);
        if (archivo.exists()) {
            System.out.println("El archivo ya existe. Se sobrescribirá.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            // Generar n caracteres aleatorios y escribirlos en el archivo
            Random random = new Random();
            for (int j = 0; j < n * orden; j++) {
                for (int i = 0; i < random.nextInt(100) + 1; i++) {
                    char caracter = (char) (random.nextInt(26) + 'a'); // Caracteres aleatorios minúsculos
                    writer.write(caracter);
                }
                if (j != n * orden - 1) {
                    writer.write("\n");
                }
            }
            System.out.println("Archivo generado exitosamente: " + nombreArchivo);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
