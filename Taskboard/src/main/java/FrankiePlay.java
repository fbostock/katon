import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Frankie Bostock on 11/06/2017.
 */
public class FrankiePlay {

    @Deprecated
    public int myfield = 10;

    public static void main(String[] args) throws IOException {

//        Class<FrankiePlay> frankiePlayClass = FrankiePlay.class;
//        Field[] declaredFields = frankiePlayClass.getDeclaredFields();
//        System.out.println(declaredFields);


//        System.out.println(declaredFields1);
//    if (true) return;

        //        System.out.println(String.format("%s", test(1.12)));

//        File directory = new File("/Users/francisbostock/Desktop/bostockwebsite/php/CampaignGP/MainCampaignSection");
        File directory = new File("/Users/francisbostock/Desktop/BostockMedalsWebsite/BACKUPS/20230321/php/CampaignGP/MainCampaignSection");
        File itemList = new File(directory, "ItemList.txt");
//        File itemList = new File(file, "TempItemList.txt");
        File removed = new File(directory, "removeditems.txt");



        File[] list = directory.listFiles();
        List<File> files = Lists.newArrayList(list);
        Collections.sort(files);

//        for (File file : files) {
//            List<String> lines = getLines(file);
//            String join = Joiner.on(",").join(lines);
//            if (join.contains("B933")) {
//                System.out.println(file);
//                break;
//            }
//        }
//        if (true) return;
        BufferedReader bufferedReader1 = new BufferedReader(new FileReader(itemList));
        List<String> listOfFilenames = Lists.newArrayList();
        String line1;
        while ((line1 = bufferedReader1.readLine()) != null) {
            listOfFilenames.add(line1);
        }

        List<File> listOfFiles = Lists.newArrayList();
        for (String s : listOfFilenames) {
            File fileName = new File(directory, s + ".txt");
            listOfFiles.add(fileName);
        }

//        File imageFile = new File("/Users/francisbostock/Desktop/bostockwebsite/Backups/Backup_20210818/public_html/BstMilMMVI/NeuesSys/CampaignGP/CampaignGPPics");
        File imageFile = new File("/Users/francisbostock/Desktop/BostockMedalsWebsite/BACKUPS/20230321/BstMilMMVI/NeuesSys/CampaignGP/CampaignGPPics");
        File[] images = imageFile.listFiles();
        List<String> imageNames = Lists.newArrayList();
        for (File image : images) {
            String name = image.getName();
            name = name.replace(".JPG", ".jpg");
            imageNames.add(name);
        }

        Pattern pattern = Pattern.compile("(?i).*/?(.*\\.jpg).*");
        Pattern subpattern = Pattern.compile("(?i)/([\\w-]*\\.jpg)");


        List<String> matches = new ArrayList<>();
        List<String> usedImages = new ArrayList<>();
        Map<File, String> matchesFiles = new TreeMap<>();

        for (File thing : listOfFiles) {
            List<String> lines = getLines(thing);
            boolean hasMatch = false;
            for (String myLine : lines) {
                Matcher matcher = pattern.matcher(myLine);
                if (matcher.matches()) {
//                    matches.add(matcher.group(1));
                    matches.add(matcher.group());
                    usedImages.add(matcher.group().replace(".JPG", ".jpg"));
                    matchesFiles.put(thing, matcher.group());
                    hasMatch = true;
//                    break;
                }

            }

            if (!hasMatch) {
                System.out.println(thing);
            }
        }

        /*
        for (Map.Entry<File, String> entry : matchesFiles.entrySet()) {
            File key = entry.getKey();
            String jpeg = entry.getValue();
            if (imageNames.contains(jpeg.replace(".JPG", ".jpg"))) {
                //System.out.println("KEEP " + key);
            } else {
                System.out.println("Should remove " + key);
            }
        }*/

        int totalImages = imageNames.size();
        int totalUsedImages = usedImages.size();
        List<String> expandedUsedImages = Lists.newArrayList();
        for (int i = 0; i < usedImages.size(); i++) {
            String s = usedImages.get(i);
            if (s.contains("CIMGX404")) {
                System.out.println();
            }
            if (s.contains("/")) {
                Matcher matcher = subpattern.matcher(s);
                while (matcher.find()) {
                    expandedUsedImages.add(matcher.group(1));
                }
            } else {
                expandedUsedImages.add(s);
            }
        }

        imageNames.removeAll(expandedUsedImages);
        int remainingImages = imageNames.size();

        System.out.println(String.format("Images: %s, Used in files %s not used %s", totalImages, totalUsedImages, remainingImages));

        for (String imageName : imageNames) {
            System.out.println(imageName);
        }

        File tempItemList = new File(directory, "AllItems.txt");
        List<String> lines = getLines(tempItemList);

        List<File> tempFiles = Lists.newArrayList();
        for (String s : lines) {
//            File fileName = new File(directory, s + ".txt");
            File fileName = new File(directory, s);
            tempFiles.add(fileName);
        }

        Map<File, List<String>> fileEntries = getFileContents(tempFiles);
        Map<File, String> fileEntriesAsString = new HashMap<>();
        for (Map.Entry<File, List<String>> entry : fileEntries.entrySet()) {
            String join = Joiner.on(" ").join(entry.getValue());
            fileEntriesAsString.put(entry.getKey(), join.toLowerCase());
        }


        for (String imageName : imageNames) {
            boolean match = false;
            for (Map.Entry<File, String> entry : fileEntriesAsString.entrySet()) {
                if (entry.getValue().contains(imageName.toLowerCase())) {
                    System.out.println(String.format("File %s contains unused image %s", entry.getKey(), imageName));
                    match = true;
                    break;
                }
            }
            if (!match) {
                System.out.println(String.format("No match found for %s", imageName));
            }
        }


        if (true) return;

        BufferedReader bufferedReader = new BufferedReader(new FileReader(itemList));
        List<String> fileNames = Lists.newArrayList();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            fileNames.add(line);
        }
        bufferedReader.close();

        bufferedReader = new BufferedReader(new FileReader(removed));
        List<String> removedFiles = Lists.newArrayList();
        while ((line = bufferedReader.readLine()) != null) {
            removedFiles.add(line);
        }

        removedFiles.add("MainCampaignSection343");

        List<File> acceptedFiles = Lists.newArrayList();
        for (File file1 : files) {
            if (file1.getName().contains("Campaign")) {
                acceptedFiles.add(file1);
            }
        }

        Iterator<File> iterator = acceptedFiles.iterator();
        while (iterator.hasNext()) {
            File next = iterator.next();
            if (removedFiles.contains(next.getName().replace(".txt", ""))) {
                System.out.println("Removing " + next);
                iterator.remove();
            }
        }

        FileWriter fileWriter = new FileWriter(itemList);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (int i = 0; i < acceptedFiles.size(); i++) {
            File acceptedFile = acceptedFiles.get(i);
            bufferedWriter.write(acceptedFile.getName().replace(".txt", ""));
            if (i < acceptedFiles.size() - 1) {
                bufferedWriter.newLine();
            }
        }

        bufferedWriter.close();

        //get list of all files from removed files.
        //get list of all files in folder of the correct formats
        //
        if (true) return;
        Double target = 152000.0;

//    double initial = 1.05;
        double initial = 2.0;
        double increment = 0.01;
        double r = initial;
        boolean decreasing = true;
        while (true) {
            //if value positive, need to decrease r
            double value = test(r) - target;
            if (value > 0) {
                if (!decreasing) {
                    decreasing = !decreasing;
                    increment /= 10.0;
                }
                r -= increment;
            } else {
                if (decreasing) {
                    increment /= 10.0;
                    decreasing = !decreasing;
                }
                r += increment;
            }
            if (Math.abs(value) < 1.0) break;
        }
        System.out.println("Rate: " + r);

        //initial * (1+r)^t
/*
Start Sep 2017, but assume at April 2018.
2018: 71000
2019: x + 20000
2020: z + 20000
2021:

(((71000 * R) + 20000) * R + 20000) * R = 148000


 */

    }

    private static double test(Double r) {
        return (((71000 * r) + 20000) * r + 20000) * r;
    }

    /*

    TODO
    add the maven runnable jar plugin


     */


    //TODO add a task or epic which lists tasks which are for design planning, such as a framework for persisting data
    //to xml, such that a given interface enforces the components to both write the data to file as well as read it back in again.
    //such tasks could be done on the train.

    /*private String getTradeString(Trade trade) {
        ArrayList<String> list = Lists.newArrayList();
        list.add("'" + trade.getInstrument() +"'");
        //TODO add a DateFormatters class to print these as required.
//        list.add(trade.getTradeDate().toString());
        LocalDate tradeDate = trade.getTradeDate();
        String date = DateTimeUtil.print(tradeDate);
        list.add(date == null ? "NULL" : date);
        list.add(String.valueOf(trade.getQuantity()));
        //TODO should have a price object which holds a value and currency
        list.add(String.valueOf(trade.getPrice()));
        //TODO column needs to handle conversion between currencies and back
        list.add("'"+trade.getCurrency().toString()+"'");
        list.add(String.valueOf(trade.getFixing()));
        return Joiner.on(",").join(list);
    }*/

    private static List<String> getLines(File file) {

        List<String> list = Lists.newArrayList();
        try (BufferedReader mybufferedReader = new BufferedReader(new FileReader(file))) {
            String myLine = null;
            while ((myLine = mybufferedReader.readLine()) != null) {
                list.add(myLine);
            }
        } catch (Exception ex) {
            System.out.println("Failed to process file " + file);
            ex.printStackTrace();
        }
        return list;
    }

    private static Map<File, List<String>> getFileContents(List<File> files) {
        Map<File, List<String>> fileEntries = new HashMap<>();
        for (File tempFile : files) {
            fileEntries.put(tempFile, getLines(tempFile));
        }
        return fileEntries;
    }

}
