package com.company;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.junit.Test;
import static org.junit.Assert.*;


public class TextEditor implements ActionListener {

    // Singleton nesne
    private static TextEditor instance = null;

    DarkTheme darkTheme = new DarkTheme();
    LightTheme lightTheme = new LightTheme();

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    private final JFrame frame;
    private final JTextArea textArea;
    private final JMenuBar menuBar;
    private final JMenu fileMenu;
    private final JMenu editMenu;
    private final JMenu searchMenu;
    private final JMenu themeMenu;

    private final JMenuItem openMenuItem;
    private final JMenuItem saveMenuItem;
    private final JMenuItem saveAsMenuItem;
    private final JMenuItem copyMenuItem;
    private final JMenuItem pasteMenuItem;
    private final JMenuItem undoMenuItem;
    private final JMenuItem savePageMenuItem;
    private final JMenuItem searchMenuItem;
    private final JMenuItem darkMenuEdit;
    private final JMenuItem lightMenuEdit;

    private File currentFile;

    private String text;
    private List<Memento> history = new ArrayList<>();
    private int current;

    OpenCommand openCommand = new OpenCommand(this);
    SaveCommand saveCommand = new SaveCommand(this);
    SaveAsCommand saveAsCommand = new SaveAsCommand(this);
    SearchCommand searchCommand = new SearchCommand(this);


    private TextEditor() {

        this.text = "";
        this.history = new ArrayList<>();
        this.current = 0;

        // Set up the JFrame
        frame = new JFrame("Text Editor");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set up the text area and add it to the JFrame
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Set up the menu bar
        menuBar = new JMenuBar();

        // Set up the File menu
        fileMenu = new JMenu("File");
        openMenuItem = new JMenuItem("Open");
        openMenuItem.addActionListener(this);
        fileMenu.add(openMenuItem);

        saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(this);
        fileMenu.add(saveMenuItem);

        saveAsMenuItem = new JMenuItem("Save As...");
        saveAsMenuItem.addActionListener(this);
        fileMenu.add(saveAsMenuItem);

        // Set up the Edit menu
        editMenu = new JMenu("Edit");
        searchMenu = new JMenu("Search");
        themeMenu = new JMenu("Theme");

        copyMenuItem = new JMenuItem("Copy");
        copyMenuItem.addActionListener(this);
        editMenu.add(copyMenuItem);

        pasteMenuItem = new JMenuItem("Paste");
        pasteMenuItem.addActionListener(this);
        editMenu.add(pasteMenuItem);

        undoMenuItem = new JMenuItem("Undo");
        undoMenuItem.addActionListener(this);
        editMenu.add(undoMenuItem);

        savePageMenuItem = new JMenuItem("Save Page");
        savePageMenuItem.addActionListener(this);
        editMenu.add(savePageMenuItem);

        // Set up the Search menu
        searchMenuItem = new JMenuItem("Search");
        searchMenuItem.addActionListener(this);
        searchMenu.add(searchMenuItem);

        darkMenuEdit = new JMenuItem("Dark");
        darkMenuEdit.addActionListener(this);
        themeMenu.add(darkMenuEdit);

        lightMenuEdit = new JMenuItem("Light");
        lightMenuEdit.addActionListener(this);
        themeMenu.add(lightMenuEdit);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(searchMenu);
        menuBar.add(themeMenu);

        // Add the menu bar to the frame
        frame.setJMenuBar(menuBar);

        // Add the menu bar to the frame
        frame.setJMenuBar(menuBar);

        // Display the frame
        frame.setVisible(true);
    }

    // ------------- SINGLETON --------------------
    public static TextEditor getInstance() {
        if (instance == null) {
            instance = new TextEditor();
        }
        return instance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openMenuItem) {
            openCommand.execute();
        }
        else if (e.getSource() == saveMenuItem) {
            saveCommand.execute();
        }
        else if (e.getSource() == saveAsMenuItem) {
            saveAsCommand.execute();
        }
        else if (e.getSource() == copyMenuItem) {
            textArea.copy();
        }
        else if (e.getSource() == pasteMenuItem) {
            textArea.paste();
        }
        else if (e.getSource() == searchMenuItem){
            searchCommand.execute();
        }
        else if (e.getSource() == undoMenuItem){
            undo();
        }
        else if (e.getSource() == savePageMenuItem){
            setText();
        }

        else if (e.getSource() == darkMenuEdit){
            darkTheme.applyTheme(this);
        }
        else if (e.getSource() == lightMenuEdit){
            lightTheme.applyTheme(this);
        }
    }


    // ITERATOR ILE BUL METODU
    public void search(String word) {
        // Create an iterator for the text area
        TextAreaIterator iterator = new TextAreaIterator(textArea);

        // Get the text from the text area
        String text = textArea.getText();

        // Find the index of the first occurrence of the word
        int index = text.indexOf(word);

        if (index >= 0) {
            // If the word is found, select it
            textArea.setSelectionStart(index);
            textArea.setSelectionEnd(index + word.length());
        } else {
            // If the word is not found, show an error message
            JOptionPane.showMessageDialog(frame, "Kelime bulunamadı !!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    // ------------ MEMENTO ----------------
    public void setText() {
        history.add(current, new Memento(textArea.getText()));
        current++;
    }

    public void undo() {
        if (current > 0) {
            current--;
            text = history.get(current).getText();
            textArea.setText(text);
            System.out.println(history.get(current).getText());
            System.out.println(history.get(current-1).getText());
        }
    }

    private static class Memento {
        private final String text;

        public Memento(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }



    // -------- ITERATOR --------------
    class TextAreaIterator implements Iterator<String> {
        private JTextArea textArea;
        private int index;
        private String currentWord;

        public TextAreaIterator(JTextArea textArea) {
            this.textArea = textArea;
            index = 0;
            currentWord = "";
        }

        @Override
        public boolean hasNext() {
            return index < textArea.getText().length();
        }

        @Override
        public String next() {
            // Get the next word from the text area
            StringBuilder sb = new StringBuilder();
            char c = textArea.getText().charAt(index++);
            while (index < textArea.getText().length() && !Character.isWhitespace(c)) {
                sb.append(c);
                c = textArea.getText().charAt(index++);
            }
            currentWord = sb.toString();
            return currentWord;
        }

        public int getIndex() {
            return index - currentWord.length();
        }
    }


    // ------------ COMMAND ----------------------
    private abstract class Command {
        public abstract void execute();
    }

    private class SearchCommand extends Command{

        private TextEditor editor;

        public SearchCommand(TextEditor editor){
            this.editor = editor;
        }

        @Override
        public void execute(){
            String word = JOptionPane.showInputDialog(frame, "Enter a word to search for:");
            if (word != null && !word.isEmpty()) {
                search(word);
            }
        }
    }

    class OpenCommand extends Command {

        private TextEditor editor;

        public OpenCommand(TextEditor editor) {
            this.editor = editor;
        }

        @Override
        public void execute() {
            // Show the file chooser and get the selected file
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                // Read the contents of the file into the text area
                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                    textArea.setText("");
                    String line;
                    while ((line = reader.readLine()) != null) {
                        textArea.append(line + "\n");
                    }
                    currentFile = selectedFile;
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error reading file", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    class SaveCommand extends Command {
        private TextEditor textEditor;

        public SaveCommand(TextEditor textEditor) {
            this.textEditor = textEditor;
        }

        @Override
        public void execute() {
            if (currentFile == null) {
                // If no file is currently open, show the Save As dialog
                actionPerformed(new ActionEvent(saveAsMenuItem, 0, null));
            } else {
                // Save the current file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                    writer.write(textArea.getText());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error saving file", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    class SaveAsCommand extends Command {
        private TextEditor textEditor;

        public SaveAsCommand(TextEditor textEditor) {
            this.textEditor = textEditor;
        }

        @Override
        public void execute() {
            // Show the file chooser and get the selected file
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                // Save the contents of the text area to the selected file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                    writer.write(textArea.getText());
                    currentFile = selectedFile;
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error saving file", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // -------------------- FACTORY ------------------------
    interface Theme {
        // Method to apply the theme to the text editor
        void applyTheme(TextEditor editor);
    }

    public class DarkTheme implements Theme {
        @Override
        public void applyTheme(TextEditor editor) {
            editor.setBackground(Color.DARK_GRAY);
            editor.setForeground(Color.WHITE);
        }
    }

    public void setBackground(Color color) {
        textArea.setBackground(color);
    }

    public void setForeground(Color color) {
        textArea.setForeground(color);
    }


    class LightTheme implements Theme {
        @Override
        public void applyTheme(TextEditor editor) {
            editor.setBackground(Color.WHITE);
            editor.setForeground(Color.BLACK);
        }
    }

    class ThemeFactory {
        public Theme createTheme(String themeName) {
            if (themeName.equalsIgnoreCase("dark")) {
                return new DarkTheme();
            } else if (themeName.equalsIgnoreCase("light")) {
                return new LightTheme();
            } else {
                return null;
            }
        }
    }



    public class TextEditorTest {
        @Test
        public void testSave() {
            TextEditor editor = TextEditor.getInstance();
            editor.setText("This is a test");
            File file = new File("test.txt");
            SaveCommand saveCommand = new SaveCommand(editor);
            saveCommand.execute();
            assertTrue(file.exists());
        }
    }

}