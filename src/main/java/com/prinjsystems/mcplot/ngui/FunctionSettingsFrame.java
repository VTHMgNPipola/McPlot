package com.prinjsystems.mcplot.ngui;

import com.prinjsystems.mcplot.ngui.components.FunctionCard;
import com.prinjsystems.mcplot.nmath.Function;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class FunctionSettingsFrame extends JFrame {
    private final Function function;
    private final FunctionCard functionCard;

    public FunctionSettingsFrame(Function function, FunctionCard functionCard, int index) {
        super(MessageFormat.format(BUNDLE.getString("functionSettings.title"), index));
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.function = function;
        this.functionCard = functionCard;
    }

    public void init(PlottingPanel plottingPanel) {
        initContentPane(plottingPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private void initContentPane(PlottingPanel plottingPanel) {
        JPanel contentPane = new JPanel(new MigLayout("insets 15", "[]15",
                "[]10"));
        setContentPane(contentPane);

        JLabel domainStartLabel = new JLabel(BUNDLE.getString("functionSettings.domainStart"));
        contentPane.add(domainStartLabel);
        JTextField domainStart = new JTextField(function.getDomainStart() != null ?
                function.getDomainStart().toString() : "*");
        contentPane.add(domainStart, "growx, wrap");
        domainStart.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = domainStart.getText().trim();
                function.setDomainStart(text.equals("*") ? null : Double.parseDouble(text));
                functionCard.recalculateFunction();
                plottingPanel.repaint();
            }
        });

        JLabel domainEndLabel = new JLabel(BUNDLE.getString("functionSettings.domainEnd"));
        contentPane.add(domainEndLabel);
        JTextField domainEnd = new JTextField(function.getDomainEnd() != null ?
                function.getDomainEnd().toString() : "*");
        contentPane.add(domainEnd, "growx, wrap");
        domainEnd.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = domainEnd.getText().trim();
                function.setDomainEnd(text.equals("*") ? null : Double.parseDouble(text));
                functionCard.recalculateFunction();
                plottingPanel.repaint();
            }
        });

        JCheckBox fillArea = new JCheckBox(BUNDLE.getString("functionSettings.fillArea"), function.isFilled());
        contentPane.add(fillArea, "span");
        fillArea.setToolTipText(BUNDLE.getString("functionSettings.fillAreaTooltip"));
        fillArea.addActionListener(e -> {
            function.setFilled(fillArea.isSelected());
            plottingPanel.repaint();
        });
    }
}
