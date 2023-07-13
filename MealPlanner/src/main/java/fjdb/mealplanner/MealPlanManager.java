package fjdb.mealplanner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import fjdb.threading.LazyInitializer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
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
        }
    }

    protected MealPlan deserialize(File file) throws IOException {
        if (file.getName().contains("Plan-") && !file.getName().toLowerCase().contains("csv")) {
            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
                return SerializationUtils.deserialize(bufferedInputStream.readAllBytes());
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

    private File toExcel(MealPlan plan, File file) {
        try {
            String[] headers = new String[]{MealPlan.DATE, MealPlan.UNFREEZE, MealPlan.COOK, MealPlan.BREAKFAST, MealPlan.LUNCH, MealPlan.DINNER};
            List<LocalDate> dates = plan.getDates();
            Workbook workbook = new HSSFWorkbook();

            Sheet sheet = workbook.createSheet("MealPlan");
            sheet.setColumnWidth(0, 4000);
            sheet.setColumnWidth(1, 6000);
            for (int i = 2; i < headers.length; i++) {
                sheet.setColumnWidth(i, 6000);
            }

            Row header = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                String label = headers[i];
                Cell headerCell = header.createCell(i);
                headerCell.setCellValue(label);
            }

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            for (int rowNumber = 0; rowNumber < dates.size(); rowNumber++) {
                LocalDate date = dates.get(rowNumber);
                DayPlanIF dayPlan = plan.getPlan(date);
                Object[] details = new Object[]{formatter.format(date), dayPlan.getUnfreeze(), dayPlan.getToCook(), dayPlan.getBreakfast().getDescription(), dayPlan.getLunch().getDescription(), dayPlan.getDinner().getDescription()};
                Row row = sheet.createRow(rowNumber + 1);
                for (int i = 0; i < details.length; i++) {
                    Object detail = details[i];
                    Cell cell = row.createCell(i);
                    cell.setCellValue(detail.toString());
                    cell.setCellStyle(style);
                }
            }

            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException ex) {
            System.out.println("Failure to save file.");
            ex.printStackTrace();
        }
        return file;
    }

    private File toPdf(MealPlan mealPlan, File file) {
        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));

            document.open();

            String[] headers = new String[]{MealPlan.DATE, MealPlan.UNFREEZE, MealPlan.COOK, MealPlan.BREAKFAST, MealPlan.LUNCH, MealPlan.DINNER};
            List<LocalDate> dates = mealPlan.getDates();

            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);
            Arrays.stream(headers).forEach(columnTitle -> {
                PdfPCell header = new PdfPCell();
                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header.setBorderWidth(2);
                header.setPhrase(new Phrase(columnTitle));
                table.addCell(header);
            });

            for (LocalDate date : dates) {
                DayPlanIF dayPlan = mealPlan.getPlan(date);
                Object[] details = new Object[]{formatter.format(date), dayPlan.getUnfreeze(), dayPlan.getToCook(), dayPlan.getBreakfast().getDescription(), dayPlan.getLunch().getDescription(), dayPlan.getDinner().getDescription()};
                for (Object detail : details) {
                    table.addCell(detail.toString());
                }
            }
            document.add(table);
            document.close();

//        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
//        Chunk chunk = new Chunk("Hello World", font);
//        document.add(chunk);
            document.close();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
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

    public File toPdf(MealPlan plan) {
        return toPdf(plan, new File(getCSVDirectory(), plan.getName() + ".pdf"));
    }

    public File toExcel(MealPlan plan) {
        return toExcel(plan, new File(getCSVDirectory(), plan.getName() + ".xls"));
    }


    public DishActionFactory getDishActionFactory() {
        return dishActionFactoryLazyInitializer.get();
    }

    /**
     * First try /Users/username/, then app launch directory. If the latter does not exist, it creates it.
     *
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

    public void initialise() {
        Set<Dish> allDishes = Sets.newHashSet();

        List<MealPlan> allMealPlans = getAllMealPlans();
        for (MealPlan mealPlan : allMealPlans) {
            List<LocalDate> dates = mealPlan.getDates();
            for (LocalDate date : dates) {
                DayPlanIF plan = mealPlan.getPlan(date);
                List<Meal> meals = plan.getMeals();
                for (Meal meal : meals) {
                    if (Dish.isStub(meal.getDish())) {
                        if (!meal.getNotes().isBlank()) {
                            allDishes.add(new Dish(meal.getNotes(), ""));
                        }
                    } else {
                        allDishes.add(meal.getDish());
                    }
                }
                allDishes.addAll(plan.getDishes());
            }
        }
        DishManager.getInstance().loadDishes(allDishes);
    }

    public static class DishManager {
        private static DishManager instance = new DishManager();

        private TreeSet<Dish> allDishes = Sets.newTreeSet();

        public static DishManager getInstance() {
            return instance;
        }

        public synchronized void loadDishes(Collection<Dish> dishes) {
            allDishes.addAll(dishes);
        }

        /**
         * All dishes used in meal plans, whether concrete dishes or not. Concrete dishes which exist in the database
         * but not used in any meal plans will <i>not</i> appear in this list.
         *
         * @return
         */
        public synchronized List<Dish> getAll() {
            return Lists.newArrayList(allDishes);
        }
    }
}
