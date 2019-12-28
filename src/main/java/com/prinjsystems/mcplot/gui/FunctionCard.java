package com.prinjsystems.mcplot.gui;

import com.prinjsystems.mcplot.math.PlottableFunction;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class FunctionCard extends JPanel {
    private static final Random random = new Random();
    private static final Font monospacedFont = new Font("Monospaced", Font.PLAIN, 12);

    private PlottableFunction function;

    public FunctionCard(int functionIndex) {
        super(new BorderLayout());

        function = new PlottableFunction();
        function.setTraceColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));

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
        traceColorButton.setToolTipText(BUNDLE.getString("workspace.actions.selectColor"));
        traceColorButton.addActionListener(event -> {
            Color selectedColor = JColorChooser.showDialog(null,
                    BUNDLE.getString("workspace.actions.selectColor"), function.getTraceColor());
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
        JTextField functionTextField = new JTextField();
        functionTextField.setToolTipText(BUNDLE.getString("workspace.actions.functionDefinition"));
        functionTextField.setFont(monospacedFont);
        functionPanel.add(functionTextField, BorderLayout.CENTER);

        // JLabel
        functionPanel.add(new JLabel(BUNDLE.getString("workspace.actions.functionId").replace("{0}",
                functionIndex + "")), BorderLayout.PAGE_START);

        add(functionPanel, BorderLayout.CENTER);

        // Close and apply buttons
        JPanel buttons = new JPanel(new BorderLayout());

        JButton removeFunctionCard = new JButton("X");
        buttons.add(removeFunctionCard, BorderLayout.PAGE_START);

        JButton apply = new JButton("✓");
        apply.setToolTipText(BUNDLE.getString("generics.apply"));
        apply.addActionListener(event -> {
            function.setDefinition(functionTextField.getText());
            PlottingPanel.getInstance().plot();
        });
        buttons.add(apply, BorderLayout.PAGE_END);

        add(buttons, BorderLayout.LINE_END);
    }

    public PlottableFunction getFunction() {
        return function;
    }
}
