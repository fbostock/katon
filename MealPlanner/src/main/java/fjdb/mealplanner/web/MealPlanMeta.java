package fjdb.mealplanner.web;

import java.time.Instant;

public class MealPlanMeta {

    /*
    * modified date stamp
    * a name unique to the mealplan - the state range of the plan.
    *
    * Use case 1:
    * I want a user to upload a mealplan to the server, and another user downloads it.
    * The first user then uploads a new version of it.
    * The second user polls the server, and see that the plan has metadata different to theirs.
    * They compare timestamps from the meta data to see that the server version is newer, and download it.
    *
    * Use case 2:
    * I want to store multiple mealplans on the server for different date ranges.
    * A user can see what plans are on the server, and choose to download one.
    *
    * I want to request a mealplan from the server by specifying a guid at the meal plan endpoint to
    * retrieve a particular mealplan.
    * I will get the guid by requesting the MealPlanMeta files from the server.
    *
    */
    private final String name;//name of the meal plan, e.g. MealPlan-2024-06-01_to_2024-06-30
    private final Long timestamp;//upload timestamp of the plan
    private final String uploadUser;//user who uploaded the plan.
    private final String json;//json of the mealplan

    public MealPlanMeta(String name, Instant timestamp, String uploadUser, String json) {
        this.name = name;
        this.timestamp = timestamp.getEpochSecond();
        this.uploadUser = uploadUser;
        this.json = json;
    }

    public String getName() {
        return name;
    }

    public Long getTime() {
        return timestamp;
    }
    public Instant getTimestamp() {
        return Instant.ofEpochSecond(timestamp);
    }

    public String getUploadUser() {
        return uploadUser;
    }

    public String getJson() {
        return json;
    }

    @Override
    public int hashCode() {
        return timestamp.hashCode() + name.hashCode();

    }


    @Override
    public boolean equals(Object obj) {
        return (obj instanceof MealPlanMeta other) &&
                other.name.equals(this.name) &&
                other.timestamp.equals(this.timestamp);
    }
}
