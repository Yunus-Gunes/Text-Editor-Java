package com.company;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class TextEditorTest {
    @Test
    public void testSave() {
        TextEditor editor = TextEditor.getInstance();
        editor.setText("This is a test");
        File file = new File("C:\\Users\\Halil\\Desktop\\test\\test.txt");
        TextEditor.SaveCommand saveCommand = TextEditor.getInstance().saveCommand;
        saveCommand.execute();
        assertTrue(file.exists());
    }

    @Test
    public void testOpen() {
        TextEditor editor = TextEditor.getInstance();
        File file = new File("C:\\Users\\Halil\\Desktop\\test\\test.txt");
        TextEditor.OpenCommand openCommand = TextEditor.getInstance().openCommand;

        openCommand.execute();
        assertEquals("This is a test", editor.getText());
    }
}
