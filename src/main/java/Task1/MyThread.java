package Task1;

public class MyThread extends Thread {

    public MyThread(ThreadGroup tg, String s) {
        super(tg, s);
    }

    @Override
    public void run() {
        try {
            while(!isInterrupted()) {
                Thread.sleep(2500);
                System.out.printf("Я поток %s. Всем привет! \n", getName());
            }
        } catch (InterruptedException err) {

        } finally{
            System.out.printf("%s завершен\n", getName());
        }
    }

}
