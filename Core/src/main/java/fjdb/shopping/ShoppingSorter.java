package fjdb.shopping;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.FuzzyScore;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
import java.util.stream.Collectors;

public class ShoppingSorter {

    public static void main(String[] args) {

        /*
        input list of items - this will be a string.
        Map each item in list to a known item or category. Use a matcher to closely match similar items like "egegs" to "eggs"

        Identify department in store where item to be found. Potentially multiple departments may be relevant.

        Identify location of departments in store, and order items based on that arrangement (assume moving round in anti-clockwise
        direction, i.e. up food aisles and back down the booze, cleaning, smellies, baby aisles.
         */

        List<String> inputs = List.of("milk", "eggs", "margarine", "plants", "marge", "blue milk");


        String marge = "blue milk";


        List<Item> allItems = Items.getAllItems();
//
//        for (Item allItem : allItems) {
//            System.out.printf("Fuzzy Item %s: %s\n", allItem.getName(), new FuzzyScore(Locale.getDefault()).fuzzyScore(allItem.getName(), marge));
//        }
//        for (Item allItem : allItems) {
//            System.out.printf("Jaro Item %s: %s\n", allItem.getName(), new JaroWinklerDistance().apply(allItem.getName(), marge));
//            System.out.printf("Jaro Item %s: %s\n", allItem.getName(), new JaroWinklerSimilarity().apply(allItem.getName(), marge));
//        }
//        for (Item allItem : allItems) {
//            System.out.printf("Levenshtein Item %s: %s\n", allItem.getName(), new LevenshteinDistance().apply(allItem.getName(), marge));
//        }

        //TODO could improve matching algorithm by making use of previous and next department matches - i.e. if they're the same
        //and we have an unknown one, could assume it's that enclosing department.


        Map<String, Item> itemMap = allItems.stream().collect(Collectors.toMap(Item::getName, item -> item));

        Map<Item, String> itemToInput = new HashMap<>();

        Set<Item> items = new HashSet<>();
        for (String item : inputs) {
            Item foundItem = findMatch(item, itemMap);

            if (items.contains(foundItem)) {
                foundItem = foundItem.copy();
            }
            items.add(foundItem);
            itemToInput.put(foundItem, item);
        }

        int i=0,j=0;

        Multimap<Department, Item> itemOrder = ArrayListMultimap.create();
        for (Item item : items) {
            Iterable<Department> departments = item.getDepartments();
            departments.forEach(d -> itemOrder.put(d, item));
        }

        TreeSet<Department> departmentOrder = new TreeSet<>(itemOrder.keySet());


        Set<Item> itemUsed = new HashSet<>();

        for (Department department : departmentOrder) {
            Collection<Item> items1 = itemOrder.get(department);
            for (Item item : items1) {
                if (itemUsed.contains(item)) continue;
                itemUsed.add(item);
                System.out.println(itemToInput.get(item) + "(" + item + ")");
            }
            System.out.println("");//new line for new department
        }
    }

    private static Item findMatch(String input, Map<String, Item> itemMap) {
        if (itemMap.containsKey(input)) {
            return itemMap.get(input);
        }
        String[] split = input.split("\\s");
        Collection<Item> values = itemMap.values();
        if (split.length > 1) {
            Set<String> parts = Set.of(split);
            Set<Item> matches = new HashSet<>();
            for (Item value : values) {
                if (parts.contains(value.getName())) {
                    matches.add(value);
                }
            }
            if (matches.size() == 1) {
                return matches.iterator().next();
            } else {
                return findMatch(input, matches);
            }
        }
        return findMatch(input, new HashSet<>(values));
        //TODO put in constraint such that if mathc is too poor, we return filler.
    }

    private static Item findMatch(String input, Set<Item> items) {
        return findJaroWinkler(input, items);
    }

    private static Item findFuzzy(String input, Set<Item> items) {
        FuzzyScore fuzzyScore = new FuzzyScore(Locale.getDefault());
        Item bestMatch = Items.filler;
        Integer best = Integer.MIN_VALUE;
        for (Item value : items) {
            Integer result = fuzzyScore.fuzzyScore(value.getName(), input);
            if (result > best) {
                best = result;
                bestMatch = value;
            }
        }
        return bestMatch;
    }

    private static Item findLevenshtein(String input, Set<Item> items) {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        Item bestMatch = Items.filler;
        Integer best = Integer.MAX_VALUE;
        for (Item value : items) {
            Integer result = levenshteinDistance.apply(value.getName(), input);
            if (result < best) {
                best = result;
                bestMatch = value;
            }
        }
        return bestMatch;
    }

    private static Item findJaroWinkler(String input, Set<Item> items) {

        JaroWinklerSimilarity jaroWinklerDistance = new JaroWinklerSimilarity();
        Item bestMatch = Items.filler;
        Double best = 0.0;
        for (Item value : items) {
            Double result = jaroWinklerDistance.apply(value.getName(), input);
            if (result > best) {
                best = result;
                bestMatch = value;
            }
        }
        return (best > 0.6) ? bestMatch : Items.filler;
    }

}
