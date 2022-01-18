import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
 * Autor - Michał Kuśmidrowicz 244021
 * Zadanie 2 - Program na ocenę bardzo dobrą
 */

public class Main {

    public static class StateValuePair extends AbstractMap.SimpleEntry<Integer, String> {
        public StateValuePair(Integer key, String value) {
            super(key, value);
        }
    }

    /* Tablica przejść
     *
     * Pierwszy indeks tablicy to indeks obecnego stanu - np. indeks 0 odpowiada stanowi q0
     *
     * Drugi indeks tablicy jest powiązany z konkretnym znakiem alfabetu w następujący sposób:
     * indeks 0 - znak a
     * indeks 1 - znak b
     * indeks 2 - znak c
     * indeks 3 - znak 0
     * indeks 4 - znak 1
     * indeks 5 - znak 2
     * indeks 6 - znak 3
     * indeks 7 - znak ε
     *
     * Zawartość tablicy przejść to indeksy kolejnych stanów - np. wartość 6 odpowiada stanowi q6
     */
    private final int[][][] transitionArray = new int[][][]{
            {{}, {}, {}, {}, {}, {}, {}, {1, 2}},
            {{1, 3}, {1, 4}, {1, 5}, {}, {}, {}, {}, {}},
            {{}, {}, {}, {2, 10}, {2, 11}, {2, 12}, {2, 13}, {}},
            {{6}, {}, {}, {}, {}, {}, {}, {}},
            {{}, {7}, {}, {}, {}, {}, {}, {}},
            {{}, {}, {8}, {}, {}, {}, {}, {}},
            {{9}, {}, {}, {}, {}, {}, {}, {}},
            {{}, {9}, {}, {}, {}, {}, {}, {}},
            {{}, {}, {9}, {}, {}, {}, {}, {}},
            {{9}, {9}, {9}, {}, {}, {}, {}, {}},
            {{}, {}, {}, {14}, {}, {}, {}, {}},
            {{}, {}, {}, {}, {15}, {}, {}, {}},
            {{}, {}, {}, {}, {}, {16}, {}, {}},
            {{}, {}, {}, {}, {}, {}, {17}, {}},
            {{}, {}, {}, {18}, {}, {}, {}, {}},
            {{}, {}, {}, {}, {18}, {}, {}, {}},
            {{}, {}, {}, {}, {}, {18}, {}, {}},
            {{}, {}, {}, {}, {}, {}, {18}, {}},
            {{}, {}, {}, {18}, {18}, {18}, {18}, {}},
    };

    // Lista stanów akceptujących NFA
    private final List<Integer> acceptingStates = List.of(9, 18);

    // Lista zawierająca ścieżki jakie pokonał NFA - listy stanów wraz z kolejnymi elementami łancucha znaków
    private final List<List<StateValuePair>> statesPaths = new ArrayList<>();
    // Lista zawierająca ścieżkę prowadzącą do stanu akceptującego - listę stanów wraz z kolejnymi elementami
    // łancucha znaków
    private List<StateValuePair> successfulPath;

    /*
     * Funkcja zwracająca drugi indeks tablicy przejść z wartości znaku alfabetu.
     * Indeks ten jest powiązany z konkretnym znakiem alfabetu w następujący sposób:
     * indeks 0 - znak a
     * indeks 1 - znak b
     * indeks 2 - znak c
     * indeks 3 - znak 0
     * indeks 4 - znak 1
     * indeks 5 - znak 2
     * indeks 6 - znak 3
     * indeks 7 - znak ε
     */
    private int getArrayIndexFromAlphabetValue(String alphabetValue) {
        switch (alphabetValue) {
            case "a":
                return 0;
            case "b":
                return 1;
            case "c":
                return 2;
            case "0":
                return 3;
            case "1":
                return 4;
            case "2":
                return 5;
            case "3":
                return 6;
            case "ε":
                return 7;
            default:
                throw new IllegalArgumentException("Provided alphabet value is not included in the alphabet set! Accepted values - a, b, c, 0, 1, 2, 3, ε");
        }
    }

    // Funkcja zwracająca nowe stany na podstawie tablicy przejść, starego stanu i kolejnego znaku alfabetu
    private int[] getNewStates(int oldState, String alphabetValue) {
        int alphabetIndex = getArrayIndexFromAlphabetValue(alphabetValue);
        return transitionArray[oldState][alphabetIndex];
    }

    // Funkcja wyświetlająca szczegóły dot. obecnego stanu
    private void displayState(int state, String alphabetValue) {
        System.out.println("Current state: q" + state + ", next alphabet value: " + alphabetValue);
    }

    // Funkcja wyświetlająca ścieżki jakie pokonał NFA
    private void displayAllTraversedPaths() {
        for (int i = 0; i < statesPaths.size(); i++) {
            displayTraversedPath(i);
        }
    }

    // Funkcja wyświetlająca szczegóły dot. jednej ze ścieżek jakie pokonał NFA
    private void displayTraversedPath(int index) {
        List<StateValuePair> statesPath = statesPaths.get(index);
        System.out.print(index + 1 + ". " + "Traversed path consists of the following states: ");
        displayStateValuePath(statesPath);
        int lastState = statesPath.get(statesPath.size() - 1).getKey();
        if (acceptingStates.contains(lastState)) {
            System.out.println("Last state is in accepting state - a program is successfully finished");
            successfulPath = statesPath;
            return;
        }
        System.out.println("Last state is not in accepting state - a path does not lead to the solution");
    }

    // Funkcja wyświetlająca jedną ze ścieżek jakie pokonał NFA
    private void displayStateValuePath(List<StateValuePair> statesPath) {
        StringBuilder msg = new StringBuilder();
        for (StateValuePair stateValuePair : statesPath) {
            msg.append("q").append(stateValuePair.getKey()).append("[").append(stateValuePair.getValue()).append("], ");
        }
        System.out.println(msg);
    }

    // Funkcja wyświetlająca ostateczne rezultaty działania programu
    private void displayResults() {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        if (Objects.isNull(successfulPath)) {
            System.out.println("Program did not finish successfully - there is no successful path leading to the accepting state");
        } else {
            System.out.print("Program finished successfully - the successful path leading to the accepting state consists of the following states: ");
            displayStateValuePath(successfulPath);
        }
        IntStream.range(1, 4).forEach(i -> System.out.println("-----------------------------"));
    }

    /*
     * Funkcją przetwarzająca cały ciąg znaków.
     *
     * Jako argument przyjmuje kolejne znaki alfabetu - słowa wejściowe muszą być niemieszane - albo same cyfry albo litery.
     *
     * Przy użyciu tablicy przejść wypełnia przebyte przez NFA ścieżki oraz wyświetla informację dot.
     * kolejnych stanów i przebytych ścieżek.
     *
     */
    public void processWholeCharacterChain(List<String> characterChain) {
        statesPaths.clear();
        successfulPath = null;
        characterChain.add(0, "ε");
        List<StateValuePair> statesPath = new ArrayList<>(List.of(new StateValuePair(0, "ε")));
        statesPaths.add(statesPath);
        processCharacterChain(characterChain, 0, 0, statesPath);
        displayAllTraversedPaths();
        displayResults();
    }

    /*
     * Funkcją przetwarzająca ciąg znaków.
     * Jako argument przyjmuje kolejne znaki alfabetu - słowa wejściowe muszą być niemieszane - albo same cyfry albo litery.
     */
    private void processCharacterChain(List<String> characterChain, int currentCharacterChainIndex, int currentState, List<StateValuePair> statesPath) {
        String currentCharacterChainValue = characterChain.get(currentCharacterChainIndex);
        displayState(currentState, currentCharacterChainValue);
        int[] nextStates = getNewStates(currentState, currentCharacterChainValue);
        List<StateValuePair> previousStatesPath = new ArrayList<>(statesPath);
        for (int i = 0; i < nextStates.length; i++) {
            if (i == 0) {
                // dodawanie pierwszego nowego stanu do przekazanej ścieżki
                processPath(characterChain, currentCharacterChainIndex, nextStates[i], statesPath);
                continue;
            }
            // dodawanie kolejnych nowych stanów do nowej ścieżki stworzonej na podstawie przekazanej ścieżki
            List<StateValuePair> newStatesPath = new ArrayList<>(previousStatesPath);
            statesPaths.add(newStatesPath);
            processPath(characterChain, currentCharacterChainIndex, nextStates[i], newStatesPath);
        }
    }

    /*
     * Funkcja przetwarzająca ścieżkę NFA.
     */
    private void processPath(List<String> characterChain, int currentCharacterChainIndex, int nextState, List<StateValuePair> statesPath) {
        // koniec zczytywania łańcucha znaków
        if (currentCharacterChainIndex == characterChain.size() - 1) {
            statesPath.add(new StateValuePair(nextState, ""));
            return;
        }
        statesPath.add(new StateValuePair(nextState, characterChain.get(++currentCharacterChainIndex)));
        processCharacterChain(characterChain, currentCharacterChainIndex, nextState, statesPath);
    }

    /*
     * Funkcją zczytująca plik wejściowy. Zwraca sparsowane ciągi znaków alfabetu.
     */
    private List<List<String>> parseFile(String fileName) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        String content = readFromInputStream(inputStream);
        return Arrays.stream(content.split("#"))
                .map(chain -> new ArrayList<>(Arrays.asList(chain.split("(?!^)")))).collect(Collectors.toList());
    }

    private String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line);
            }
        }
        return resultStringBuilder.toString();
    }

    public static void main(String... args) throws IOException {
        Main main = new Main();
        // ładowanie i przetwarzanie predefiniowanych łańcuchów
        System.out.println("PROCESSING PREDEFINED CHAINS:");
        main.processWholeCharacterChain(new ArrayList<>(List.of("a", "b", "b", "b", "a", "a", "c")));
        main.processWholeCharacterChain(new ArrayList<>(List.of("1", "1", "1", "1", "0", "0", "2", "1", "2", "3")));
        main.processWholeCharacterChain(new ArrayList<>(List.of("2", "1", "3", "3", "1", "2")));
        // ładowanie i przetwarzanie łańcuchów z pliku
        System.out.println("PROCESSING CHAINS FROM FILE:");
        main.parseFile("input.txt").forEach(main::processWholeCharacterChain);
    }
}
