package Task3;

import java.util.List;
import java.util.concurrent.RecursiveTask;

public class CounterRecursiveTask extends RecursiveTask<Double> {

    private List<Integer> arr;
    private int start;
    private int end;

    public CounterRecursiveTask(List<Integer> arr, int start, int end) {
        this.arr = arr;
        this.start = Math.max(start, 0);
        this.end = end < 0 ? arr.size() : end;
    }

    @Override
    protected Double compute() {
        final int diff = end - start;
        switch(diff){
            case 0:
                return (double) 0;
            case 1:
                return arr.get(start).doubleValue();
            case 2:
                double val = arr.get(start) + arr.get(start+1);
                return val;
            default:
                return forkTasksAndGetResult();
        }
    }

    private Double forkTasksAndGetResult() {
        final int middle = (end - start)/2 + start;// Создаем задачу для левой части диапазона
        CounterRecursiveTask task1 = new CounterRecursiveTask(arr, start, middle);// Создаем задачу для правой части диапазона
        CounterRecursiveTask task2 = new CounterRecursiveTask(arr, middle, end);// Запускаем обе задачи в пуле
        invokeAll(task1, task2);// Суммируем результаты выполнения обоих задач
        Double r1 = task1.join(), r2 = task2.join();
        return r1 + r2;
    }
}
