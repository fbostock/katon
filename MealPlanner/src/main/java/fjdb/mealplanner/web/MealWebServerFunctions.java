package fjdb.mealplanner.web;

import com.google.gson.*;
import fjdb.mealplanner.*;
import fjdb.threading.Threading;
import javafx.scene.control.Alert;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.tools.ant.taskdefs.Local;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
This is not an actual webserver, but serves as an interface for messaging with the webserver managed by the DemoApplication
in the demo repo.
 */
public class MealWebServerFunctions {

    private static final String mealListWriteEndpoint = "/api/mealswrite";
    private static final String mealListFetchEndpoint = "/api/meallist";
    private static final String mealPlanWriteEndpoint = "/api/mealplanwrite";
    private static final String mealPlanFetchEndpoint = "/api/mealplan";
    private static final String mealPlanMetaWriteEndpoint = "/api/mealplanmeta";
    private static final String mealPlanMetaListFetchEndpoint = "/api/mealplanmetalist";
    private static final String mealPlanMetaFetchEndpoint = "/api/mealplanmeta";


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

    public static boolean uploadMealList(List<Meal> meals) throws IOException {
        //TODO put this on a separate thread, so it doesn't hang the UI if the server is down.
        if (!attemptPostMeals(meals)) {
            File dishFile = new File(getWebserverFolder(), "meallist.json");
            String json = new Gson().toJson(new MealList(meals));
            FileWriter writer = new FileWriter(dishFile);
            writer.write(json);
            writer.close();
            return false;
        }
        return true;
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

    static Optional<List<Meal>> requestMealList() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(getServerUrl() + mealListFetchEndpoint))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            MealList list = new Gson().fromJson(response.body(), MealList.class);
            return Optional.of(list.getMeals());
        } catch (IOException | InterruptedException | URISyntaxException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
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
//            if (!attemptPostMealPlan(mealPlan)) {
            if (!attemptPostMealPlanMeta(mealPlan)) {
                System.out.println("Failed to upload to server. Try saving to file");
                File mealPlanFile = new File(getWebserverFolder(), "mealplan");

                MealPlanManager.serializePlan(mealPlanFile, mealPlan);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("MealPlan successfully uploaded");
                alert.show();
            }
        }));
    }

    private static boolean attemptPostMealPlanMeta(MealPlan plan) {
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .registerTypeAdapter(Instant.class, new InstantAdapter())
                    .create();
            String json = gson.toJson(plan);
            MealPlanMeta mealPlanMeta = new MealPlanMeta(plan.getName(), Instant.now(), System.getProperty("user.name"), json);
            String metaJson = gson.toJson(mealPlanMeta);
            return WebUtils.attemptPost(metaJson, URI.create(getServerUrl() + mealPlanMetaWriteEndpoint));
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static List<MealPlanMeta> requestMealPlanList() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(getServerUrl() + mealPlanMetaListFetchEndpoint))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //            List<String> list = new Gson().fromJson(response.body(), List.class);
//            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .registerTypeAdapter(Instant.class, new InstantAdapter())
                    .registerTypeAdapter(DayPlanIF.class, new DayPlanAdapter())
                    .create();
            java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<List<MealPlanMeta>>() {}.getType();
            List<MealPlanMeta> mealPlanMeta = gson.fromJson(response.body(), listType);
            return mealPlanMeta;
//            return gson.fromJson(mealPlanMeta.getJson(), List.class);
//            return SerializationUtils.deserialize(new BufferedInputStream(new ByteArrayInputStream(response.body())));
        } catch (IOException | InterruptedException | URISyntaxException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static MealPlan requestMealPlan2(MealPlanMeta mealPlanMetaName) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(getServerUrl() + mealPlanMetaFetchEndpoint + "/?name=" + mealPlanMetaName.getName()))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //            List<String> list = new Gson().fromJson(response.body(), List.class);
//            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .registerTypeAdapter(Instant.class, new InstantAdapter())
                    .registerTypeAdapter(DayPlanIF.class, new DayPlanAdapter())
                    .create();

            MealPlanMeta mealPlanMeta = gson.fromJson(response.body(), MealPlanMeta.class);
            return gson.fromJson(mealPlanMeta.getJson(), MealPlan.class);
//            return SerializationUtils.deserialize(new BufferedInputStream(new ByteArrayInputStream(response.body())));
        } catch (IOException | InterruptedException | URISyntaxException ex) {
            ex.printStackTrace();
        }
        return null;
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

    public static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        private final DateTimeFormatter formatter;

        public LocalDateAdapter() {
            this(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        public LocalDateAdapter(DateTimeFormatter formatter) {
            this.formatter = formatter;
        }

        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(formatter));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return LocalDate.parse(json.getAsString(), formatter);
            } catch (Exception ex) {
                throw new JsonParseException(ex);
            }
        }
    }


    public static class InstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

        private final DateTimeFormatter formatter;

        public InstantAdapter() {
            this(DateTimeFormatter.ISO_INSTANT);
        }

        public InstantAdapter(DateTimeFormatter formatter) {
            this.formatter = formatter;
        }

        @Override
        public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return JsonNull.INSTANCE;
            }
            return new JsonPrimitive(formatter.format(src));
        }

        @Override
        public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null || json.isJsonNull()) {
                return null;
            }
            try {
                return Instant.from(formatter.parse(json.getAsString()));
            } catch (Exception ex) {
                throw new JsonParseException(ex);
            }
        }
    }


    private static class DayPlanAdapter implements JsonSerializer<DayPlanIF>, JsonDeserializer<DayPlanIF> {

        @Override
        public JsonElement serialize(DayPlanIF src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return JsonNull.INSTANCE;
            }
            // Delegate serialization to DayPlan to produce JSON for the concrete type
            return context.serialize(src, DayPlan.class);
        }

        @Override
        public DayPlanIF deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null || json.isJsonNull()) {
                return null;
            }
            try {
                // Delegate deserialization to DayPlan class
                return context.deserialize(json, DayPlan.class);
            } catch (JsonParseException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new JsonParseException(ex);
            }
        }
    }


    public static void main(String[] args) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();

        MealPlanMeta mealPlanMeta = new MealPlanMeta("fdsfd", Instant.now(), System.getProperty("user.name"), "");
        String metaJson = new Gson().toJson(mealPlanMeta);

        Instant now = Instant.now();
        String json = gson.toJson(now); // e.g. "2025-11-04T12:34:56Z"
        System.out.println(json);

        Instant parsed = gson.fromJson(json, Instant.class);
        System.out.println(parsed);
    }
}
