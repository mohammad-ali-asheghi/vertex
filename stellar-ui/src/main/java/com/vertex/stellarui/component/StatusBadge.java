package com.vertex.stellarui.component;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class StatusBadge extends Span {

    public StatusBadge(boolean active) {
        this(active, active ? "فعال" : "غیرفعال");
    }

    public StatusBadge(boolean active, String customLabel) {
        super(customLabel);
        addClassNames(
                LumoUtility.FontSize.XSMALL,
                LumoUtility.FontWeight.SEMIBOLD,
                LumoUtility.Padding.Vertical.XSMALL,
                LumoUtility.Padding.Horizontal.SMALL,
                LumoUtility.BorderRadius.FULL
        );

        if (active) {
            getStyle()
                    .set("background-color", "rgba(16, 185, 129, 0.15)")
                    .set("color", "#059669")
                    .set("border", "1px solid rgba(16, 185, 129, 0.3)");
        } else {
            getStyle()
                    .set("background-color", "rgba(239, 68, 68, 0.12)")
                    .set("color", "#dc2626")
                    .set("border", "1px solid rgba(239, 68, 68, 0.25)");
        }
    }

    public static StatusBadge warning(String label) {
        StatusBadge badge = new StatusBadge(false, label);
        badge.getStyle()
                .set("background-color", "rgba(245, 158, 11, 0.15)")
                .set("color", "#d97706")
                .set("border", "1px solid rgba(245, 158, 11, 0.3)");
        return badge;
    }

    public static StatusBadge info(String label) {
        StatusBadge badge = new StatusBadge(true, label);
        badge.getStyle()
                .set("background-color", "rgba(59, 130, 246, 0.15)")
                .set("color", "#2563eb")
                .set("border", "1px solid rgba(59, 130, 246, 0.3)");
        return badge;
    }
}
