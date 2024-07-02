package budget;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static budget.Expense.*;

public class FinanceManager {

    private final Scanner scanner;
    private final List<Expense> purchases;
    private BigDecimal balance;
    private SortExpenseAlgorithm sortAlgorithm;


    public FinanceManager() {
        scanner = new Scanner(System.in);
        purchases = new ArrayList<>();
        balance = BigDecimal.ZERO;

    }

    public void handleInput() throws IOException {
        String inputMenu;

        while (true) {
            String menuOptions = "\nChoose your action:\n1) Add income\n2) Add purchase\n3) Show list of purchases\n4) Balance\n5) Save\n6) Load\n7) Analyze (Sort)\n0) Exit";
            System.out.println(menuOptions);
            inputMenu = scanner.nextLine();

            switch (inputMenu) {
                case "0":
                    exit();
                    return;
                case "1":
                    addIncome();
                    break;
                case "2":
                    addPurchase();
                    break;
                case "3":
                    showPurchases();
                    break;
                case "4":
                    showBalance();
                    break;
                case "5":
                    saveToFile();
                    break;
                case "6":
                    loadFromFile();
                    break;
                case "7":
                    sortExpenses();
                    break;
                default:
                    System.out.println("Invalid selection.\n");
                    break;
            }
        }
    }

    private void loadFromFile() throws IOException {
        List<String> fileContent = Files.readAllLines(Path.of("purchases.txt"));
        for (int i = 0; i < fileContent.size(); i++) {
            if (i == 0){
                increaseBalance(new BigDecimal(fileContent.get(0)));
            }
            else {
                purchases.add(Expense.parseExpense(fileContent.get(i)));
            }
        }
        System.out.println("\nPurchases were loaded!");
    }

    private void saveToFile() {
        File file = new File("purchases.txt");

        try (FileWriter writer = new FileWriter(file,false)) {
            //Although not specified in requirements, comments point out to needing balance in 1st line
            writer.write(String.valueOf(this.balance));
            writer.write("\n");
            //convert all purchases to csv delimited
            writer.write(toCsv(this.purchases));
        } catch (IOException e) {
            System.out.printf("An exception occurred %s", e.getMessage());
        }
        System.out.println("Purchases were saved!");
    }

    private void exit() {
        System.out.println("\nBye!");
        System.exit(0);
    }

    private void addIncome() {
        System.out.println("\nEnter income:");
        String inputIncome = scanner.nextLine();
        BigDecimal income = new BigDecimal(inputIncome);
        increaseBalance(income);
        System.out.println("Income was added!\n");
    }

    private void addPurchase() {
        System.out.println("\nChoose the type of purchase");
        categories.forEach((key, value) -> System.out.println(key + ") " + value));
        System.out.println("5) Back");
        int typePurchase = scanner.nextInt();
        scanner.nextLine();

        String category = categories.getOrDefault(typePurchase, null);
        if (category != null) {
            System.out.println("\nEnter purchase name:");
            String purchaseName = scanner.nextLine();
            System.out.println("Enter its price:");
            BigDecimal price = new BigDecimal(scanner.nextLine());
            decreaseBalance(price);
            Expense expense = new Expense(purchaseName, price, category);
            purchases.add(expense);
            System.out.println("Purchase was added!");
            addPurchase(); // recurse
        }
    }

    private void showPurchases() {
        if (purchases.isEmpty()) {
            System.out.println("\nThe purchase list is empty!");
            return;
        }
        System.out.println("\nChoose the type of purchase");
        categories.forEach((key, value) -> System.out.println(key + ") " + value));
        System.out.println("5) All");
        System.out.println("6) Back");
        int typePurchase = scanner.nextInt();
        scanner.nextLine();

        switch (typePurchase) {
            case 5: // All
                System.out.println("\nAll:");
                printExpenses(this.purchases);
                break;
            case 6: // Back
                return;
            default:
                String typeToFilter = categories.getOrDefault(typePurchase, null);
                System.out.println("\n" + typeToFilter + ":");
                if (typeToFilter != null) {
                    List<Expense> filteredPurchases = purchases.stream()
                            .filter(p -> p.category().equals(typeToFilter))
                            .toList();
                    printExpenses(filteredPurchases);
                }
        }
        showPurchases();//recurse
    }

    private void showBalance() {
        System.out.println("\nBalance: $" + String.format("%.2f", this.balance) + "\n");
    }

    private void increaseBalance(BigDecimal income) {
        this.balance = this.balance.add(income);
    }

    private void decreaseBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
        if (this.balance.compareTo(BigDecimal.ZERO) < 0) {
            this.balance = BigDecimal.ZERO;
        }
    }

    public void setSortAlgorithm(SortExpenseAlgorithm sortAlgorithm) {
        this.sortAlgorithm = sortAlgorithm;
    }

    public void sortExpenses (){
        System.out.println("\nHow do you want to sort?\n1) Sort all purchases\n2) Sort by type\n3) Sort certain type\n4) Back");
        int typePurchase = scanner.nextInt();
        scanner.nextLine();

        switch (typePurchase){
            case 1: // Sort all purchases
                setSortAlgorithm(new SortExpenseAllAlgorithm());
                if (purchases.isEmpty()) {
                    System.out.println("\nThe purchase list is empty!");
                }
                else {
                    System.out.println("\nAll:");
                    printExpenses(sortAlgorithm.sort(this.purchases));
                }
                break;
            case 2: // Sort by type
                setSortAlgorithm(new SortExpenseCategoryAlgorithm());
                System.out.println("\nTypes:");
                printCategories(sortAlgorithm.sort(this.purchases));
                break;
            case 3: // Sort certain type
                setSortAlgorithm(new SortExpenseAllAlgorithm());

                System.out.println("\nChoose the type of purchase");
                categories.forEach((key, value) -> System.out.println(key + ") " + value));

                int typeToFilter = scanner.nextInt();
                scanner.nextLine();
                System.out.println();
                printExpenses(sortAlgorithm.sort(filterCategory(this.purchases,categories.get(typeToFilter))));
                break;
            default: // Back
                try {
                    handleInput();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
        sortExpenses(); //recurse
    }
    private List<Expense> filterCategory(List<Expense> purchases,String category){
        return purchases.stream().filter(expense -> expense.category().equals(category)).toList();
    }
}
