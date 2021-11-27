package com.prinjsystems.mcplot.ngui.components;

import com.prinjsystems.mcplot.ngui.PlottingPanel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import net.miginfocom.swing.MigLayout;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class PlottingPanelSettingsPanel extends JPanel {
    public PlottingPanelSettingsPanel(PlottingPanel plottingPanel) {
        setLayout(new MigLayout("insets 15", "[]15", "[]10"));

        JLabel samplesPerCellLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.samplesPerCell"));
        add(samplesPerCellLabel);
        JSpinner samplesPerCell = new JSpinner(new SpinnerNumberModel(plottingPanel.getSamplesPerCell(), 1,
                999, 1));
        add(samplesPerCell, "growx, wrap");
        samplesPerCell.setToolTipText(BUNDLE.getString("settings.plottingPanel.samplesPerCellTooltip"));
        samplesPerCell.addChangeListener(e -> plottingPanel.setSamplesPerCell((int) samplesPerCell.getValue()));

        JLabel scaleXLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.scaleX"));
        add(scaleXLabel);
        JSpinner scaleX = new JSpinner(new SpinnerNumberModel(plottingPanel.getScaleX(), 0.0001, 999,
                0.5));
        add(scaleX, "growx, wrap");
        scaleX.addChangeListener(e -> plottingPanel.setScaleX((double) scaleX.getValue()));

        JLabel scaleYLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.scaleY"));
        add(scaleYLabel);
        JSpinner scaleY = new JSpinner(new SpinnerNumberModel(plottingPanel.getScaleY(), 0.0001, 999,
                0.5));
        add(scaleY, "growx, wrap");
        scaleY.addChangeListener(e -> plottingPanel.setScaleY((double) scaleY.getValue()));

        JLabel traceWidthLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.traceWidth"));
        add(traceWidthLabel);
        JSpinner traceWidth = new JSpinner(new SpinnerNumberModel(plottingPanel.getTraceWidth(), 1, 10, 1));
        add(traceWidth, "growx, wrap");
        traceWidth.addChangeListener(e -> plottingPanel.setTraceWidth((int) traceWidth.getValue()));

        JLabel fillTransparencyLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.fillTransparency"));
        add(fillTransparencyLabel);
        JSpinner fillTransparency = new JSpinner(new SpinnerNumberModel(plottingPanel.getFillTransparency(), 0,
                100, 0.5));
        add(fillTransparency, "growx, wrap");
        fillTransparency.setToolTipText(BUNDLE.getString("settings.plottingPanel.fillTransparencyTooltip"));
        fillTransparency.addChangeListener(e -> plottingPanel.setFillTransparency((double) fillTransparency.getValue()));

        JLabel backgroundColorLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.backgroundColor"));
        add(backgroundColorLabel);
        ColorChooserButton backgroundColor = new ColorChooserButton();
        add(backgroundColor, "pushx, growy, wrap");
        backgroundColor.setSelectedColor(plottingPanel.getBackgroundColor());
        backgroundColor.setColorChooserListener(plottingPanel::setBackgroundColor);

        JLabel minorGridColorLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.minorGridColor"));
        add(minorGridColorLabel);
        ColorChooserButton minorGridColor = new ColorChooserButton();
        add(minorGridColor, "pushx, growy, wrap");
        minorGridColor.setSelectedColor(plottingPanel.getMinorGridColor());
        minorGridColor.setColorChooserListener(plottingPanel::setMinorGridColor);

        JLabel majorGridColorLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.majorGridColor"));
        add(majorGridColorLabel);
        ColorChooserButton majorGridColor = new ColorChooserButton();
        add(majorGridColor, "pushx, growy, wrap");
        majorGridColor.setSelectedColor(plottingPanel.getMajorGridColor());
        majorGridColor.setColorChooserListener(plottingPanel::setMajorGridColor);

        JLabel globalAxisColorLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.globalAxisColor"));
        add(globalAxisColorLabel);
        ColorChooserButton globalAxisColor = new ColorChooserButton();
        add(globalAxisColor, "pushx, growy");
        globalAxisColor.setSelectedColor(plottingPanel.getGlobalAxisColor());
        globalAxisColor.setColorChooserListener(plottingPanel::setGlobalAxisColor);
    }
}
