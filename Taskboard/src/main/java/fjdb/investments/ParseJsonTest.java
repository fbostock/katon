package fjdb.investments;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ParseJsonTest {

    public static void main(String[] args) throws IOException {


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        URL url = new URL("source");
//        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//        urlConnection.setRequestMethod("GET");
//        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));



//        JsonReader jsonReader = new JsonReader(br);
//        MakeUpClass makeUpClass = gson.fromJson(jsonReader, MakeUpClass.class);
        //then, manipulate as you require.

        String jsonString = gson.toJson(generate());
        StringReader stringReader = new StringReader(jsonString);
        ResolvedClass resolvedClass = gson.fromJson(stringReader, ResolvedClass.class);

        System.out.println(resolvedClass);

        /*
         This demonstrates how you can first serialise from one set of classes (SourceClass, SourceFilm) and then deserialise
         to completely different classes (ResolvedClass, ResolvedFilm). Simply match up the names in the json to fields you want
         to deserialise to, and it works!
         */
    }

    private static class MakeUpClass {
        //This class would represent the data of the json, with fields corresponding to the field names;
    }

    private static SourceClass generate() {
        List<SourceFilm> films = new ArrayList<>();
        films.add(new SourceFilm("Zulu", "1960"));
        films.add(new SourceFilm("Oppenheimer", "2023"));

        SourceClass sourceClass = new SourceClass("1", "15", films);
        return sourceClass;
    }

    private static class SourceClass {
        private String pageNum;
        private String totalPages;
        private List<SourceFilm> films;

        public SourceClass(String pageNum, String totalPages, List<SourceFilm> films) {
            this.pageNum = pageNum;
            this.totalPages = totalPages;
            this.films = films;
        }
    }

    private static class SourceFilm {
        String name;
        String productionStart;

        public SourceFilm(String name, String productionStart) {
            this.name = name;
            this.productionStart = productionStart;
        }
    }

    private static class ResolvedClass {
        private String pageNum;
        private String totalPages;
        private List<ResolvedFilm> films;

        public ResolvedClass(String pageNum, String totalPages, List<ResolvedFilm> films) {
            this.pageNum = pageNum;
            this.totalPages = totalPages;
            this.films = films;
        }
    }

    private static class ResolvedFilm {
        String name;
        String productionStart;

        public ResolvedFilm(String name, String productionStart) {
            this.name = name;
            this.productionStart = productionStart;
        }
    }


}
