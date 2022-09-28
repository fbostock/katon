package fjdb.hometodo;

import java.util.List;

public interface IFilter<T> {

    List<T> filter(List<? extends T> input);

}
