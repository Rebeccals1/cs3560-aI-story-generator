package view.panels;

import model.story.SavedStoryModel;
import model.story.SceneModel;
import service.StorySaveSystem;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * LibraryPanel
 *
 * Purpose:
 *   • Shows all saved .json files inside /saves folder
 *   • Allows the user to:
 *        ✔ Refresh save list
 *        ✔ Load a selected save file into MainController
 *        ✔ Delete a selected save file
 *
 * Notes:
 *   • Compatible with StorySaveSystem (listSaves, loadGame, saveGame)
 *   • Requires MainFrame injection via setMainFrame()
 */
public class LibraryPanel extends JPanel {

    /* ============================================================
       Fields
       ============================================================ */

    private final StorySaveSystem saveSystem = new StorySaveSystem();

    private final DefaultListModel<File> listModel = new DefaultListModel<>();
    private final JList<File> saveList = new JList<>(listModel);

    private MainFrame mainFrame;   // injected from MainFrame
    private final JTextArea previewArea = new JTextArea();


    /* ============================================================
       Constructor
       ============================================================ */
    public LibraryPanel() {
        setLayout(new BorderLayout());
        buildUI();
        refreshList();
    }

    /** Called only by MainFrame */
    public void setMainFrame(MainFrame frame) {
        this.mainFrame = frame;
    }


    /* ============================================================
       UI Layout
       ============================================================ */
    private void buildUI() {

        /* ----- LEFT PANEL: Save List ----- */
        saveList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        saveList.setCellRenderer(new FileRenderer());

        saveList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showPreview();
            }
        });

        JScrollPane listScroll = new JScrollPane(saveList);

        JButton refreshBtn = new JButton("Refresh");
        JButton loadBtn = new JButton("Load Game");
        JButton deleteBtn = new JButton("Delete");

        refreshBtn.addActionListener(e -> refreshList());
        loadBtn.addActionListener(this::loadGame);
        deleteBtn.addActionListener(this::deleteSave);

        JPanel leftButtons = new JPanel(new FlowLayout());
        leftButtons.add(refreshBtn);
        leftButtons.add(loadBtn);
        leftButtons.add(deleteBtn);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(listScroll, BorderLayout.CENTER);
        leftPanel.add(leftButtons, BorderLayout.SOUTH);


        /* ----- RIGHT PANEL: Preview ----- */
        previewArea.setEditable(false);
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);
        previewArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JScrollPane(previewArea), BorderLayout.CENTER);

        /* ----- SPLIT VIEW ----- */
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftPanel,
                rightPanel
        );
        split.setDividerLocation(300);

        add(split, BorderLayout.CENTER);
    }


    /* ============================================================
       REFRESH SAVE LIST
       ============================================================ */
    private void refreshList() {
        listModel.clear();

        File[] saves = saveSystem.listSaves();
        if (saves == null || saves.length == 0) {
            previewArea.setText("No save files found.\nStart a story and click Save.");
            return;
        }

        for (File f : saves) {
            listModel.addElement(f);
        }
    }


    /* ============================================================
       SHOW PREVIEW FOR SELECTED SAVE
       ============================================================ */
    private void showPreview() {
        File file = saveList.getSelectedValue();

        if (file == null) {
            previewArea.setText("Select a save file.");
            return;
        }

        try {
            SavedStoryModel saved = saveSystem.loadGame(file);

            StringBuilder sb = new StringBuilder();

            sb.append("Title: ").append(saved.getDisplayTitle()).append("\n")
                    .append("Genre: ").append(saved.getGenre()).append("\n")
                    .append("Chapters: ").append(saved.getScenes().size()).append("\n")
                    .append("Created: ").append(saved.getFormattedCreatedDate()).append("\n\n");

            if (!saved.getScenes().isEmpty()) {
                SceneModel first = saved.getScenes().get(0);
                String text = first.getStoryText();
                text = text == null ? "" : text;

                sb.append("Chapter 1 Preview:\n")
                        .append(text.substring(0, Math.min(200, text.length())))
                        .append(text.length() > 200 ? "..." : "");
            }

            previewArea.setText(sb.toString());

        } catch (Exception ex) {
            previewArea.setText("Failed to read save:\n" + ex.getMessage());
        }
    }


    /* ============================================================
       LOAD GAME
       ============================================================ */
    private void loadGame(ActionEvent e) {
        File file = saveList.getSelectedValue();
        if (file == null) {
            JOptionPane.showMessageDialog(this, "Select a save file to load.");
            return;
        }

        try {
            if (mainFrame == null || mainFrame.getController() == null) {
                throw new IllegalStateException("MainFrame or Controller not initialized.");
            }

            mainFrame.getController().loadSaveFile(file);

            JOptionPane.showMessageDialog(this,
                    "Loaded save:\n" + file.getName());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading game:\n" + ex.getMessage());
        }
    }


    /* ============================================================
       DELETE SAVE
       ============================================================ */
    private void deleteSave(ActionEvent e) {
        File file = saveList.getSelectedValue();
        if (file == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this save file?\n" + file.getName(),
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        if (file.delete()) {
            refreshList();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete save file.");
        }
    }


    /* ============================================================
       FILE NAME RENDERER (Cleaner list view)
       ============================================================ */
    private static class FileRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean selected, boolean focus) {

            super.getListCellRendererComponent(list, value, index, selected, focus);

            if (value instanceof File f) {
                setText(f.getName());
            }

            return this;
        }
    }
}
