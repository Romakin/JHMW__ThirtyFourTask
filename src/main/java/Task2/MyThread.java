package Task2;

public class MyThread extends Thread {

    public MyThread(ThreadGroup tg, String s, Runnable r) {
        super(tg, r);
        setName(s);
    }

    @Override
    public void run() {
        try {
            while(!isInterrupted()) {
                Thread.sleep(2500);
                System.out.printf("Я поток %s. Выполняем \n", getName());
                super.run();
            }
        } catch (InterruptedException err) {

        } finally{
            System.out.printf("%s завершен\n", getName());
        }
    }
}
