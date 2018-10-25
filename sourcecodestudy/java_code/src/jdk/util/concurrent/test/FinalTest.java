package jdk.util.concurrent.test;

public class FinalTest {
//    private static final long serialVersionUID = -817911632652898426L;
    final int[] t;
    int take;

    public FinalTest(int cap) {
        this.t = new int[cap];
        t[0] = 1;
        t[1] = 2;
        t[2] = 3;
    }

    public void test() {
        final int[] t = this.t;
        t[0] = 9;
        System.out.println(t[0]);
    }
}
