package com.prinjsystems.mcplot.ngui;

import com.prinjsystems.mcplot.ngui.components.FunctionCard;
import com.prinjsystems.mcplot.nmath.Constant;
import com.prinjsystems.mcplot.nmath.Function;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class FunctionPanel extends JPanel {
    private final List<FunctionCard> functionCards;

    private AtomicInteger index;

    public FunctionPanel(List<FunctionCard> functionCards, List<Constant> constants, PlottingPanel plottingPanel) {
        setLayout(new MigLayout());
        this.functionCards = functionCards;
        index = new AtomicInteger(1);

        FunctionCard firstFunctionCard = new FunctionCard(new Function(), constants, plottingPanel, this,
                index.getAndIncrement());
        add(firstFunctionCard, "pushx, span, growx");
        functionCards.add(firstFunctionCard);

        JButton addFunctionCard = new JButton(BUNDLE.getString("workspace.actions.createFunction"));
        add(addFunctionCard, "growx");
        addFunctionCard.addActionListener(e -> {
            FunctionCard functionCard = new FunctionCard(new Function(), constants, plottingPanel, this,
                    index.getAndIncrement());
            add(functionCard, "pushx, span, growx", getComponentCount() - 1);
            functionCards.add(functionCard);
            updateUI();
        });
    }

    public void removeFunctionCard(FunctionCard functionCard) {
        remove(functionCard);
        functionCards.remove(functionCard);

        index = new AtomicInteger(1);
        functionCards.forEach(fc -> fc.setIndex(index.getAndIncrement()));

        updateUI();
    }
}
