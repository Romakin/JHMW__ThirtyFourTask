package Task2;

import java.util.concurrent.Callable;

public class PrinterCallable implements Callable<Integer> {

    private final String[] messages;

    public PrinterCallable(String[] messages) {
        this.messages = messages;
    }

    public Integer call() throws Exception {
        int counter = 0;
        for (String msg : messages) {
            System.out.printf("Сообщение потока %s: %s \n", Thread.currentThread().getName(), msg);
            counter++;
            Thread.sleep(1000);
        }
        return counter;
    }
}
