import java.util.*;

/*
 * Autor - Michał Kuśmidrowicz 244021
 * Zadanie 4
 */

public class Main {

    // Listy zawierające symbole pierwsze kolejnych produkcji
    private static final List<String> FIRST_O = List.of("*", ":", "+", "-", "^");
    private static final List<String> FIRST_C = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    private static final List<String> FIRST_L = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    private static final List<String> FIRST_L_PRIM = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    private static final List<String> FIRST_R = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    private static final List<String> FIRST_R_PRIM = List.of(".");
    private static final List<String> FIRST_P = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "(");
    private static final List<String> FIRST_W = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "(");
    private static final List<String> FIRST_W_PRIM = List.of("*", ":", "+", "-", "^");
    private static final List<String> FIRST_Z = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "(");

    // Obecnie przetwarzana wartość
    private String currentValue;
    // Stos zawierający wartości przetwarzanego wyrażenia
    private final Stack<String> valuesStack = new Stack<>();
    // Flaga określająca czy przetworzone wyrażenie jest zgodne z gramatyką S
    private boolean isProper = true;

    // Funkcja przetwarzająca produkcję S
    void readS() {
        if (valuesStack.empty()) {
            isProper = false;
            return;
        }
        currentValue = valuesStack.pop();
        if (FIRST_W.contains(currentValue)) {
            readW();
        }
        if (!checkTerminal(";")) {
            return;
        }
        if (FIRST_Z.contains(currentValue)) {
            readZ();
        }
    }

    // Funkcja przetwarzająca produkcję Z
    private void readZ() {
        if (valuesStack.empty()) {
            return;
        }
        if (FIRST_W.contains(currentValue)) {
            readW();
        }
        if (!checkTerminal(";")) {
            return;
        }
        if (FIRST_Z.contains(currentValue)) {
            readZ();
        }
    }

    // Funkcja przetwarzająca produkcję W
    void readW() {
        if (valuesStack.empty()) {
            return;
        }
        if (FIRST_P.contains(currentValue)) {
            readP();
        }
        if (FIRST_W_PRIM.contains(currentValue)) {
            readWPrim();
        }
    }

    // Funkcja przetwarzająca produkcję W'
    void readWPrim() {
        if (valuesStack.empty()) {
            return;
        }
        if (FIRST_O.contains(currentValue)) {
            currentValue = valuesStack.pop();
        }
        if (FIRST_W.contains(currentValue)) {
            readW();
        } else {
            isProper = false;
        }
    }

    // Funkcja przetwarzająca produkcję P
    void readP() {
        if (valuesStack.empty()) {
            return;
        }
        if (FIRST_R.contains(currentValue)) {
            readR();
        } else {
            if (!checkTerminal("(")) {
                return;
            }
            if (FIRST_W.contains(currentValue)) {
                readW();
            }
            checkTerminal(")");
        }
    }

    // Funkcja przetwarzająca produkcję R
    void readR() {
        if (valuesStack.empty()) {
            return;
        }
        if (FIRST_L.contains(currentValue)) {
            readL();
        }
        if (FIRST_R_PRIM.contains(currentValue)) {
            readRPrim();
        }
    }

    // Funkcja przetwarzająca produkcję R'
    void readRPrim() {
        if (valuesStack.empty()) {
            return;
        }
        if (!checkTerminal(".")) {
            return;
        }
        if (FIRST_L.contains(currentValue)) {
            readL();
        } else {
            isProper = false;
        }
    }

    // Funkcja przetwarzająca produkcję L
    void readL() {
        if (valuesStack.empty()) {
            return;
        }
        if (FIRST_C.contains(currentValue)) {
            currentValue = valuesStack.pop();
        } else {
            isProper = false;
        }
        if (FIRST_L_PRIM.contains(currentValue)) {
            readLPrim();
        }
    }

    // Funkcja przetwarzająca produkcję L'
    void readLPrim() {
        if (valuesStack.empty()) {
            return;
        }
        if (FIRST_L.contains(currentValue)) {
            readL();
        }
    }

    // Funkcja sprawdzająca czy obecnie przetwarzana wartość jest przekazanym terminalem
    private boolean checkTerminal(String terminalValue) {
        if (!currentValue.equals(terminalValue)) {
            isProper = false;
            return false;
        }
        if (valuesStack.empty()) {
            return false;
        }
        // Wczytywanie kolejnego znaku jesli zczytano odpowiedni terminal
        currentValue = valuesStack.pop();
        return true;
    }

    public static void main(String... args) {
        Main main = new Main();
        System.out.print("Wprowadz wyrazenie: ");
        Scanner scanner = new Scanner(System.in);
        String stringValue = scanner.nextLine();
        List<String> values = new ArrayList<>(Arrays.asList(stringValue.split("(?!^)")));
        Collections.reverse(values);
        values.forEach(main.valuesStack::push);
        main.readS();
        if (main.isProper) {
            System.out.println("Wyrazenie ZGODNE z gramatyka S");
        } else {
            System.out.println("Wyrazenie NIEZGODNE z gramatyka S");
        }
    }
}
