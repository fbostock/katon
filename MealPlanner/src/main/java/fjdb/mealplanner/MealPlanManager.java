package fjdb.mealplanner;

import fjdb.threading.LazyInitializer;
import jersey.repackaged.com.google.common.collect.Lists;
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
import java.util.*;
import java.util.stream.Collectors;

public class MealPlanManager {

    private static final Logger log = LoggerFactory.getLogger(MealPlanManager.class);
    //    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-EEE");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE-dd-MM-yy");

    private final File directory;
    private final File csvDirectory;

    //    private final List<MealPlan> mealPlans = Lists.newArrayList();
    private final Map<LocalDate, MealPlan> mealPlans = new TreeMap<>();

    private final LazyInitializer<DishActionFactory> dishActionFactoryLazyInitializer = new LazyInitializer<>() {
        @Override
        public DishActionFactory make() {
            MealHistoryManager historyManager = new MealHistoryManager(LocalDate.now(), getAllMealPlans());
            return new DishActionFactory(historyManager);
        }
    };

    public MealPlanManager(File directory) {
        this.directory = directory;
        csvDirectory = makeCSVFolder(directory);
    }

    public File getDirectory() {
        return directory;
    }

    public File getCSVDirectory() {
        return csvDirectory;
    }

    private static File makeCSVFolder(File parent) {
        File csvFolder = new File(parent, "CSVs");
        if (csvFolder.exists()) {
            return csvFolder;
        } else {
            boolean mkdir = csvFolder.mkdir();
            if (mkdir) {
                return csvFolder;
            } else {
                log.warn("Could not create CSV folder ({}). Using parent folder instead: {} ", csvFolder, parent);
                return parent;
            }
        }
    }

    public void load() {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) continue;
            try {
                MealPlan deserializedPlan = deserialize(file);
                if (deserializedPlan != null) {
                    MealPlan previousPlan = mealPlans.get(deserializedPlan.getStart());
                    if (previousPlan != null) {
                        log.warn("Multiple plans with same start date {}", deserializedPlan.getStart());
                        if (deserializedPlan.getEnd().isBefore(previousPlan.getEnd())) {
                            log.warn("Did not load {} as plan with same start date already exists", deserializedPlan);
                            continue;
                        }
                    }
                    mealPlans.put(deserializedPlan.getStart(), deserializedPlan);
                }
            } catch (IOException e) {
                log.warn("Could not deserialize file {}", file.getName());
                e.printStackTrace();
            } catch (Exception other) {
                other.printStackTrace();
            }
//            if (file.getName().contains("Plan-") && !file.getName().toLowerCase().contains("csv")) {
//                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
//                    MealPlan deserialize = SerializationUtils.deserialize(bufferedInputStream.readAllBytes());
//                    mealPlans.add(deserialize);
//                } catch (Exception e) {
//                    log.warn("Could not deserialize file {}", file.getName());
//                    e.printStackTrace();
//                }
//            } else {
//                log.info("Skipping {} - not valid plan", file);
//            }
        }
    }

    protected MealPlan deserialize(File file) throws IOException {
        if (file.getName().contains("Plan-") && !file.getName().toLowerCase().contains("csv")) {
            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
                MealPlan deserialize = SerializationUtils.deserialize(bufferedInputStream.readAllBytes());
                return deserialize;
//            } catch (Exception e) {
//                log.warn("Could not deserialize file {}", file.getName());
//                e.printStackTrace();
            }
        } else {
            log.info("Skipping {} - not valid plan", file);
        }
        return null;
    }

    public void addMealPlan(MealPlan plan) {
        MealPlan oldPlan = mealPlans.put(plan.getStart(), plan);
        if (oldPlan != null) {
            System.out.printf("Replaced old plan starting on %s%n", oldPlan.getStart());
        }
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
        return filterByArchivedDate(false);
    }

    public List<MealPlan> getAllMealPlans(boolean sort) {
        List<MealPlan> mealPlans = getAllMealPlans();
        if (sort) {
            mealPlans = mealPlans.stream().sorted(Comparator.comparing(MealPlan::getStart)).collect(Collectors.toList());
        }
        return mealPlans;

    }

    public List<MealPlan> getAllMealPlans() {
        synchronized (mealPlans) {
            if (mealPlans.isEmpty()) {
                load();
            }
        }
        return new ArrayList<>(mealPlans.values());
    }


    public List<MealPlan> getArchived() {
        return filterByArchivedDate(true);
    }

    private List<MealPlan> filterByArchivedDate(boolean retrieveArchive) {
        LocalDate today = LocalDate.now().minusMonths(2);
        List<MealPlan> mealPlans = getAllMealPlans(true);
        List<MealPlan> archived = Lists.newArrayList();
        for (MealPlan mealPlan : mealPlans) {
            boolean isArchiveDate = mealPlan.getStart().isBefore(today);
            if (isArchiveDate == retrieveArchive) {
                archived.add(mealPlan);
            }
        }
        return archived;
    }

    private File toCSV(MealPlan plan, File file) {
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
        return toCSV(plan, new File(getCSVDirectory(), plan.getName() + ".csv"));
    }

    public DishActionFactory getDishActionFactory() {
        return dishActionFactoryLazyInitializer.get();
    }

    /**
     * First try /Users/username/, then app launch directory. If the latter does not exist, it creates it.
     * @return
     */
    public static File tryFindMealPlans() {
        String currentUsersHomeDir = System.getProperty("user.home");
        File mealPlansFolder = new File(currentUsersHomeDir, "MealPlans");
        if (mealPlansFolder.exists()) {
            return mealPlansFolder;
        }
        String userDir = System.getProperty("user.dir");
        mealPlansFolder = new File(userDir, "MealPlans");
        if (mealPlansFolder.exists()) {
            return mealPlansFolder;
        } else {
            mealPlansFolder.mkdir();
        }
        return mealPlansFolder;
    }
}
