package Task2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        int threadPoolSize = 4;
        final String[] threadNames = {
                "1", "2", "3", "4"
        };
        String[] messages = {
                "hi", "it's printer", "multithreadings is cool", "isn't it?"
        };

        final ThreadGroup myThreadGroup = new ThreadGroup("test");
        final int[] indexName = {0};

        final ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolSize, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new MyThread(myThreadGroup, threadNames[indexName[0]++], r);
            }
        });

        final Callable<Integer> myCallable = new PrinterCallable(messages);
        System.out.println("Создаю потоки...");
        final FutureTask<Integer> intFutureTask = (FutureTask<Integer>) threadPool.submit(myCallable);

        Collection<Callable<Integer>> tasks = new ArrayList<>();
        int tasksCount = 5;
        for (int i = tasksCount; i > 0; i--) {
            tasks.add(myCallable);
        }

        final int[] shutdownsCount = {0};
        try {

            /*
                Функция invokeAny просто выполнит в каком-то случайном потоке задание и выдаст его результат,
                    что не так занимательно, как выполнения списка задач с случаный распределением по пулу.
                    Ниже - использование invokeAll для списка из 4ех задач при условии, что одна уже выполняется.
                    Получается, что задач больше, чем потоков и в консоли наглядно видно, как дальше происходит обработка.
            */

            List<Future<Integer>> otherResults = threadPool.invokeAll(tasks);

            Thread.currentThread().sleep(6000);

            int result = intFutureTask.get();
            System.out.println("результат 1: " + result);
            shutdownsCount[0]++;
            System.out.println("Прочие результаты: ");
            otherResults.stream().map(f -> {
                try {
                    return f.get();
                } catch (InterruptedException | ExecutionException e) {
                    return -100;
                }
            }).forEach(f -> {
                shutdownsCount[0]++;
                System.out.println(f);
            });
        } catch (InterruptedException | ExecutionException e) {
        } finally {
            System.out.println("Завершаю все потоки.");
            System.out.println(shutdownsCount[0]);
            int shutdowned = -1;
            while (shutdowned <= shutdownsCount[0]) {
                threadPool.shutdown();
                shutdowned++;
            }
            System.out.println(threadPool.isShutdown());
        }



    }
}
