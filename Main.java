import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Консольный интерфейс для работы с матрицами комплексных чисел.
 */
public class Main {
    private static final Map<String, ComplexMatrix> matrices = new LinkedHashMap<>();
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Калькулятор матриц комплексных чисел");
        System.out.println("Формат чисел: 3, -2.5, i, -i, 2i, 1+2i, 1.5-3i");
        while (true) {
            System.out.println();
            System.out.println("1. Создать матрицу");
            System.out.println("2. Показать все матрицы");
            System.out.println("3. Сложить");
            System.out.println("4. Умножить");
            System.out.println("5. Разделить (A / B = A * B^-1)");
            System.out.println("6. Транспонировать");
            System.out.println("7. Определитель");
            System.out.println("0. Выход");
            System.out.print("Выберите действие: ");
            String choice = in.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        createMatrix();
                        break;
                    case "2":
                        showAll();
                        break;
                    case "3":
                        binary("A + B", ComplexMatrix::add);
                        break;
                    case "4":
                        binary("A * B", ComplexMatrix::multiply);
                        break;
                    case "5":
                        binary("A / B", ComplexMatrix::divide);
                        break;
                    case "6":
                        unaryTranspose();
                        break;
                    case "7":
                        determinant();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Неизвестная команда.");
                }
            } catch (IllegalArgumentException | ArithmeticException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int v = Integer.parseInt(in.nextLine().trim());
                if (v > 0) {
                    return v;
                }
            } catch (NumberFormatException ignored) {
                // повторяем ввод
            }
            System.out.println("Введите положительное целое число.");
        }
    }

    private static void createMatrix() {
        System.out.print("Имя матрицы: ");
        String name = in.nextLine().trim();
        if (name.isEmpty() || name.contains(" ")) {
            System.out.println("Имя не должно быть пустым или содержать пробелы.");
            return;
        }
        int rows = readInt("Число строк: ");
        int cols = readInt("Число столбцов: ");
        ComplexMatrix m = new ComplexMatrix(rows, cols);
        System.out.println("Вводите элементы по строкам, через пробел (" + cols + " шт. в строке).");
        System.out.println("Комплексные числа записывайте без пробелов внутри числа, например 1+2i");
        for (int i = 0; i < rows; i++) {
            while (true) {
                System.out.print("Строка " + (i + 1) + ": ");
                String[] parts = in.nextLine().trim().split("\\s+");
                if (parts.length != cols) {
                    System.out.println("Нужно ввести ровно " + cols + " элементов.");
                    continue;
                }
                try {
                    for (int j = 0; j < cols; j++) {
                        m.set(i, j, Complex.parse(parts[j]));
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        matrices.put(name, m);
        System.out.println("Матрица " + name + " сохранена:");
        System.out.print(m);
    }

    private static void showAll() {
        if (matrices.isEmpty()) {
            System.out.println("Матриц пока нет.");
            return;
        }
        for (Map.Entry<String, ComplexMatrix> e : matrices.entrySet()) {
            System.out.println(e.getKey() + " (" + e.getValue().getRows() + "x" + e.getValue().getCols() + "):");
            System.out.print(e.getValue());
        }
    }

    private static ComplexMatrix ask(String prompt) {
        if (matrices.isEmpty()) {
            throw new IllegalArgumentException("Сначала создайте хотя бы одну матрицу.");
        }
        System.out.print(prompt + " (доступны: " + String.join(", ", matrices.keySet()) + "): ");
        String name = in.nextLine().trim();
        ComplexMatrix m = matrices.get(name);
        if (m == null) {
            throw new IllegalArgumentException("Матрица \"" + name + "\" не найдена.");
        }
        return m;
    }

    private static void binary(String title, java.util.function.BinaryOperator<ComplexMatrix> op) {
        ComplexMatrix a = ask("Матрица A");
        ComplexMatrix b = ask("Матрица B");
        ComplexMatrix r = op.apply(a, b);
        System.out.println(title + " =");
        System.out.print(r);
        saveResult(r);
    }

    private static void unaryTranspose() {
        ComplexMatrix a = ask("Матрица");
        ComplexMatrix r = a.transpose();
        System.out.println("Транспонированная матрица:");
        System.out.print(r);
        saveResult(r);
    }

    private static void determinant() {
        ComplexMatrix a = ask("Матрица");
        System.out.println("det = " + a.determinant());
    }

    private static void saveResult(ComplexMatrix r) {
        System.out.print("Сохранить результат под именем (Enter - не сохранять): ");
        String name = in.nextLine().trim();
        if (!name.isEmpty() && !name.contains(" ")) {
            matrices.put(name, r);
            System.out.println("Сохранено как " + name);
        }
    }
}
