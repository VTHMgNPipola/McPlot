package com.prinjsystems.mcplot.ngui;

import java.awt.Font;
import java.text.MessageFormat;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.prinjsystems.mcplot.Main.BUNDLE;
import static com.prinjsystems.mcplot.Main.VERSION;

public class AboutFrame extends JFrame {
    public AboutFrame() {
        super(BUNDLE.getString("about.title"));
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public void init() {
        initContentPane();
        pack();
        setLocationRelativeTo(null);
    }

    private void initContentPane() {
        JPanel contentPane = new JPanel(new MigLayout("insets 40, flowy"));

        JLabel mcplot = new JLabel("McPlot");
        contentPane.add(mcplot, "alignx center");
        mcplot.setFont(new Font("Helvetica", Font.BOLD, 40));

        JLabel description = new JLabel(BUNDLE.getString("about.description"));
        contentPane.add(description, "alignx center");

        JLabel version = new JLabel(MessageFormat.format(BUNDLE.getString("about.version"), VERSION));
        contentPane.add(version, "alignx center");

        setContentPane(contentPane);
    }
}
