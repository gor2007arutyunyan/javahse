/**
 * Матрица комплексных чисел.
 * Поддерживает сложение, умножение, деление (A / B = A * B^-1),
 * транспонирование, вычисление определителя и обращение.
 */
public class ComplexMatrix {
    private final int rows;
    private final int cols;
    private final Complex[][] data;

    public ComplexMatrix(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Размеры матрицы должны быть положительными");
        }
        this.rows = rows;
        this.cols = cols;
        this.data = new Complex[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = Complex.ZERO;
            }
        }
    }

    public ComplexMatrix(Complex[][] values) {
        this(values.length, values[0].length);
        for (int i = 0; i < rows; i++) {
            if (values[i].length != cols) {
                throw new IllegalArgumentException("Строки матрицы разной длины");
            }
            System.arraycopy(values[i], 0, data[i], 0, cols);
        }
    }

    public static ComplexMatrix identity(int n) {
        ComplexMatrix m = new ComplexMatrix(n, n);
        for (int i = 0; i < n; i++) {
            m.data[i][i] = Complex.ONE;
        }
        return m;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Complex get(int i, int j) {
        return data[i][j];
    }

    public void set(int i, int j, Complex value) {
        data[i][j] = value;
    }

    public boolean isSquare() {
        return rows == cols;
    }

    public ComplexMatrix add(ComplexMatrix o) {
        if (rows != o.rows || cols != o.cols) {
            throw new IllegalArgumentException("Сложение невозможно: размеры матриц не совпадают ("
                    + rows + "x" + cols + " и " + o.rows + "x" + o.cols + ")");
        }
        ComplexMatrix r = new ComplexMatrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                r.data[i][j] = data[i][j].plus(o.data[i][j]);
            }
        }
        return r;
    }

    public ComplexMatrix multiply(ComplexMatrix o) {
        if (cols != o.rows) {
            throw new IllegalArgumentException("Умножение невозможно: число столбцов первой матрицы ("
                    + cols + ") не равно числу строк второй (" + o.rows + ")");
        }
        ComplexMatrix r = new ComplexMatrix(rows, o.cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < o.cols; j++) {
                Complex sum = Complex.ZERO;
                for (int k = 0; k < cols; k++) {
                    sum = sum.plus(data[i][k].times(o.data[k][j]));
                }
                r.data[i][j] = sum;
            }
        }
        return r;
    }

    /**
     * Деление матриц: this / o = this * o^(-1).
     * Возможно, если o квадратная и невырожденная, а число столбцов this равно её порядку.
     */
    public ComplexMatrix divide(ComplexMatrix o) {
        if (!o.isSquare()) {
            throw new IllegalArgumentException("Деление невозможно: делитель должен быть квадратной матрицей");
        }
        if (cols != o.rows) {
            throw new IllegalArgumentException("Деление невозможно: число столбцов делимого ("
                    + cols + ") не равно порядку делителя (" + o.rows + ")");
        }
        return multiply(o.inverse());
    }

    public ComplexMatrix transpose() {
        ComplexMatrix r = new ComplexMatrix(cols, rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                r.data[j][i] = data[i][j];
            }
        }
        return r;
    }

    /**
     * Определитель методом Гаусса с выбором главного элемента.
     */
    public Complex determinant() {
        if (!isSquare()) {
            throw new IllegalArgumentException("Определитель существует только у квадратной матрицы");
        }
        int n = rows;
        Complex[][] a = copyData();
        Complex det = Complex.ONE;
        for (int c = 0; c < n; c++) {
            int pivot = c;
            for (int r = c + 1; r < n; r++) {
                if (a[r][c].abs() > a[pivot][c].abs()) {
                    pivot = r;
                }
            }
            if (a[pivot][c].isZero()) {
                return Complex.ZERO;
            }
            if (pivot != c) {
                Complex[] tmp = a[pivot];
                a[pivot] = a[c];
                a[c] = tmp;
                det = det.negate();
            }
            det = det.times(a[c][c]);
            for (int r = c + 1; r < n; r++) {
                Complex f = a[r][c].divide(a[c][c]);
                for (int k = c; k < n; k++) {
                    a[r][k] = a[r][k].minus(f.times(a[c][k]));
                }
            }
        }
        return det;
    }

    /**
     * Обратная матрица методом Гаусса-Жордана.
     */
    public ComplexMatrix inverse() {
        if (!isSquare()) {
            throw new IllegalArgumentException("Обратная матрица существует только для квадратной матрицы");
        }
        int n = rows;
        Complex[][] a = copyData();
        Complex[][] inv = identity(n).data;
        for (int c = 0; c < n; c++) {
            int pivot = c;
            for (int r = c + 1; r < n; r++) {
                if (a[r][c].abs() > a[pivot][c].abs()) {
                    pivot = r;
                }
            }
            if (a[pivot][c].isZero()) {
                throw new ArithmeticException("Матрица вырождена (определитель равен 0), деление невозможно");
            }
            Complex[] t = a[pivot];
            a[pivot] = a[c];
            a[c] = t;
            t = inv[pivot];
            inv[pivot] = inv[c];
            inv[c] = t;

            Complex p = a[c][c];
            for (int k = 0; k < n; k++) {
                a[c][k] = a[c][k].divide(p);
                inv[c][k] = inv[c][k].divide(p);
            }
            for (int r = 0; r < n; r++) {
                if (r == c) {
                    continue;
                }
                Complex f = a[r][c];
                if (f.isZero()) {
                    continue;
                }
                for (int k = 0; k < n; k++) {
                    a[r][k] = a[r][k].minus(f.times(a[c][k]));
                    inv[r][k] = inv[r][k].minus(f.times(inv[c][k]));
                }
            }
        }
        return new ComplexMatrix(inv);
    }

    private Complex[][] copyData() {
        Complex[][] copy = new Complex[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, copy[i], 0, cols);
        }
        return copy;
    }

    @Override
    public String toString() {
        String[][] s = new String[rows][cols];
        int width = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                s[i][j] = data[i][j].toString();
                width = Math.max(width, s[i][j].length());
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            sb.append("| ");
            for (int j = 0; j < cols; j++) {
                sb.append(String.format("%" + width + "s", s[i][j])).append(' ');
            }
            sb.append("|\n");
        }
        return sb.toString();
    }
}
