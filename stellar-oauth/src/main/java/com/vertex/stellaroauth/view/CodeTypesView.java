package com.vertex.stellaroauth.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vertex.core.dto.CodeTypeItemModel;
import com.vertex.core.dto.CodeTypeModel;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.RestResponse;
import com.vertex.stellaroauth.service.CodeTypeClient;
import com.vertex.stellaroauth.service.CodeTypeItemClient;
import com.vertex.stellarui.component.EntityModal;
import com.vertex.stellarui.component.StatusBadge;
import com.vertex.stellarui.constant.Routing;
import com.vertex.stellarui.layout.MainLayout;
import com.vertex.stellarui.util.Notifier;
import com.vertex.stellarui.util.Requester;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Route(value = Routing.CONSTANTS, layout = MainLayout.class)
public class CodeTypesView extends VerticalLayout implements HasDynamicTitle {

    private final CodeTypeClient codeTypeClient;
    private final CodeTypeItemClient codeTypeItemClient;

    private final Tabs tabs = new Tabs();
    private final Tab itemsTab = new Tab(VaadinIcon.LIST.create(), new Span("اقلام مقادیر ثابت (Items)"));
    private final Tab typesTab = new Tab(VaadinIcon.FOLDER_O.create(), new Span("گروه‌های مقادیر ثابت (Types)"));

    private final VerticalLayout itemsContent = new VerticalLayout();
    private final VerticalLayout typesContent = new VerticalLayout();

    // Items view state
    private final ComboBox<CodeTypeModel> codeTypeFilter = new ComboBox<>("گروه مقادیر ثابت");
    private final TextField itemSearch = new TextField();
    private final Grid<CodeTypeItemModel> itemGrid = new Grid<>();
    private final List<CodeTypeItemModel> itemList = new ArrayList<>();
    private EntityModal itemModal;
    private TextField itemFarsiTitleField;
    private TextField itemEnglishTitleField;
    private IntegerField itemPriorityField;
    private TextArea itemDescField;
    private Checkbox itemDisabledCheckbox;
    private String editingItemId = null;

    // Types view state
    private final Grid<CodeTypeModel> typeGrid = new Grid<>();
    private final List<CodeTypeModel> typeList = new ArrayList<>();
    private EntityModal typeModal;
    private TextField typeIdField;
    private TextField typeTitleField;
    private TextField typeCategoryField;
    private Checkbox typeIsEnumCheckbox;
    private String editingTypeId = null;

    public CodeTypesView(CodeTypeClient codeTypeClient, CodeTypeItemClient codeTypeItemClient) {
        this.codeTypeClient = codeTypeClient;
        this.codeTypeItemClient = codeTypeItemClient;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        setupHeader();
        setupTabs();
        setupItemsView();
        setupTypesView();

        setupModals();

        loadTypes();
    }

    private void setupHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        HorizontalLayout titleArea = new HorizontalLayout();
        titleArea.setAlignItems(FlexComponent.Alignment.CENTER);
        titleArea.setSpacing(true);

        var icon = VaadinIcon.SLIDERS.create();
        icon.setSize("28px");
        icon.setColor("var(--lumo-primary-color)");

        VerticalLayout textInfo = new VerticalLayout();
        textInfo.setPadding(false);
        textInfo.setSpacing(false);

        H3 title = new H3("مدیریت مقادیر ثابت و کدینگ سامانه");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.BOLD, LumoUtility.Margin.NONE);

        Paragraph subtitle = new Paragraph("پیکربندی انواع کدها، دسته‌بندی‌ها و اقلام شمارشی مورد استفاده در اکوسیستم");
        subtitle.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.NONE);

        textInfo.add(title, subtitle);
        titleArea.add(icon, textInfo);

        header.add(titleArea);
        add(header);
    }

    private void setupTabs() {
        tabs.add(itemsTab, typesTab);
        tabs.setWidthFull();
        tabs.setSelectedTab(itemsTab);

        tabs.addSelectedChangeListener(e -> {
            boolean isItems = Objects.equals(e.getSelectedTab(), itemsTab);
            itemsContent.setVisible(isItems);
            typesContent.setVisible(!isItems);
            if (!isItems) {
                typeGrid.setItems(typeList);
            }
        });

        add(tabs);
    }

    private void setupItemsView() {
        itemsContent.setSizeFull();
        itemsContent.setPadding(false);
        itemsContent.setSpacing(true);

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.END);
        toolbar.setSpacing(true);
        toolbar.addClassNames("vertex-card", LumoUtility.Padding.MEDIUM);

        codeTypeFilter.setWidth("320px");
        codeTypeFilter.setItemLabelGenerator(ct -> ct.getCategory() != null ? ct.getTitle() + " (" + ct.getCategory() + ")" : ct.getTitle());
        codeTypeFilter.setPlaceholder("انتخاب نوع کد...");
        codeTypeFilter.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                loadItems(e.getValue().getId());
            }
        });

        itemSearch.setPlaceholder("جستجو در عناوین اقلام...");
        itemSearch.setPrefixComponent(VaadinIcon.SEARCH.create());
        itemSearch.setClearButtonVisible(true);
        itemSearch.setValueChangeMode(ValueChangeMode.LAZY);
        itemSearch.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        itemSearch.setWidth("280px");
        itemSearch.addValueChangeListener(e -> applyItemFilter(e.getValue()));

        Button addItemBtn = new Button("قلم جدید", VaadinIcon.PLUS.create());
        addItemBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addItemBtn.addClickListener(e -> openCreateItemModal());

        toolbar.add(codeTypeFilter, itemSearch, addItemBtn);
        itemsContent.add(toolbar);

        itemGrid.setSizeFull();

        itemGrid.addColumn(CodeTypeItemModel::getId)
                .setHeader("شناسه")
                .setWidth("120px")
                .setFlexGrow(0);

        itemGrid.addColumn(new ComponentRenderer<>(item -> {
            Span title = new Span(item.getFarsiTitle());
            title.addClassNames(LumoUtility.FontWeight.SEMIBOLD);
            return title;
        })).setHeader("عنوان فارسی").setFlexGrow(2);

        itemGrid.addColumn(new ComponentRenderer<>(item -> {
            Span en = new Span(item.getEnglishTitle() != null ? item.getEnglishTitle() : "-");
            en.getStyle().set("font-family", "monospace");
            return en;
        })).setHeader("عنوان انگلیسی").setFlexGrow(1);

        itemGrid.addColumn(CodeTypeItemModel::getPriority)
                .setHeader("اولویت")
                .setWidth("90px")
                .setFlexGrow(0);

        itemGrid.addColumn(new ComponentRenderer<>(item -> {
            boolean active = item.getIsDisable() == null || !item.getIsDisable();
            return new StatusBadge(active, active ? "فعال" : "غیرفعال");
        })).setHeader("وضعیت").setWidth("120px").setFlexGrow(0);

        itemGrid.addColumn(CodeTypeItemModel::getDescription)
                .setHeader("توضیحات")
                .setFlexGrow(2);

        itemGrid.addColumn(new ComponentRenderer<>(item -> {
            HorizontalLayout actions = new HorizontalLayout();
            Button editBtn = new Button(VaadinIcon.EDIT.create(), e -> openEditItemModal(item));
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            actions.add(editBtn);
            return actions;
        })).setHeader("عملیات").setWidth("100px").setFlexGrow(0);

        itemsContent.add(itemGrid);
        add(itemsContent);
    }

    private void setupTypesView() {
        typesContent.setSizeFull();
        typesContent.setPadding(false);
        typesContent.setSpacing(true);
        typesContent.setVisible(false);

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        toolbar.addClassNames("vertex-card", LumoUtility.Padding.MEDIUM);

        Span info = new Span("لیست دسته‌بندی‌ها و گروه‌های مقادیر ثابت سامانه");
        info.addClassNames(LumoUtility.FontWeight.SEMIBOLD);

        Button addTypeBtn = new Button("گروه کد جدید", VaadinIcon.PLUS.create());
        addTypeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addTypeBtn.addClickListener(e -> openCreateTypeModal());

        toolbar.add(info, addTypeBtn);
        typesContent.add(toolbar);

        typeGrid.setSizeFull();

        typeGrid.addColumn(CodeTypeModel::getId)
                .setHeader("شناسه سیستمی")
                .setWidth("180px")
                .setFlexGrow(0);

        typeGrid.addColumn(new ComponentRenderer<>(ct -> {
            Span t = new Span(ct.getTitle());
            t.addClassNames(LumoUtility.FontWeight.BOLD);
            return t;
        })).setHeader("عنوان").setFlexGrow(2);

        typeGrid.addColumn(CodeTypeModel::getCategory)
                .setHeader("دسته‌بندی (Category)")
                .setFlexGrow(1);

        typeGrid.addColumn(new ComponentRenderer<>(ct -> {
            boolean isEnum = ct.getIsEnum() != null && ct.getIsEnum();
            return new StatusBadge(isEnum, isEnum ? "شمارشی (Enum)" : "پویا");
        })).setHeader("نوع داده").setWidth("140px");

        typeGrid.addColumn(new ComponentRenderer<>(ct -> {
            HorizontalLayout actions = new HorizontalLayout();
            Button editBtn = new Button(VaadinIcon.EDIT.create(), e -> openEditTypeModal(ct));
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            actions.add(editBtn);
            return actions;
        })).setHeader("عملیات").setWidth("100px");

        typesContent.add(typeGrid);
        add(typesContent);
    }

    private void setupModals() {
        // Item Modal
        itemModal = new EntityModal("قلم مقدار ثابت", this::saveItem);
        itemFarsiTitleField = new TextField("عنوان فارسی");
        itemFarsiTitleField.setWidthFull();
        itemFarsiTitleField.setRequired(true);

        itemEnglishTitleField = new TextField("عنوان انگلیسی (کد)");
        itemEnglishTitleField.setWidthFull();

        itemPriorityField = new IntegerField("اولویت نمایش");
        itemPriorityField.setWidthFull();
        itemPriorityField.setValue(1);

        itemDescField = new TextArea("توضیحات");
        itemDescField.setWidthFull();

        itemDisabledCheckbox = new Checkbox("غیرفعال باشد");

        itemModal.setFormContent(itemFarsiTitleField, itemEnglishTitleField, itemPriorityField, itemDescField, itemDisabledCheckbox);

        // Type Modal
        typeModal = new EntityModal("گروه مقادیر ثابت", this::saveType);
        typeIdField = new TextField("شناسه سیستمی (انگلیسی)");
        typeIdField.setWidthFull();
        typeIdField.setRequired(true);

        typeTitleField = new TextField("عنوان گروه");
        typeTitleField.setWidthFull();
        typeTitleField.setRequired(true);

        typeCategoryField = new TextField("دسته‌بندی (Category)");
        typeCategoryField.setWidthFull();

        typeIsEnumCheckbox = new Checkbox("نوع شمارشی (Enum)");

        typeModal.setFormContent(typeIdField, typeTitleField, typeCategoryField, typeIsEnumCheckbox);
    }

    private void openCreateItemModal() {
        if (codeTypeFilter.getValue() == null) {
            Notifier.error("لطفاً ابتدا یک گروه مقادیر ثابت انتخاب نمایید.");
            return;
        }
        editingItemId = null;
        itemModal.setHeaderTitle("ایجاد قلم مقدار ثابت جدید");
        itemFarsiTitleField.clear();
        itemEnglishTitleField.clear();
        itemPriorityField.setValue(1);
        itemDescField.clear();
        itemDisabledCheckbox.setValue(false);
        itemModal.open();
    }

    private void openEditItemModal(CodeTypeItemModel item) {
        editingItemId = item.getId();
        itemModal.setHeaderTitle("ویرایش قلم: " + item.getFarsiTitle());
        itemFarsiTitleField.setValue(item.getFarsiTitle() != null ? item.getFarsiTitle() : "");
        itemEnglishTitleField.setValue(item.getEnglishTitle() != null ? item.getEnglishTitle() : "");
        itemPriorityField.setValue(item.getPriority() != null ? item.getPriority() : 1);
        itemDescField.setValue(item.getDescription() != null ? item.getDescription() : "");
        itemDisabledCheckbox.setValue(item.getIsDisable() != null && item.getIsDisable());
        itemModal.open();
    }

    private void saveItem() {
        if (itemFarsiTitleField.isEmpty()) {
            Notifier.error("عنوان فارسی الزامی است.");
            return;
        }

        CodeTypeModel parentType = codeTypeFilter.getValue();
        if (parentType == null) return;

        CodeTypeItemModel model = new CodeTypeItemModel();
        model.setCodeTypeId(parentType.getId());
        model.setFarsiTitle(itemFarsiTitleField.getValue());
        model.setEnglishTitle(itemEnglishTitleField.getValue());
        model.setPriority(itemPriorityField.getValue());
        model.setDescription(itemDescField.getValue());
        model.setIsDisable(itemDisabledCheckbox.getValue());

        if (editingItemId == null) {
            boolean ok = Requester.executeAndCheck(() -> codeTypeItemClient.create(model));
            if (ok) {
                Notifier.success("قلم مقدار ثابت با موفقیت ذخیره شد.");
                itemModal.close();
                loadItems(parentType.getId());
            }
        } else {
            model.setId(editingItemId);
            boolean ok = Requester.executeAndCheck(() -> codeTypeItemClient.update(model));
            if (ok) {
                Notifier.success("قلم مقدار ثابت با موفقیت به‌روزرسانی شد.");
                itemModal.close();
                loadItems(parentType.getId());
            }
        }
    }

    private void openCreateTypeModal() {
        editingTypeId = null;
        typeModal.setHeaderTitle("ایجاد گروه مقادیر ثابت");
        typeIdField.clear();
        typeIdField.setEnabled(true);
        typeTitleField.clear();
        typeCategoryField.clear();
        typeIsEnumCheckbox.setValue(false);
        typeModal.open();
    }

    private void openEditTypeModal(CodeTypeModel ct) {
        editingTypeId = ct.getId();
        typeModal.setHeaderTitle("ویرایش گروه: " + ct.getTitle());
        typeIdField.setValue(ct.getId());
        typeIdField.setEnabled(false);
        typeTitleField.setValue(ct.getTitle() != null ? ct.getTitle() : "");
        typeCategoryField.setValue(ct.getCategory() != null ? ct.getCategory() : "");
        typeIsEnumCheckbox.setValue(ct.getIsEnum() != null && ct.getIsEnum());
        typeModal.open();
    }

    private void saveType() {
        if (typeTitleField.isEmpty()) {
            Notifier.error("عنوان گروه الزامی است.");
            return;
        }

        CodeTypeModel model = new CodeTypeModel();
        model.setId(editingTypeId != null ? editingTypeId : typeIdField.getValue());
        model.setTitle(typeTitleField.getValue());
        model.setCategory(typeCategoryField.getValue());
        model.setIsEnum(typeIsEnumCheckbox.getValue());

        boolean ok = Requester.executeAndCheck(() -> codeTypeClient.update(model));
        if (ok) {
            Notifier.success("گروه مقادیر ثابت با موفقیت ثبت شد.");
            typeModal.close();
            loadTypes();
        }
    }

    private void loadTypes() {
        CodeTypeModel search = new CodeTypeModel();
        PagedResponse<CodeTypeModel> resp = Requester.executePaged(() -> codeTypeClient.list(search, PageRequest.of(0, 100)));
        if (resp != null && resp.getResultList() != null) {
            typeList.clear();
            typeList.addAll(resp.getResultList());
            typeGrid.setItems(typeList);
            codeTypeFilter.setItems(typeList);
            if (!typeList.isEmpty()) {
                codeTypeFilter.setValue(typeList.get(0));
            }
        }
    }

    private void loadItems(String codeTypeId) {
        RestResponse<List<CodeTypeItemModel>> resp = Requester.execute(() -> codeTypeItemClient.list(codeTypeId));
        itemList.clear();
        if (resp != null && resp.getData() != null) {
            itemList.addAll(resp.getData());
        }
        applyItemFilter(itemSearch.getValue());
    }

    private void applyItemFilter(String query) {
        String q = query != null ? query.trim().toLowerCase() : "";
        List<CodeTypeItemModel> filtered = itemList.stream().filter(item -> {
            if (q.isEmpty()) return true;
            return (item.getFarsiTitle() != null && item.getFarsiTitle().toLowerCase().contains(q))
                    || (item.getEnglishTitle() != null && item.getEnglishTitle().toLowerCase().contains(q))
                    || (item.getId() != null && item.getId().toLowerCase().contains(q));
        }).toList();
        itemGrid.setItems(filtered);
    }

    @Override
    public String getPageTitle() {
        return "مقادیر و ثابت‌ها — ورتکس";
    }
}
