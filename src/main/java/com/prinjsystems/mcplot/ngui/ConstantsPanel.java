package com.prinjsystems.mcplot.ngui;

import com.prinjsystems.mcplot.ngui.components.ConstantCard;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class ConstantsPanel extends JPanel {
    private final AtomicInteger constantCount;

    public ConstantsPanel() {
        super(new MigLayout());
        constantCount = new AtomicInteger(1);

        add(new ConstantCard(constantCount.getAndIncrement()), "pushx, span, growx");

        JButton addConstantCard = new JButton(BUNDLE.getString("workspace.actions.createConstant"));
        add(addConstantCard, "growx");
        addConstantCard.addActionListener(e -> {
            add(new ConstantCard(constantCount.getAndIncrement()), "pushx, span, growx",
                    getComponentCount() - 1);
            updateUI();
        });
    }
}
