package ExecutionMeasuring;

public class TestThread {

    public static void testDrive(){
        Thread thread = new Thread(){
            public void run() {

                Thread.currentThread().setName("TestThread");
                while (true) {

                try {
                    method1();

                    method2();
                    method3();
                    method4();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            }
        };

        thread.start();
    }

    static void method1() throws InterruptedException {
        print("method1()");
        Thread.sleep(10000);
    }


    static void method2() throws InterruptedException {
        print("method2()");
        Thread.sleep(3000);
    }
    static void method3() throws InterruptedException {
        print("method3()");
        Thread.sleep(3000);
    }
    static void method4() throws InterruptedException {
        print("method4()");
        Thread.sleep(3000);
    }

    static void print(String msg){
        System.out.println(msg);
    }
}
