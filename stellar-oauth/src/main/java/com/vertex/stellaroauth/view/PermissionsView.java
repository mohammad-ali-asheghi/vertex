package com.vertex.stellaroauth.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vertex.core.dto.MenuModel;
import com.vertex.core.dto.PermissionModel;
import com.vertex.core.dto.RoleModel;
import com.vertex.core.dto.view.ViewMenuDto;
import com.vertex.core.dto.view.ViewPermissionDto;
import com.vertex.core.enums.PermissionEnum;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.RestResponse;
import com.vertex.stellaroauth.service.MenuClient;
import com.vertex.stellaroauth.service.PermissionClient;
import com.vertex.stellaroauth.service.RoleClient;
import com.vertex.stellarui.auth.AuthManager;
import com.vertex.stellarui.constant.Routing;
import com.vertex.stellarui.layout.MainLayout;
import com.vertex.stellarui.util.Notifier;
import com.vertex.stellarui.util.Requester;
import org.springframework.data.domain.PageRequest;

import java.util.*;

@Route(value = Routing.PERMISSIONS, layout = MainLayout.class)
public class PermissionsView extends VerticalLayout implements HasDynamicTitle {

    private final RoleClient roleClient;
    private final PermissionClient permissionClient;
    private final MenuClient menuClient;

    private final ComboBox<RoleModel> roleComboBox = new ComboBox<>("انتخاب نقش جهت تنظیم مجوزها");
    private final TextField searchField = new TextField();
    private final Grid<MenuPermissionRow> grid = new Grid<>();

    private final List<RoleModel> rolesList = new ArrayList<>();
    private final List<MenuPermissionRow> allRows = new ArrayList<>();
    private final List<MenuPermissionRow> displayedRows = new ArrayList<>();

    public static class MenuPermissionRow {
        private final Long menuId;
        private final String title;
        private final String searchKey;
        private boolean canRead;
        private boolean canCreate;
        private boolean canUpdate;
        private boolean canDelete;

        public MenuPermissionRow(Long menuId, String title, String searchKey) {
            this.menuId = menuId;
            this.title = title;
            this.searchKey = searchKey != null ? searchKey : "";
        }

        public Long getMenuId() { return menuId; }
        public String getTitle() { return title; }
        public String getSearchKey() { return searchKey; }
        public boolean isCanRead() { return canRead; }
        public void setCanRead(boolean canRead) { this.canRead = canRead; }
        public boolean isCanCreate() { return canCreate; }
        public void setCanCreate(boolean canCreate) { this.canCreate = canCreate; }
        public boolean isCanUpdate() { return canUpdate; }
        public void setCanUpdate(boolean canUpdate) { this.canUpdate = canUpdate; }
        public boolean isCanDelete() { return canDelete; }
        public void setCanDelete(boolean canDelete) { this.canDelete = canDelete; }
    }

    public PermissionsView(RoleClient roleClient, PermissionClient permissionClient, MenuClient menuClient) {
        this.roleClient = roleClient;
        this.permissionClient = permissionClient;
        this.menuClient = menuClient;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        setupHeader();
        setupControlBar();
        setupGrid();

        loadRoles();
    }

    private void setupHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        HorizontalLayout titleArea = new HorizontalLayout();
        titleArea.setAlignItems(FlexComponent.Alignment.CENTER);
        titleArea.setSpacing(true);

        Icon icon = VaadinIcon.SHIELD.create();
        icon.setSize("28px");
        icon.setColor("var(--lumo-primary-color)");

        VerticalLayout textInfo = new VerticalLayout();
        textInfo.setPadding(false);
        textInfo.setSpacing(false);

        H3 title = new H3("ماتریس کنترل دسترسی و مجوزها (RBAC)");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.BOLD, LumoUtility.Margin.NONE);

        Paragraph subtitle = new Paragraph("تنظیم دقیق مجوزهای مجاز (مشاهده، ایجاد، ویرایش، حذف) برای هر نقش و منو");
        subtitle.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.NONE);

        textInfo.add(title, subtitle);
        titleArea.add(icon, textInfo);

        Button saveBtn = new Button("ذخیره تغییرات ماتریس", VaadinIcon.CHECK.create());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.addClickListener(e -> savePermissions());

        header.add(titleArea, saveBtn);
        add(header);
    }

    private void setupControlBar() {
        HorizontalLayout bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.setAlignItems(FlexComponent.Alignment.END);
        bar.setSpacing(true);
        bar.addClassNames("vertex-card", LumoUtility.Padding.MEDIUM);

        roleComboBox.setItemLabelGenerator(r -> r.getAliasName() != null ? r.getAliasName() + " (" + r.getName() + ")" : r.getName());
        roleComboBox.setWidth("350px");
        roleComboBox.setPlaceholder("یک نقش را انتخاب فرمایید...");
        roleComboBox.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                loadRolePermissions(e.getValue());
            }
        });

        searchField.setPlaceholder("جستجو در عناوین منو...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        searchField.setWidth("280px");
        searchField.addValueChangeListener(e -> applyFilter(e.getValue()));

        Button selectAllBtn = new Button("انتخاب همه", e -> setAllPermissions(true));
        selectAllBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);

        Button clearAllBtn = new Button("لغو همه", e -> setAllPermissions(false));
        clearAllBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);

        bar.add(roleComboBox, searchField, selectAllBtn, clearAllBtn);
        add(bar);
    }

    private void setupGrid() {
        grid.setSizeFull();

        grid.addColumn(MenuPermissionRow::getMenuId)
                .setHeader("شناسه")
                .setWidth("80px")
                .setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(row -> {
            HorizontalLayout box = new HorizontalLayout();
            box.setAlignItems(FlexComponent.Alignment.CENTER);
            Icon menuIcon = VaadinIcon.FOLDER_OPEN_O.create();
            menuIcon.setSize("16px");
            menuIcon.setColor("var(--lumo-primary-color)");

            Span t = new Span(row.getTitle());
            t.addClassNames(LumoUtility.FontWeight.SEMIBOLD);

            box.add(menuIcon, t);
            return box;
        })).setHeader("عنوان منو").setFlexGrow(2);

        grid.addColumn(MenuPermissionRow::getSearchKey)
                .setHeader("مسیر / کلید")
                .setFlexGrow(1);

        grid.addColumn(new ComponentRenderer<>(row -> {
            Checkbox cb = new Checkbox("مشاهده (READ)");
            cb.setValue(row.isCanRead());
            cb.addValueChangeListener(e -> row.setCanRead(e.getValue()));
            return cb;
        })).setHeader("مشاهده").setWidth("140px");

        grid.addColumn(new ComponentRenderer<>(row -> {
            Checkbox cb = new Checkbox("ایجاد (CREATE)");
            cb.setValue(row.isCanCreate());
            cb.addValueChangeListener(e -> row.setCanCreate(e.getValue()));
            return cb;
        })).setHeader("ایجاد").setWidth("140px");

        grid.addColumn(new ComponentRenderer<>(row -> {
            Checkbox cb = new Checkbox("ویرایش (UPDATE)");
            cb.setValue(row.isCanUpdate());
            cb.addValueChangeListener(e -> row.setCanUpdate(e.getValue()));
            return cb;
        })).setHeader("ویرایش").setWidth("140px");

        grid.addColumn(new ComponentRenderer<>(row -> {
            Checkbox cb = new Checkbox("حذف (DELETE)");
            cb.setValue(row.isCanDelete());
            cb.addValueChangeListener(e -> row.setCanDelete(e.getValue()));
            return cb;
        })).setHeader("حذف").setWidth("140px");

        add(grid);
    }

    private void loadRoles() {
        RoleModel search = new RoleModel();
        PagedResponse<RoleModel> response = Requester.executePaged(() -> roleClient.getList(search, PageRequest.of(0, 100)));
        if (response != null && response.getResultList() != null) {
            rolesList.clear();
            rolesList.addAll(response.getResultList());
            roleComboBox.setItems(rolesList);
            if (!rolesList.isEmpty()) {
                roleComboBox.setValue(rolesList.get(0));
            }
        }
    }

    private void loadRolePermissions(RoleModel role) {
        // Load all menus first
        PagedResponse<ViewMenuDto> menuResp = Requester.executePaged(
                () -> menuClient.getSidebarList(new MenuModel(), PageRequest.of(0, 200))
        );

        allRows.clear();
        if (menuResp != null && menuResp.getResultList() != null) {
            for (ViewMenuDto m : menuResp.getResultList()) {
                allRows.add(new MenuPermissionRow(m.getId(), m.getTitle(), m.getSearchKey()));
            }
        }

        // If no menus returned from backend, supply core system menus as defaults
        if (allRows.isEmpty()) {
            allRows.add(new MenuPermissionRow(1L, "میز کار مدیریت", ""));
            allRows.add(new MenuPermissionRow(2L, "مدیریت کاربران", "users"));
            allRows.add(new MenuPermissionRow(3L, "مدیریت نقش‌ها", "roles"));
            allRows.add(new MenuPermissionRow(4L, "ماتریس دسترسی‌ها", "permissions"));
            allRows.add(new MenuPermissionRow(5L, "مقادیر و ثابت‌ها", "constants"));
            allRows.add(new MenuPermissionRow(6L, "مستندات سیستم", "documents"));
        }

        // Load permissions assigned to this role
        RestResponse<List<ViewPermissionDto>> permResp = Requester.execute(
                () -> permissionClient.getRelatedList(role.getName())
        );

        if (permResp != null && permResp.getData() != null) {
            Map<Long, List<PermissionEnum>> permMap = new HashMap<>();
            for (ViewPermissionDto dto : permResp.getData()) {
                permMap.put(dto.getMenuId(), dto.getPermissions());
            }

            for (MenuPermissionRow row : allRows) {
                List<PermissionEnum> perms = permMap.get(row.getMenuId());
                if (perms != null) {
                    row.setCanRead(perms.contains(PermissionEnum.READ));
                    row.setCanCreate(perms.contains(PermissionEnum.CREATE));
                    row.setCanUpdate(perms.contains(PermissionEnum.UPDATE));
                    row.setCanDelete(perms.contains(PermissionEnum.DELETE));
                }
            }
        }

        applyFilter(searchField.getValue());
    }

    private void applyFilter(String filter) {
        String q = filter != null ? filter.trim().toLowerCase() : "";
        displayedRows.clear();
        for (MenuPermissionRow row : allRows) {
            if (q.isEmpty() || (row.getTitle() != null && row.getTitle().toLowerCase().contains(q))
                    || (row.getSearchKey() != null && row.getSearchKey().toLowerCase().contains(q))) {
                displayedRows.add(row);
            }
        }
        grid.setItems(displayedRows);
    }

    private void setAllPermissions(boolean enable) {
        for (MenuPermissionRow row : allRows) {
            row.setCanRead(enable);
            row.setCanCreate(enable);
            row.setCanUpdate(enable);
            row.setCanDelete(enable);
        }
        grid.getDataProvider().refreshAll();
    }

    private void savePermissions() {
        RoleModel selectedRole = roleComboBox.getValue();
        if (selectedRole == null) {
            Notifier.error("لطفاً ابتدا نقش مورد نظر را انتخاب نمایید.");
            return;
        }

        String token = AuthManager.getToken();
        if (token == null) {
            Notifier.error("نشست شما معتبر نیست. لطفاً مجدداً وارد شوید.");
            return;
        }

        List<PermissionModel.Detail> details = new ArrayList<>();
        for (MenuPermissionRow row : allRows) {
            List<PermissionEnum> pList = new ArrayList<>();
            if (row.isCanRead()) pList.add(PermissionEnum.READ);
            if (row.isCanCreate()) pList.add(PermissionEnum.CREATE);
            if (row.isCanUpdate()) pList.add(PermissionEnum.UPDATE);
            if (row.isCanDelete()) pList.add(PermissionEnum.DELETE);

            if (!pList.isEmpty()) {
                details.add(new PermissionModel.Detail(row.getMenuId(), pList));
            }
        }

        PermissionModel.Request request = PermissionModel.Request.builder()
                .role(selectedRole.getName())
                .details(details)
                .createdBy(AuthManager.getUsername())
                .device("web-browser")
                .userIp("127.0.0.1")
                .build();

        boolean ok = Requester.executeAndCheck(() -> permissionClient.sync(request, token));
        if (ok) {
            Notifier.success("ماتریس دسترسی نقش «" + selectedRole.getName() + "» با موفقیت ذخیره و همگام‌سازی شد.");
        }
    }

    @Override
    public String getPageTitle() {
        return "ماتریس دسترسی‌ها — ورتکس";
    }
}
