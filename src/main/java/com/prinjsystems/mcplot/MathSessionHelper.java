package com.prinjsystems.mcplot;

import com.prinjsystems.mcplot.nmath.Constant;
import com.prinjsystems.mcplot.nmath.Function;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import static com.prinjsystems.mcplot.Main.BUNDLE;
import static com.prinjsystems.mcplot.PreferencesHelper.KEY_CURRENT_DIRECTORY_OPEN;
import static com.prinjsystems.mcplot.PreferencesHelper.KEY_CURRENT_DIRECTORY_SAVE;
import static com.prinjsystems.mcplot.PreferencesHelper.PREFERENCES;

public class MathSessionHelper {
    private static final JFileChooser FILE_CHOOSER = new JFileChooser();
    private static final String EXTENSION = "mcp";

    static {
        FILE_CHOOSER.setFileFilter(new FileNameExtensionFilter(BUNDLE.getString("workspace.menu.file.fileFormat"),
                EXTENSION));
    }

    public static void saveSession(List<Function> functions, List<Constant> constants) {
        String saveDirectory = PREFERENCES.get(KEY_CURRENT_DIRECTORY_SAVE, null);
        if (saveDirectory != null) {
            FILE_CHOOSER.setCurrentDirectory(new File(saveDirectory));
        }

        int state = FILE_CHOOSER.showSaveDialog(null);
        if (state == JFileChooser.APPROVE_OPTION) {
            String chosenPath = FILE_CHOOSER.getSelectedFile().getAbsolutePath();
            if (!chosenPath.endsWith(EXTENSION)) {
                chosenPath += "." + EXTENSION;
            }

            PREFERENCES.put(KEY_CURRENT_DIRECTORY_SAVE, chosenPath);
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Path.of(chosenPath)))) {
                oos.writeObject(constants);
                oos.writeObject(functions);
            } catch (Throwable t) {
                JOptionPane.showMessageDialog(null, BUNDLE.getString("errors.save"),
                        BUNDLE.getString("generics.errorDialog"), JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void openSession(BiConsumer<List<Function>, List<Constant>> consumer) {
        String openDirectory = PREFERENCES.get(KEY_CURRENT_DIRECTORY_OPEN, null);
        if (openDirectory != null) {
            FILE_CHOOSER.setCurrentDirectory(new File(openDirectory));
        }

        int state = FILE_CHOOSER.showOpenDialog(null);
        if (state == JFileChooser.APPROVE_OPTION) {
            String chosenPath = FILE_CHOOSER.getSelectedFile().getAbsolutePath();
            PREFERENCES.put(KEY_CURRENT_DIRECTORY_OPEN, chosenPath);
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Path.of(chosenPath)))) {
                List<Constant> constants = (List<Constant>) ois.readObject();
                List<Function> functions = (List<Function>) ois.readObject();

                consumer.accept(functions, constants);
            } catch (Throwable t) {
                JOptionPane.showMessageDialog(null, BUNDLE.getString("errors.save"),
                        BUNDLE.getString("generics.errorDialog"), JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
