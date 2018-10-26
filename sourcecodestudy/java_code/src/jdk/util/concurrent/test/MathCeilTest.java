package jdk.util.concurrent.test;

public class MathCeilTest {
    public static void main(String[] args) {
        Double d = 100d;
        double d1 = d/0;
        int t = (int) Math.ceil(d/0);
        int t1 = (int) Math.ceil(d1);
        System.out.println(t);
    }
}
