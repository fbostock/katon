package fjdb.mealplanner.web;

import com.google.gson.Gson;
import fjdb.mealplanner.*;
import fjdb.threading.Threading;
import org.apache.commons.lang3.SerializationUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

/*
This is not an actual webserver, but serves as an interface for messaging with the webserver managed by the DemoApplication
in the demo repo.
 */
public class MealWebServer {

    private static final String mealListWriteEndpoint = "/api/mealswrite";
    private static final String mealListFetchEndpoint = "/api/meallist";
    private static final String mealPlanWriteEndpoint = "/api/mealplanwrite";
    private static final String mealPlanFetchEndpoint = "/api/mealplan";


    private static String getServerUrl() {
        if (MealPlanner.isMasterApplication()) {
            return "http://localhost:8080";
        } else {
            return "http://192.168.0.46:8080";
        }
    }

    public static File getWebserverFolder() {
        File mealPlansDirectory = MealPlanManager.tryFindMealPlans();
        File webserver = new File(mealPlansDirectory, "webserver");
        File webServer = webserver;
        if (!webserver.exists()) {
            webServer.mkdir();
        }
        return webserver;
    }

    public static void uploadDishes(List<Dish> dishes) throws IOException {
        //TODO put this on a separate thread, so it doesn't hang the UI if the server is down.
        if (!attemptPost(dishes)) {
            File dishFile = new File(getWebserverFolder(), "dishlist.json");
            String json = new Gson().toJson(dishes.stream().map(Dish::getName).collect(Collectors.toList()));
            FileWriter writer = new FileWriter(dishFile);
            writer.write(json);
            writer.close();
        }
    }

    public static void uploadMealList(List<Meal> meals) throws IOException {
        //TODO put this on a separate thread, so it doesn't hang the UI if the server is down.
        if (!attemptPostMeals(meals)) {
            File dishFile = new File(getWebserverFolder(), "meallist.json");
            String json = new Gson().toJson(new MealList(meals));
            FileWriter writer = new FileWriter(dishFile);
            writer.write(json);
            writer.close();
        }
    }

    private static boolean attemptPost(List<Dish> dishes) {
        try {
            return WebUtils.attemptPost(new Gson().toJson(dishes), URI.create(getServerUrl() + "/api/dishlistwrite"));
        } catch (Exception ex) {
            return false;
        }
    }

    private static boolean attemptPostMeals(List<Meal> meals) {
        try {
            return WebUtils.attemptPost(new Gson().toJson(new MealList(meals)), URI.create(getServerUrl() + mealListWriteEndpoint));
        } catch (Exception ex) {
            return false;
        }
    }

    public static List<Dish> requestDishList() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(getServerUrl() + "/api/dishlist"))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            DishList list = new Gson().fromJson(response.body(), DishList.class);
            return list.getDishes();
        } catch (IOException | InterruptedException | URISyntaxException ex) {
            ex.printStackTrace();
        }
        return List.of();
    }

    public static List<Meal> requestMealList() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(getServerUrl() + mealListFetchEndpoint))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            MealList list = new Gson().fromJson(response.body(), MealList.class);
            return list.getMeals();
        } catch (IOException | InterruptedException | URISyntaxException ex) {
            ex.printStackTrace();
        }
        return List.of();
    }




    public static List<Dish> requestDishes() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(getServerUrl() + "/api/meals"))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            List<String> list = new Gson().fromJson(response.body(), List.class);
            return list.stream().map(item -> new Dish(item, "")).toList();
        } catch (IOException | InterruptedException | URISyntaxException ex) {
            ex.printStackTrace();
        }

        return List.of();
    }

    public static void uploadMealPlan(MealPlan mealPlan) {
        Threading.runAndReturn(List.of(() -> {
            System.out.println("Attempting to upload mealplan to server " + mealPlan);
            if (!attemptPostMealPlan(mealPlan)) {
                System.out.println("Failed to upload to server. Try saving to file");
                File mealPlanFile = new File(getWebserverFolder(), "mealplan");

                MealPlanManager.serializePlan(mealPlanFile, mealPlan);
            }
        }));
    }

    private static boolean attemptPostMealPlan(MealPlan plan) {
        try {
            return WebUtils.attemptPost(SerializationUtils.serialize(plan), URI.create(getServerUrl() + mealPlanWriteEndpoint));
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static MealPlan requestMealPlan() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(getServerUrl() + mealPlanFetchEndpoint))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            return SerializationUtils.deserialize(new BufferedInputStream(new ByteArrayInputStream(response.body())));
        } catch (IOException | InterruptedException | URISyntaxException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static class DishList {
        private final List<Dish> dishes;

        public DishList(List<Dish> dishes) {
            this.dishes = dishes;
        }


        public List<Dish> getDishes() {
            return dishes;
        }
    }

    private static class MealList {
        private final List<Meal> meals;

        public MealList(List<Meal> meals) {
            this.meals = meals;
        }


        public List<Meal> getMeals() {
            return meals;
        }
    }
}
