package com.prinjsystems.mcplot.ngui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.text.MessageFormat;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class FunctionCard extends JPanel {
    private static final Random RANDOM = new Random();

    public FunctionCard(int index) {
        setLayout(new MigLayout());
        setBorder(BorderFactory.createTitledBorder(
                MessageFormat.format(BUNDLE.getString("functionCard.functionId"), index)));

        ColorChooserButton colorChooserButton = new ColorChooserButton();
        add(colorChooserButton, "growy");
        colorChooserButton.setToolTipText(BUNDLE.getString("functionCard.selectColor"));
        colorChooserButton.setSelectedColor(new Color(RANDOM.nextInt(255), RANDOM.nextInt(255),
                RANDOM.nextInt(255)));
        colorChooserButton.setMaximumSize(new Dimension(40, 40));

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
