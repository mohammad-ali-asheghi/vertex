package com.vertex.stellaroauth.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vertex.core.dto.UserDto;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.RestResponse;
import com.vertex.stellaroauth.service.UserClient;
import com.vertex.stellarui.component.*;
import com.vertex.stellarui.constant.Routing;
import com.vertex.stellarui.layout.MainLayout;
import com.vertex.stellarui.util.Notifier;
import com.vertex.stellarui.util.Requester;
import org.springframework.data.domain.PageRequest;

import java.util.*;

@Route(value = Routing.USERS, layout = MainLayout.class)
public class UsersView extends VerticalLayout implements HasDynamicTitle {

    private final UserClient userClient;

    private final List<UserDto.Response> userRecords = new ArrayList<>();
    private final List<UserDto.RoleOption> roleOptions = new ArrayList<>();

    private final Grid<UserDto.Response> grid = new Grid<>();
    private final VerticalLayout cardsContainer = new VerticalLayout();
    private final Div contentWrapper = new Div();

    private ViewModeToggle.Mode currentMode = ViewModeToggle.Mode.TABLE;
    private String currentSearch = "";
    private Long editingUserId = null;

    private EntityModal userModal;
    private TextField usernameField;
    private PasswordField passwordField;
    private Checkbox enabledCheckbox;
    private CheckboxGroup<Long> rolesCheckboxGroup;

    public UsersView(UserClient userClient) {
        this.userClient = userClient;

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
        loadUsers();
    }

    private void setupHeader() {
        ResourceHeader header = new ResourceHeader(
                VaadinIcon.USERS,
                "مدیریت کاربران سامانه",
                "ایجاد حساب کاربری جدید، کنترل وضعیت فعال بودن و تخصیص نقش‌های امنیتی",
                "کاربر جدید",
                this::openCreateModal
        );

        header.setOnSearch(value -> {
            this.currentSearch = value != null ? value.trim() : "";
            loadUsers();
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

        grid.addColumn(UserDto.Response::id)
                .setHeader("شناسه")
                .setWidth("80px")
                .setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(user -> {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setAlignItems(FlexComponent.Alignment.CENTER);
            layout.setSpacing(true);

            Avatar avatar = new Avatar(user.username());
            avatar.setColorIndex((int) (user.id() % 7));

            Span name = new Span(user.username());
            name.addClassNames(LumoUtility.FontWeight.SEMIBOLD);

            layout.add(avatar, name);
            return layout;
        })).setHeader("نام کاربری").setFlexGrow(1);

        grid.addColumn(new ComponentRenderer<>(user -> {
            HorizontalLayout rolesLayout = new HorizontalLayout();
            rolesLayout.setSpacing(true);
            if (user.roles() == null || user.roles().isEmpty()) {
                Span empty = new Span("بدون نقش");
                empty.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);
                rolesLayout.add(empty);
            } else {
                for (String roleName : user.roles()) {
                    Span chip = new Span(roleName);
                    chip.addClassNames(
                            LumoUtility.FontSize.XXSMALL,
                            LumoUtility.FontWeight.MEDIUM,
                            LumoUtility.Padding.Vertical.XSMALL,
                            LumoUtility.Padding.Horizontal.SMALL,
                            LumoUtility.BorderRadius.FULL
                    );
                    chip.getStyle()
                            .set("background-color", "rgba(59, 130, 246, 0.1)")
                            .set("color", "#1d4ed8")
                            .set("border", "1px solid rgba(59, 130, 246, 0.2)");
                    rolesLayout.add(chip);
                }
            }
            return rolesLayout;
        })).setHeader("نقش‌های تخصیص‌یافته").setFlexGrow(2);

        grid.addColumn(new ComponentRenderer<>(user -> new StatusBadge(user.enabled())))
                .setHeader("وضعیت")
                .setWidth("120px")
                .setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(this::createActionsComponent))
                .setHeader("عملیات")
                .setWidth("160px")
                .setFlexGrow(0);
    }

    private Component createActionsComponent(UserDto.Response user) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);

        Button editBtn = new Button(VaadinIcon.EDIT.create());
        editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        editBtn.setTooltipText("ویرایش کاربر");
        editBtn.addClickListener(e -> openEditModal(user));

        Button toggleBtn = new Button(user.enabled() ? VaadinIcon.CLOSE.create() : VaadinIcon.CHECK.create());
        toggleBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        toggleBtn.setTooltipText(user.enabled() ? "غیرفعال‌سازی" : "فعال‌سازی");
        toggleBtn.addClickListener(e -> toggleUserStatus(user));

        Button deleteBtn = new Button(VaadinIcon.TRASH.create());
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        deleteBtn.setTooltipText("حذف کاربر");
        deleteBtn.addClickListener(e -> confirmDelete(user));

        actions.add(editBtn, toggleBtn, deleteBtn);
        return actions;
    }

    private void setupCardsContainer() {
        cardsContainer.setWidthFull();
        cardsContainer.setPadding(false);
        cardsContainer.setSpacing(true);
    }

    private void renderCards() {
        cardsContainer.removeAll();
        if (userRecords.isEmpty()) {
            VerticalLayout empty = new VerticalLayout();
            empty.setAlignItems(FlexComponent.Alignment.CENTER);
            empty.addClassNames("vertex-card", LumoUtility.Padding.XLARGE);
            empty.add(new H3("کاربری یافت نشد"), new Paragraph("برای ایجاد کاربر جدید از دکمه بالا استفاده فرمایید."));
            cardsContainer.add(empty);
            return;
        }

        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);

        int count = 0;
        for (UserDto.Response user : userRecords) {
            VerticalLayout card = new VerticalLayout();
            card.addClassNames("vertex-card");
            card.setWidth("350px");
            card.setSpacing(true);

            HorizontalLayout top = new HorizontalLayout();
            top.setWidthFull();
            top.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            top.setAlignItems(FlexComponent.Alignment.CENTER);

            HorizontalLayout userInfo = new HorizontalLayout();
            userInfo.setAlignItems(FlexComponent.Alignment.CENTER);
            userInfo.setSpacing(true);

            Avatar av = new Avatar(user.username());
            av.setColorIndex((int) (user.id() % 7));

            Span uName = new Span(user.username());
            uName.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.MEDIUM);

            userInfo.add(av, uName);
            top.add(userInfo, new StatusBadge(user.enabled()));

            HorizontalLayout rolesBox = new HorizontalLayout();
            rolesBox.setSpacing(true);
            if (user.roles() != null) {
                for (String r : user.roles()) {
                    Span chip = new Span(r);
                    chip.addClassNames(LumoUtility.FontSize.XXSMALL, LumoUtility.BorderRadius.FULL, LumoUtility.Padding.Horizontal.SMALL);
                    chip.getStyle().set("background", "rgba(59, 130, 246, 0.1)").set("color", "#1d4ed8");
                    rolesBox.add(chip);
                }
            }

            HorizontalLayout bottom = new HorizontalLayout();
            bottom.setWidthFull();
            bottom.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            bottom.setAlignItems(FlexComponent.Alignment.CENTER);

            Span idSpan = new Span("شناسه: " + user.id());
            idSpan.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY);

            Component actions = createActionsComponent(user);
            bottom.add(idSpan, actions);

            card.add(top, rolesBox, bottom);
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
        userModal = new EntityModal("اطلاعات کاربر", this::saveUser);

        usernameField = new TextField("نام کاربری");
        usernameField.setWidthFull();
        usernameField.setRequired(true);

        passwordField = new PasswordField("کلمه عبور");
        passwordField.setWidthFull();
        passwordField.setHelperText("برای عدم تغییر کلمه عبور در زمان ویرایش، این فیلد را خالی بگذارید.");

        enabledCheckbox = new Checkbox("حساب کاربری فعال باشد");
        enabledCheckbox.setValue(true);

        rolesCheckboxGroup = new CheckboxGroup<>();
        rolesCheckboxGroup.setLabel("نقش‌های کاربری");
        rolesCheckboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        rolesCheckboxGroup.setItemLabelGenerator(id -> {
            return roleOptions.stream()
                    .filter(r -> Objects.equals(r.id(), id))
                    .findFirst()
                    .map(r -> r.aliasName() != null ? r.aliasName() + " (" + r.name() + ")" : r.name())
                    .orElse("نقش #" + id);
        });

        userModal.setFormContent(usernameField, passwordField, enabledCheckbox, rolesCheckboxGroup);
    }

    private void openCreateModal() {
        editingUserId = null;
        userModal.setHeaderTitle("ایجاد کاربر جدید");
        usernameField.clear();
        passwordField.clear();
        passwordField.setRequired(true);
        enabledCheckbox.setValue(true);
        rolesCheckboxGroup.deselectAll();
        userModal.open();
    }

    private void openEditModal(UserDto.Response user) {
        editingUserId = user.id();
        userModal.setHeaderTitle("ویرایش کاربر: " + user.username());
        usernameField.setValue(user.username());
        passwordField.clear();
        passwordField.setRequired(false);
        enabledCheckbox.setValue(user.enabled());

        Set<Long> assignedRoleIds = new HashSet<>(user.roleIds() != null ? user.roleIds() : List.of());
        rolesCheckboxGroup.setValue(assignedRoleIds);
        userModal.open();
    }

    private void saveUser() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();
        boolean enabled = enabledCheckbox.getValue();
        List<Long> roleIds = new ArrayList<>(rolesCheckboxGroup.getValue());

        if (username == null || username.trim().isEmpty()) {
            Notifier.error("لطفاً نام کاربری را وارد فرمایید.");
            return;
        }

        if (editingUserId == null && (password == null || password.trim().isEmpty())) {
            Notifier.error("برای ایجاد کاربر جدید، تعیین کلمه عبور الزامی است.");
            return;
        }

        UserDto.AdminRequest request = new UserDto.AdminRequest(
                username.trim(),
                password != null && !password.trim().isEmpty() ? password.trim() : null,
                enabled,
                roleIds
        );

        if (editingUserId == null) {
            RestResponse<UserDto.Response> resp = Requester.execute(() -> userClient.create(request));
            if (resp != null && resp.getData() != null) {
                Notifier.success("کاربر جدید با موفقیت ایجاد شد.");
                userModal.close();
                loadUsers();
            }
        } else {
            RestResponse<UserDto.Response> resp = Requester.execute(() -> userClient.update(editingUserId, request));
            if (resp != null && resp.getData() != null) {
                Notifier.success("اطلاعات کاربر با موفقیت به‌روزرسانی شد.");
                userModal.close();
                loadUsers();
            }
        }
    }

    private void toggleUserStatus(UserDto.Response user) {
        UserDto.AdminRequest request = new UserDto.AdminRequest(
                user.username(),
                null,
                !user.enabled(),
                user.roleIds()
        );
        RestResponse<UserDto.Response> resp = Requester.execute(() -> userClient.update(user.id(), request));
        if (resp != null) {
            Notifier.success("وضعیت کاربر با موفقیت تغییر کرد.");
            loadUsers();
        }
    }

    private void confirmDelete(UserDto.Response user) {
        ConfirmDialog.show(
                "حذف کاربر",
                "آیا از حذف کاربر «" + user.username() + "» اطمینان دارید؟ این عمل غیرقابل بازگشت است.",
                () -> {
                    boolean ok = Requester.executeAndCheck(() -> userClient.delete(user.id()));
                    if (ok) {
                        Notifier.success("کاربر با موفقیت حذف شد.");
                        loadUsers();
                    }
                }
        );
    }

    private void loadRoles() {
        RestResponse<List<UserDto.RoleOption>> response = Requester.execute(userClient::getRoleOptions);
        if (response != null && response.getData() != null) {
            roleOptions.clear();
            roleOptions.addAll(response.getData());
            rolesCheckboxGroup.setItems(roleOptions.stream().map(UserDto.RoleOption::id).toList());
        }
    }

    private void loadUsers() {
        UserDto.Search search = new UserDto.Search(
                null, null, currentSearch, 0, 50, "id", "desc"
        );
        PagedResponse<UserDto.Response> response = Requester.executePaged(() -> userClient.getList(search, PageRequest.of(0, 50)));
        if (response != null && response.getResultList() != null) {
            userRecords.clear();
            userRecords.addAll(response.getResultList());
            grid.setItems(userRecords);
            if (currentMode == ViewModeToggle.Mode.GRID) {
                renderCards();
            }
        }
    }

    @Override
    public String getPageTitle() {
        return "مدیریت کاربران — ورتکس";
    }
}
