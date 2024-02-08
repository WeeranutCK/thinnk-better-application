module cs211.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires bcrypt;
    requires java.sql;

    opens cs211.project.cs211661project to javafx.fxml;
    exports cs211.project.cs211661project;

    exports cs211.project.controllers;
    opens cs211.project.controllers to javafx.fxml;

    exports cs211.project.models;
    opens cs211.project.models to javafx.base;

    exports cs211.project.models.collections;
    opens cs211.project.models.collections to javafx.base;

    exports cs211.project.services;
    opens cs211.project.services to javafx.base;
}
