import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/*
 * Autor - Michał Kuśmidrowicz 244021
 * Zadanie 3 - Program na ocenę dobrą
 */

public class Main {

    /* Tablica przejść
     *
     * Pierwszy indeks tablicy to indeks obecnego stanu - np. indeks 0 odpowiada stanowi q0
     *
     * Drugi indeks tablicy jest powiązany z konkretnym znakiem alfabetu w następujący sposób:
     * indeks 0 - znak 0
     * indeks 1 - znak 1
     * indeks 2 - znak θ
     */
    private final String[][][] transitionArray = new String[][][]{
            {{"3", "1", "L"}, {"2", "0", "L"}, {"-", "-", "-"}},
            {{"1", "0", "L"}, {"1", "1", "L"}, {"-", "-", "-"}},
            {{"4", "0", "L"}, {"4", "1", "L"}, {"-", "-", "-"}},
            {{"1", "1", "L"}, {"4", "0", "L"}, {"-", "-", "-"}},
            {{"1", "1", "L"}, {"4", "0", "L"}, {"1", "1", "-"}}
    };

    // Lista stanów akceptujących MT
    private final List<Integer> acceptingStates = List.of(1);

    // Lista zawierająca ścieżkę jaką pokonała MT
    private final List<Integer> statesPath = new ArrayList<>();

    /*
     * Funkcja zwracająca drugi indeks tablicy przejść z wartości znaku alfabetu.
     * Indeks ten jest powiązany z konkretnym znakiem alfabetu w następujący sposób:
     * indeks 0 - znak 0
     * indeks 1 - znak 1
     * indeks 2 - znak θ
     */
    private int getArrayIndexFromAlphabetValue(String alphabetValue) {
        switch (alphabetValue) {
            case "0":
                return 0;
            case "1":
                return 1;
            case "θ":
                return 2;
            default:
                throw new IllegalArgumentException("Provided alphabet value is not included in the alphabet set! Accepted values - 0, 1, θ");
        }
    }

    // Funkcja zwracająca nowe stany na podstawie tablicy przejść, starego stanu i kolejnego znaku alfabetu
    private String[] getNewStateValues(int oldState, String alphabetValue) {
        int alphabetIndex = getArrayIndexFromAlphabetValue(alphabetValue);
        return transitionArray[oldState][alphabetIndex];
    }

    // Funkcja wyświetlająca szczegóły dot. obecnego stanu
    private void displayState(int currentState, String alphabetValue, int nextState, String valueToPlace, String tapeMovementValue) {
        System.out.println("Current state: " + currentState + ", alphabet value: " + alphabetValue + ", next state: " + nextState + ", value to write: "
                + valueToPlace + ", tape movement: " + tapeMovementValue);
    }

    // Funkcja wyświetlająca finalną zawartość taśmy
    private void displayResultingTape(List<String> tape) {
        System.out.println("-----------------------------");
        System.out.print("Resulting tape value: ");
        tape.forEach(s -> {
            if (!s.equals("θ")) {
                System.out.print(s);
            }
        });
        System.out.println();
        System.out.println("-----------------------------");
    }

    // Funkcja wyświetlająca ścieżkę jaką pokonała MT
    private void displayPath() {
        System.out.println("-----------------------------");
        StringBuilder msg = new StringBuilder("Traversed path consists of the following states: ");
        for (int i = 0; i < statesPath.size() - 1; i++) {
            msg.append("q").append(statesPath.get(i)).append(", ");
        }
        int lastState = statesPath.get(statesPath.size() - 1);
        msg.append("q").append(lastState);
        System.out.println(msg);
        if (acceptingStates.contains(lastState)) {
            System.out.println("Last state is in accepting state - a program is successfully finished");
            return;
        }
        System.out.println("Last state is not in accepting state - a program is not successfully finished");
    }

    /*
     * Funkcją dodająca trzy do wielocyfrowej liczby binarnej.
     *
     * Jako argument przyjmuje wartość kolejne symbole taśmy - symbole analizowane są od prawej do lewej.
     *
     * Przy użyciu tablicy przejść wypełnia przebytą przez MT ścieżkę stanów oraz wyświetla informację dot.
     * kolejnych stanów
     *
     */
    public void processWholeTape(List<String> tape) {
        statesPath.clear();
        tape.add(0, "θ");
        System.out.print("Starting tape value: ");
        tape.forEach(System.out::print);
        System.out.println();
        System.out.println("-----------------------------");
        processTape(tape, tape.size() - 1, 0);
        displayResultingTape(tape);
        displayPath();
    }

    /*
     * Funkcją przetwarzająca ciąg znaków.
     * Jako argument przyjmuje kolejne znaki alfabetu - słowa wejściowe muszą być niemieszane - albo same cyfry albo litery.
     */
    private void processTape(List<String> tape, int currentTapeIndex, int currentState) {
        statesPath.add(currentState);
        String currentTapeValue = tape.get(currentTapeIndex);
        String[] nextStateValues = getNewStateValues(currentState, currentTapeValue);
        String valueToPlace = nextStateValues[1];
        if (!valueToPlace.equals("-")) {
            tape.set(currentTapeIndex, valueToPlace);
        }
        String tapeMovementValue = nextStateValues[2];
        currentTapeIndex = getNewTapeIndex(tapeMovementValue, currentTapeIndex);
        String nextStateValue = nextStateValues[0];
        int nextState = currentState;
        if (!nextStateValue.equals("-")) {
            nextState = Integer.parseInt(nextStateValue);
        }
        displayState(currentState, currentTapeValue, nextState, valueToPlace, tapeMovementValue);
        if (!currentTapeValue.equals("θ")) {
            processTape(tape, currentTapeIndex, nextState);
        } else {
            statesPath.add(nextState);
        }
    }

    // Funkcja zwracająca nową pozycję głowicy na bazie  obecnej pozycji i wartości przesunięcia głowicy
    private int getNewTapeIndex(String tapeMovementValue, int currentTapeIndex) {
        switch (tapeMovementValue) {
            case "L":
                return currentTapeIndex - 1;
            case "R":
                return currentTapeIndex + 1;
            case "-":
                return currentTapeIndex;
            default:
                throw new IllegalArgumentException("Provided tape movement value is improper! Accepted values - L, R, -");
        }
    }

    public static void main(String... args) {
        Main main = new Main();
        System.out.print("Input binary number (without θ): ");
        Scanner scanner = new Scanner(System.in);
        String stringValue = scanner.nextLine();
        List<String> tape = new ArrayList<>(Arrays.asList(stringValue.split("(?!^)")));
        main.processWholeTape(tape);
    }
}
