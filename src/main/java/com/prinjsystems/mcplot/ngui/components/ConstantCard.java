package com.prinjsystems.mcplot.ngui.components;

import com.prinjsystems.mcplot.nmath.Constant;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class ConstantCard extends JPanel {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#####");

    private final Constant constant;

    private final JLabeledTextField value;

    public ConstantCard(Constant constant, int index) {
        super(new MigLayout());
        this.constant = constant;

        setBorder(BorderFactory.createTitledBorder(
                MessageFormat.format(BUNDLE.getString("constantCard.constantId"), index)));

        JLabeledTextField name = new JLabeledTextField();
        add(name, "pushx, growx");
        name.setText(constant.getName());
        name.setPlaceholderText(BUNDLE.getString("constantCard.name"));
        name.setToolTipText(BUNDLE.getString("constantCard.nameTooltip"));
        name.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                constant.setName(name.getText());
            }
        });
        name.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    constant.setName(name.getText());
                }
            }
        });

        JButton remove = new JButton("X");
        add(remove, "wrap");
        remove.setToolTipText(BUNDLE.getString("generics.remove"));

        value = new JLabeledTextField();
        add(value, "pushx, span, growx");
        value.setText(constant.getDefinition());
        value.setPlaceholderText(BUNDLE.getString("constantCard.value"));
        updateValueTooltip();
        value.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateConstantValue();
            }
        });
        value.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    updateConstantValue();
                }
            }
        });
    }

    private void updateValueTooltip() {
        String tooltip = BUNDLE.getString("constantCard.valueTooltip");
        if (constant.getActualValue() != null) {
            tooltip = String.format("(%s) %s", DECIMAL_FORMAT.format(constant.getActualValue()), tooltip);
        }
        value.setToolTipText(tooltip);
    }

    private void updateConstantValue() {
        boolean success = constant.setDefinition(value.getText());
        if (success) {
            updateValueTooltip();
        }
    }
}
