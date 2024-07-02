package budget;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // Read data from console
        FinanceManager manager = new FinanceManager();
        while (true) {
            manager.handleInput();
        }
    }
}
