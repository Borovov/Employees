package com.max.employees;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.List;

@SpringUI
public class Page extends UI {

    private HorizontalLayout root;
    private Grid<Employee> grid;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setLayout();
        
        UI.getCurrent().getPage().getStyles().add(
                ".padding {\n" +
                "padding: 10px;" +
                "}");
    }

    private void setLayout() {
        root = new HorizontalLayout();
        root.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        root.setSizeFull();
        setContent(root);
        ControllerDB controller = new ControllerDB();

        // Загрузка данных сотрудников
        grid = new Grid<>();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        ComboBox<Company> boxCompany = setForm(controller);
        boxCompany.addValueChangeListener(event -> {
            if (boxCompany.getValue() != null)
                updateGrid(event.getValue().getId());
        });

        grid.addItemClickListener(event -> showEditWindow(event.getItem()));
        grid.setWidth("90%");
        root.addComponent(grid);
    }

    private void updateGrid(int companyId) {
        List<Employee> list = new ControllerDB().loadEmployees(companyId);

        if (list != null) {
            clearGrid(grid);
            grid.addColumn(Employee::getFirstName).setCaption("Имя");
            grid.addColumn(Employee::getLastName).setCaption("Фамилия");
            grid.addColumn(Employee::getDate).setCaption("День рождения");
            grid.addColumn(Employee::getPosition).setCaption("Должность");
            grid.setItems(list);
        }
    }

    private void clearGrid(Grid<Employee> grid) {
        List<Grid.Column<Employee, ?>> list =  grid.getColumns();

        for (Grid.Column<Employee, ?> employeeColumn : list) {
            grid.removeColumn(employeeColumn);
        }
    }

    private void showEditWindow(Employee employee) {
        Window window = new Window("Информация о сотруднике");
        window.setModal(true);
        window.setPositionX(650);
        window.setPositionY(300);
        window.setResizable(false);

        FormLayout layout = new FormLayout();
        layout.setStyleName("padding");
        TextField tFName = new TextField("Имя");
        tFName.setValue(employee.getFirstName());
        layout.addComponent(tFName);
        TextField tLName = new TextField("Фамилия");
        tLName.setValue(employee.getLastName());
        layout.addComponent(tLName);
        TextField tDate = new TextField("Дата рождения");
        tDate.setValue(employee.getDate());
        layout.addComponent(tDate);
        TextField tPos = new TextField("Должность");
        tPos.setValue(employee.getPosition());
        layout.addComponent(tPos);

        Button bSave = new Button("Сохранить");
        bSave.setWidth("100%");
        bSave.addClickListener(event -> {
            boolean isUpdate = new ControllerDB().updateEmployee(tFName.getValue(), tLName.getValue(), tDate.getValue(), tPos.getValue(), employee.getId());

            if (isUpdate)
                updateGrid(employee.getCompanyId());

            window.close();
        });
        layout.addComponent(bSave);

        window.setContent(layout);
        UI.getCurrent().addWindow(window);
    }

    public ComboBox<Company> setForm(ControllerDB controller) {
        // Загрузка списка компаний
        ComboBox<Company> boxCompany = new ComboBox<>("Компания");
        List<Company> companies = controller.loadCompanies();

        if (companies != null) {
            boxCompany.setItemCaptionGenerator(Company::getName);
            boxCompany.setItems(companies);
        }
        String[] inputField = new String[] {"ID", "Имя", "Фамилия", "Дата рождения", "Должность"};
        List<TextField> inputList = new ArrayList<>();

        for (String title : inputField)
            inputList.add(new TextField(title));

        FormLayout form = new FormLayout();
        for (TextField input: inputList)
            form.addComponent(input);

        form.addComponent(boxCompany);
        Button bAdd = new Button("Добавить");
        bAdd.addClickListener(event -> {

            boolean isEmpty = false;
            for (TextField input: inputList) {
                if (input.isEmpty()) {
                    isEmpty = true;
                    break;
                }
            }

            if (!isEmpty && !boxCompany.isEmpty()) {
                boolean isAdd = controller.addEmployee(inputList, boxCompany.getValue().getId());
                if (!isAdd)
                    Notification.show(null,
                            "Данный ID некоректен или занят другим пользователем",
                            Notification.Type.WARNING_MESSAGE);
                else
                    Notification.show(null,
                            "Новый пользователь был добавлен",
                            Notification.Type.HUMANIZED_MESSAGE);

                updateGrid(boxCompany.getValue().getId());
            } else
                Notification.show(null,
                        "Заполните все поля ввода",
                        Notification.Type.WARNING_MESSAGE);
        });
        form.addComponent(bAdd);

        form.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        form.setStyleName("padding");
        form.setWidth(null);
        root.addComponent(form);
        root.setComponentAlignment(form, Alignment.MIDDLE_CENTER);

        return boxCompany;
    }

}
