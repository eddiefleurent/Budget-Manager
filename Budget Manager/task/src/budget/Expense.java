package budget;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record Expense(String description, BigDecimal amount, String category) {
    public static final String delimiter = "----";

    public static final Map<Integer, String> categories;

    static {
        Map<Integer, String> tempMap = new LinkedHashMap<>();
        tempMap.put(1, "Food");
        tempMap.put(2, "Clothes");
        tempMap.put(3, "Entertainment");
        tempMap.put(4, "Other");
        categories = Collections.unmodifiableMap(tempMap);
    }

    public String toStringConsole() {
        return description + " $" + String.format("%.2f",amount);
    }

    public static void printExpenses(List<Expense> purchases) {
        BigDecimal total = BigDecimal.ZERO;
        if (purchases.isEmpty()) {
            System.out.println("\nThe purchase list is empty!");
        } else {
            for (Expense p : purchases) {
                System.out.println(p.toStringConsole());
                total = total.add(p.amount());
            }
            System.out.println("Total sum: $" + String.format("%.2f", total));
        }
    }

    public static void printCategories(List<Expense> purchases){
        BigDecimal total = BigDecimal.ZERO;
        if (purchases.isEmpty()) {
            categories.forEach((key, value) -> System.out.println(value + " - $0"));
            System.out.println("Total sum: $0");
        } else {
            for (Expense p : purchases) {
                System.out.println(p.category() + " - $" + String.format("%.2f", p.amount()));
                total = total.add(p.amount());
            }
            System.out.println("Total sum: $" + String.format("%.2f", total));
        }
    }

    public static String toCsv(List<Expense> purchases){
        StringBuilder sb = new StringBuilder();
        for (Expense p : purchases) {
            sb.append(String.join(delimiter,p.description(),p.amount().toString(),p.category()));
            sb.append("\n");
        }
        return sb.toString();
    }
    public static Expense parseExpense(String lineContent){
        String [] content = lineContent.split(delimiter);
        return new Expense(content[0],new BigDecimal(content[1]), content[2]);
    }



}
