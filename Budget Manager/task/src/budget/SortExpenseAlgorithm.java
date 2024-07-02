package budget;

import java.util.List;

public interface SortExpenseAlgorithm {

    public List<Expense> sort(List<Expense> expenses);

    public default List<Expense> swap(List<Expense> expenses, int i, int j) {
        Expense temp = expenses.get(i);
        expenses.set(i, expenses.get(j));
        expenses.set(j, temp);
        return expenses;
    }
}
