package com.space.engine.core.graphics.ui;

import java.util.function.DoubleSupplier;

public class DebugStatsUIElement extends UIElement{

    private final DoubleSupplier dtSupplier;
    private final DoubleSupplier rtSupplier;
    private final DoubleSupplier ptSupplier;

    public DebugStatsUIElement(
            int x,
            int y,
            int width,
            int height,
            DoubleSupplier dtSupplier,
            DoubleSupplier rtSupplier,
            DoubleSupplier ptSupplier
    ) {
        super(x, y, width, height);

        this.dtSupplier = dtSupplier;
        this.rtSupplier = rtSupplier;
        this.ptSupplier = ptSupplier;
    }

    @Override
    public void update(double deltaTime) {
        var dt = dtSupplier.getAsDouble();

        putText(0, 0, String.format("DT: %.2f ms", dt));
        putText(0, 1, String.format("RT: %.2f ms", rtSupplier.getAsDouble()));
        putText(0, 2, String.format("PT: %.2f ms", ptSupplier.getAsDouble()));
    }
}
