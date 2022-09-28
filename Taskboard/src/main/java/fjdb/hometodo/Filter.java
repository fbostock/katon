package fjdb.hometodo;

import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Filter implements IFilter<TodoDataItem>{

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


    @Override
    public List<TodoDataItem> filter(List<? extends TodoDataItem> input) {
        Stream<? extends TodoDataItem> stream = input.stream();
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
