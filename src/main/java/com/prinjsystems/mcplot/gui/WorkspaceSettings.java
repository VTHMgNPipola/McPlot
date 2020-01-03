package com.prinjsystems.mcplot.gui;

import com.prinjsystems.mcplot.Main;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class WorkspaceSettings {
    private static final String PLOT_ON_OPEN_KEY = "plotOnOpen";
    private static Preferences prefs;
    private static boolean plotOnOpen;

    static {
        prefs = Preferences.userRoot().node(Main.PREFERENCES_PATH);

        plotOnOpen = prefs.getBoolean(PLOT_ON_OPEN_KEY, true);
    }

    public static void showWorkspaceSettingsDialog(JFrame topJFrame) {
        WorkspaceSettingsDialog dialog = new WorkspaceSettingsDialog(topJFrame);
        dialog.setVisible(true);
    }

    public static boolean isPlotOnOpen() {
        return plotOnOpen;
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

            add(mainPanel, BorderLayout.CENTER);

            // Bottom buttons
            JPanel optionButtons = new JPanel();

            // Apply and close
            JButton apply = new JButton(BUNDLE.getString("generics.apply"));
            apply.addActionListener(e -> {
                plotOnOpen = plotOnOpenField.isSelected();
                prefs.putBoolean(PLOT_ON_OPEN_KEY, plotOnOpen);
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
