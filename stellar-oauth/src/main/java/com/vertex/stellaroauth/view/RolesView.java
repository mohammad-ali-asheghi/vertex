package com.vertex.stellaroauth.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vertex.core.dto.RoleModel;
import com.vertex.core.util.PagedResponse;
import com.vertex.stellaroauth.service.RoleClient;
import com.vertex.stellarui.component.*;
import com.vertex.stellarui.constant.Routing;
import com.vertex.stellarui.layout.MainLayout;
import com.vertex.stellarui.util.Notifier;
import com.vertex.stellarui.util.Requester;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

@Route(value = Routing.ROLES, layout = MainLayout.class)
public class RolesView extends VerticalLayout implements HasDynamicTitle {

    private final RoleClient roleClient;

    private final List<RoleModel> roleRecords = new ArrayList<>();
    private final Grid<RoleModel> grid = new Grid<>();
    private final VerticalLayout cardsContainer = new VerticalLayout();
    private final Div contentWrapper = new Div();

    private ViewModeToggle.Mode currentMode = ViewModeToggle.Mode.TABLE;
    private String currentSearch = "";
    private Long editingRoleId = null;

    private EntityModal roleModal;
    private TextField nameField;
    private TextField aliasNameField;
    private TextArea descriptionField;
    private Checkbox activeCheckbox;

    public RolesView(RoleClient roleClient) {
        this.roleClient = roleClient;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        setupHeader();
        setupGrid();
        setupCardsContainer();
        setupModal();

        contentWrapper.setSizeFull();
        contentWrapper.add(grid);
        add(contentWrapper);

        loadRoles();
    }

    private void setupHeader() {
        ResourceHeader header = new ResourceHeader(
                VaadinIcon.KEY,
                "مدیریت نقش‌های کاربری",
                "تعریف دسته‌بندی مسئولیت‌ها و الگوهای مجوز دسترسی در سطح سیستم",
                "نقش جدید",
                this::openCreateModal
        );

        header.setOnSearch(value -> {
            this.currentSearch = value != null ? value.trim() : "";
            loadRoles();
        });

        header.setOnViewModeChange(mode -> {
            this.currentMode = mode;
            contentWrapper.removeAll();
            if (mode == ViewModeToggle.Mode.TABLE) {
                contentWrapper.add(grid);
            } else {
                renderCards();
                contentWrapper.add(cardsContainer);
            }
        });

        add(header);
    }

    private void setupGrid() {
        grid.setSizeFull();

        grid.addColumn(RoleModel::getId)
                .setHeader("شناسه")
                .setWidth("80px")
                .setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(role -> {
            Span name = new Span(role.getName());
            name.addClassNames(LumoUtility.FontWeight.BOLD);
            name.getStyle().set("font-family", "monospace");
            return name;
        })).setHeader("نام سیستمی (انگلیسی)").setFlexGrow(1);

        grid.addColumn(new ComponentRenderer<>(role -> {
            Span alias = new Span(role.getAliasName() != null ? role.getAliasName() : "-");
            alias.addClassNames(LumoUtility.FontWeight.MEDIUM);
            return alias;
        })).setHeader("عنوان فارسی (مستعار)").setFlexGrow(1);

        grid.addColumn(RoleModel::getDescription)
                .setHeader("توضیحات")
                .setFlexGrow(2);

        grid.addColumn(new ComponentRenderer<>(role -> new StatusBadge(role.isActive())))
                .setHeader("وضعیت")
                .setWidth("120px")
                .setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(this::createActionsComponent))
                .setHeader("عملیات")
                .setWidth("180px")
                .setFlexGrow(0);
    }

    private Component createActionsComponent(RoleModel role) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);

        Button permBtn = new Button(VaadinIcon.SHIELD.create());
        permBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        permBtn.setTooltipText("تنظیم دسترسی‌های این نقش");
        permBtn.addClickListener(e -> UI.getCurrent().navigate(Routing.PERMISSIONS));

        Button editBtn = new Button(VaadinIcon.EDIT.create());
        editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        editBtn.setTooltipText("ویرایش نقش");
        editBtn.addClickListener(e -> openEditModal(role));

        Button deleteBtn = new Button(VaadinIcon.TRASH.create());
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        deleteBtn.setTooltipText("حذف نقش");
        deleteBtn.addClickListener(e -> confirmDelete(role));

        actions.add(permBtn, editBtn, deleteBtn);
        return actions;
    }

    private void setupCardsContainer() {
        cardsContainer.setWidthFull();
        cardsContainer.setPadding(false);
        cardsContainer.setSpacing(true);
    }

    private void renderCards() {
        cardsContainer.removeAll();
        if (roleRecords.isEmpty()) {
            VerticalLayout empty = new VerticalLayout();
            empty.setAlignItems(FlexComponent.Alignment.CENTER);
            empty.addClassNames("vertex-card", LumoUtility.Padding.XLARGE);
            empty.add(new H3("نقشی یافت نشد"), new Paragraph("برای ایجاد نقش جدید از دکمه بالا استفاده فرمایید."));
            cardsContainer.add(empty);
            return;
        }

        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);

        int count = 0;
        for (RoleModel role : roleRecords) {
            VerticalLayout card = new VerticalLayout();
            card.addClassNames("vertex-card");
            card.setWidth("350px");
            card.setSpacing(true);

            HorizontalLayout top = new HorizontalLayout();
            top.setWidthFull();
            top.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            top.setAlignItems(FlexComponent.Alignment.CENTER);

            Span title = new Span(role.getAliasName() != null ? role.getAliasName() : role.getName());
            title.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.MEDIUM);

            top.add(title, new StatusBadge(role.isActive()));

            Span enName = new Span(role.getName());
            enName.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
            enName.getStyle().set("font-family", "monospace");

            Paragraph desc = new Paragraph(role.getDescription() != null ? role.getDescription() : "بدون توضیحات");
            desc.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY);

            HorizontalLayout bottom = new HorizontalLayout();
            bottom.setWidthFull();
            bottom.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            bottom.setAlignItems(FlexComponent.Alignment.CENTER);

            Span idSpan = new Span("شناسه: " + role.getId());
            idSpan.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY);

            Component actions = createActionsComponent(role);
            bottom.add(idSpan, actions);

            card.add(top, enName, desc, bottom);
            row.add(card);
            count++;

            if (count % 3 == 0) {
                cardsContainer.add(row);
                row = new HorizontalLayout();
                row.setWidthFull();
                row.setSpacing(true);
            }
        }

        if (count % 3 != 0) {
            cardsContainer.add(row);
        }
    }

    private void setupModal() {
        roleModal = new EntityModal("اطلاعات نقش کاربری", this::saveRole);

        nameField = new TextField("نام سیستمی انگلیسی");
        nameField.setWidthFull();
        nameField.setRequired(true);
        nameField.setPlaceholder("مثلاً ADMIN یا MANAGER");

        aliasNameField = new TextField("عنوان مستعار فارسی");
        aliasNameField.setWidthFull();
        aliasNameField.setRequired(true);
        aliasNameField.setPlaceholder("مثلاً مدیر ارشد سامانه");

        descriptionField = new TextArea("توضیحات و اهداف نقش");
        descriptionField.setWidthFull();

        activeCheckbox = new Checkbox("نقش فعال باشد");
        activeCheckbox.setValue(true);

        roleModal.setFormContent(nameField, aliasNameField, descriptionField, activeCheckbox);
    }

    private void openCreateModal() {
        editingRoleId = null;
        roleModal.setHeaderTitle("ایجاد نقش جدید");
        nameField.clear();
        aliasNameField.clear();
        descriptionField.clear();
        activeCheckbox.setValue(true);
        roleModal.open();
    }

    private void openEditModal(RoleModel role) {
        editingRoleId = role.getId();
        roleModal.setHeaderTitle("ویرایش نقش: " + role.getName());
        nameField.setValue(role.getName() != null ? role.getName() : "");
        aliasNameField.setValue(role.getAliasName() != null ? role.getAliasName() : "");
        descriptionField.setValue(role.getDescription() != null ? role.getDescription() : "");
        activeCheckbox.setValue(role.isActive());
        roleModal.open();
    }

    private void saveRole() {
        String name = nameField.getValue();
        String alias = aliasNameField.getValue();

        if (name == null || name.trim().isEmpty()) {
            Notifier.error("تعیین نام سیستمی نقش الزامی است.");
            return;
        }

        RoleModel model = new RoleModel();
        model.setName(name.trim());
        model.setAliasName(alias != null ? alias.trim() : null);
        model.setDescription(descriptionField.getValue());
        model.setActive(activeCheckbox.getValue());

        if (editingRoleId == null) {
            boolean ok = Requester.executeAndCheck(() -> roleClient.create(model));
            if (ok) {
                Notifier.success("نقش جدید با موفقیت ایجاد شد.");
                roleModal.close();
                loadRoles();
            }
        } else {
            model.setId(editingRoleId);
            boolean ok = Requester.executeAndCheck(() -> roleClient.update(model));
            if (ok) {
                Notifier.success("اطلاعات نقش با موفقیت به‌روزرسانی شد.");
                roleModal.close();
                loadRoles();
            }
        }
    }

    private void confirmDelete(RoleModel role) {
        ConfirmDialog.show(
                "حذف نقش",
                "آیا از حذف نقش «" + role.getName() + "» اطمینان دارید؟ تمامی دسترسی‌های مرتبط با این نقش لغو خواهد شد.",
                () -> {
                    boolean ok = Requester.executeAndCheck(() -> roleClient.delete(role.getId()));
                    if (ok) {
                        Notifier.success("نقش با موفقیت حذف شد.");
                        loadRoles();
                    }
                }
        );
    }

    private void loadRoles() {
        RoleModel search = new RoleModel();
        if (currentSearch != null && !currentSearch.isEmpty()) {
            search.setName(currentSearch);
            search.setAliasName(currentSearch);
        }

        PagedResponse<RoleModel> response = Requester.executePaged(() -> roleClient.getList(search, PageRequest.of(0, 100)));
        if (response != null && response.getResultList() != null) {
            roleRecords.clear();
            roleRecords.addAll(response.getResultList());
            grid.setItems(roleRecords);
            if (currentMode == ViewModeToggle.Mode.GRID) {
                renderCards();
            }
        }
    }

    @Override
    public String getPageTitle() {
        return "مدیریت نقش‌ها — ورتکس";
    }
}
