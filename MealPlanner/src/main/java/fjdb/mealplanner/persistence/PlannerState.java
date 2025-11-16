package fjdb.mealplanner.persistence;

import fjdb.mealplanner.Meal;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang3.SerializationUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PlannerState implements Serializable {

    private List<Meal> mealsInFreezer = new ArrayList<>();

    public PlannerState() {
    }

    public List<Meal> getMealsInFreezer() {
        return mealsInFreezer;
    }

    public void addMealInFreezer(Meal mealInFreezer) {
        this.mealsInFreezer.add(mealInFreezer);
    }

    public void saveData() {
        byte[] serialize = SerializationUtils.serialize(this);
        try (FileOutputStream fileOutputStream = new FileOutputStream(getFile())) {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
                bufferedOutputStream.write(serialize);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return null;
    }


    /*
    Have a class that is responsible for handling data persistence. e.g. StatePersister
    Objects that represent persisted state should have some interface and get registered with that StatePersister
    Registration should include a file to store the persisted data, or being created dynamically based on the class type
    if not provided.
    On app loading, the StatePersister should deserialise all persisted state.
    On app closing, it should attempt to persist those state objects, using some sort of shutdown hook.



     */

}
