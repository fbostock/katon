package fjdb.mealplanner;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import fjdb.databases.ColumnDao;
import fjdb.databases.ColumnGroup;
import fjdb.mealplanner.dao.DishHistoryDao;
import fjdb.mealplanner.dao.DishTagDao;
import fjdb.mealplanner.fx.FilterPanel;
import fjdb.mealplanner.fx.MealPlanConfigurator;
import fjdb.mealplanner.fx.MealPlanPanel;
import fjdb.mealplanner.fx.Selectors;
import fjdb.mealplanner.loaders.CompositeDishLoader;
import fjdb.mealplanner.swing.MealPlannerTest;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * To make use of the appropriate javafx gui controls, we need to add the following to the vm args
 * -p /Users/francisbostock/Code/javafx-sdk-11.0.2/lib --add-modules javafx.controls
 * Edit the dir to the javafx lib as approriate.
 */
public class MealPlanner extends Application {

    private final DaoManager daoManager;
    private ObservableList<Dish> dishList;
    private final MealPlanManager mealPlanManager;

    /*
            TODO list for Center Parcs
            -2) Remove (or disable, to make it optional) feature that the cell highlighted in the table gets the dish added
            when selected in the lefthand table. Given we can drag, it makes it redundant, and in fact problematic.
            -1) Perhaps the cells in MealPlanPanel should use a Meal object but should string convert to and from
            the Meal object. So if you set a cell programmatically, it should have a Dish object, and blank notes. But
            if edited manually, it will just have notes - but we can attempt to infer the Dish from the text if blank.
            0) When making the mealplan/shopping, see what steps are still required for this app, e.g. print out
            to csv, saving the mealplans etc. To save, we should start by serializing out the mealplan object.
            1) DONE In this class, add another tab for the DishTagDao, and mirror the machinery for the DishHistory tab, where
            we have a table of dishes and their tags, and can insert.
            3) Spend some time working through some TODOs scattered around, either addressing them or consolidating them
            into this list.
            4) Add a side panel to the meal planner containing all the dishes, and a field at the top to filter the list.
            Also, there should be a dropdown of tags to add to the filter list. Adding a tag should add a button towards the top.
            Clicking on that button should remove the filter/tag. Clicking on any dish in the list should automatically populate
            the selected (or last selected) field in the table).
            5)
            6) Refactor IdColumnDao so that it caches the ids to beans (e.g. DishId to Dish), so we can remove DishId from
            Dish. This will replace the caching done in DishDao. IdColumnDao should be generic on the DataId. Minimise duplicated
            code in ColumnDao versus IdColumnDao, and ColumnGroup versus IdColumnGroup.
            This will also allow us to remove the serialization from DishId as it will no longer be stored in the Dish object.
            7) When a dish is deleted, the references to the DishHistory and DishTag tables would need updating. Need
            to ensure this somehow.
            8) Continuing from 1) - For the table, I want some generic machinery which can take a dao and its
            columns, and automatically generate a viewable table. This is a bit more involved, so kicking for now.
            9) Review MealType and date used in Meal - perhaps we don't need those.


            Small items
            1) Add convenience StringColumn constructors for different varchar lengths.
            2) DONE Create side tabs - Admin and Plans, the latter containing actual meal plans, the former a panel to manage
            dishes, their tags etc.
            3) Write out the MealPlan created from the MealPlanPanel to csv.
            4) Add new dishes via table - have insert ability
            TODO features/work
            - if dishes know what ingredients they need, you could create an "ingredients to use" list, and the planner
            suggests dishes that use that, like pesto.
            - Make cell editor for Meal or Dish objects. Could start by a dish and see uf we can add a combo box which searchs
            as you type.
            - Add a panel to the MealPlanPanel to store a list of Dishes - dishes we want to have but not assigned to a day yet.
                - This dish list should be saved along with the MealPlan object
            - Add a panel to the MealPlanPanel to store notes - a string
                - These notes list should be saved along with the MealPlan object
            - Implement undoable operations. Start with adding/removing items from the temp dish panel.
            - autosave feature - save modified meal plans after a few minutes.
            - modify dish selector table so that each row contains two dishes, to compactify the table and show more dishes in the view.
            - add a text search field to the dish selector in the mealPlanPanel.
            - add a build script to create a jar file. It would also need a copy of the hsql db as part of the 'release'.

            TABLE IMPROVEMENTS
            - delete on a cell should remove the content. It should be undoable.
            - should be able to drag from one cell to another.

            - Dish visitor/handler pattern. To allow handlers to do one thing for "normal" dishes, and something else for special
            dishes, such as "Leftovers" (which requires a parent dish), or "Roast" which requires subtypes (lamb, beef, chicken...).

             */
    public static void main(String[] args) {
        launch(args);
    }

    public MealPlanner() {

        String currentUsersHomeDir = System.getProperty("user.home");
        File mealPlansFolder = new File(currentUsersHomeDir, "MealPlans");
        mealPlanManager = new MealPlanManager(mealPlansFolder);
        //TODO handle any errors loading these.
        mealPlanManager.load();


        System.out.println("Created MealPlanner");
        daoManager = DaoManager.PRODUCTION;
        DishDao dishDao = daoManager.getDishDao();
        List<Dish> dishes = new CompositeDishLoader(daoManager).getDishes();
        for (Dish dish : dishes) {
            dishDao.findId(dish);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        List<Dish> meals = Lists.newArrayList();
        meals.add(new MealPlannerTest.Leftovers(new MealPlannerTest.StubDish()));

        meals.addAll(new CompositeDishLoader(daoManager).getDishes());

        dishList = FXCollections.observableList(meals);
        TableView<Dish> dishTableView = new TableView<>(dishList);
        TableColumn<Dish, String> columnName = new TableColumn<>("Name");
        TableColumn<Dish, String> columnDetails = new TableColumn<>("Details");
        //TODO work out how to replace these dodgy propertyValueFactories (which are not compilation safe!) with
        //factories which take a functional lambda expression so we can pass in the getName method etc.
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
//        columnName.setCellValueFactory(dishStringCellDataFeatures -> Bindings.createStringBinding(() -> dishStringCellDataFeatures.getValue().getName()));
        columnDetails.setCellValueFactory(new PropertyValueFactory<>("details"));
        dishTableView.getColumns().add(columnName);
        dishTableView.getColumns().add(columnDetails);

        TabPane mainTabs = new TabPane();
        mainTabs.setSide(Side.LEFT);


        PlansPane plansPane = new PlansPane(mealPlanManager, dishList);
        plansPane.setSide(Side.TOP);
//        plansPane.getTabs().add(new Tab("Meal Plan Test", new MealPlanPanel(MealPlanConfigurator.Configuration.defaultConfig(), dishList, mealPlanManager)));

        //            LocalDate start = mealPlanPanel.getStart();
        //            plansPane.getTabs().add(new Tab(String.format("Plan %s", start), mealPlanPanel));
        Consumer<MealPlanPanel> consumer = plansPane::addMealPlanPanel;

        TabPane adminPane = new TabPane();
        adminPane.setSide(Side.TOP);
        adminPane.getTabs().add(new Tab("Dishes", getDishesPane(dishTableView)));
        adminPane.getTabs().add(new Tab("Configure", new MealPlanConfigurator(consumer, dishList, mealPlanManager)));
        adminPane.getTabs().add(new Tab("Dish History", getDishHistoryPanel()));
        adminPane.getTabs().add(new Tab("Dish Tags", getDishTagPanel()));


        mainTabs.getTabs().add(new Tab("Admin", adminPane));
        mainTabs.getTabs().add(new Tab("Plans", plansPane));

        final BorderPane sceneRoot = new BorderPane();
        sceneRoot.setCenter(mainTabs);

        final Scene scene = new Scene(sceneRoot, 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private FlowPane getDishesPane(TableView<Dish> dishTableView) {
        FlowPane flowPane = new FlowPane(Orientation.VERTICAL);
        Button insert = new Button("Add new dish");
        insert.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                final Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
//                dialog.initOwner(primaryStage);
                VBox dialogVbox = new VBox(20);
                dialogVbox.getChildren().add(new Text("Insert New Dish"));
                HBox dishName = new HBox();
                dishName.getChildren().add(new Text("Dish Name"));
                TextField dishNameField = new TextField();
                TextField dishDetailsField = new TextField();
                dishName.getChildren().add(dishNameField);
                HBox dishDetails = new HBox();
                dishDetails.getChildren().add(new Text("Dish Details"));
                dishDetails.getChildren().add(dishDetailsField);
                dialogVbox.getChildren().add(dishName);
                dialogVbox.getChildren().add(dishDetails);
                DishTagDao dishTagDao = daoManager.getDishTagDao();
                Multimap<Dish, DishTag> dishesToTags = dishTagDao.getDishesToTags();
                Set<DishTag> tags = dishTagDao.getTags(false);
                FilterPanel filterPanel = new FilterPanel(tags, dishesToTags);
                dialogVbox.getChildren().add(filterPanel);


                HBox okCancel = new HBox();
                Button ok = new Button("OK");
                Button cancel = new Button("Cancel");
                cancel.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        dialog.close();
                    }
                });
                ok.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        String dishName = dishNameField.getText();
                        String dishDetails = dishDetailsField.getText();
                        Dish dish = new Dish(dishName, dishDetails);
                        daoManager.getDishDao().insert(dish);
                        dishList.add(dish);
                        dishTagDao.insert(dish, filterPanel.getSelectedTags());
                        dishTableView.refresh();
                        dialog.close();
                    }
                });
                okCancel.getChildren().add(ok);
                okCancel.getChildren().add(cancel);
                dialogVbox.getChildren().add(okCancel);

                Scene dialogScene = new Scene(dialogVbox, 300, 200);
                dialog.setScene(dialogScene);
                dialog.show();


//                daoManager.getDishDao().insert();
            }
        });
        flowPane.getChildren().add(dishTableView);
        flowPane.getChildren().add(insert);
        return flowPane;
    }

    private FlowPane getDishHistoryPanel() {
        FlowPane flowPane = new FlowPane(Orientation.VERTICAL);
        DishHistoryDao dishHistoryDao = daoManager.getDishHistoryDao();
        /*
        Want a table for the meal histories - to be sortable by date and dish.
        Want a field to add a dish and date:

         */

        List<DishHistoryDao.DishEntry> load = dishHistoryDao.load();
        ObservableList<DishHistoryDao.DishEntry> dishEntries = FXCollections.observableList(load);
        TableView<DishHistoryDao.DishEntry> table = new TableView<>(dishEntries);

        DatePicker dateSelector = Selectors.getDateSelector();
        ComboBox<Dish> dishComboBox = new ComboBox<>(dishList);
        Button insertButton = new Button("Insert");
        insertButton.setOnAction(actionEvent -> {
            LocalDate date = dateSelector.getValue();
            Dish dish = dishComboBox.getValue();
            DishHistoryDao.DishEntry dataItem = new DishHistoryDao.DishEntry(dish, date);
            dishHistoryDao.insert(dataItem);
            dishEntries.add(dataItem);
            table.refresh();
        });

        FlowPane insertPanel = new FlowPane(Orientation.HORIZONTAL);
        insertPanel.getChildren().add(dishComboBox);
        insertPanel.getChildren().add(dateSelector);
        insertPanel.getChildren().add(insertButton);
        flowPane.getChildren().add(insertPanel);


        TableColumn<DishHistoryDao.DishEntry, Dish> dishColumn = new TableColumn<>("Dish");
        TableColumn<DishHistoryDao.DishEntry, LocalDate> dateColumn = new TableColumn<>("Date");
        dishColumn.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().getDish()));
        dateColumn.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().getDate()));
        table.getColumns().add(dishColumn);
        table.getColumns().add(dateColumn);

        flowPane.getChildren().add(table);

        return flowPane;
    }

    private FlowPane getDishTagPanel() {
        FlowPane flowPane = new FlowPane(Orientation.VERTICAL);
        DishTagDao dishTagDao = daoManager.getDishTagDao();

        Multimap<Dish, DishTag> dishesToTags = dishTagDao.getDishesToTags();
        List<DishTagDao.TagEntry> load = dishTagDao.load();
        ObservableList<DishTagDao.TagEntry> dishTagList = FXCollections.observableList(load);
        TableView<DishTagDao.TagEntry> table = new TableView<>(dishTagList);

        Set<DishTag> tags = dishTagDao.getTags(false);

        FilterPanel abPanel = new FilterPanel(tags, dishesToTags);

        ComboBox<Dish> dishComboBox = new ComboBox<>(dishList);
        dishComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Dish selectedItem = dishComboBox.getValue();
                abPanel.update(selectedItem);
            }
        });
        Button insertButton = new Button("Insert");
        insertButton.setOnAction(actionEvent -> {
            Dish dish = dishComboBox.getValue();

            List<DishTag> selectedTags = abPanel.getSelectedTags();
            Collection<DishTag> currentTags = dishesToTags.get(dish);
            selectedTags.removeAll(currentTags);
            for (DishTag selectedTag : selectedTags) {
                DishTagDao.TagEntry dataItem = new DishTagDao.TagEntry(dish, selectedTag);
                dishTagDao.insert(dataItem);
                dishTagList.add(dataItem);
            }
            abPanel.addTags(dish, selectedTags);
            table.refresh();
        });

        VBox insertPanel = new VBox();
        insertPanel.getChildren().add(dishComboBox);
        insertPanel.getChildren().add(abPanel);
        insertPanel.getChildren().add(insertButton);

        TableColumn<DishTagDao.TagEntry, Dish> dishColumn = new TableColumn<>("Dish");
        TableColumn<DishTagDao.TagEntry, DishTag> tagColumn = new TableColumn<>("Tag");
        dishColumn.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().getDish()));
        tagColumn.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().getTag()));
        table.getColumns().add(dishColumn);
        table.getColumns().add(tagColumn);

        VBox vbox = new VBox();

        vbox.getChildren().add(insertPanel);
        vbox.getChildren().add(table);
        flowPane.getChildren().add(vbox);

        return flowPane;
    }

    //TODO rename
    private <T> FlowPane getGenericPanel(ColumnDao<T> dao) {
        FlowPane flowPane = new FlowPane(Orientation.VERTICAL);

        ColumnGroup<T> columnGroup = dao.getColumnGroup();
//        columnGroup.
        List<T> load = dao.load();
        ObservableList<T> dishList = FXCollections.observableList(load);
        TableView<T> table = new TableView<>(dishList);

//        Set<DishTag> tags = dishTagDao.getTags(false);
//columnGroup.handle()
        /*
        The columnDao could have an insert method taking a list of objects, but I don't like the idea of that being public.
        What if the columnGroup produced an Object containing the selectors for each field, and produced a Selection object
        which is a wrapper for the list of Objects generated by the selectors, and that Selection object is inserted into the dao?
         */

//        ComboBox<DishTag> tagComboBox = Selectors.getDropDown(tags);
        List<Dish> dishes = daoManager.getDishDao().load().stream().sorted().collect(Collectors.toList());
        ComboBox<Dish> dishComboBox = Selectors.getDropDown(dishes);
        Button insertButton = new Button("Insert");
        insertButton.setOnAction(actionEvent -> {
//            DishTag tag = tagComboBox.getValue();
//            Dish dish = dishComboBox.getValue();
//            columnGroup.

//            DishTagDao.TagEntry dataItem = new DishTagDao.TagEntry(dish, tag);
//            dao.insert(dataItem);
//            dishList.add(dataItem);
//            table.refresh();
        });

        FlowPane insertPanel = new FlowPane(Orientation.HORIZONTAL);
        insertPanel.getChildren().add(dishComboBox);
//        insertPanel.getChildren().add(tagComboBox);
        insertPanel.getChildren().add(insertButton);
        flowPane.getChildren().add(insertPanel);

        TableColumn<DishTagDao.TagEntry, Dish> dishColumn = new TableColumn<>("Dish");
        TableColumn<DishTagDao.TagEntry, DishTag> tagColumn = new TableColumn<>("Tag");
        dishColumn.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().getDish()));
        tagColumn.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().getTag()));
//        table.getColumns().add(dishColumn);
//        table.getColumns().add(tagColumn);

        flowPane.getChildren().add(table);

        return flowPane;
    }

    private static class PlansPane extends TabPane {
        public PlansPane(MealPlanManager manager, ObservableList<Dish> dishList) {
            List<MealPlan> mealPlans = manager.getMealPlans();
            for (MealPlan mealPlan : mealPlans) {
                addMealPlanPanel(new MealPlanPanel(mealPlan, dishList, manager));
            }
        }

        public void addMealPlanPanel(MealPlanPanel mealPlanPanel) {
            getTabs().add(new Tab(String.format("Plan %s", mealPlanPanel.getStart()), mealPlanPanel));
        }
    }

}
