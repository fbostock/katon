package fjdb;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Play {

    public static void main(String[] args) throws IOException {

        Pattern pattern = Pattern.compile("(?i).*/(.*\\.jpg).*");
//        Pattern pattern = Pattern.compile("(?i).*(.*\\.jpg).*");

        List<String> matches = new ArrayList<>();

//        File file = new File("/Users/francisbostock/Desktop/bostockwebsite/jsFiles/BoerWar/picsToSave.txt");
//        File file = new File("/Users/francisbostock/Desktop/bostockwebsite/jsFiles/Gallantry/picsToSave.txt");
        File file = new File("/Users/francisbostock/Desktop/bostockwebsite/jsFiles/SingVicFiles/picsToSave.txt");
//        File file = new File("/Users/francisbostock/Desktop/bostockwebsite/jsFiles/CampaignGP/picsToSave.txt");
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {

            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                matches.add(matcher.group(1));
            }

        }

        Collections.sort(matches);

        for (String match : matches) {
            System.out.println(match);
        }
        System.out.println(matches.size());
    }

    /*

     */


}
