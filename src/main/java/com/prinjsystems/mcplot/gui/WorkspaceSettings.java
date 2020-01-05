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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class WorkspaceSettings {
    private static final String PLOT_ON_OPEN_KEY = "plotOnOpen";
    private static final String LOOK_AND_FEEL_KEY = "lookAndFeel";
    private static Preferences prefs;
    private static final String OPEN_MAXIMIZED_KEY = "openMaximized";
    private static boolean plotOnOpen;
    private static final String LANGUAGE_KEY = "language";
    private static String language;
    private static boolean openMaximized;
    private static String lookAndFeel;

    static {
        prefs = Preferences.userRoot().node(Main.PREFERENCES_PATH);

        plotOnOpen = prefs.getBoolean(PLOT_ON_OPEN_KEY, true);

        openMaximized = prefs.getBoolean(OPEN_MAXIMIZED_KEY, false);

        language = prefs.get(LANGUAGE_KEY, Locale.getDefault().toLanguageTag());

        lookAndFeel = prefs.get(LOOK_AND_FEEL_KEY, "javax.swing.plaf.metal.MetalLookAndFeel");
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

    public static String getLookAndFeel() {
        return lookAndFeel;
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
            JLabel languageLabel = new JLabel(BUNDLE.getString("workspace.menu.file.settings.language.title"));
            gbc.gridx = 0;
            gbc.gridy = 2;
            mainPanel.add(languageLabel, gbc, 2);

            class StringPair {
                public String str1;
                public String str2;

                public StringPair(String str1, String str2) {
                    this.str1 = str1;
                    this.str2 = str2;
                }

                public StringPair(Locale locale) {
                    this.str1 = locale.getDisplayName();
                    this.str2 = locale.toLanguageTag();
                }

                @Override
                public String toString() {
                    return str1;
                }
            }

            StringPair[] languages = new StringPair[]{new StringPair(Locale.US),
                    new StringPair(new Locale("pt", "BR"))};
            JComboBox<StringPair> languageField = new JComboBox<>(languages);
            StringPair selectedLanguage = null;
            for (StringPair lang : languages) {
                if (lang.str2.equals(language)) {
                    selectedLanguage = lang;
                }
            }
            languageField.setSelectedItem(selectedLanguage);
            gbc.gridx = 1;
            gbc.gridy = 2;
            mainPanel.add(languageField, gbc, 3);

            // Look-and-Feel
            JLabel lookAndFeelLabel = new JLabel(BUNDLE.getString("workspace.menu.file.settings.laf.title"));
            gbc.gridx = 0;
            gbc.gridy = 3;
            mainPanel.add(lookAndFeelLabel, gbc, 4);

            StringPair[] lafs = new StringPair[]{
                    new StringPair(BUNDLE.getString("workspace.menu.file.settings.laf.metal"),
                            "javax.swing.plaf.metal.MetalLookAndFeel"),
                    new StringPair(BUNDLE.getString("workspace.menu.file.settings.laf.systemDefault"),
                            UIManager.getSystemLookAndFeelClassName()),
                    new StringPair(BUNDLE.getString("workspace.menu.file.settings.laf.motif"),
                            "com.sun.java.swing.plaf.motif.MotifLookAndFeel"),
                    new StringPair(BUNDLE.getString("workspace.menu.file.settings.laf.nimbus"),
                            "javax.swing.plaf.nimbus.NimbusLookAndFeel"),
            };
            JComboBox<StringPair> lookAndFeelField = new JComboBox<>(lafs);
            StringPair selectedLaf = null;
            for (StringPair laf : lafs) {
                if (laf.str2.equals(lookAndFeel)) {
                    selectedLaf = laf;
                }
            }
            lookAndFeelField.setSelectedItem(selectedLaf);
            gbc.gridx = 1;
            gbc.gridy = 3;
            mainPanel.add(lookAndFeelField, gbc, 5);

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

                language = ((StringPair) Objects.requireNonNull(languageField.getSelectedItem())).str2;
                prefs.put(LANGUAGE_KEY, language);

                lookAndFeel = ((StringPair) Objects.requireNonNull(lookAndFeelField.getSelectedItem())).str2;
                prefs.put(LOOK_AND_FEEL_KEY, lookAndFeel);
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
