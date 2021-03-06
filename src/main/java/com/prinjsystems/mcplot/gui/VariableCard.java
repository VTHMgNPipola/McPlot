package com.prinjsystems.mcplot.gui;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.prinjsystems.mcplot.math.Variable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class VariableCard extends JPanel {
    private static final Font monospacedFont = new Font("Monospaced", Font.PLAIN, 12);
    private static final DoubleEvaluator EVALUATOR = new DoubleEvaluator();

    private Variable variable;

    private JLabeledTextField nameField;
    private JLabeledTextField valueField;

    public VariableCard(Variable v) {
        super(new BorderLayout());

        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        variable = v;

        JPanel definitionPanel = new JPanel();
        definitionPanel.setLayout(new BoxLayout(definitionPanel, BoxLayout.Y_AXIS));

        nameField = new JLabeledTextField();
        nameField.setText(variable.getName());
        nameField.setFont(monospacedFont);
        nameField.setPlaceholderText(BUNDLE.getString("variableCard.name"));
        nameField.setToolTipText(BUNDLE.getString("variableCard.nameTooltip"));
        definitionPanel.add(nameField);

        valueField = new JLabeledTextField();
        valueField.setText(variable.getValue() + "");
        valueField.setFont(monospacedFont);
        valueField.setPlaceholderText(BUNDLE.getString("variableCard.value"));
        valueField.setToolTipText(BUNDLE.getString("variableCard.valueTooltip"));
        definitionPanel.add(valueField);

        add(definitionPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());

        JButton settings = new JButton("⋮");
        settings.setToolTipText(BUNDLE.getString("generics.settings"));

        JPopupMenu settingsMenu = new JPopupMenu();

        JMenuItem removeVariable = new JMenuItem(BUNDLE.getString("generics.remove"));
        removeVariable.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(null,
                    BUNDLE.getString("generics.confirmDialog"),
                    BUNDLE.getString("generics.confirmDialog.title"),
                    JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                Workspace.getInstance().removeVariableCard(this);
            }
        });
        settingsMenu.add(removeVariable);

        settings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                settingsMenu.show(settings, e.getX(), e.getY());
            }
        });
        rightPanel.add(settings, BorderLayout.PAGE_START);

        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "apply");
        getActionMap().put("apply", new AbstractAction() {
            private static final long serialVersionUID = -7271902751222030104L;

            @Override
            public void actionPerformed(ActionEvent e) {
                updateVariable();
                PlottingPanel.getInstance().plot();
                PlottingPanel.getInstance().requestFocus();
            }
        });

        JButton apply = new JButton("✓");
        apply.setToolTipText(BUNDLE.getString("generics.apply"));
        apply.addActionListener(e -> getActionMap().get("apply").actionPerformed(e));
        rightPanel.add(apply, BorderLayout.PAGE_END);

        add(rightPanel, BorderLayout.LINE_END);
    }

    public void updateVariable() {
        variable.setName(nameField.getText());
        variable.setValue(EVALUATOR.evaluate(valueField.getText()));
        valueField.setText(variable.getValue() + "");
    }

    public Variable getVariable() {
        return variable;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }
}
