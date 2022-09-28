package fjdb.hometodo;

import fjdb.databases.ColumnsSet;

public class TodoItemFilter {

    public TodoItemFilter(ColumnsSet<TodoDataItem> columnSet) {

        /*
        want to potentially have filters for each field.
        Each column knows the type of each field.

        We need to specify the choices for each field. For enum fields the choices are the values from the enum. For other
        types, there wouldn't be predefined choices. We would need a way to override the choices so only particular types
        can be specified.


         */

    }



    /*
    private static class Filter {

        Set<Category> categories = Sets.newHashSet();
        Set<Term> terms = Sets.newHashSet();
        Set<Owner> owners = Sets.newHashSet();
        Set<Size> sizes = Sets.newHashSet();
        Set<Progress> progresses = Sets.newHashSet();

        public Filter() {
        }

        public Filter addCategory(Category category) {
            categories.add(category);
            return this;
        }

        public Filter addTerm(Term term) {
            terms.add(term);
            return this;
        }

        public Filter addOwner(Owner owner) {
            owners.add(owner);
            return this;
        }

        public Filter addSize(Size size) {
            sizes.add(size);
            return this;
        }

        public Filter addProgress(Progress progress) {
            progresses.add(progress);
            return this;
        }

        public void clear() {
            categories.clear();
            owners.clear();
            sizes.clear();
            terms.clear();
            progresses.clear();
        }


        List<TodoDataItem> filter(List<TodoDataItem> input) {
            Stream<TodoDataItem> stream = input.stream();
            if (!owners.isEmpty()) {
                stream = stream.filter(item -> owners.contains(item.getOwner()));
            }
            if (!categories.isEmpty()) {
                stream = stream.filter(item -> categories.contains(item.getCategory()));
            }
            if (!terms.isEmpty()) {
                stream = stream.filter(item -> terms.contains(item.getTerm()));
            }
            if (!sizes.isEmpty()) {
                stream = stream.filter(item -> sizes.contains(item.getSize()));
            }
            if (!progresses.isEmpty()) {
                stream = stream.filter(item -> progresses.contains(item.getProgress()));
            }
            return stream.collect(Collectors.toList());
        }
    }
     */
}
