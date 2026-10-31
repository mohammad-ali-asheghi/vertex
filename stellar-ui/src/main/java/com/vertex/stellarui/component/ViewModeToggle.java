package com.vertex.stellarui.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.function.Consumer;

public class ViewModeToggle extends HorizontalLayout {

    public enum Mode {
        TABLE,
        GRID
    }

    private Mode currentMode = Mode.TABLE;
    private final Button tableBtn;
    private final Button gridBtn;
    private Consumer<Mode> changeListener;

    public ViewModeToggle() {
        setSpacing(false);
        getStyle()
                .set("background-color", "var(--lumo-contrast-5pct)")
                .set("padding", "3px")
                .set("border-radius", "8px")
                .set("border", "1px solid var(--lumo-contrast-15pct)");

        tableBtn = new Button(VaadinIcon.TABLE.create());
        tableBtn.setTooltipText("نمایش جدولی (فهرستی)");
        tableBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

        gridBtn = new Button(VaadinIcon.GRID_BIG.create());
        gridBtn.setTooltipText("نمایش کارتی (شبکه‌ای)");
        gridBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

        updateVisuals();

        tableBtn.addClickListener(e -> {
            if (currentMode != Mode.TABLE) {
                currentMode = Mode.TABLE;
                updateVisuals();
                if (changeListener != null) changeListener.accept(currentMode);
            }
        });

        gridBtn.addClickListener(e -> {
            if (currentMode != Mode.GRID) {
                currentMode = Mode.GRID;
                updateVisuals();
                if (changeListener != null) changeListener.accept(currentMode);
            }
        });

        add(tableBtn, gridBtn);
    }

    private void updateVisuals() {
        if (currentMode == Mode.TABLE) {
            tableBtn.getStyle()
                    .set("background-color", "var(--lumo-base-color)")
                    .set("box-shadow", "0 1px 3px rgba(0,0,0,0.1)")
                    .set("color", "var(--lumo-primary-text-color)");
            gridBtn.getStyle()
                    .remove("background-color")
                    .remove("box-shadow")
                    .set("color", "var(--lumo-secondary-text-color)");
        } else {
            gridBtn.getStyle()
                    .set("background-color", "var(--lumo-base-color)")
                    .set("box-shadow", "0 1px 3px rgba(0,0,0,0.1)")
                    .set("color", "var(--lumo-primary-text-color)");
            tableBtn.getStyle()
                    .remove("background-color")
                    .remove("box-shadow")
                    .set("color", "var(--lumo-secondary-text-color)");
        }
    }

    public void setOnModeChange(Consumer<Mode> listener) {
        this.changeListener = listener;
    }

    public Mode getMode() {
        return currentMode;
    }
}
