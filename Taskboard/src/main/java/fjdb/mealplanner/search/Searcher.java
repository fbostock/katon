package fjdb.mealplanner.search;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Function;

public class Searcher<T> {

    private final List<T> inputs;
    private final Function<T, String> function;
    private Modifier modifier = new StubModifier();

    public Searcher(List<T> inputs, Function<T, String> function) {
        this.inputs = inputs;
        this.function = function;
    }

    public Searcher(List<T> inputs) {
        this.inputs = inputs;
        this.function = T::toString;
    }

    public void ignoreCase() {
        modifier = new CaseModifier();
    }

    public List<T> results(String searchString) {
        String lowerCaseSearch = modifier.mod(searchString);
        List<T> list = Lists.newArrayList();
        for (T item : inputs) {
            if (modifier.mod(function.apply(item)).contains(lowerCaseSearch)) {
                list.add(item);
            }
        }
        return list;
    }

    private interface Modifier {
        String mod(String input);
    }

    private static class StubModifier implements Modifier {

        @Override
        public String mod(String input) {
            return input;
        }
    }
    private static class CaseModifier implements Modifier {

        @Override
        public String mod(String input) {
            return input.toLowerCase();
        }
    }
}
