package fjdb.mealplanner.web;

import fjdb.mealplanner.Meal;
import fjdb.mealplanner.MealPlan;
import fjdb.mealplanner.events.EventProcessor;
import fjdb.mealplanner.events.MealEvent;
import fjdb.threading.Threading;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class MealWebServer {

    private volatile List<Meal> mealsFromServer = new ArrayList<>();
    private final AtomicBoolean hasFetched = new AtomicBoolean(false);
    private final AtomicBoolean hasFetchedPlans = new AtomicBoolean(false);
    private volatile List<MealPlanMeta> mealPlansFromServer = new ArrayList<>();
    private final AtomicBoolean hasUpdates = new AtomicBoolean(false);


    public MealWebServer() {
        attemptMealFetch();
    }

    public List<Meal> getServerMeals() {
        if (hasFetched.get()) {
            return mealsFromServer;
        } else {
            return List.of();
        }
    }

    public List<MealPlanMeta> getServerMealPlans() {
        if (hasFetchedPlans.get()) {
            return mealPlansFromServer;
        } else {
            return List.of();
        }
    }

    public List<Meal> requestMealList() {
        Optional<List<Meal>> meals = MealWebServerFunctions.requestMealList();
        if (meals.isPresent()) {
            return meals.get();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Could not connect to server");
            alert.show();
            return List.of();
        }
    }

    private List<MealPlanMeta> requestMealPlans() {
        return MealWebServerFunctions.requestMealPlanList();
    }

    public void attemptMealFetch() {
        hasFetched.set(false);
        hasFetchedPlans.set(false);
        Threading.runAndReturn(List.of(() -> {
                    ArrayList<Meal> oldMeals = new ArrayList<>(mealsFromServer);
                    mealsFromServer = requestMealList();
                    System.out.printf("Meals have been fetched from server (%s)%n", mealsFromServer.size());
                    hasFetched.set(true);
                    if (!oldMeals.equals(mealsFromServer)) {
                        hasUpdates.set(true);
                    }
                    EventProcessor.getInstance().processEvent(new MealEvent(MealEvent.SERVER_EVENT));
                }, () -> {
                    ArrayList<MealPlanMeta> oldMealPlanMetas = new ArrayList<>(mealPlansFromServer);
                    mealPlansFromServer = requestMealPlans();
                    if (!oldMealPlanMetas.equals(mealPlansFromServer)) {
                        hasUpdates.set(true);
                    }
                    System.out.printf("MealPlans have been fetched from server (%s)%n", mealPlansFromServer.size());
                    hasFetchedPlans.set(true);
                }
        ));
    }

    public void uploadMealList(List<Meal> meals) throws IOException {
        boolean success = MealWebServerFunctions.uploadMealList(meals);
        Alert alert;
        if (success) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Upload successful");
        } else {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Server unavailable");
        }
        alert.show();
    }

    public void uploadMealPlan(MealPlan mealPlan) {
        MealWebServerFunctions.uploadMealPlan(mealPlan);
    }

    public boolean hasUpdates() {
        if (hasFetched.get() && hasFetchedPlans.get()) {
            return hasUpdates.getAndSet(false);
        }
        return false;
    }

    //TODO we will need to provide a GUID to the MealList to identify new ones on the server.
}
