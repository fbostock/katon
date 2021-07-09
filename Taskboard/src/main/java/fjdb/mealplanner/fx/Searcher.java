package fjdb.mealplanner.fx;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Function;

public class Searcher<T> {

    private final List<T> inputs;
    private final Function<T, String> function;

    public Searcher(List<T> inputs, Function<T, String> function) {
        this.inputs = inputs;
        this.function = function;
    }

    public Searcher(List<T> inputs) {
        this.inputs = inputs;
        this.function = T::toString;
    }

    public List<T> results(String searchString) {
        List<T> list = Lists.newArrayList();
        inputs.forEach(t -> {
            if (function.apply(t).contains(searchString)) {
                list.add(t);
            }
        });
        return list;
    }
}
