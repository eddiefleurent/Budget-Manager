package budget;

import java.util.List;

public class SortExpenseAllAlgorithm implements SortExpenseAlgorithm {

    @Override
    public List<Expense> sort(List<Expense> expenses) {
        boolean swapped;
        int n = expenses.size();
        do {
            swapped = false;
            for (int i = 1; i < n; i++) {
                if (expenses.get(i - 1).amount().compareTo(expenses.get(i).amount()) < 0) {
                    swap(expenses, i - 1, i);
                    swapped = true;
                }
            }
            n--; // Reduce the range for the next pass
        } while (swapped);
        return expenses;
    }
}
