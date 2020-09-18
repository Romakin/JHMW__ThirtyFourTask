package Task3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {

    static Random random;

    public static void main(String[] args) {
        random = new Random();
        List<Integer> arr = getArr();
        countInCurrentThread(arr);
        countInCurrentThreadLambda(arr);
        // после прохождения этих тестов становиться очевидным, что выгодней делать через цикл.
        //countInMultiThread(arr);

        countInForkJoinPool(arr);
    }

    public static void countInCurrentThread(List<Integer> arr) {
        long start = System.currentTimeMillis();
        double sum = 0;
        double mean = 0;
        for (Integer i: arr) {
            sum += i;
        }
        mean = sum/arr.size();
        long end = System.currentTimeMillis();
        System.out.printf("Тест в цикле \n Сумма: %f, Среднее арифметическое: %f \n", sum, mean);
        System.out.printf(" Benchmark: %d \n", (end - start) );
    }

    public static void countInCurrentThreadLambda(List<Integer> arr) {
        long start = System.currentTimeMillis();
        final double[] sum = {0};
        double mean = 0;
        arr.forEach(e -> sum[0] += e);
        mean = sum[0]/arr.size();
        long end = System.currentTimeMillis();
        System.out.printf("Lambda тест \n Сумма: %f, Среднее арифметическое: %f \n", sum[0], mean);
        System.out.printf(" Benchmark: %d \n", (end - start) );
    }

    public static void countInForkJoinPool(List<Integer> arr) {
        CounterRecursiveTask crt = new CounterRecursiveTask(arr, -1, -1);
        long start = System.currentTimeMillis();
        final Double[] res = new ForkJoinPool().invoke(crt);
        long end = System.currentTimeMillis();
        System.out.printf(
                "ForkJoinPool тест \n Сумма: %f, Среднее арифметическое: %f \n",
                res[0], res[1]
        );
        System.out.printf(" Benchmark: %d \n", (end - start) );
    }

    public static void countInMultiThread(List<Integer> arr) {
        ExecutorService es = Executors.newCachedThreadPool();
        Callable<Double[]> cd = new CounterCallable(arr);
        int batchsSize = 1000;
        try {
            long start = System.currentTimeMillis();
            Double[] sumAndMean = invokeAndCalcCallables(es, batchsSize, arr);
            long end = System.currentTimeMillis();
            System.out.printf(
                    "MultiThread тест \n Сумма: %f, Среднее арифметическое: %f \n",
                    sumAndMean[0], sumAndMean[1]
            );
            System.out.printf(" Benchmark: %d \n", (end - start) );
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            es.shutdown();
        }
    }

    public static Double[] invokeAndCalcCallables(ExecutorService es, int batchSize, List<Integer> arr)
            throws InterruptedException, ExecutionException  {
        int arrsz = arr.size(), batchCount = (arrsz/batchSize) + (arrsz%batchSize > 0 ? 1 : 0);
        List<Callable<Double[]>> cds = new ArrayList<>();
        int lastInd = 0;
        for (int i = 0; i < batchCount; i++) {
            int startInd = i * batchSize,
                    endInd = (i + 1) * batchSize;
            endInd = (endInd > arrsz) ? arrsz - 1 : endInd;
            cds.add(new CounterCallable(arr.subList(startInd, endInd)));
            lastInd = endInd;
        }
//        System.out.println("---");
//        System.out.println(batchCount + " == " + cds.size());
//        System.out.println("(" + arrsz + " - 1 )" + " == " + lastInd);
        List<Future<Double[]>> results = es.invokeAll(cds);
        if (batchCount > 1) {
            Integer[] sums = new Integer[batchCount];
            Integer[] means = new Integer[batchCount];
            for (int ri = 0; ri < results.size(); ri++) {
                Double[] res = results.get(ri).get();
                sums[ri] = res[0].intValue();
                means[ri] = res[1].intValue();
            }
            double smt = 0; for(Integer i : sums) smt += i;
//            System.out.printf("Total sum: %f\n", smt);
//            System.out.println("---");
            return new Double[] {
                    invokeAndCalcCallables(es,batchSize,Arrays.asList(sums))[0],
                    invokeAndCalcCallables(es,batchSize,Arrays.asList(means))[0]/batchCount
            };
        } else {
//            System.out.println("---");
            return results.get(0).get();
        }
    }

    public static List<Integer> getArr() {
        System.out.println("Создаем массив");
        List<Integer> arr = Arrays.asList(new Integer[rand(800000, 1000000)]);
        for (int i = 0; i < arr.size(); i++) arr.set(i,rand(0, 100));
        System.out.println("Создан массив длинной " + arr.size());
        return arr;
    }

    public static int rand(int min, int max) {
        return random.nextInt(max + 1 - min) + min;
    }
}
