package com.prinjsystems.mcplot.ngui;

import com.prinjsystems.mcplot.ngui.components.FunctionCard;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class FunctionPanel extends JPanel {
    private final AtomicInteger functionCount;

    public FunctionPanel() {
        setLayout(new MigLayout());
        functionCount = new AtomicInteger(1);

        add(new FunctionCard(functionCount.getAndIncrement()), "pushx, span, growx");
    }
}
