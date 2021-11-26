package com.prinjsystems.mcplot.ngui.components;

import java.text.MessageFormat;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class FunctionCard extends JPanel {
    public FunctionCard(int index) {
        setLayout(new MigLayout());
        setBorder(BorderFactory.createTitledBorder(
                MessageFormat.format(BUNDLE.getString("functionCard.functionId"), index)));

        ColorChooserButton colorChooserButton = new ColorChooserButton();
        add(colorChooserButton, "growy");
        colorChooserButton.setToolTipText(BUNDLE.getString("functionCard.selectColor"));

        JLabeledTextField functionField = new JLabeledTextField();
        add(functionField, "pushx, growx");
        functionField.setPlaceholderText(BUNDLE.getString("functionCard.functionDefinition.placeholder"));
        functionField.setToolTipText(BUNDLE.getString("functionCard.functionDefinition.tooltip"));

        JCheckBox active = new JCheckBox();
        add(active);
        active.setToolTipText(BUNDLE.getString("functionCard.settings.functionVisible"));
        active.setSelected(true);
    }
}
