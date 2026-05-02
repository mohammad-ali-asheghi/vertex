package com.vertex.stellarui.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vertex.core.dto.view.ViewMenuDto;
import com.vertex.stellarui.auth.AuthManager;
import com.vertex.stellarui.constant.Routing;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
@CssImport("./styles/core.css")
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addHeaderContent();
        addDrawerContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");
        toggle.getStyle().set("color", "white");

        Icon logoIcon = VaadinIcon.SHIELD.create();
        logoIcon.setSize("24px");
        logoIcon.getStyle().set("color", "white");

        H1 viewTitle = new H1(getTranslation("OrganizationCenterPortal"));
        viewTitle.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.NONE,
                LumoUtility.FontWeight.BOLD
        );
        viewTitle.getStyle().set("color", "white").set("letter-spacing", "0.5px");

        HorizontalLayout logoArea = new HorizontalLayout(logoIcon, viewTitle);
        logoArea.setAlignItems(FlexComponent.Alignment.CENTER);
        logoArea.setSpacing(true);

        Avatar avatar = new Avatar("dev");
        avatar.setColorIndex(2);

        Span userInfo = new Span("developer");
        userInfo.getStyle().set("color", "white").set("font-weight", "500");

        HorizontalLayout userArea = new HorizontalLayout(avatar, userInfo);
        userArea.setAlignItems(FlexComponent.Alignment.CENTER);
        userArea.getStyle()
                .set("background", "rgba(255,255,255,0.2)")
                .set("padding", "4px 16px 4px 4px")
                .set("border-radius", "24px")
                .set("backdrop-filter", "blur(10px)")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease")
                .set("margin-left", "auto");

        userArea.getElement().addEventListener("mouseenter",
                e -> userArea.getStyle().set("background", "rgba(255,255,255,0.3)"));
        userArea.getElement().addEventListener("mouseleave",
                e -> userArea.getStyle().set("background", "rgba(255,255,255,0.2)"));

        Header header = new Header(toggle, logoArea, userArea);
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER
        );

        header.getStyle()
                .set("background", "linear-gradient(135deg, var(--lumo-primary-color) 0%, var(--lumo-primary-color-50pct) 100%)")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)");

        addToNavbar(true, header);
    }

    private void addDrawerContent() {
        TextField searchField = new TextField();
        searchField.setPlaceholder(getTranslation("ServicesSearch"));
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setWidthFull();
        searchField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        searchField.getStyle().set("margin", "var(--lumo-space-m)");

        SideNav sideNav = new SideNav();
        sideNav.setWidthFull();

        List<ViewMenuDto> allMenus = AuthManager.getMenus();

        List<ViewMenuDto> rootMenus = allMenus.stream()
                .filter(menu -> menu.getParentId() == null)
                .toList();

        for (ViewMenuDto root : rootMenus) {
            sideNav.addItem(createNavItem(root, allMenus));
        }

        Scroller scroller = new Scroller(sideNav);
        scroller.addClassNames(LumoUtility.Padding.SMALL);

        VerticalLayout drawerLayout = new VerticalLayout(searchField, scroller);
        drawerLayout.setSizeFull();
        drawerLayout.setPadding(false);
        drawerLayout.setSpacing(false);
        drawerLayout.getStyle().set("background-color", "var(--lumo-base-color)");

        addToDrawer(drawerLayout);
    }

    private SideNavItem createNavItem(ViewMenuDto menu, List<ViewMenuDto> allMenus) {
        String title = getTranslation(menu.getTitle());
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
