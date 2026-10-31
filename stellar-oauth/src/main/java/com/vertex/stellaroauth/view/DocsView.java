package com.vertex.stellaroauth.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vertex.core.dto.DocumentDto;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.RestResponse;
import com.vertex.stellaroauth.service.DocumentClient;
import com.vertex.stellarui.component.StatusBadge;
import com.vertex.stellarui.constant.Routing;
import com.vertex.stellarui.layout.MainLayout;
import com.vertex.stellarui.util.Requester;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Route(value = Routing.DOCUMENTS, layout = MainLayout.class)
public class DocsView extends VerticalLayout implements HasDynamicTitle {

    private final DocumentClient documentClient;

    private final Tabs tabs = new Tabs();
    private final Tab archTab = new Tab(VaadinIcon.CLUSTER.create(), new Span("معماری میکروسرویس"));
    private final Tab securityTab = new Tab(VaadinIcon.SHIELD.create(), new Span("امنیت و توکن‌ها"));
    private final Tab apiTab = new Tab(VaadinIcon.CODE.create(), new Span("کاتالوگ وب‌سرویس‌ها (API)"));
    private final Tab filesTab = new Tab(VaadinIcon.FILE_TEXT_O.create(), new Span("مخزن فایل و اسناد"));

    private final VerticalLayout archContent = new VerticalLayout();
    private final VerticalLayout securityContent = new VerticalLayout();
    private final VerticalLayout apiContent = new VerticalLayout();
    private final VerticalLayout filesContent = new VerticalLayout();

    private final Grid<DocumentDto.Response> filesGrid = new Grid<>();
    private final List<DocumentDto.Response> filesList = new ArrayList<>();

    public DocsView(DocumentClient documentClient) {
        this.documentClient = documentClient;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        setupHeader();
        setupTabs();

        createArchTabContent();
        createSecurityTabContent();
        createApiTabContent();
        createFilesTabContent();

        loadFiles();
    }

    private void setupHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        HorizontalLayout titleArea = new HorizontalLayout();
        titleArea.setAlignItems(FlexComponent.Alignment.CENTER);
        titleArea.setSpacing(true);

        var icon = VaadinIcon.BOOK.create();
        icon.setSize("28px");
        icon.setColor("var(--lumo-primary-color)");

        VerticalLayout textInfo = new VerticalLayout();
        textInfo.setPadding(false);
        textInfo.setSpacing(false);

        H3 title = new H3("مرکز اسناد، معماری و راهنمای فنی ورتکس");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.BOLD, LumoUtility.Margin.NONE);

        Paragraph subtitle = new Paragraph("مستندات جامع طراحی سیستم، توپولوژی میکروسرویس‌ها، پروتکل‌های امنیتی و راهنمای APIها");
        subtitle.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.NONE);

        textInfo.add(title, subtitle);
        titleArea.add(icon, textInfo);

        header.add(titleArea);
        add(header);
    }

    private void setupTabs() {
        tabs.add(archTab, securityTab, apiTab, filesTab);
        tabs.setWidthFull();
        tabs.setSelectedTab(archTab);

        tabs.addSelectedChangeListener(e -> {
            Tab sel = e.getSelectedTab();
            archContent.setVisible(Objects.equals(sel, archTab));
            securityContent.setVisible(Objects.equals(sel, securityTab));
            apiContent.setVisible(Objects.equals(sel, apiTab));
            filesContent.setVisible(Objects.equals(sel, filesTab));
        });

        add(tabs);
    }

    private void createArchTabContent() {
        archContent.setSizeFull();
        archContent.setPadding(false);
        archContent.setSpacing(true);

        HorizontalLayout servicesRow1 = new HorizontalLayout();
        servicesRow1.setWidthFull();
        servicesRow1.setSpacing(true);

        servicesRow1.add(
                createServiceDocCard(
                        "Eureka Service Registry",
                        "Port 7801",
                        "رجیستری کشف سرویس‌ها در شبکه میکروسرویس. کلیه سرویس‌ها به هنگام بالا آمدن خود را در Eureka ثبت می‌نمایند تا مسیریابی پویا و توازن بار (Load Balancing) تضمین گردد.",
                        VaadinIcon.CONNECT,
                        "#2563eb"
                ),
                createServiceDocCard(
                        "Config Server",
                        "Port 8888",
                        "سرور متمرکز کانفیگ‌ها و متغیرهای محیطی با بک‌اند Git. فایل‌های پیکربندی و کلمات عبور به صورت رمزنگاری‌شده با ابزار Jasypt نگهداری و بارگذاری می‌شوند.",
                        VaadinIcon.COG,
                        "#059669"
                ),
                createServiceDocCard(
                        "API Gateway Server",
                        "Port 8080",
                        "درگاه ورودی واحد اکوسیستم پیاده‌سازی شده با Spring Cloud Gateway و فریم‌ورک غیرمسدودکننده WebFlux جهت مسیریابی ترافیک بیرونی به سرویس‌های داخلی.",
                        VaadinIcon.SIGN_IN,
                        "#7c3aed"
                )
        );

        HorizontalLayout servicesRow2 = new HorizontalLayout();
        servicesRow2.setWidthFull();
        servicesRow2.setSpacing(true);

        servicesRow2.add(
                createServiceDocCard(
                        "OAuth & Auth Server",
                        "Port 7901",
                        "میکروسرویس احراز هویت متمرکز، صدور توکن‌های JWT و Refresh Token، اعتبارسنجی کپچا و سرویس‌های مدیریت کاربران، نقش‌ها و ماتریس مجوزها (RBAC).",
                        VaadinIcon.KEY,
                        "#d97706"
                ),
                createServiceDocCard(
                        "Stellar UI (Shared Lib)",
                        "Shared JAR",
                        "کتابخانه اشتراکی رابط کاربری Vaadin 25 با طراحی Lumo/Aura، پشتیبانی کامل از زبان فارسی و راست‌چین (RTL)، تایپوگرافی وزیرمتن و کامپوننت‌های پایه.",
                        VaadinIcon.PAINTBRUSH,
                        "#0284c7"
                ),
                createServiceDocCard(
                        "Stellar OAuth (Admin Panel)",
                        "Vaadin App",
                        "پنل مدیریت یکپارچه مبتنی بر جاوا و وادین جهت مدیریت کاربران، نقش‌ها، دسترسی‌ها و مقادیر ثابت با مصرف وب‌سرویس‌های OAuth-Server از طریق OpenFeign.",
                        VaadinIcon.DESKTOP,
                        "#dc2626"
                )
        );

        archContent.add(servicesRow1, servicesRow2);
        add(archContent);
    }

    private VerticalLayout createServiceDocCard(String title, String badge, String description, VaadinIcon icon, String color) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames("vertex-card");
        card.setWidthFull();
        card.setPadding(true);
        card.setSpacing(true);

        HorizontalLayout top = new HorizontalLayout();
        top.setWidthFull();
        top.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        top.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout left = new HorizontalLayout();
        left.setAlignItems(FlexComponent.Alignment.CENTER);
        Icon i = icon.create();
        i.setColor(color);
        i.setSize("20px");

        H4 t = new H4(title);
        t.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.FontWeight.BOLD, LumoUtility.Margin.NONE);
        left.add(i, t);

        Span b = new Span(badge);
        b.addClassNames(
                LumoUtility.FontSize.XXSMALL,
                LumoUtility.FontWeight.SEMIBOLD,
                LumoUtility.Padding.Vertical.XSMALL,
                LumoUtility.Padding.Horizontal.SMALL,
                LumoUtility.BorderRadius.FULL
        );
        b.getStyle().set("background-color", "rgba(0,0,0,0.06)");

        top.add(left, b);

        Paragraph desc = new Paragraph(description);
        desc.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.NONE);
        desc.getStyle().set("line-height", "1.7");

        card.add(top, desc);
        return card;
    }

    private void createSecurityTabContent() {
        securityContent.setSizeFull();
        securityContent.setPadding(false);
        securityContent.setSpacing(true);
        securityContent.setVisible(false);

        VerticalLayout secCard = new VerticalLayout();
        secCard.addClassNames("vertex-card", LumoUtility.Padding.LARGE);
        secCard.setWidthFull();
        secCard.setSpacing(true);

        H4 secTitle = new H4("فرآیند جامع امنیت، توکن‌ها و چرخه حیات نشست کاربری");
        secTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.BOLD);

        Paragraph p1 = new Paragraph("""
                ۱. ورود امن و اعتبارسنجی کپچا:
                در هنگام ارسال فرم ورود، اطلاعات نام کاربری، کلمه عبور و کد کپچا با استفاده از کلید عمومی RSA رمزنگاری شده و به اندپوینت /v1/token ارسال می‌شود. کپچا تک‌بارمصرف بوده و بلافاصله پس از بررسی از ردیس حذف می‌گردد.
                """);
        p1.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        Paragraph p2 = new Paragraph("""
                ۲. تفکیک توکن دسترسی (Access Token) و رفرش توکن (Refresh Token):
                - توکن دسترسی: با طول عمر کوتاه‌مدت صادر می‌شود و شامل اطلاعات هویتی و فهرست نقش‌های کاربر است.
                - رفرش توکن: با طول عمر بلندمدت (۱۴ روز) صادر شده و قابلیت چرخش (Refresh Token Rotation) دارد؛ یعنی با هر بار تمدید، رفرش توکن قبلی ابطال و یک رفرش توکن جدید صادر می‌شود.
                """);
        p2.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        Paragraph p3 = new Paragraph("""
                ۳. ابطال توکن‌ها و جلوگیری از حملات Replay (Redis Blacklist):
                در زمان خروج (Logout) یا تمدید توکن، توکن‌های قبلی در کش توزیع‌شده Redis به عنوان توکن‌های باطل‌شده علامت‌گذاری می‌شوند تا هرگونه استفاده مجدد از آن‌ها بلافاصله رد شود.
                """);
        p3.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        Paragraph p4 = new Paragraph("""
                ۴. تزریق خودکار توکن در ارتباطات بین‌میکروسرویسی (OpenFeign):
                با استفاده از FeignConfig در لایه فرانت‌اند، هدر Authorization: Bearer <token> به صورت خودکار به تمام درخواست‌های فراخوانی میکروسرویس اضافه می‌گردد.
                """);
        p4.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        secCard.add(secTitle, p1, p2, p3, p4);
        securityContent.add(secCard);
        add(securityContent);
    }

    private void createApiTabContent() {
        apiContent.setSizeFull();
        apiContent.setPadding(false);
        apiContent.setSpacing(true);
        apiContent.setVisible(false);

        VerticalLayout apiCard = new VerticalLayout();
        apiCard.addClassNames("vertex-card", LumoUtility.Padding.LARGE);
        apiCard.setWidthFull();

        H4 apiTitle = new H4("کاتالوگ وب‌سرویس‌های اصلی اکوسیستم Vertex");
        apiTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.BOLD, LumoUtility.Margin.Bottom.MEDIUM);

        VerticalLayout endpoints = new VerticalLayout();
        endpoints.setPadding(false);
        endpoints.setSpacing(true);

        endpoints.add(createEndpointRow("POST", "/v1/token", "احراز هویت و دریافت Access Token و Refresh Token"));
        endpoints.add(createEndpointRow("POST", "/v1/token/refresh", "تمدید توکن منقضی‌شده با ارسال Refresh Token"));
        endpoints.add(createEndpointRow("POST", "/v1/token/logout", "خروج امن کاربر و ابطال توکن‌ها در Redis"));
        endpoints.add(createEndpointRow("GET", "/v1/captcha", "دریافت تصویر کپچا با شناسه اختصاصی"));
        endpoints.add(createEndpointRow("POST", "/api/user/page", "جستجو و دریافت فهرست کاربران با صفحه‌بندی"));
        endpoints.add(createEndpointRow("POST", "/api/user", "ایجاد کاربر جدید و تخصیص نقش‌ها"));
        endpoints.add(createEndpointRow("PUT", "/api/user/{id}", "ویرایش حساب، کلمه عبور و نقش‌های کاربر"));
        endpoints.add(createEndpointRow("DELETE", "/api/user/{id}", "حذف حساب کاربری"));
        endpoints.add(createEndpointRow("POST", "/api/role/list", "دریافت فهرست نقش‌های سیستم"));
        endpoints.add(createEndpointRow("POST", "/api/permission/sync", "همگام‌سازی ماتریس دسترسی به ازای نقش"));
        endpoints.add(createEndpointRow("POST", "/api/code-type/list", "دریافت لیست دسته‌بندی مقادیر ثابت"));
        endpoints.add(createEndpointRow("POST", "/api/code-type-item/list", "دریافت اقلام زیرمجموعه مقدار ثابت"));

        apiCard.add(apiTitle, endpoints);
        apiContent.add(apiCard);
        add(apiContent);
    }

    private HorizontalLayout createEndpointRow(String method, String path, String desc) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setSpacing(true);
        row.addClassNames(LumoUtility.Padding.Vertical.XSMALL, LumoUtility.Border.BOTTOM, LumoUtility.BorderColor.CONTRAST_5);

        Span methodBadge = new Span(method);
        methodBadge.addClassNames(
                LumoUtility.FontSize.XXSMALL,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Padding.Vertical.XSMALL,
                LumoUtility.Padding.Horizontal.SMALL,
                LumoUtility.BorderRadius.SMALL
        );

        if ("GET".equals(method)) {
            methodBadge.getStyle().set("background-color", "#dbeafe").set("color", "#1e40af");
        } else if ("POST".equals(method)) {
            methodBadge.getStyle().set("background-color", "#dcfce7").set("color", "#15803d");
        } else if ("PUT".equals(method)) {
            methodBadge.getStyle().set("background-color", "#fef3c7").set("color", "#b45309");
        } else if ("DELETE".equals(method)) {
            methodBadge.getStyle().set("background-color", "#fee2e2").set("color", "#b91c1c");
        }

        Span pathSpan = new Span(path);
        pathSpan.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.FontWeight.SEMIBOLD);
        pathSpan.getStyle().set("font-family", "monospace");
        pathSpan.setWidth("220px");

        Span descSpan = new Span(desc);
        descSpan.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        row.add(methodBadge, pathSpan, descSpan);
        return row;
    }

    private void createFilesTabContent() {
        filesContent.setSizeFull();
        filesContent.setPadding(false);
        filesContent.setSpacing(true);
        filesContent.setVisible(false);

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        toolbar.addClassNames("vertex-card", LumoUtility.Padding.MEDIUM);

        Span title = new Span("اسناد فنی، دیاگرام‌های معماری و مشخصات سیستم");
        title.addClassNames(LumoUtility.FontWeight.BOLD);

        toolbar.add(title);
        filesContent.add(toolbar);

        filesGrid.setSizeFull();

        filesGrid.addColumn(DocumentDto.Response::id)
                .setHeader("شناسه")
                .setWidth("80px")
                .setFlexGrow(0);

        filesGrid.addColumn(new ComponentRenderer<>(doc -> {
            HorizontalLayout box = new HorizontalLayout();
            box.setAlignItems(FlexComponent.Alignment.CENTER);
            box.setSpacing(true);

            Icon fIcon = VaadinIcon.FILE_TEXT.create();
            fIcon.setColor("var(--lumo-primary-color)");
            fIcon.setSize("18px");

            Span n = new Span(doc.name());
            n.addClassNames(LumoUtility.FontWeight.SEMIBOLD);

            box.add(fIcon, n);
            return box;
        })).setHeader("نام سند / فایل").setFlexGrow(2);

        filesGrid.addColumn(DocumentDto.Response::extension)
                .setHeader("نوع فایل")
                .setWidth("100px")
                .setFlexGrow(0);

        filesGrid.addColumn(new ComponentRenderer<>(doc -> {
            long kb = doc.size() != null ? doc.size() / 1024 : 0;
            return new Span(kb + " KB");
        })).setHeader("حجم").setWidth("120px").setFlexGrow(0);

        filesGrid.addColumn(new ComponentRenderer<>(doc -> {
            Anchor download = new Anchor(doc.downloadUrl() != null ? doc.downloadUrl() : "#", "دانلود فایل");
            download.getElement().setAttribute("download", true);
            download.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.PRIMARY);
            return download;
        })).setHeader("دریافت").setWidth("140px").setFlexGrow(0);

        filesContent.add(filesGrid);
        add(filesContent);
    }

    private void loadFiles() {
        PagedResponse<DocumentDto.Response> resp = Requester.executePaged(
                () -> documentClient.getList(new DocumentDto.Search(null, null), PageRequest.of(0, 50))
        );
        if (resp != null && resp.getResultList() != null) {
            filesList.clear();
            filesList.addAll(resp.getResultList());
            filesGrid.setItems(filesList);
        }
    }

    @Override
    public String getPageTitle() {
        return "مستندات سیستم — ورتکس";
    }
}
