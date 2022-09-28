package fjdb.mealplanner.search;

import fjdb.util.StringUtil;

public class SimpleSearch<T> implements SearchAlgo<T> {

    @Override
    public boolean search(String input, String searchText) {
        if (searchText.isBlank()) return false;
        if (StringUtil.containsUpperCase(searchText)) {
            return input.contains(searchText);
        } else {
            return input.toLowerCase().contains(searchText);
        }
    }


}
