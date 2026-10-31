package com.vertex.stellarui.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class ConfirmDialog extends Dialog {

    public ConfirmDialog(String title, String message, Runnable onConfirm) {
        setHeaderTitle(title);
        setWidth("420px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);

        HorizontalLayout warningBanner = new HorizontalLayout();
        warningBanner.setAlignItems(FlexComponent.Alignment.CENTER);
        warningBanner.setSpacing(true);
        warningBanner.addClassNames(
                LumoUtility.Background.ERROR_10,
                LumoUtility.Padding.SMALL,
                LumoUtility.BorderRadius.MEDIUM
        );
        warningBanner.setWidthFull();

        var icon = VaadinIcon.WARNING.create();
        icon.setColor("var(--lumo-error-color)");
        icon.setSize("24px");

        Paragraph desc = new Paragraph(message);
        desc.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.Margin.NONE);
        desc.getStyle().set("color", "var(--lumo-error-text-color)");

        warningBanner.add(icon, desc);
        content.add(warningBanner);

        add(content);

        Button cancelBtn = new Button("انصراف", e -> close());
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button deleteBtn = new Button("حذف", e -> {
            close();
            onConfirm.run();
        });
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        getFooter().add(cancelBtn, deleteBtn);
    }

    public static void show(String title, String message, Runnable onConfirm) {
        ConfirmDialog dialog = new ConfirmDialog(title, message, onConfirm);
        dialog.open();
    }
}
