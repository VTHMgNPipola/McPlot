package com.prinjsystems.mcplot.ngui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.text.MessageFormat;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
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
        add(colorChooserButton, "growy, split 2");
        colorChooserButton.setToolTipText(BUNDLE.getString("functionCard.selectColor"));
        colorChooserButton.setSelectedColor(new Color(RANDOM.nextInt(255), RANDOM.nextInt(255),
                RANDOM.nextInt(255)));
        colorChooserButton.setMaximumSize(new Dimension(40, 40));

        JCheckBox active = new JCheckBox(BUNDLE.getString("functionCard.settings.functionVisible"), true);
        add(active, "pushx, growx, wrap");
        active.setToolTipText(BUNDLE.getString("functionCard.settings.functionVisibleTooltip"));

        JLabeledTextField functionField = new JLabeledTextField();
        add(functionField, "pushx, growx");
        functionField.setPlaceholderText(BUNDLE.getString("functionCard.functionDefinition.placeholder"));
        functionField.setToolTipText(BUNDLE.getString("functionCard.functionDefinition.tooltip"));

        JButton remove = new JButton("X");
        add(remove);
        remove.setToolTipText(BUNDLE.getString("generics.remove"));
    }
}
