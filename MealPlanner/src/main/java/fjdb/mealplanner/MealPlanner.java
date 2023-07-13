package fjdb.mealplanner;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import fjdb.calendar.Holiday;
import fjdb.databases.ColumnDao;
import fjdb.databases.ColumnGroup;
import fjdb.mealplanner.admin.DishTagPanel;
import fjdb.mealplanner.dao.DishHistoryDao;
import fjdb.mealplanner.dao.DishTagDao;
import fjdb.mealplanner.fx.DishTagSelectionPanel;
import fjdb.mealplanner.fx.MealPlanConfigurator;
import fjdb.mealplanner.fx.planpanel.MealPlanPanel;
import fjdb.mealplanner.fx.Selectors;
import fjdb.mealplanner.loaders.CompositeDishLoader;
import fjdb.util.DateTimeUtil;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.stage.WindowEvent;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * To make use of the appropriate javafx gui controls, we need to add the following to the vm args
 * -p /Users/francisbostock/Code/javafx-sdk-11.0.2/lib --add-modules javafx.controls
 * <p>
 * To enable helpful exceptions, use -XX:+ShowCodeDetailsInExceptionMessages
 * Edit the dir to the javafx lib as appropriate.
 */
public class MealPlanner extends Application {

    private final DaoManager daoManager;
    private ObservableList<Dish> dishList;
    private final MealPlanManager mealPlanManager;
    private PlansPane plansPane;

    /*

    December 2021 TODO

    November2021 TODO
      = When editing a cell, there should be a panel that opens up showing a list of dishes based on what the user has typed.
      = Write a program that checks what meals have been previously included, and tries to identify every entry against
      a meal in the database (for manually added items). We'll need to work out how to address typos ("nearly" matches).
      It should spit out things which haven't been matched so we can gradually improve it.

      = Can we introduce a "type" of Meal called an Event, for instance "Bluewater" or "OUT" or "AWAY". These could
      be stored in a separate db table, just to help recognise elements in the meal plans.

      = Ability to store "types" of certain dishes, e.g. pizzas.

            TODO
            - Create a folder/package for the meal plan panel, and extract the inner classes.
            - Create Dish options: a user can right click a dish, to add an "option" for it e.g.
            pizza add a bbq beef one, a goats cheese one, and can also show options. I would say a
            db table like DishTags.

        //TODO for dayPlaysTable, can we call setItems and pass in an observable list from the builder. The builder, when making any changes
        //to a day plan, should reset the element in that list. In doing so, will that then get the table to update the particular
        //row that has changed, rather than having to call tableView.refresh() all the time?


            Add a class to manage history data (wrap/use the history dao, and provides accessors to get dates for a meal etc.)
                + When the application loads, on a separate thread we could load all the meal plans and get the data that way.

            3) Spend some time working through some TODOs scattered around, either addressing them or consolidating them
            into this list.

            6) Refactor IdColumnDao so that it caches the ids to beans (e.g. DishId to Dish), so we can remove DishId from
            Dish. This will replace the caching done in DishDao. IdColumnDao should be generic on the DataId. Minimise duplicated
            code in ColumnDao versus IdColumnDao, and ColumnGroup versus IdColumnGroup.
            This will also allow us to remove the serialization from DishId as it will no longer be stored in the Dish object.
            7) When a dish is deleted, the references to the DishHistory and DishTag tables would need updating. Need
            to ensure this somehow.
            8) Continuing from 1) - For the table, I want some generic machinery which can take a dao and its
            columns, and automatically generate a viewable table. This is a bit more involved, so kicking for now.


            Small items
            1) Add convenience StringColumn constructors for different varchar lengths.

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
            - while we can drag meals from one cell to another (in addition to dishes), we can't store meals down in the holder
            area, only dishes. This requires a way to handle meals (add a common interface to dish and meal perhaps?), and being able to
            serialize and deserialize them (though I think that should work automatically).

            TABLE IMPROVEMENTS
            - DONE delete on a cell should remove the content.
            - DONE should be able to drag from one cell to another.
            - RIGHT CLICK option: add dish to cook/unfreeze
            - RIGHT CLICK option: delete meal
            - copy/paste from one cell to another (ctrl c/p)
            - add drag for cook/unfreeze
            - Deleting should be undoable.

            - Dish visitor/handler pattern. To allow handlers to do one thing for "normal" dishes, and something else for special
            dishes, such as "Leftovers" (which requires a parent dish), or "Roast" which requires subtypes (lamb, beef, chicken...).

             */
    public static void main(String[] args) {
        launch(args);
    }

    public MealPlanner() {

//        String currentUsersHomeDir = System.getProperty("user.home");
//        File mealPlansFolder = new File(currentUsersHomeDir, "MealPlans");
//        mealPlanManager = new MealPlanManager(mealPlansFolder);
        mealPlanManager = new MealPlanManager(MealPlanManager.tryFindMealPlans());
        //TODO perform this on a separate thread, adding them to the tab gradually.
        //Once there are separate archived and "current" mealplans, we can prioritise the current ones.
        //In fact, current ones should be on this thread, and archived on a separate thread.
        mealPlanManager.load();
        mealPlanManager.initialise();

        //TODO have this parse the contents on a separate thread, and requests made to the manager should
        //ensure requests wait while it is loading, or at least are handled thread safely.

        daoManager = DaoManager.PRODUCTION;
        DishDao dishDao = daoManager.getDishDao();
        List<Dish> dishes = new CompositeDishLoader(daoManager).getDishes();
//TODO should this be removed?
        for (Dish dish : dishes) {
            dishDao.findId(dish);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        List<Dish> meals = Lists.newArrayList();
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


        plansPane = new PlansPane(mealPlanManager, dishList, false);
        plansPane.setSide(Side.TOP);
        plansPane.getSelectionModel().selectLast();
        Consumer<MealPlanPanel> consumer = plansPane::addMealPlanPanel;

        TabPane adminPane = new TabPane();
        adminPane.setSide(Side.TOP);
        adminPane.getTabs().add(new Tab("Dishes", getDishesPane(dishTableView)));
        adminPane.getTabs().add(new Tab("Configure", new MealPlanConfigurator(consumer, dishList, mealPlanManager)));
        adminPane.getTabs().add(new Tab("Dish History", getDishHistoryPanel()));
        adminPane.getTabs().add(new Tab("Meal History", getMealHistoryPanel()));
        adminPane.getTabs().add(new Tab("Dish Tags", getDishTagPanel()));

        mainTabs.getTabs().add(new Tab("Plans", plansPane));
        mainTabs.getTabs().add(new Tab("Admin", adminPane));

        MenuBar menuBar = new MenuBar();
        VBox vBox = new VBox(menuBar);
        Menu functions = new Menu("Functions");
        MenuItem menuItem = new MenuItem("Add new default plan");
        menuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LocalDate nextDate = plansPane.getNextDateForNewPlan();
                consumer.accept(MealPlanConfigurator.makePanel(nextDate, dishList, mealPlanManager));
            }
        });
        functions.getItems().add(menuItem);
        menuBar.getMenus().add(functions);

        final BorderPane sceneRoot = new BorderPane();
        sceneRoot.setCenter(mainTabs);
        sceneRoot.setTop(vBox);

        final Scene scene = new Scene(sceneRoot, 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {

                Platform.exit();
            }
        });
        primaryStage.setOnCloseRequest(windowEvent -> Platform.exit());
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
                DishTagSelectionPanel filterPanel = new DishTagSelectionPanel(tags, dishesToTags);
                dialogVbox.getChildren().add(filterPanel);


                HBox okCancel = new HBox();
                Button ok = new Button("OK");
                Button cancel = new Button("Cancel");
                cancel.setOnAction(actionEvent12 -> dialog.close());
                ok.setOnAction(actionEvent1 -> {
                    String dishName1 = dishNameField.getText();
                    String dishDetails1 = dishDetailsField.getText();
                    Dish dish = new Dish(dishName1, dishDetails1);
                    daoManager.getDishDao().insert(dish);
                    dishList.add(dish);
                    dishTagDao.insert(dish, filterPanel.getSelectedItems());
                    dishTableView.refresh();
                    dialog.close();
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

    private FlowPane getMealHistoryPanel() {
        FlowPane flowPane = new FlowPane(Orientation.VERTICAL);
        DishActionFactory dishActionFactory = mealPlanManager.getDishActionFactory();
        dishActionFactory.setCurrentMealPlan(plansPane.getCurrentPlan());
        TableView<DishActionFactory.HistoryRow> historyTable = dishActionFactory.getHistoryTable(dishList);
        historyTable.prefHeightProperty().bind(flowPane.heightProperty());
        historyTable.prefWidthProperty().bind(flowPane.widthProperty());
        flowPane.getChildren().add(historyTable);
        return flowPane;
    }

    private FlowPane getDishTagPanel() {
        return new DishTagPanel(daoManager, dishList);
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

        private MealPlanPanel currentPlan = null;

        public PlansPane(MealPlanManager manager, ObservableList<Dish> dishList, boolean isArchive) {
            if (!isArchive) {
                getTabs().add(new Tab("Archive", new PlansPane(manager, dishList, true)));
            }
            List<MealPlan> mealPlans = isArchive ? manager.getArchived() : manager.getMealPlans();
            for (MealPlan mealPlan : mealPlans) {
                MealPlanPanel mealPlanPanel = new MealPlanPanel(mealPlan, dishList, manager);
                currentPlan = mealPlanPanel;
                addMealPlanPanel(mealPlanPanel);
            }
        }

        public void addMealPlanPanel(MealPlanPanel mealPlanPanel) {
            ScrollPane scrollPane = new ScrollPane(mealPlanPanel);
            getTabs().add(new Tab(String.format("Plan %s", mealPlanPanel.getStart()), scrollPane));
            if (currentPlan == null || mealPlanPanel.getStart().isAfter(currentPlan.getStart())) {
                currentPlan = mealPlanPanel;
            }
        }

        protected MealPlanPanel getCurrentPlan() {
            return currentPlan;
        }

        protected LocalDate getNextDateForNewPlan() {
            if (currentPlan != null) {
                return currentPlan.getEnd().plusDays(1);
            } else {
                LocalDate date = DateTimeUtil.today();
                while(!date.getDayOfWeek().equals(DayOfWeek.TUESDAY)) {
                    date = date.plusDays(1);
                }
                return date;
            }
        }
    }

}
