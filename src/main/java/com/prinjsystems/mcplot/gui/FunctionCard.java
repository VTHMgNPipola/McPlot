package com.prinjsystems.mcplot.gui;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.prinjsystems.mcplot.math.PlottableFunction;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class FunctionCard extends JPanel {
    private static final Random random = new Random();
    private static final Font monospacedFont = new Font("Monospaced", Font.PLAIN, 12);

    private PlottableFunction function;

    public FunctionCard(int functionIndex) {
        super(new BorderLayout());

        function = new PlottableFunction();
        function.setTraceColor(new Color(random.nextInt(255), random.nextInt(255),
                random.nextInt(255)));

        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JButton traceColorButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                int squareSize = 25;
                g.setColor(function.getTraceColor());
                g.fillRect(getWidth() / 2 - squareSize / 2, getHeight() / 2 - squareSize / 2, 25, 25);
                g.setColor(Color.BLACK);
                g.drawRect(getWidth() / 2 - squareSize / 2, getHeight() / 2 - squareSize / 2, 25, 25);
            }
        };
        traceColorButton.setToolTipText(BUNDLE.getString("functionCard.selectColor"));
        traceColorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(null,
                    BUNDLE.getString("functionCard.selectColor"), function.getTraceColor());
            if (selectedColor != null) {
                function.setTraceColor(selectedColor);
                PlottingPanel.getInstance().plot();
            }
        });
        traceColorButton.setPreferredSize(new Dimension(40, 40)); // The width and height are bigger than
        // what is drawn because the remaining pixels are used as padding
        traceColorButton.setContentAreaFilled(false);
        add(traceColorButton, BorderLayout.LINE_START);

        // Function definition panel
        JPanel functionPanel = new JPanel(new BorderLayout());

        // Function JTextField
        JLabeledTextField functionTextField = new JLabeledTextField();
        functionTextField.setPlaceholderText(BUNDLE.getString("functionCard.functionDefinitionPlaceholder"));
        functionTextField.setToolTipText(BUNDLE.getString("functionCard.functionDefinitionTooltip"));
        functionTextField.setFont(monospacedFont);
        functionPanel.add(functionTextField, BorderLayout.CENTER);

        // JLabel
        functionPanel.add(new JLabel(BUNDLE.getString("functionCard.functionId").replace("{0}",
                functionIndex + "")), BorderLayout.PAGE_START);

        add(functionPanel, BorderLayout.CENTER);

        // Close and apply buttons
        JPanel buttons = new JPanel(new BorderLayout());

        JButton functionSettings = new JButton("⋮");
        functionSettings.setToolTipText(BUNDLE.getString("generics.settings"));
        JPopupMenu settingsMenu = new JPopupMenu();

        JCheckBoxMenuItem visible = new JCheckBoxMenuItem(BUNDLE.getString("functionCard.settings.functionVisible"));
        visible.setState(function.isVisible());
        visible.addActionListener(e -> {
            function.setVisible(visible.getState());
            PlottingPanel.getInstance().plot();
        });
        settingsMenu.add(visible);

        JMenuItem domain = new JMenuItem(BUNDLE.getString("functionCard.settings.functionDomain"));
        DoubleEvaluator evaluator = new DoubleEvaluator();
        domain.addActionListener(e -> {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JPanel domainStartPanel = new JPanel();
            domainStartPanel.setLayout(new BoxLayout(domainStartPanel, BoxLayout.X_AXIS));
            JLabel domainStartLabel = new JLabel(BUNDLE.getString("functionCard.settings.domainStart"));
            domainStartLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            domainStartPanel.add(domainStartLabel);

            domainStartPanel.add(Box.createHorizontalGlue());

            Dimension maximumFieldDimension = new Dimension(200, 20);

            JTextField domainStartField = new JTextField();
            domainStartField.setText(function.getDomainStart() == -Double.MAX_VALUE ? "*" : function.getDomainStart() + "");
            domainStartField.setAlignmentX(Component.RIGHT_ALIGNMENT);
            domainStartField.setPreferredSize(maximumFieldDimension);
            domainStartField.setMaximumSize(maximumFieldDimension);
            domainStartPanel.add(domainStartField);
            panel.add(domainStartPanel);

            panel.add(Box.createVerticalStrut(5));

            JPanel domainEndPanel = new JPanel();
            domainEndPanel.setLayout(new BoxLayout(domainEndPanel, BoxLayout.X_AXIS));
            JLabel domainEndLabel = new JLabel(BUNDLE.getString("functionCard.settings.domainEnd"));
            domainEndLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            domainEndPanel.add(domainEndLabel);

            domainEndPanel.add(Box.createHorizontalStrut(20));

            JTextField domainEndField = new JTextField();
            domainEndField.setText(function.getDomainEnd() == Double.MAX_VALUE ? "*" : function.getDomainEnd() + "");
            domainEndField.setAlignmentX(Component.RIGHT_ALIGNMENT);
            domainEndField.setPreferredSize(maximumFieldDimension);
            domainEndField.setMaximumSize(maximumFieldDimension);
            domainEndPanel.add(domainEndField);
            panel.add(domainEndPanel);

            int option = JOptionPane.showConfirmDialog(null, panel,
                    BUNDLE.getString("functionCard.settings.functionDomain"), JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String domainStart = domainStartField.getText().trim();
                String domainEnd = domainEndField.getText().trim();

                double domainStartValue = -Double.MAX_VALUE;
                double domainEndValue = Double.MAX_VALUE;
                if (!domainStart.equals("*")) {
                    domainStartValue = evaluator.evaluate(domainStart);
                }
                if (!domainEnd.equals("*")) {
                    domainEndValue = evaluator.evaluate(domainEnd);
                }
                function.setDomainStart(domainStartValue);
                function.setDomainEnd(domainEndValue);
                PlottingPanel.getInstance().plot();
            }
        });
        settingsMenu.add(domain);

        functionSettings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                settingsMenu.show(functionSettings, e.getX(), e.getY());
            }
        });
        buttons.add(functionSettings, BorderLayout.PAGE_START);

        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "apply");
        getActionMap().put("apply", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                function.setDefinition(functionTextField.getText());
                PlottingPanel.getInstance().plot();
            }
        });

        JButton apply = new JButton("✓");
        apply.setToolTipText(BUNDLE.getString("generics.apply"));
        apply.addActionListener(e -> getActionMap().get("apply").actionPerformed(e));
        buttons.add(apply, BorderLayout.PAGE_END);

        add(buttons, BorderLayout.LINE_END);
    }

    public PlottableFunction getFunction() {
        return function;
    }
}
