package com.vertex.oauthui.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vertex.frontendcore.constant.Routing;
import com.vertex.frontendcore.layout.MainLayout;

@Route(value = Routing.DASHBOARD, layout = MainLayout.class)
public class DashboardView extends VerticalLayout implements HasDynamicTitle {

    public DashboardView() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        addClassNames(LumoUtility.Background.CONTRAST_5);

        H2 welcomeTitle = new H2(getTranslation("DashboardViewTitle"));
        welcomeTitle.addClassNames(LumoUtility.TextColor.PRIMARY);

        Paragraph desc = new Paragraph("🚀");
        desc.addClassNames(LumoUtility.TextColor.SECONDARY);

        add(welcomeTitle, desc);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("DashboardViewTitle");
    }
}
