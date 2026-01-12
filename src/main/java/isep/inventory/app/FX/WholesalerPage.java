package isep.inventory.app.FX;

import isep.inventory.app.entity.Product;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class WholesalerPage extends VBox {
    private final TableView<Product> productTable;
    private final ObservableList<Product> products;
    private Stage ownerStage;

    // Single parameter constructor (primary)
    public WholesalerPage(ObservableList<Product> allProducts) {
        this(allProducts, null);
    }

    // Two parameter constructor
    public WholesalerPage(ObservableList<Product> allProducts, Stage ownerStage) {
        this.products = allProducts;
        this.ownerStage = ownerStage;
        this.productTable = createProductTable();

        Label title = new Label("💰 Wholesaler Product Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Buttons container
        HBox buttonContainer = new HBox(10);
        buttonContainer.setPadding(new Insets(10, 0, 10, 0));

        Button addButton = new Button("➕ Add New Item");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(e -> showProductDialog(null));

        Button editButton = new Button("✏️ Edit Selected Item");
        editButton.getStyleClass().add("primary-button");
        editButton.setOnAction(e -> {
            Product selected = productTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showProductDialog(selected);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Selection");
                alert.setHeaderText("No Product Selected");
                alert.setContentText("Please select a product to edit.");
                alert.showAndWait();
            }
        });

        Button importCsvButton = new Button("📄 Import from CSV");
        importCsvButton.getStyleClass().add("primary-button");
        importCsvButton.setOnAction(e -> importProductsFromCSV());

        buttonContainer.getChildren().addAll(addButton, editButton, importCsvButton);

        // Layout setup
        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.getChildren().addAll(title, buttonContainer, productTable);
    }

    public void setOwnerStage(Stage stage) {
        this.ownerStage = stage;
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
        ButtonType saveButtonType = new ButtonType("💾 Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Product Name");
        TextField descField = new TextField();
        descField.setPromptText("Description");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        TextField stockField = new TextField();
        stockField.setPromptText("Stock Quantity");

        // Populate fields if editing
        if (productToEdit != null) {
            nameField.setText(productToEdit.getName());
            descField.setText(productToEdit.getDescription());
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
                    String name = nameField.getText().trim();
                    String desc = descField.getText().trim();
                    double price = Double.parseDouble(priceField.getText().trim());
                    int stock = Integer.parseInt(stockField.getText().trim());

                    if (name.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Product name cannot be empty.", ButtonType.OK);
                        alert.showAndWait();
                        return null;
                    }

                    if (productToEdit == null) {
                        // Add new product
                        products.add(new Product(name, desc, price, stock));
                    } else {
                        // Edit existing product
                        productToEdit.setName(name);
                        productToEdit.setDescription(desc);
                        productToEdit.setPrice(price);
                        productToEdit.setStock(stock);
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

    private void importProductsFromCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Products from CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File selectedFile = fileChooser.showOpenDialog(ownerStage);

        if (selectedFile != null) {
            try {
                int importedCount = parseAndImportCSV(selectedFile);

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Import Successful");
                successAlert.setHeaderText("✅ Products Imported");
                successAlert.setContentText("Successfully imported " + importedCount + " product(s) from CSV file.");
                successAlert.showAndWait();

                productTable.refresh();

            } catch (IOException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Import Error");
                errorAlert.setHeaderText("❌ Failed to Import CSV");
                errorAlert.setContentText("Error reading CSV file: " + e.getMessage());
                errorAlert.showAndWait();
            } catch (Exception e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Import Error");
                errorAlert.setHeaderText("❌ Failed to Parse CSV");
                errorAlert.setContentText("Error parsing CSV data: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }

    private int parseAndImportCSV(File file) throws IOException {
        int count = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                // Skip header row
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    String[] values = line.split(",");

                    if (values.length >= 4) {
                        String name = values[0].trim();
                        String description = values[1].trim();
                        double price = Double.parseDouble(values[2].trim());
                        int stock = Integer.parseInt(values[3].trim());

                        // Create and add new product
                        Product newProduct = new Product(name, description, price, stock);
                        products.add(newProduct);
                        count++;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Skipping invalid line: " + line);
                    // Continue to next line instead of failing completely
                }
            }
        }

        return count;
    }
}