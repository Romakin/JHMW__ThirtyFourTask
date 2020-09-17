package Task3;

import java.util.List;
import java.util.concurrent.Callable;

public class CounterCallable implements Callable<Double[]> {

    private List<Integer> arr;

    public CounterCallable(List<Integer> batch) {
        arr = batch;
    }

    @Override
    public Double[] call() throws Exception {
        double mean, sum = 0;
        for (Integer i: arr) { sum += i; }
        mean = sum/arr.size();
        return new Double[] {
            sum, mean
        };
    }
}
