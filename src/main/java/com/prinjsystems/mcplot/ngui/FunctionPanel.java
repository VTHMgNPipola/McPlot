package com.prinjsystems.mcplot.ngui;

import com.prinjsystems.mcplot.ngui.components.FunctionCard;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class FunctionPanel extends JPanel {
    private final AtomicInteger functionCount;

    public FunctionPanel() {
        setLayout(new MigLayout());
        functionCount = new AtomicInteger(1);

        add(new FunctionCard(functionCount.getAndIncrement()), "pushx, span, growx");

        JButton addFunctionCard = new JButton(BUNDLE.getString("workspace.actions.createFunction"));
        add(addFunctionCard, "growx");
        addFunctionCard.addActionListener(e -> {
            add(new FunctionCard(functionCount.getAndIncrement()), "pushx, span, growx",
                    getComponentCount() - 1);
            updateUI();
        });
    }
}
