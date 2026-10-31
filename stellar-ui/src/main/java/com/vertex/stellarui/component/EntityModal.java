package com.vertex.stellarui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class EntityModal extends Dialog {

    private final VerticalLayout bodyLayout = new VerticalLayout();
    private final Button saveButton;
    private final Button cancelButton;

    public EntityModal(String title, Runnable onSave) {
        setHeaderTitle(title);
        setWidth("540px");
        setMaxWidth("90vw");

        bodyLayout.setPadding(false);
        bodyLayout.setSpacing(true);
        bodyLayout.setWidthFull();
        add(bodyLayout);

        cancelButton = new Button("انصراف", e -> close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        saveButton = new Button("ذخیره تغییرات", e -> onSave.run());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(VaadinIcon.CHECK.create());

        getFooter().add(cancelButton, saveButton);
    }

    public void setFormContent(Component... components) {
        bodyLayout.removeAll();
        bodyLayout.add(components);
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }
}
