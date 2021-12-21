package fjdb.mealplanner.admin;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import fjdb.mealplanner.DaoManager;
import fjdb.mealplanner.Dish;
import fjdb.mealplanner.DishTag;
import fjdb.mealplanner.dao.DishTagDao;
import fjdb.mealplanner.fx.DishTagSelectionPanel;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class DishTagPanel extends FlowPane {

    public DishTagPanel(DaoManager daoManager, ObservableList<Dish> dishList) {
        super(Orientation.VERTICAL);
        DishTagDao dishTagDao = daoManager.getDishTagDao();
        ObservableList<TableEntry> entries = FXCollections.observableArrayList();
        Multimap<Dish, DishTag> dishesToTags = dishTagDao.getDishesToTags();
        for (Dish dish : dishesToTags.keySet()) {
            ArrayList<DishTag> dishTags = Lists.newArrayList(dishesToTags.get(dish));
            entries.add(new TableEntry(dish, dishTags));
        }

        List<DishTagDao.TagEntry> load = dishTagDao.load();
        ObservableList<DishTagDao.TagEntry> dishTagList = FXCollections.observableList(load);
        TableView<TableEntry> table = new TableView<>(entries);

        Set<DishTag> tags = dishTagDao.getTags(false);

        DishTagSelectionPanel dishTagSelectionPanel = new DishTagSelectionPanel(tags, dishesToTags);
        dishTagSelectionPanel.includeDishSelector(dishList);
        Button insertButton = new Button("Insert");
        insertButton.setOnAction(actionEvent -> {
            Dish dish = dishTagSelectionPanel.getSelectedDish();

            List<DishTag> selectedTags = dishTagSelectionPanel.getSelectedItems();
            Collection<DishTag> currentTags = dishesToTags.get(dish);
            selectedTags.removeAll(currentTags);
            for (DishTag selectedTag : selectedTags) {
                DishTagDao.TagEntry dataItem = new DishTagDao.TagEntry(dish, selectedTag);
                dishTagDao.insert(dataItem);
                dishTagList.add(dataItem);
            }
            dishTagSelectionPanel.addTags(dish, selectedTags);
            table.refresh();
        });

        VBox insertPanel = new VBox();
        insertPanel.getChildren().add(dishTagSelectionPanel);
        insertPanel.getChildren().add(insertButton);

        TableColumn<TableEntry, Dish> dishColumn = new TableColumn<>("Dish");
        TableColumn<TableEntry, String> tagColumn = new TableColumn<>("Tag");
        dishColumn.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().dish));
        tagColumn.setCellValueFactory(x -> Bindings.createObjectBinding(() -> x.getValue().getTags()));
        table.getColumns().add(dishColumn);
        table.getColumns().add(tagColumn);

        VBox vbox = new VBox();

        vbox.getChildren().add(insertPanel);
        vbox.getChildren().add(table);
        getChildren().add(vbox);

    }

    class TableEntry {
        Dish dish;
        List<DishTag> tags;

        public TableEntry(Dish dish, List<DishTag> tags) {
            this.dish = dish;
            this.tags = tags;
        }

        public String getTags() {
            return Joiner.on(",").join(tags);
        }

    }
}
