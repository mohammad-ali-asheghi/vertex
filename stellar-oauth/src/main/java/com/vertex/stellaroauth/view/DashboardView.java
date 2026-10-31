package com.vertex.stellaroauth.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vertex.stellarui.auth.AuthManager;
import com.vertex.stellarui.component.StatusBadge;
import com.vertex.stellarui.constant.Routing;
import com.vertex.stellarui.layout.MainLayout;

@Route(value = Routing.DASHBOARD, layout = MainLayout.class)
public class DashboardView extends VerticalLayout implements HasDynamicTitle {

    public DashboardView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        addClassNames(LumoUtility.Padding.LARGE);

        createWelcomeBanner();
        createMetricsRow();
        createModulesSection();
        createArchitectureSummary();
    }

    private void createWelcomeBanner() {
        HorizontalLayout banner = new HorizontalLayout();
        banner.setWidthFull();
        banner.setAlignItems(FlexComponent.Alignment.CENTER);
        banner.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        banner.addClassNames(
                "vertex-card",
                LumoUtility.Padding.LARGE,
                LumoUtility.Margin.Bottom.MEDIUM
        );
        banner.getStyle()
                .set("background", "linear-gradient(135deg, #1e40af 0%, #3b82f6 100%)")
                .set("color", "white")
                .set("border-radius", "16px");

        VerticalLayout textInfo = new VerticalLayout();
        textInfo.setPadding(false);
        textInfo.setSpacing(false);

        H2 greeting = new H2("میز کار مدیریت و کنترل دسترسی ورتکس");
        greeting.addClassNames(LumoUtility.FontSize.XXLARGE, LumoUtility.FontWeight.BOLD, LumoUtility.Margin.NONE);
        greeting.getStyle().set("color", "white");

        Paragraph desc = new Paragraph("سامانه متمرکز مدیریت هویت، نقش‌ها، مجوزهای امنیتی و تنظیمات پایه بر بستر میکروسرویس");
        desc.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Margin.Top.XSMALL, LumoUtility.Margin.Bottom.NONE);
        desc.getStyle().set("color", "rgba(255,255,255,0.9)");

        textInfo.add(greeting, desc);

        HorizontalLayout badgeGroup = new HorizontalLayout();
        badgeGroup.setAlignItems(FlexComponent.Alignment.CENTER);

        Span userBadge = new Span("کاربر: " + AuthManager.getUsername());
        userBadge.getStyle()
                .set("background", "rgba(255,255,255,0.2)")
                .set("padding", "6px 14px")
                .set("border-radius", "20px")
                .set("font-weight", "500")
                .set("backdrop-filter", "blur(8px)");

        badgeGroup.add(userBadge);
        banner.add(textInfo, badgeGroup);
        add(banner);
    }

    private void createMetricsRow() {
        HorizontalLayout metricsRow = new HorizontalLayout();
        metricsRow.setWidthFull();
        metricsRow.setSpacing(true);
        metricsRow.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

        metricsRow.add(
                createStatCard(VaadinIcon.USERS, "کاربران سیستم", "فعال و ثبت‌شده", "#2563eb", Routing.USERS),
                createStatCard(VaadinIcon.KEY, "نقش‌های امنیتی", "الگوهای دسترسی", "#7c3aed", Routing.ROLES),
                createStatCard(VaadinIcon.SHIELD, "ماتریس دسترسی", "کنترل سطح دسترسی", "#059669", Routing.PERMISSIONS),
                createStatCard(VaadinIcon.SLIDERS, "مقادیر ثابت", "کدینگ و تنظیمات", "#d97706", Routing.CONSTANTS)
        );

        add(metricsRow);
    }

    private VerticalLayout createStatCard(VaadinIcon icon, String title, String subtitle, String accentColor, String targetRoute) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames("stat-card");
        card.setWidthFull();
        card.setPadding(true);
        card.getStyle()
                .set("cursor", "pointer")
                .set("border-top", "4px solid " + accentColor);

        HorizontalLayout top = new HorizontalLayout();
        top.setWidthFull();
        top.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        top.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon cardIcon = icon.create();
        cardIcon.setSize("24px");
        cardIcon.setColor(accentColor);

        Span badge = new Span("مدیریت");
        badge.addClassNames(LumoUtility.FontSize.XXSMALL, LumoUtility.TextColor.SECONDARY);

        top.add(cardIcon, badge);

        H3 val = new H3(title);
        val.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.BOLD, LumoUtility.Margin.Vertical.XSMALL);

        Paragraph sub = new Paragraph(subtitle);
        sub.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.NONE);

        card.add(top, val, sub);

        card.addClickListener(e -> UI.getCurrent().navigate(targetRoute));
        return card;
    }

    private void createModulesSection() {
        VerticalLayout section = new VerticalLayout();
        section.setWidthFull();
        section.setPadding(false);
        section.setSpacing(true);
        section.addClassNames(LumoUtility.Margin.Bottom.LARGE);

        H3 title = new H3("بخش‌های عملیاتی پنل مدیریت");
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.BOLD, LumoUtility.Margin.Bottom.SMALL);
        section.add(title);

        HorizontalLayout cards = new HorizontalLayout();
        cards.setWidthFull();
        cards.setSpacing(true);

        cards.add(
                createModuleCard(
                        VaadinIcon.USERS,
                        "مدیریت کاربران",
                        "ثبت کاربر جدید، تغییر وضعیت فعال/غیرفعال، ویرایش کلمه عبور و تخصیص نقش‌ها به کاربران سامانه.",
                        Routing.USERS
                ),
                createModuleCard(
                        VaadinIcon.KEY,
                        "مدیریت نقش‌ها",
                        "تعریف نقش‌های کاربری، تعیین عناوین فارسی و انگلیسی، و فعال‌سازی یا غیرفعال‌سازی نقش‌ها.",
                        Routing.ROLES
                ),
                createModuleCard(
                        VaadinIcon.SHIELD,
                        "ماتریس دسترسی‌ها",
                        "تنظیم دقیق سطوح دسترسی (مشاهده، ایجاد، ویرایش، حذف) به ازای هر نقش و منوی سامانه به صورت ماتریسی.",
                        Routing.PERMISSIONS
                )
        );

        HorizontalLayout cardsRow2 = new HorizontalLayout();
        cardsRow2.setWidthFull();
        cardsRow2.setSpacing(true);

        cardsRow2.add(
                createModuleCard(
                        VaadinIcon.SLIDERS,
                        "مقادیر ثابت و کدینگ",
                        "مدیریت دسته‌بندی‌ها و آیتم‌های ثابت سامانه (Code Types & Items) با قابلیت تعیین اولویت و فعال/غیرفعال.",
                        Routing.CONSTANTS
                ),
                createModuleCard(
                        VaadinIcon.BOOK,
                        "مستندات و راهنمای سیستم",
                        "مشاهده مستندات معماری میکروسرویس، راهنمای APIها، فرآیندهای امنیتی توکن و مخزن اسناد.",
                        Routing.DOCUMENTS
                )
        );

        section.add(cards, cardsRow2);
        add(section);
    }

    private VerticalLayout createModuleCard(VaadinIcon icon, String title, String desc, String route) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames("vertex-card");
        card.setWidthFull();
        card.setPadding(true);
        card.setSpacing(true);

        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);

        Icon i = icon.create();
        i.setSize("22px");
        i.setColor("var(--lumo-primary-color)");

        H3 t = new H3(title);
        t.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.FontWeight.BOLD, LumoUtility.Margin.NONE);

        header.add(i, t);

        Paragraph p = new Paragraph(desc);
        p.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Vertical.XSMALL);
        p.getStyle().set("line-height", "1.6");

        Button action = new Button("ورود به بخش", VaadinIcon.ARROW_LEFT.create());
        action.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        action.addClickListener(e -> UI.getCurrent().navigate(route));

        card.add(header, p, action);
        return card;
    }

    private void createArchitectureSummary() {
        HorizontalLayout archCard = new HorizontalLayout();
        archCard.setWidthFull();
        archCard.setAlignItems(FlexComponent.Alignment.CENTER);
        archCard.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        archCard.addClassNames("vertex-card", LumoUtility.Padding.MEDIUM);
        archCard.getStyle().set("background-color", "var(--lumo-contrast-5pct)");

        HorizontalLayout left = new HorizontalLayout();
        left.setAlignItems(FlexComponent.Alignment.CENTER);
        left.setSpacing(true);

        Icon archIcon = VaadinIcon.CLUSTER.create();
        archIcon.setSize("24px");
        archIcon.setColor("var(--lumo-primary-color)");

        VerticalLayout info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);

        Span archTitle = new Span("وضعیت اکوسیستم میکروسرویس");
        archTitle.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.SMALL);

        Span archSub = new Span("Eureka: 7801 | Config: 8888 | Gateway: 8080 | OAuth: 7901 | Redis: 6379");
        archSub.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY);

        info.add(archTitle, archSub);
        left.add(archIcon, info);

        HorizontalLayout statusPills = new HorizontalLayout();
        statusPills.setAlignItems(FlexComponent.Alignment.CENTER);
        statusPills.add(new StatusBadge(true, "سرویس‌ها فعال و آماده به‌کار"));

        Button viewDocsBtn = new Button("مشاهده توپولوژی کامل", VaadinIcon.EXTERNAL_LINK.create());
        viewDocsBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        viewDocsBtn.addClickListener(e -> UI.getCurrent().navigate(Routing.DOCUMENTS));

        archCard.add(left, statusPills, viewDocsBtn);
        add(archCard);
    }

    @Override
    public String getPageTitle() {
        return "میز کار مدیریت — ورتکس";
    }
}
