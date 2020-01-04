package com.prinjsystems.mcplot.gui;

import com.prinjsystems.mcplot.Main;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Locale;
import java.util.Objects;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class WorkspaceSettings {
    private static final String OPEN_MAXIMIZED_KEY = "openMaximized";

    private static final String PLOT_ON_OPEN_KEY = "plotOnOpen";
    private static boolean plotOnOpen;
    private static final String LANGUAGE_KEY = "language";
    private static Preferences prefs;
    private static boolean openMaximized;
    private static String language;

    static {
        prefs = Preferences.userRoot().node(Main.PREFERENCES_PATH);

        plotOnOpen = prefs.getBoolean(PLOT_ON_OPEN_KEY, true);

        openMaximized = prefs.getBoolean(OPEN_MAXIMIZED_KEY, false);

        language = prefs.get(LANGUAGE_KEY, Locale.getDefault().toLanguageTag());
    }

    public static void showWorkspaceSettingsDialog(JFrame topJFrame) {
        WorkspaceSettingsDialog dialog = new WorkspaceSettingsDialog(topJFrame);
        dialog.setVisible(true);
    }

    public static boolean isPlotOnOpen() {
        return plotOnOpen;
    }

    public static boolean isOpenMaximized() {
        return openMaximized;
    }

    public static String getLanguage() {
        return language;
    }

    private static class WorkspaceSettingsDialog extends JDialog {
        public WorkspaceSettingsDialog(JFrame topJFrame) {
            super(topJFrame, BUNDLE.getString("generics.settings"));

            JPanel mainPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            gbc.insets = new Insets(10, 10, 10, 10);
            mainPanel.add(new JPanel(), gbc);

            // Plot on open
            JCheckBox plotOnOpenField = new JCheckBox(BUNDLE.getString("workspace.menu.file.settings.plotOnOpen"),
                    plotOnOpen);
            mainPanel.add(plotOnOpenField, gbc, 0);

            // Open maximized
            JCheckBox openMaximizedField = new JCheckBox(BUNDLE.getString("workspace.menu.file.settings.openMaximized"),
                    openMaximized);
            gbc.gridx = 0;
            gbc.gridy = 1;
            mainPanel.add(openMaximizedField, gbc, 1);

            // Language
            class Language {
                public String name;
                public String tag;

                public Language(Locale locale) {
                    this.name = locale.getDisplayName();
                    this.tag = locale.toLanguageTag();
                }

                @Override
                public String toString() {
                    return name;
                }
            }

            Language[] languages = new Language[]{new Language(Locale.US),
                    new Language(new Locale("pt", "BR"))};
            JComboBox<Language> languageField = new JComboBox<>(languages);
            Language selectedLanguage = null;
            for (Language lang : languages) {
                if (lang.tag.equals(language)) {
                    selectedLanguage = lang;
                }
            }
            languageField.setSelectedItem(selectedLanguage);
            gbc.gridx = 0;
            gbc.gridy = 2;
            mainPanel.add(languageField, gbc, 2);

            add(mainPanel, BorderLayout.CENTER);

            // Bottom buttons
            JPanel optionButtons = new JPanel();

            // Apply and close
            JButton apply = new JButton(BUNDLE.getString("generics.apply"));
            apply.addActionListener(e -> {
                plotOnOpen = plotOnOpenField.isSelected();
                prefs.putBoolean(PLOT_ON_OPEN_KEY, plotOnOpen);

                openMaximized = openMaximizedField.isSelected();
                prefs.putBoolean(OPEN_MAXIMIZED_KEY, openMaximized);

                language = ((Language) Objects.requireNonNull(languageField.getSelectedItem())).tag;
                prefs.put(LANGUAGE_KEY, language);
            });
            optionButtons.add(apply);

            JButton close = new JButton(BUNDLE.getString("generics.close"));
            close.addActionListener(e -> dispose());
            optionButtons.add(close);

            add(optionButtons, BorderLayout.PAGE_END);

            pack();
            setResizable(false);
            setLocation(topJFrame.getLocation());
        }
    }
}
