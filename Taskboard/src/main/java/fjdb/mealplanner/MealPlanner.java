package fjdb.mealplanner;

import com.google.common.collect.Lists;
import fjdb.mealplanner.dao.DishHistoryDao;
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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * To make use of the appropriate javafx gui controls, we need to add the following to the vm args
 * -p /Users/francisbostock/Code/javafx-sdk-11.0.2/lib --add-modules javafx.controls
 * Edit the dir to the javafx lib as approriate.
 */
public class MealPlanner extends Application {

    private final DaoManager daoManager;

    /*
            TODO features/work
            - if dishes know what ingredients they need, you could create an "ingredients to use" list, and the planner
            suggests dishes that use that, like pesto.
            - Make cell editor for Meal or Dish objects. Could start by a dish and see uf we can add a combo box which searchs
            as you type.

            - Dish visitor/handler pattern. To allow handlers to do one thing for "normal" dishes, and something else for special
            dishes, such as "Leftovers" (which requires a parent dish), or "Roast" which requires subtypes (lamb, beef, chicken...).

             */
    public static void main(String[] args) {
        launch(args);
    }

    public MealPlanner() {
        System.out.println("Created MealPlanner");
        daoManager = DaoManager.PRODUCTION;
        DishDao dishDao = daoManager.getDishDao();
        List<Dish> dishes = new CompositeDishLoader(daoManager).getDishes();
        for (Dish dish : dishes) {
            dishDao.findId(dish);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        List<Dish> meals = Lists.newArrayList();
        meals.add(new MealPlannerTest.Leftovers(new MealPlannerTest.StubDish()));

        meals.addAll(new CompositeDishLoader(daoManager).getDishes());
//        List<String> columnNames = Lists.newArrayList("Meal", "Description");
//        MealPlanner.DishModel model = new MealPlanner.DishModel(columnNames, meals, dishLoader);
//        JTable table = new JTable(model);

//        JFrame frame = new JFrame("");
//        frame.setPreferredSize(new Dimension(500, 500));
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

//        JTabbedPane tabs = new JTabbedPane();
//        JPanel panel = new JPanel();
//        panel.add(new JScrollPane(table));
//        JButton ok = new JButton("OK");
//        ok.addActionListener(e -> {
//            Box box = Box.createHorizontalBox();
//            box.add(new JLabel("Name"));
//            JTextField nameField = new JTextField();
//            box.add(nameField);
//            box.add(new JLabel("Description"));
//            JTextField descriptionField = new JTextField();
//            box.add(descriptionField);
//
//            int result = JOptionPane.showConfirmDialog(null, box, "Add Dish", JOptionPane.OK_CANCEL_OPTION);
//            if (result == JOptionPane.OK_OPTION) {
//                Dish dish = new Dish(nameField.getText(), descriptionField.getText());
//                dishLoader.addDish(dish);
//                model.refresh();
//            }
//        });

        ObservableList<Dish> dishList = FXCollections.observableList(meals);
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

        TabPane tabPane = new TabPane();

        tabPane.getTabs().add(new Tab("Dishes", dishTableView));
        tabPane.getTabs().add(new Tab("Configure", new MealPlanConfigurator()));
        tabPane.getTabs().add(new Tab("Meal Plan Test", new MealPlanPanel(MealPlanConfigurator.Configuration.defaultConfig(), dishList)));
        tabPane.getTabs().add(new Tab("Dish History", getDishHistoryPanel()));


//        panel.add(ok);
//        tabs.addTab("DB Dishes", panel);
//        tabs.addTab("Configure a plan", new JLabel("TODO"));
//
//        tabs.addTab("Table Planner", new MealPlanPanel());
//        tabs.addTab("Plan Editor", new JLabel("TODO"));
//        frame.add(tabs);
//        frame.pack();
//        frame.setVisible(true);

        final BorderPane sceneRoot = new BorderPane();
        sceneRoot.setCenter(tabPane);

        final Scene scene = new Scene(sceneRoot, 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private FlowPane getDishHistoryPanel() {
        FlowPane flowPane = new FlowPane(Orientation.VERTICAL);
        DishHistoryDao dishHistoryDao = daoManager.getDishHistoryDao();
        /*
        Want a table for the meal histories - to be sortable by date and dish.
        Want a field to add a dish and date:

         */

        List<DishHistoryDao.DishEntry> load = dishHistoryDao.load();
        ObservableList<DishHistoryDao.DishEntry> dishList = FXCollections.observableList(load);
        TableView<DishHistoryDao.DishEntry> table = new TableView<>(dishList);

        DatePicker dateSelector = Selectors.getDateSelector();
        List<Dish> dishes = daoManager.getDishDao().load().stream().sorted().collect(Collectors.toList());
        ComboBox<Dish> dishComboBox = new ComboBox<>(FXCollections.observableArrayList(dishes));
        Button insertButton = new Button("Insert");
        insertButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LocalDate date = dateSelector.getValue();
                Dish dish = dishComboBox.getValue();
                DishHistoryDao.DishEntry dataItem = new DishHistoryDao.DishEntry(dish, date);
                dishHistoryDao.insert(dataItem);
                dishList.add(dataItem);
                //TODO refresh the table, and ensure it works.
                table.refresh();
            }
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

}
