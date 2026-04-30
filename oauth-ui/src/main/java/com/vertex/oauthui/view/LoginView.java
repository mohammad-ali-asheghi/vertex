package com.vertex.oauthui.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vertex.core.dto.MenuModel;
import com.vertex.core.dto.response.CaptchaResponse;
import com.vertex.core.dto.view.ViewMenuDto;
import com.vertex.core.util.PagedResponse;
import com.vertex.frontendcore.auth.AuthManager;
import com.vertex.frontendcore.constant.Constant;
import com.vertex.frontendcore.constant.Routing;
import com.vertex.frontendcore.util.Notifier;
import com.vertex.frontendcore.util.Requester;
import com.vertex.oauthui.security.Encryptor;
import com.vertex.oauthui.service.MenuClient;
import com.vertex.oauthui.service.TokenClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.CompletableFuture;

@Route(Routing.LOGIN)
@CssImport("./styles/login-view.css")
public class LoginView extends VerticalLayout implements HasDynamicTitle {

    private final TokenClient tokenClient;
    private final MenuClient menuClient;
    private final Encryptor encryptor;

    private TextField usernameField;
    private PasswordField passwordField;
    private TextField captchaField;
    private Image captchaImage;
    private Button loginButton;
    private Button refreshCaptchaBtn;

    private String currentCaptchaId;

    public LoginView(TokenClient tokenClient, MenuClient menuClient, Encryptor encryptor /*, CaptchaClient captchaClient*/) {
        this.tokenClient = tokenClient;
        this.menuClient = menuClient;
        this.encryptor = encryptor;

        setupLayout();
        createCustomForm();

        refreshCaptcha();
    }

    private void setupLayout() {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        getStyle().set("background", "linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)");
    }

    private void createCustomForm() {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(
                "login-card",
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.XLARGE,
                LumoUtility.Padding.LARGE
        );
        card.setWidth("420px");
        card.setAlignItems(Alignment.STRETCH);

        H1 title = new H1(getTranslation("WelcomeApp"));
        title.addClassNames(LumoUtility.TextAlignment.CENTER, LumoUtility.Margin.Top.NONE, LumoUtility.FontSize.XXLARGE);

        Paragraph subtitle = new Paragraph(getTranslation("EnterYourInformation"));
        subtitle.addClassNames(LumoUtility.TextAlignment.CENTER, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Bottom.LARGE);

        usernameField = new TextField(getTranslation("Username"));
        usernameField.setPrefixComponent(new Icon(VaadinIcon.USER));
        usernameField.addClassName("ltr-inputs");

        passwordField = new PasswordField(getTranslation("Password"));
        passwordField.setPrefixComponent(new Icon(VaadinIcon.LOCK));
        passwordField.addClassName("ltr-inputs");

        captchaField = new TextField(getTranslation("CaptchaCode"));
        captchaField.addClassName("ltr-inputs");
        captchaField.setPlaceholder("مثلاً X7Y2Z");

        captchaImage = new Image();
        captchaImage.setHeight("40px");
        captchaImage.setWidth("120px");
        captchaImage.getStyle().set("border-radius", "4px").set("border", "1px solid var(--lumo-contrast-20pct)");

        refreshCaptchaBtn = new Button(new Icon(VaadinIcon.REFRESH));
        refreshCaptchaBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        refreshCaptchaBtn.addClickListener(e -> refreshCaptcha());

        HorizontalLayout captchaLayout = new HorizontalLayout(captchaField, captchaImage, refreshCaptchaBtn);
        captchaLayout.setAlignItems(Alignment.END);
        captchaLayout.setFlexGrow(1, captchaField);

        loginButton = new Button(getTranslation("LoginButton"));
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        loginButton.addClassNames(LumoUtility.Margin.Top.LARGE);

        loginButton.addClickShortcut(Key.ENTER);
        loginButton.addClickListener(e -> attemptLogin());

        card.add(title, subtitle, usernameField, passwordField, captchaLayout, loginButton);
        add(card);
    }

    private void refreshCaptcha() {
        try {
            CaptchaResponse captchaData = Requester.executeAndUnwrap(tokenClient::getCaptcha);
            if (captchaData != null) {
                currentCaptchaId = captchaData.getCaptchaId();
                captchaImage.setSrc(captchaData.getImage());
                captchaField.clear();
            }
        } catch (Exception e) {
            Notifier.error(getTranslation("CaptchaError"));
        }
    }

    private void attemptLogin() {
        if (usernameField.isEmpty() || passwordField.isEmpty() || captchaField.isEmpty()) {
            Notifier.error(getTranslation("RequiredFieldEmpty"));
            return;
        }

        UI ui = UI.getCurrent();
        VaadinSession session = VaadinSession.getCurrent();

        setFormEnabled(false);

        Notification loading = Notifier.loading(getTranslation("InProgressYourRequest"));

        CompletableFuture.supplyAsync(() -> {
            String encryptedData = encryptor.encryptData(
                    usernameField.getValue(),
                    passwordField.getValue(),
                    currentCaptchaId,
                    captchaField.getValue()
            );
            return Requester.executeAndUnwrap(() -> tokenClient.getToken(encryptedData));
        }).whenComplete((response, throwable) -> ui.access(() -> {
            try {
                loading.close();

                if (throwable != null) {
                    Throwable root = throwable.getCause() != null ? throwable.getCause() : throwable;
                    String backendErrorMessage = root.getMessage();
                    if (backendErrorMessage == null || backendErrorMessage.trim().isEmpty()) {
                        backendErrorMessage = getTranslation("InternalServerError");
                    }
                    Notifier.error(backendErrorMessage);
                    refreshCaptcha();
                    passwordField.clear();
                    return;
                }
                AuthManager.saveToken(session, response.token());
                createMenuSession(session);
                Notifier.success(getTranslation("LoginViewSuccess"));
                ui.navigate(Routing.DASHBOARD);
            } finally {
                setFormEnabled(true);
            }
        }));
    }

    private void setFormEnabled(boolean enabled) {
        usernameField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        captchaField.setEnabled(enabled);
        loginButton.setEnabled(enabled);
        refreshCaptchaBtn.setEnabled(enabled);
    }

    private void createMenuSession(VaadinSession session) {
        Pageable pageable = PageRequest.of(Constant.DEFAULT_PAGE_INDEX, Constant.DEFAULT_PAGE_SIZE);
        PagedResponse<ViewMenuDto> response = Requester.executePaged(
                () -> menuClient.getSidebarList(new MenuModel(), pageable)
        );
        if (response != null && response.getResultList() != null) {
            AuthManager.saveMenus(session, response.getResultList());
        }
    }

    @Override
    public String getPageTitle() {
        return getTranslation("LoginViewTitle");
    }
}
