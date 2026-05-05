import javax.swing.JFileChooser;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        // 1. Tworzymy okno wyboru pliku
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Wybierz plik z danymi grafu (z projektu w C)");

        // 2. Pokazujemy okno użytkownikowi
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();

            // 3. Uruchamiamy Twój loader
            TextLoader loader = new TextLoader();
            Graph graph = loader.load(path);

            System.out.println("Sukces! Wczytano " + graph.getNodes().size() + " węzłów.");
        } else {
            System.out.println("Anulowano wybór pliku.");
        }
    }
}