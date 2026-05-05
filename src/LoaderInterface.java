/**
 * Interfejs, który definiuje, jak ma wyglądać każdy loader grafu.
 */
public interface LoaderInterface {

    /**
     * Każda klasa implementująca ten interfejs musi mieć metodę load,
     * która przyjmuje ścieżkę do pliku i zwraca wypełniony obiekt Graph.
     */
    Graph load(String filePath);
}