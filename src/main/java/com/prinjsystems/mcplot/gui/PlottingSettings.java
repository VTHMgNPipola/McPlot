package com.prinjsystems.mcplot.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class PlottingSettings {
    private static double step;

    static {
        step = 0.1;
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
            super(topJFrame, BUNDLE.getString("plotting.settings.title"));

            JPanel mainPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            gbc.insets = new Insets(15, 0, 0, 10);
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
            apply.addActionListener(event -> {
                step = (double) stepField.getValue();

                PlottingPanel.getInstance().plot();
            });
            optionButtons.add(apply);

            JButton close = new JButton(BUNDLE.getString("generics.close"));
            close.addActionListener(event -> dispose());
            optionButtons.add(close);

            add(optionButtons, BorderLayout.PAGE_END);

            pack();
            setResizable(false);
            setLocation(topJFrame.getLocation());
        }
    }
}
