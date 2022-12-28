package com.company;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class TextEditor implements ActionListener {

    // Singleton nesne
    private static TextEditor instance = null;

    private JFrame frame;
    private JTextArea textArea;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu searchMenu;
    private JMenuItem openMenuItem;
    private JMenuItem saveMenuItem;
    private JMenuItem saveAsMenuItem;
    private JMenuItem newMenu;
    private JMenuItem copyMenuItem;
    private JMenuItem pasteMenuItem;
    private JMenuItem undoMenuItem;
    private JMenuItem savePageMenuItem;
    JMenuItem searchMenuItem;
    private File currentFile;

    private String text;
    private List<Memento> history = new ArrayList<>();
    private int current;

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
        newMenu = new JMenuItem("New Menu");
        newMenu.addActionListener(this);
        fileMenu.add(newMenu);
        menuBar.add(fileMenu);


        // Set up the Edit menu
        editMenu = new JMenu("Edit");
        searchMenu = new JMenu("Search");
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

        menuBar.add(editMenu);
        menuBar.add(searchMenu);

        // Add the menu bar to the frame
        frame.setJMenuBar(menuBar);


        // Display the frame
        frame.setVisible(true);





    }

    // Singleton nesneyi çağırma
    public static TextEditor getInstance() {
        if (instance == null) {
            instance = new TextEditor();
        }
        return instance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openMenuItem) {
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
        else if (e.getSource() == saveMenuItem) {
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
        else if (e.getSource() == saveAsMenuItem) {
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
        else if (e.getSource() == copyMenuItem) {
            // Copy the selected text to the clipboard
            textArea.copy();
        }
        else if (e.getSource() == pasteMenuItem) {
            // Copy the selected text to the clipboard
            textArea.paste();
        }
        else if (e.getSource() == searchMenuItem){
            String word = JOptionPane.showInputDialog(frame, "Enter a word to search for:");
            if (word != null && !word.isEmpty()) {
                search(word);
            }
        }
        else if (e.getSource() == newMenu){
            undo();
        }
        else if (e.getSource() == undoMenuItem){
            undo();
        }
        else if (e.getSource() == savePageMenuItem){
            setText();
        }






    }

    public void search(String word) {
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
            JOptionPane.showMessageDialog(frame, "Word not found", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

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
}