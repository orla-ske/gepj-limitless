package isep.inventory.app;

//import isep.inventory.app.DAO.DashboardStats;
import isep.inventory.app.entity.Product;
import isep.inventory.app.entity.Role;
import isep.inventory.app.entity.User;
import isep.inventory.app.services.InventoryService;
import isep.inventory.app.services.AuthenticationService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.Optional;



public class InventoryManagementSys extends Application {

    private Stage primaryStage;
    private InventoryService inventoryService;
    private AuthenticationService authenticationService;
    private User currentUser;
    private ObservableList<Product> tableData;

    // Dashboard Components
    private Label totalStockLabel;
    private Label totalItemsLabel;
    private Label lowStockLabel;
    private TableView<Product> table;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.inventoryService = new InventoryService();
        this.authenticationService = new AuthenticationService();
        this.tableData = FXCollections.observableArrayList();

        primaryStage.setTitle("Limitless FX - Inventory Hub");

        showLoginScreen();
    }

    private void showLoginScreen() {
        VBox loginLayout = new VBox(15);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setPadding(new Insets(40));
        loginLayout.getStyleClass().add("login-container");

        Label titleLabel = new Label("Limitless Login");
        titleLabel.getStyleClass().add("title-label");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username (admin)");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (password)");

        Button loginButton = new Button("Sign In");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        loginButton.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();

            if(authenticationService.login(user, pass)){
                this.currentUser = authenticationService.getUserByUsername(user);
                showDashboardScreen();
            } else {
                errorLabel.setText("Invalid credentials");
            }
        });

        loginLayout.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton, errorLabel);

        Scene loginScene = new Scene(loginLayout, 400, 500);
        applyStyles(loginScene.getStylesheets());
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private void showDashboardScreen() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("main-background");

        HBox header = new HBox(20);
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 25, 15, 25));

        Label appTitle = new Label("Limitless Inventory");
        appTitle.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("Logged in as: " + (currentUser != null ? currentUser.getUsername() : "Unknown"));
        userLabel.getStyleClass().add("user-label");

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("danger-button");
        logoutButton.setOnAction(e -> {
            currentUser = null;
            showLoginScreen();
        });

        header.getChildren().addAll(appTitle, spacer, userLabel, logoutButton);
        root.setTop(header);

        VBox content = new VBox(20);
        content.setPadding(new Insets(25));

        // 1. Stats Cards
        HBox statsContainer = new HBox(20);
        statsContainer.setAlignment(Pos.CENTER);

        totalStockLabel = new Label("0");
        totalItemsLabel = new Label("0");
        lowStockLabel = new Label("0");

        statsContainer.getChildren().addAll(
                createStatCard("Total Stock", totalStockLabel, "stat-card-blue"),
                createStatCard("Unique Items", totalItemsLabel, "stat-card-green"),
                createStatCard("Low Stock Alerts", lowStockLabel, "stat-card-yellow")
        );

        // 2. Toolbar (Search + Add)
        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search inventory...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterTable(newVal));

        Region toolbarSpacer = new Region();
        HBox.setHgrow(toolbarSpacer, Priority.ALWAYS);

        Button addButton = new Button("Add New Item");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(e -> showItemDialog(null));

        toolbar.getChildren().addAll(searchField, toolbarSpacer, addButton);

        table = new TableView<>();
        setupTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        content.getChildren().addAll(statsContainer, toolbar, table);
        root.setCenter(content);

        Scene dashboardScene = new Scene(root, 1000, 700);
        applyStyles(dashboardScene.getStylesheets());
        primaryStage.setScene(dashboardScene);
        primaryStage.centerOnScreen();

        refreshData();
    }


    private VBox createStatCard(String title, Label valueLabel, String styleClass) {
        VBox card = new VBox(10);
        card.getStyleClass().addAll("stat-card", styleClass);
        card.setPrefWidth(250);

        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("stat-title");

        valueLabel.getStyleClass().add("stat-value");

        card.getChildren().addAll(titleLbl, valueLabel);
        return card;
    }

    @SuppressWarnings("unchecked")
    private void setupTable() {
        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> skuCol = new TableColumn<>("Category");
        skuCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Product, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("stock"));

        TableColumn<Product, String> locCol = new TableColumn<>("Company");
        locCol.setCellValueFactory(new PropertyValueFactory<>("sourceCompany"));

        TableColumn<Product, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("isAvailable"));
        statusCol.setCellFactory(column -> new TableCell<Product, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Low")) setStyle("-fx-text-fill: #d97706; -fx-font-weight: bold;");
                    else if (item.contains("Out")) setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
                    else setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<Product, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<Product, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(10, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("small-button");
                deleteBtn.getStyleClass().add("small-danger-button");
                editBtn.setOnAction(event -> showItemDialog(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(event -> {
                    Product item = getTableView().getItems().get(getIndex());
                    deleteItem(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        table.getColumns().addAll(nameCol, skuCol, qtyCol, locCol, statusCol, actionCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // --- LOGIC & DATA ---

    private void refreshData() {
//        tableData.setAll(inventoryService.findAll());
//        table.setItems(tableData);
//
//        DashboardStats stats = inventoryService.getDashboardStats();
//        totalStockLabel.setText(String.valueOf(stats.getTotalStock()));
//        totalItemsLabel.setText(String.valueOf(stats.getTotalItems()));
//        lowStockLabel.setText(String.valueOf(stats.getLowStockCount()));
    }

    private void filterTable(String query) {
        if (query == null || query.isEmpty()) {
            table.setItems(tableData);
        } else {
            ObservableList<Product> filtered = FXCollections.observableArrayList();
            String lowerCaseQuery = query.toLowerCase();
            for (Product item : tableData) {
                if (item.getName().toLowerCase().contains(lowerCaseQuery) ||
                        item.getSourceCompany().toLowerCase().contains(lowerCaseQuery)) {
                    filtered.add(item);
                }
            }
            table.setItems(filtered);
        }
    }

    private void deleteItem(Product item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Item");
        alert.setHeaderText("Are you sure you want to delete " + item.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            inventoryService.delete(item);
            refreshData();
        }
    }

    private void showItemDialog(Product item) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle(item == null ? "Add New Item" : "Edit Item");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField skuField = new TextField();
        skuField.setPromptText("Category");
        TextField qtyField = new TextField();
        qtyField.setPromptText("Quantity");
        TextField locField = new TextField();
        locField.setPromptText("Company");

        if (item != null) {
            nameField.setText(item.getName());
            skuField.setText(item.getCategory());
            qtyField.setText(String.valueOf(item.getStock()));
            locField.setText(item.getSourceCompany());
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(skuField, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(qtyField, 1, 2);
        grid.add(new Label("Company:"), 0, 3);
        grid.add(locField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        applyStyles(dialog.getDialogPane().getStylesheets());
        dialog.getDialogPane().getStyleClass().add("my-dialog");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Product newItem = item != null ? item : new Product();
                newItem.setName(nameField.getText());
                newItem.setCategory(skuField.getText());
                newItem.setSourceCompany(locField.getText());
                try {
                    newItem.setQuantity(Integer.parseInt(qtyField.getText()));
                } catch (NumberFormatException e) {
                    newItem.setQuantity(0);
                }
                return newItem;
            }
            return null;
        });

        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(newItem -> {
            inventoryService.save(newItem);
            refreshData();
        });
    }

    private void applyStyles(ObservableList<String> stylesheets) {
        URL cssResource = getClass().getResource("/styles.css");
        if (cssResource != null) {
            stylesheets.add(cssResource.toExternalForm());
        } else {
            System.err.println("WARNING: styles.css not found in resources! Application will look unstyled.");
        }
    }
}
