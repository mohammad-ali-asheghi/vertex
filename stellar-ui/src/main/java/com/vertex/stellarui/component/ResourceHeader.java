package com.vertex.stellarui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.function.Consumer;

public class ResourceHeader extends VerticalLayout {

    private final TextField searchField;
    private final ViewModeToggle viewModeToggle;
    private final Button actionButton;
    private final HorizontalLayout toolbar;

    public ResourceHeader(
            VaadinIcon icon,
            String title,
            String subtitle,
            String actionLabel,
            Runnable onActionClick
    ) {
        setWidthFull();
        setPadding(false);
        setSpacing(true);
        addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

        // Top Row: Title info and Action Button
        HorizontalLayout topRow = new HorizontalLayout();
        topRow.setWidthFull();
        topRow.setAlignItems(FlexComponent.Alignment.CENTER);
        topRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        HorizontalLayout titleArea = new HorizontalLayout();
        titleArea.setAlignItems(FlexComponent.Alignment.CENTER);
        titleArea.setSpacing(true);

        Icon headerIcon = icon.create();
        headerIcon.setSize("28px");
        headerIcon.setColor("var(--lumo-primary-color)");

        VerticalLayout textLayout = new VerticalLayout();
        textLayout.setPadding(false);
        textLayout.setSpacing(false);

        H2 headerTitle = new H2(title);
        headerTitle.addClassNames(
                LumoUtility.FontSize.XLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Margin.NONE
        );

        Paragraph headerSubtitle = new Paragraph(subtitle);
        headerSubtitle.addClassNames(
                LumoUtility.FontSize.SMALL,
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.Margin.NONE
        );

        textLayout.add(headerTitle, headerSubtitle);
        titleArea.add(headerIcon, textLayout);

        actionButton = new Button(actionLabel);
        actionButton.setIcon(VaadinIcon.PLUS.create());
        actionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        actionButton.getStyle().set("border-radius", "8px");
        if (onActionClick != null) {
            actionButton.addClickListener(e -> onActionClick.run());
        }

        topRow.add(titleArea, actionButton);

        // Bottom Row: Search Field & View Mode Toggle
        toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        searchField = new TextField();
        searchField.setPlaceholder("جستجو در سوابق...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        searchField.setWidth("320px");

        viewModeToggle = new ViewModeToggle();

        toolbar.add(searchField, viewModeToggle);

        add(topRow, toolbar);
    }

    public void setOnSearch(Consumer<String> listener) {
        searchField.addValueChangeListener(e -> listener.accept(e.getValue()));
    }

    public void setOnViewModeChange(Consumer<ViewModeToggle.Mode> listener) {
        viewModeToggle.setOnModeChange(listener);
    }

    public void addToolbarComponent(Component component) {
        toolbar.addComponentAtIndex(1, component);
    }

    public Button getActionButton() {
        return actionButton;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public ViewModeToggle getViewModeToggle() {
        return viewModeToggle;
    }
}
