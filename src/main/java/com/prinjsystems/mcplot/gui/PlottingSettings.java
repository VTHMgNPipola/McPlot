package com.prinjsystems.mcplot.gui;

import com.prinjsystems.mcplot.Main;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class PlottingSettings {
    private static final String STEP_KEY = "step";
    private static Preferences prefs;
    private static double step;

    static {
        prefs = Preferences.userRoot().node(Main.PREFERENCES_PATH);

        step = prefs.getDouble(STEP_KEY, 0.1);
    }

    public static void showPlottingSettingsDialog(JFrame topJFrame) {
        PlottingSettingsDialog dialog = new PlottingSettingsDialog(topJFrame);
        dialog.setVisible(true);
    }

    public static double getStep() {
        return step;
    }

    private static class PlottingSettingsDialog extends JDialog {
        public PlottingSettingsDialog(JFrame topJFrame) {
            super(topJFrame, BUNDLE.getString("generics.settings"));

            JPanel mainPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            gbc.insets = new Insets(10, 10, 0, 10);
            mainPanel.add(new JPanel(), gbc);

            JLabel stepLabel = new JLabel(BUNDLE.getString("plotting.settings.step"));
            mainPanel.add(stepLabel, gbc, 0);

            // Step
            JSpinner stepField = new JSpinner(new SpinnerNumberModel(step, 0.0000001, 1000000,
                    0.05));
            mainPanel.add(stepField, gbc, 1);

            add(mainPanel, BorderLayout.CENTER);

            JPanel optionButtons = new JPanel();

            // Apply and close
            JButton apply = new JButton(BUNDLE.getString("generics.apply"));
            apply.addActionListener(e -> {
                step = (double) stepField.getValue();
                prefs.putDouble(STEP_KEY, step);

                PlottingPanel.getInstance().plot();

                dispose();
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
