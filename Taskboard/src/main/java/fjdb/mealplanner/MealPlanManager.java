package fjdb.mealplanner;

import com.google.common.collect.Lists;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class MealPlanManager {

    private static final Logger log = LoggerFactory.getLogger(MealPlanManager.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final File directory;
    private final List<MealPlan> mealPlans = Lists.newArrayList();


    public MealPlanManager(File directory) {
        this.directory = directory;
    }

    public File getDirectory() {
        return directory;
    }

    public void load() {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.getName().contains("Plan-") && !file.getName().toLowerCase().contains("csv")) {
                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
                    MealPlan deserialize = SerializationUtils.deserialize(bufferedInputStream.readAllBytes());
                    mealPlans.add(deserialize);
                } catch (Exception e) {
                    log.warn("Could not deserialize file {}", file.getName());
                    e.printStackTrace();
                }
            } else {
                log.info("Skipping {} - not valid plan", file);
            }
        }
    }

    public void addMealPlan(MealPlan plan) {
        mealPlans.add(plan);
        byte[] serialize = SerializationUtils.serialize(plan);
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(directory, plan.getName()))) {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
                bufferedOutputStream.write(serialize);
            }
            log.info("Created meal plan: {}", plan.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<MealPlan> getMealPlans() {
        return mealPlans;
    }

    public File toCSV(MealPlan plan, File file) {
        String[] headers = new String[]{MealPlan.DATE, MealPlan.UNFREEZE, MealPlan.COOK, MealPlan.BREAKFAST, MealPlan.LUNCH, MealPlan.DINNER};
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(file), CSVFormat.DEFAULT.withHeader(headers))) {
            List<LocalDate> dates = plan.getDates();
            for (LocalDate date : dates) {
                DayPlanIF dayPlan = plan.getPlan(date);
                Object[] details = new Object[]{formatter.format(date), dayPlan.getUnfreeze(), dayPlan.getToCook(), dayPlan.getBreakfast().getDescription(), dayPlan.getLunch().getDescription(), dayPlan.getDinner().getDescription()};
                printer.printRecord(details);
            }
        } catch (IOException e) {
            log.warn("Failed to write entry to csv {}", file, e);
        }
        return file;
    }

    public MealPlan fromCSV(File file) {
//TODO not necessary, as we won't generally be importing from excel.
        try {
            MealPlanBuilder builder = new MealPlanBuilder();
            CSVParser records = CSVFormat.DEFAULT.parse(new FileReader(file));
            Map<String, Integer> headerMap = records.getHeaderMap();
            for (CSVRecord record : records) {
                String dateString = record.get(MealPlan.DATE);
                LocalDate date = LocalDate.parse(dateString, formatter);
//                builder.setBreakfast();

            }
//            CSVParser parse = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.DEFAULT);
//            parse.re
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public File toCSV(MealPlan plan) {
        return toCSV(plan, new File(directory, plan.getName() + ".csv"));
    }
}
