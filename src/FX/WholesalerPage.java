import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class WholesalerPage extends VBox {
    private final TableView<Product> productTable;
    private final ObservableList<Product> products;

    public WholesalerPage(ObservableList<Product> allProducts) {
        this.products = allProducts;
        this.productTable = createProductTable();

        Label title = new Label("💰 Wholesaler Product Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button addButton = new Button("➕ Add New Item");
        addButton.setOnAction(e -> showProductDialog(null));

        Button editButton = new Button("✏️ Edit Selected Item");
        editButton.setOnAction(e -> {
            Product selected = productTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showProductDialog(selected);
            }
        });

        // Layout setup
        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.getChildren().addAll(title, productTable, addButton, editButton);
    }

    private TableView<Product> createProductTable() {
        TableView<Product> table = new TableView<>(products);

        // Define columns
        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameCol.setMinWidth(150);

        TableColumn<Product, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        descCol.setMinWidth(250);

        TableColumn<Product, Number> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        priceCol.setMinWidth(100);

        TableColumn<Product, Number> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(cellData -> cellData.getValue().stockProperty());
        stockCol.setMinWidth(100);

        table.getColumns().addAll(nameCol, descCol, priceCol, stockCol);
        return table;
    }

    private void showProductDialog(Product productToEdit) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle(productToEdit == null ? "Add New Product" : "Edit Product");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        TextField descField = new TextField();
        TextField priceField = new TextField();
        TextField stockField = new TextField();

        // Populate fields if editing
        if (productToEdit != null) {
            nameField.setText(productToEdit.getName());
            descField.setText(productField.getDescription());
            priceField.setText(String.valueOf(productToEdit.getPrice()));
            stockField.setText(String.valueOf(productToEdit.getStock()));
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Stock:"), 0, 3);
        grid.add(stockField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText();
                    String desc = descField.getText();
                    double price = Double.parseDouble(priceField.getText());
                    int stock = Integer.parseInt(stockField.getText());

                    if (productToEdit == null) {
                        // Add new product
                        products.add(new Product(name, desc, price, stock));
                    } else {
                        // Edit existing product
                        productToEdit.nameProperty().set(name);
                        productToEdit.descriptionProperty().set(desc);
                        productToEdit.priceProperty().set(price);
                        productToEdit.stockProperty().set(stock);
                        // Force table refresh
                        productTable.refresh();
                    }
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid number format for Price or Stock.", ButtonType.OK);
                    alert.showAndWait();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
}