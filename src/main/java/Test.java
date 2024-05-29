import java.math.BigDecimal;

public class Test {
    public static void main(String[] args) {
        if (new BigDecimal("0.99").compareTo(BigDecimal.ZERO) >= 0) {
            System.out.println("0sss");
        }
    }
}
