/**
 * Неизменяемое комплексное число вида re + im*i.
 */
public final class Complex {
    public static final double EPS = 1e-9;
    public static final Complex ZERO = new Complex(0, 0);
    public static final Complex ONE = new Complex(1, 0);

    private final double re;
    private final double im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public Complex(double re) {
        this(re, 0);
    }

    public double getRe() {
        return re;
    }

    public double getIm() {
        return im;
    }

    public Complex plus(Complex o) {
        return new Complex(re + o.re, im + o.im);
    }

    public Complex minus(Complex o) {
        return new Complex(re - o.re, im - o.im);
    }

    public Complex times(Complex o) {
        return new Complex(re * o.re - im * o.im, re * o.im + im * o.re);
    }

    public Complex negate() {
        return new Complex(-re, -im);
    }

    public Complex divide(Complex o) {
        double d = o.re * o.re + o.im * o.im;
        if (d == 0) {
            throw new ArithmeticException("Деление на комплексный ноль");
        }
        return new Complex((re * o.re + im * o.im) / d, (im * o.re - re * o.im) / d);
    }

    public double abs() {
        return Math.hypot(re, im);
    }

    public boolean isZero() {
        return abs() < EPS;
    }

    /**
     * Разбор строки: "3", "-2.5", "i", "-i", "2i", "1+2i", "1.5 - 3i".
     */
    public static Complex parse(String text) {
        String s = text.replace(" ", "").replace(',', '.');
        if (s.isEmpty()) {
            throw new NumberFormatException("Пустая строка");
        }
        try {
            if (!s.endsWith("i")) {
                return new Complex(Double.parseDouble(s), 0);
            }
            String body = s.substring(0, s.length() - 1);
            int split = -1;
            for (int k = body.length() - 1; k > 0; k--) {
                char c = body.charAt(k);
                char prev = body.charAt(k - 1);
                if ((c == '+' || c == '-') && prev != 'e' && prev != 'E') {
                    split = k;
                    break;
                }
            }
            double real = 0;
            String imagPart = body;
            if (split > 0) {
                real = Double.parseDouble(body.substring(0, split));
                imagPart = body.substring(split);
            }
            double imag;
            if (imagPart.isEmpty() || imagPart.equals("+")) {
                imag = 1;
            } else if (imagPart.equals("-")) {
                imag = -1;
            } else {
                imag = Double.parseDouble(imagPart);
            }
            return new Complex(real, imag);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Не удалось разобрать комплексное число: \"" + text + "\"");
        }
    }

    private static String fmt(double v) {
        if (Math.abs(v) < EPS) {
            v = 0;
        }
        if (v == Math.rint(v) && Math.abs(v) < 1e12) {
            return String.valueOf((long) v);
        }
        return String.format(java.util.Locale.US, "%.4f", v)
                .replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    @Override
    public String toString() {
        double r = Math.abs(re) < EPS ? 0 : re;
        double i = Math.abs(im) < EPS ? 0 : im;
        if (i == 0) {
            return fmt(r);
        }
        String imagStr = Math.abs(i - 1) < EPS ? "i" : Math.abs(i + 1) < EPS ? "-i" : fmt(i) + "i";
        if (r == 0) {
            return imagStr;
        }
        return fmt(r) + (i > 0 ? "+" : "") + imagStr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Complex)) {
            return false;
        }
        Complex c = (Complex) o;
        return Double.compare(re, c.re) == 0 && Double.compare(im, c.im) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * Double.hashCode(re) + Double.hashCode(im);
    }
}
