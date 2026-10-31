package com.vertex.stellarui.layout;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vertex.core.dto.view.ViewMenuDto;
import com.vertex.stellarui.auth.AuthManager;
import com.vertex.stellarui.component.ConfirmDialog;
import com.vertex.stellarui.constant.Routing;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
@CssImport("./styles/core.css")
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private SideNav sideNav;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addHeaderContent();
        addDrawerContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");
        toggle.getStyle().set("color", "white");

        Icon logoIcon = VaadinIcon.GRID_BIG.create();
        logoIcon.setSize("24px");
        logoIcon.getStyle().set("color", "white");

        H1 viewTitle = new H1("سامانه مدیریت یکپارچه ورتکس");
        viewTitle.addClassNames(
                LumoUtility.FontSize.MEDIUM,
                LumoUtility.Margin.NONE,
                LumoUtility.FontWeight.BOLD
        );
        viewTitle.getStyle().set("color", "white").set("letter-spacing", "0.5px");

        HorizontalLayout logoArea = new HorizontalLayout(logoIcon, viewTitle);
        logoArea.setAlignItems(FlexComponent.Alignment.CENTER);
        logoArea.setSpacing(true);

        String username = AuthManager.getUsername();
        Avatar avatar = new Avatar(username);
        avatar.setColorIndex(2);

        Span userInfo = new Span(username);
        userInfo.getStyle().set("color", "white").set("font-weight", "600");

        Button logoutBtn = new Button(VaadinIcon.SIGN_OUT.create());
        logoutBtn.setTooltipText("خروج از حساب کاربری");
        logoutBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        logoutBtn.getStyle().set("color", "white").set("opacity", "0.85");
        logoutBtn.addClickListener(e -> {
            ConfirmDialog.show("خروج از سامانه", "آیا می‌خواهید از حساب کاربری خود خارج شوید؟", () -> {
                UI ui = UI.getCurrent();
                AuthManager.redirectToLogin(ui);
            });
        });

        HorizontalLayout userArea = new HorizontalLayout(avatar, userInfo, logoutBtn);
        userArea.setAlignItems(FlexComponent.Alignment.CENTER);
        userArea.setSpacing(true);
        userArea.getStyle()
                .set("background", "rgba(255,255,255,0.18)")
                .set("padding", "4px 12px 4px 6px")
                .set("border-radius", "24px")
                .set("backdrop-filter", "blur(10px)")
                .set("margin-left", "auto");

        Header header = new Header(toggle, logoArea, userArea);
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER
        );

        header.getStyle()
                .set("background", "linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%)")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.12)");

        addToNavbar(true, header);
    }

    private void addDrawerContent() {
        TextField searchField = new TextField();
        searchField.setPlaceholder("جستجو در منوها...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setWidthFull();
        searchField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        searchField.getStyle().set("margin", "var(--lumo-space-s)");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);

        sideNav = new SideNav();
        sideNav.setWidthFull();

        populateNavigation("");

        searchField.addValueChangeListener(e -> populateNavigation(e.getValue()));

        Scroller scroller = new Scroller(sideNav);
        scroller.addClassNames(LumoUtility.Padding.XSMALL);
        scroller.setSizeFull();

        HorizontalLayout drawerFooter = new HorizontalLayout();
        drawerFooter.setWidthFull();
        drawerFooter.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        drawerFooter.addClassNames(LumoUtility.Padding.SMALL, LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
        Span versionSpan = new Span("Vertex Platform • نسخه ۲.۰");
        versionSpan.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY);
        drawerFooter.add(versionSpan);

        VerticalLayout drawerLayout = new VerticalLayout(searchField, scroller, drawerFooter);
        drawerLayout.setSizeFull();
        drawerLayout.setPadding(false);
        drawerLayout.setSpacing(false);
        drawerLayout.getStyle().set("background-color", "var(--lumo-base-color)");

        addToDrawer(drawerLayout);
    }

    private void populateNavigation(String filter) {
        sideNav.removeAll();
        String q = filter != null ? filter.trim().toLowerCase() : "";

        // Standard core admin navigation items
        if (matches("میز کار مدیریت داشبورد", q)) {
            sideNav.addItem(new SideNavItem("میز کار مدیریت", Routing.DASHBOARD, VaadinIcon.DASHBOARD.create()));
        }
        if (matches("مدیریت کاربران حساب‌ها", q)) {
            sideNav.addItem(new SideNavItem("مدیریت کاربران", Routing.USERS, VaadinIcon.USERS.create()));
        }
        if (matches("مدیریت نقش‌ها نقش ها", q)) {
            sideNav.addItem(new SideNavItem("مدیریت نقش‌ها", Routing.ROLES, VaadinIcon.KEY.create()));
        }
        if (matches("ماتریس دسترسی‌ها مجوزها دسترسی ها", q)) {
            sideNav.addItem(new SideNavItem("ماتریس دسترسی‌ها", Routing.PERMISSIONS, VaadinIcon.SHIELD.create()));
        }
        if (matches("مقادیر و ثابت‌ها کدها کاتالوگ", q)) {
            sideNav.addItem(new SideNavItem("مقادیر و ثابت‌ها", Routing.CONSTANTS, VaadinIcon.SLIDERS.create()));
        }
        if (matches("مستندات و معماری سیستم داکیومنت", q)) {
            sideNav.addItem(new SideNavItem("مستندات سیستم", Routing.DOCUMENTS, VaadinIcon.BOOK.create()));
        }

        // Dynamic menus from AuthManager
        List<ViewMenuDto> allMenus = AuthManager.getMenus();
        if (allMenus != null && !allMenus.isEmpty()) {
            List<ViewMenuDto> rootMenus = allMenus.stream()
                    .filter(menu -> menu.getParentId() == null)
                    .toList();

            for (ViewMenuDto root : rootMenus) {
                if (q.isEmpty() || matches(root.getTitle(), q)) {
                    sideNav.addItem(createNavItem(root, allMenus));
                }
            }
        }
    }

    private boolean matches(String text, String query) {
        if (query.isEmpty()) return true;
        if (text == null) return false;
        return text.toLowerCase().contains(query);
    }

    private SideNavItem createNavItem(ViewMenuDto menu, List<ViewMenuDto> allMenus) {
        String title = menu.getTitle() != null ? menu.getTitle() : "منو";
        String route = (menu.getSearchKey() != null && !menu.getSearchKey().isBlank())
                ? menu.getSearchKey() : "";

        SideNavItem item = new SideNavItem(title, route, VaadinIcon.FOLDER_OPEN_O.create());

        List<ViewMenuDto> children = allMenus.stream()
                .filter(m -> Objects.equals(m.getParentId(), menu.getId()))
                .toList();

        for (ViewMenuDto child : children) {
            item.addItem(createNavItem(child, allMenus));
        }

        return item;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String token = AuthManager.getToken();
        if (token == null || token.isBlank()) {
            event.forwardTo(Routing.LOGIN);
        }
    }
}
