package fjdb.util.functions;

import java.util.function.Function;

/**
 * Provides an interface and utility method for wrapping functions that throw checked exceptions into a
 * function that throws an unchecked exception. This can be used for instance in streams which require the use
 * of lambdas which don't throw checked exceptions.
 * For instance:
 * myList.stream()
 *        .map(wrap(item -> doSomething(item)))
 *        .forEach(System.out::println);
 * @param <T>
 * @param <R>
 */

@FunctionalInterface
public interface CheckedFunction<T, R> {

    public R apply(T t) throws Exception;

    public static <T, R> Function<T, R> wrap(CheckedFunction<T, R> checkedFunction) {
        return t -> {
            try {
                return checkedFunction.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
