package fjdb.notesapp;

import com.google.common.collect.Lists;
import fjdb.databases.DefaultId;
import fjdb.databases.tools.DataTable;
import fjdb.hometodo.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class NotesPanel {

    public static void main(String[] args) {



        NotesRepository notesRepository = new NotesRepository();
        JFrame frame = new JFrame("");
        frame.setPreferredSize(new Dimension(1000, 600));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new BorderLayout());

        DataTable<NoteDataItem, DefaultId> table = DataTable.makeTable(notesRepository.getDao());
        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);


        panel.add(addInsertPanel(notesRepository.getDao(), e -> {
            DataItemModel<?,?> model = table.getModel();
            model.refresh();
//            view.refresh();
        }), BorderLayout.SOUTH);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private static JPanel addInsertPanel(NotesDao notesDao, ActionListener listener) {
        JPanel panel = new JPanel();

        Box row = Box.createHorizontalBox();
        JTextField nameField = new JFormattedTextField("Name");
        nameField.setColumns(30);
        JTextField contentField = new JFormattedTextField("Content");
        contentField.setColumns(30);

        row.add(nameField);
        row.add(contentField);
        JButton ok = new JButton("Insert");
        ok.addActionListener(e -> {
            NoteDataItem noteDataItem = new NoteDataItem(nameField.getText(), LocalDateTime.now(), LocalDateTime.now(), nameField.getText(), NoteCategory.NORMAL, Lists.newArrayList(Tag.NONE));
            notesDao.insert(noteDataItem);
            listener.actionPerformed(e);
        });

        panel.add(row);
        panel.add(ok);
        return panel;
    }

    public static <T extends Enum<T>> JComboBox<T> makeCombo(Class<? extends T> enumClass) {
        T[] enumConstants = enumClass.getEnumConstants();
        JComboBox<T> combo = new JComboBox<>(enumConstants);
        combo.setSelectedIndex(0);
        return combo;
    }
}

