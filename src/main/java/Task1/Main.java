package Task1;

public class Main {

    public static void main(String[] args) {
        String[] threadNames = {
          "1", "2", "3", "4"
        };
        ThreadGroup myThreadGroup = new ThreadGroup("test");
        System.out.println("Создаю потоки...");
        for (String nm : threadNames) {
            new MyThread(myThreadGroup, nm).start();
        }
        try {
            Thread.currentThread().sleep(5100);
        } catch (InterruptedException e) {

        } finally {
            System.out.println("Завершаю все потоки.");
            myThreadGroup.interrupt();
        }
    }
}
