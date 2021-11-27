package com.prinjsystems.mcplot.ngui.components;

import com.prinjsystems.mcplot.ngui.PlottingPanel;
import com.prinjsystems.mcplot.nmath.Constant;
import com.prinjsystems.mcplot.nmath.Function;
import com.prinjsystems.mcplot.nmath.MathEvaluatorPool;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class FunctionCard extends JPanel {
    private static final Random RANDOM = new Random();

    private final Function function;
    private final PlottingPanel plottingPanel;

    public FunctionCard(Function function, List<Constant> constants, PlottingPanel plottingPanel,
                        int index) {
        this.plottingPanel = plottingPanel;
        setLayout(new MigLayout());
        this.function = function;

        setBorder(BorderFactory.createTitledBorder(
                MessageFormat.format(BUNDLE.getString("functionCard.functionId"), index)));

        ColorChooserButton colorChooserButton = new ColorChooserButton();
        add(colorChooserButton, "growy, split 2");
        colorChooserButton.setToolTipText(BUNDLE.getString("functionCard.selectColor"));
        Color startingColor = new Color(RANDOM.nextInt(255), RANDOM.nextInt(255),
                RANDOM.nextInt(255));
        function.setTraceColor(startingColor);
        colorChooserButton.setSelectedColor(startingColor);
        colorChooserButton.setMaximumSize(new Dimension(40, 40));
        colorChooserButton.setColorChooserListener(color -> {
            function.setTraceColor(color);
            plottingPanel.repaint();
        });

        JCheckBox active = new JCheckBox(BUNDLE.getString("functionCard.settings.functionVisible"), true);
        add(active, "pushx, growx, wrap");
        active.setToolTipText(BUNDLE.getString("functionCard.settings.functionVisibleTooltip"));

        JLabeledTextField functionField = new JLabeledTextField();
        add(functionField, "pushx, growx");
        functionField.setPlaceholderText(BUNDLE.getString("functionCard.functionDefinition.placeholder"));
        functionField.setToolTipText(BUNDLE.getString("functionCard.functionDefinition.tooltip"));
        functionField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!Objects.equals(function.getDefinition(), functionField.getText())) {
                    function.setDefinition(functionField.getText());
                    recalculateFunction(constants);
                    plottingPanel.repaint();
                }
            }
        });
        functionField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER
                        && !Objects.equals(function.getDefinition(), functionField.getText())) {
                    function.setDefinition(functionField.getText());
                    recalculateFunction(constants);
                }
            }
        });

        JButton remove = new JButton("X");
        add(remove);
        remove.setToolTipText(BUNDLE.getString("generics.remove"));
    }

    public void recalculateFunction(List<Constant> constants) {
        double zoomX = plottingPanel.getScaleX() * plottingPanel.getPixelsPerStep() * plottingPanel.getZoom();
        double domainStart = plottingPanel.getCameraX() / zoomX;
        double domainEnd = (plottingPanel.getCameraX() + plottingPanel.getWidth()) / zoomX;
        double step =
                (domainEnd - domainStart) / (plottingPanel.getWidth() / zoomX * plottingPanel.getSamplesPerCell());
        MathEvaluatorPool.getInstance().evaluateFunction(function.getDefinition(), domainStart - step,
                domainEnd + step, step, constants, path -> plottingPanel.getFunctions().put(function, path));
    }
}
