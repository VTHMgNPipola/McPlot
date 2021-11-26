package com.prinjsystems.mcplot.ngui.components;

import java.text.MessageFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class ConstantCard extends JPanel {
    public ConstantCard(int index) {
        super(new MigLayout());
        setBorder(BorderFactory.createTitledBorder(
                MessageFormat.format(BUNDLE.getString("constantCard.constantId"), index)));

        JLabeledTextField name = new JLabeledTextField();
        add(name, "pushx, growx");
        name.setPlaceholderText(BUNDLE.getString("constantCard.name"));
        name.setToolTipText(BUNDLE.getString("constantCard.nameTooltip"));

        JButton remove = new JButton("X");
        add(remove, "wrap");
        remove.setToolTipText(BUNDLE.getString("generics.remove"));

        JLabeledTextField value = new JLabeledTextField();
        add(value, "pushx, span, growx");
        value.setPlaceholderText(BUNDLE.getString("constantCard.value"));
        value.setToolTipText(BUNDLE.getString("constantCard.valueTooltip"));
    }
}
