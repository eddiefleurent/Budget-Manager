package budget;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class SortExpenseCategoryAlgorithm implements SortExpenseAlgorithm {
    @Override
    public List<Expense> sort(List<Expense> expenses) {
        // Step 1: Group and sum the expenses by category
        Map<String, BigDecimal> categorySums = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::category,
                        Collectors.reducing(BigDecimal.ZERO, Expense::amount, BigDecimal::add)
                ));

        // Step 2: Sort the categories by total amount in descending order and map to Expense objects

        return categorySums.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(entry -> new Expense("Total for " + entry.getKey(), entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());
    }
}
