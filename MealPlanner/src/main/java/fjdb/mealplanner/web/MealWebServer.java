package fjdb.mealplanner.web;

import fjdb.mealplanner.GeneralNotesImpl;
import fjdb.mealplanner.Meal;
import fjdb.mealplanner.MealPlan;
import fjdb.mealplanner.events.EventProcessor;
import fjdb.mealplanner.events.MealEvent;
import fjdb.threading.Threading;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.time.Instant;
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

    private volatile GeneralNotesImpl generalNotes;


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

    public GeneralNotesImpl getGeneralNotes() {
        return generalNotes;
    }

    public GeneralNotesImpl refreshGeneralNotes() {
        return MealWebServerFunctions.requestGeneralNotes().orElse(null);
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
                }, () -> {
                    Optional<GeneralNotesImpl> optional = MealWebServerFunctions.requestGeneralNotes();
                    if (optional.isPresent()) {
                        generalNotes = optional.get();
                        System.out.println("General notes have been fetched from server.");
                    } else {
                        System.out.println("Could not fetch general notes from server.");
                    }
                }
        ));
    }

    public void uploadGeneralNotes(GeneralNotesImpl notes) {
        MealWebServerFunctions.uploadGeneralNotes(notes);
        this.generalNotes = notes;
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

    /**
     * Uploads the given meal plan to the server if a plan with the same name does not already exist there.
     */
    public void uploadMealPlanIfNew(MealPlan mealPlan) {
        Threading.runAndReturn(List.of(
                () -> {
                    while (!hasFetchedPlans.get()) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    String planName = mealPlan.getName();
                    //if there is no plan on the server with the same name, upload it.
                    //if there is, check the timestamp to see if it is older than a day, and if so, upload it as a new version.
                    Optional<MealPlanMeta> metaData = getServerMealPlans().stream().filter(meta -> meta.getName().equals(planName)).findFirst();
                    if (metaData.isPresent()) {
                        Instant timestamp = metaData.get().getTimestamp();
                        Instant now = Instant.now();
                        if (timestamp.plusSeconds(86400).isBefore(now)) {
                            //server version is older than a day, upload new version
                            uploadMealPlan(mealPlan);
                        }
                    } else {
                        uploadMealPlan(mealPlan);
                    }
                }
        ));
    }

    /*
    User has two meal plans. With their own names, PlanA and PlanB.
    On the server there is PlanA but no PlanB.

I want the to check the most recent mealplans (say 2 or 3) against the ones on teh server.
If a Plan is missing on the Server, I want to upload it.
If a plan is on the server but not locally, I want to download it.
If a plan is both on the server and local, I want to compare timestamps to see if either is newer, and update accordingly.




     */
}
