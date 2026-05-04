public class Main {
    public static void main(String[] args) {
        TextLoader loader = new TextLoader();
        // Podaj ścieżkę do pliku wygenerowanego w C
        Graph g = loader.load("test_grafu.txt"); 
        
        System.out.println("Wczytano węzłów: " + g.getNodes().size());
    }
}