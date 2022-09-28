package fjdb.mealplanner.search;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;

/*

 */
public interface SearchAlgo<T> {
    /*Changes to search field passed to algorithm*/
    public boolean search(String input, String searchText);

    /**
     * Performs a search of inputs matching the given search string, and appends matching items to the given queue.
     * This blocks until the search is complete.
     * The labeller is used to convert the given inputs into a string form to match against the search string.
     * By default, this method simply delegates each input to the {@link #search(String, String)}. More complex
     * algorithms are free to override this to perform less simple iterations, for instance performing multiple
     * iterations, where each iteration performs a different level of search. For example, the first iteration
     * could be comparing matches beginning with the searchString, followed by matches simply containing the
     * search string.
     */
    default
    void searchAll(String searchText, List<T> inputs, Function<T, String> labeller, BlockingQueue<T> queue) {
        for (T input : inputs) {
            if (search(labeller.apply(input), searchText)) {
                queue.add(input);
            }
        }
    }
}
