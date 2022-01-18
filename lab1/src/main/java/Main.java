import java.util.ArrayList;
import java.util.List;

/*
 * Autor - Michał Kuśmidrowicz 244021
 * Zadanie 1 - Program na ocenę dobrą
 */

public class Main {

    /* Tablica przejść
    *
    * Pierwszy indeks tablicy to indeks obecnego stanu - np. indeks 0 odpowiada stanowi q0
    *
    * Drugi indeks tablicy jest powiązany z wrzuconą monetą w następujący sposób:
    * indeks 0 - moneta 1
    * indeks 1 - moneta 2
    * indeks 2 - moneta 5
    *
    * Zawartość tablicy przejść to indeksy kolejnych stanów - np. wartość 6 odpowiada stanowi q6
    */
    private final int[][] transitionArray = new int[][]{
            {1, 2, 5},
            {2, 3, 6},
            {3, 4, 7},
            {4, 5, 8},
            {5, 6, 9},
            {6, 7, 10},
            {7, 8, 11},
            {8, 9, 12},
            {9, 10, 13},
            {10, 11, 14},
            {11, 12, 15},
            {12, 13, 16},
            {13, 14, 17},
            {14, 15, 18},
            {15, 16, 19},
            {16, 17, 20},
            {17, 18, 21},
            {18, 19, 22},
            {19, 20, 23},
            {20, 21, 24},
            {20, 21, 24},
            {20, 20, 20},
            {21, 21, 21},
            {22, 22, 22},
            {23, 23, 23},
            {24, 24, 24}
    };

    // Lista stanów akceptujących DFA
    private final List<Integer> acceptingStates = List.of(20, 21, 22, 23, 24);

    // Lista zawierająca ścieżkę jaką pokonał DFA
    private final List<Integer> statesPath = new ArrayList<>();

    /*
    * Funkcja zwracająca drugi indeks tablicy przejść z wartości wrzuconej monety.
    * Indeks ten jest powiązany z wrzuconą monetą w następujący sposób:
    * indeks 0 - moneta 1
    * indeks 1 - moneta 2
    * indeks 2 - moneta 5
    */
    private int getArrayIndexFromCoinValue(int coinValue) {
        switch (coinValue) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 5:
                return 2;
            default:
                throw new IllegalArgumentException("Provided coin value is not included in the alphabet! Accepted values - 1, 2, 5");
        }
    }

    // Funkcja zwracająca nowy stan na podstawie tablicy przejść, starego stanu i wrzuconej monety
    private int getNewState(int oldState, int coinValue) {
        int alphabetIndex = getArrayIndexFromCoinValue(coinValue);
        return transitionArray[oldState][alphabetIndex];
    }

    // Funkcja wyświetlająca szczegóły dot. obecnego stanu
    private void displayState(int state, int insertedCoin) {
        if (state > 20) {
            int change = state - 20;
            System.out.println("Inserted coin: " + insertedCoin + ", current state: q" + state + ", current sum: 20, change: " + change);
            return;
        }
        System.out.println("Inserted coin: " + insertedCoin + ", current state: q" + state + ", current sum: " + state + ", change: 0");
    }

    // Funkcja wyświetlająca ścieżkę jaką pokonał DFA
    private void displayPath() {
        StringBuilder msg = new StringBuilder("Traversed path consists of the following states: ");
        for (int i = 0; i < statesPath.size() - 1; i++) {
            msg.append("q").append(statesPath.get(i)).append(", ");
        }
        int lastState = statesPath.get(statesPath.size() - 1);
        msg.append("q").append(lastState);
        System.out.println(msg);
        if (acceptingStates.contains(lastState)) {
            System.out.print("Last state is in accepting state - a program is successfully finished");
            return;
        }
        System.out.print("Last state is not in accepting state - a program is not successfully finished");
    }

    /*
     * Funkcją przetwarzająca płatność.
     *
     * Jako argument przyjmuje listę wrzucanych po kolei monet.
     *
     * Przy użyciu tablicy przejść wypełnia przebytą przez DFA ścieżkę oraz wyświetla informację dot.
     * kolejnych stanów i przebytej ścieżki.
     */
    public void processPayment(List<Integer> coinValues) {
        statesPath.clear();
        int currentState = 0;
        statesPath.add(currentState);
        for (Integer coinValue : coinValues) {
            currentState = getNewState(currentState, coinValue);
            statesPath.add(currentState);
            displayState(currentState, coinValue);
        }
        displayPath();
    }

    public static void main(String... args) {
        Main carWash = new Main();
        carWash.processPayment(List.of(5, 5, 5, 5));
        System.out.println("-----------------------------");
        carWash.processPayment(List.of(1, 2, 2, 1, 2, 2, 1, 2, 2, 2, 5));
        System.out.println("-----------------------------");
        carWash.processPayment(List.of(1, 2, 2));
    }
}
