package com.prinjsystems.mcplot.ngui;

import com.prinjsystems.mcplot.ngui.components.ColorChooserButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import net.miginfocom.swing.MigLayout;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class SettingsFrame extends JFrame {
    private final PlottingPanel plottingPanel;

    public SettingsFrame(PlottingPanel plottingPanel) {
        super(BUNDLE.getString("settings.title"));
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.plottingPanel = plottingPanel;
    }

    public void init() {
        initContentPane();
        pack();
        setLocationRelativeTo(null);
    }

    private void initContentPane() {
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        getContentPane().add(tabbedPane);

        JPanel plottingPanelSettings = new JPanel(new MigLayout("insets 15", "[]15",
                "[]10"));
        tabbedPane.addTab(BUNDLE.getString("settings.plottingPanel.title"),
                new JScrollPane(plottingPanelSettings));

        JLabel scaleXLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.scaleX"));
        plottingPanelSettings.add(scaleXLabel);
        JSpinner scaleX = new JSpinner(new SpinnerNumberModel(plottingPanel.getScaleX(), 0.0001, 999,
                0.5));
        plottingPanelSettings.add(scaleX, "growx, wrap");
        scaleX.addChangeListener(e -> plottingPanel.setScaleX((double) scaleX.getValue()));

        JLabel scaleYLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.scaleY"));
        plottingPanelSettings.add(scaleYLabel);
        JSpinner scaleY = new JSpinner(new SpinnerNumberModel(plottingPanel.getScaleY(), 0.0001, 999,
                0.5));
        plottingPanelSettings.add(scaleY, "growx, wrap");
        scaleY.addChangeListener(e -> plottingPanel.setScaleY((double) scaleY.getValue()));

        JLabel backgroundColorLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.backgroundColor"));
        plottingPanelSettings.add(backgroundColorLabel);
        ColorChooserButton backgroundColor = new ColorChooserButton();
        plottingPanelSettings.add(backgroundColor, "pushx, growy, wrap");
        backgroundColor.setSelectedColor(plottingPanel.getBackgroundColor());

        JLabel minorGridColorLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.minorGridColor"));
        plottingPanelSettings.add(minorGridColorLabel);
        ColorChooserButton minorGridColor = new ColorChooserButton();
        plottingPanelSettings.add(minorGridColor, "pushx, growy, wrap");
        minorGridColor.setSelectedColor(plottingPanel.getMinorGridColor());

        JLabel majorGridColorLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.majorGridColor"));
        plottingPanelSettings.add(majorGridColorLabel);
        ColorChooserButton majorGridColor = new ColorChooserButton();
        plottingPanelSettings.add(majorGridColor, "pushx, growy, wrap");
        majorGridColor.setSelectedColor(plottingPanel.getMajorGridColor());

        JLabel globalAxisColorLabel = new JLabel(BUNDLE.getString("settings.plottingPanel.globalAxisColor"));
        plottingPanelSettings.add(globalAxisColorLabel);
        ColorChooserButton globalAxisColor = new ColorChooserButton();
        plottingPanelSettings.add(globalAxisColor, "pushx, growy");
        globalAxisColor.setSelectedColor(plottingPanel.getGlobalAxisColor());
    }
}