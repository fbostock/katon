package javatesting;

public class JavaTest {

    public static void main1(String[] args) {


        int a = 3;
        int b = 4;
        String var = "g";

        System.out.println(" " + (a + b) + " ");
        System.out.println(var + a + b + var);

        final class G {

        }
    }

    public static int y;

    public static int foo(int x) {
        System.out.print("foo ");
        y = x;
        return y;
    }

    public static int bar(int z) {
        System.out.print("bar ");
        return y = z;
    }


    public static void main(String args[]) throws InterruptedException {
        s run = new s();
        Thread t1 = new Thread(run);
        Thread t2 = new Thread(run);
        t1.start();
        t2.start();

        Thread.sleep(1000);
        System.out.println("Done");
    }

    static class s implements Runnable {
        int x, y;

        public void run() {
            for (int i = 0; i < 1000; i++)
                synchronized (this) {
                    x = 12;
                    y = 12;
                }
            System.out.print(x + " " + y + " ");
        }

    }

    class A
    {
        A( ) { }
    }

    class B extends A
    { }
}
