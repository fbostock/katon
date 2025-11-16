package fjdb.mealplanner.persistence;

public class PersistedStates {

    public static void registerStateObjects() {
        StatePersister.STATE_PERSISTER.register(PlannerState.class, null);
    }

}
