package isep.inventory.app.FX;

import isep.inventory.app.entity.Product;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RetailerPage extends VBox {
    private final TableView<Product> productTable;
    private final TableView<CartItem> cartTable;
    private final Map<Product, Integer> cart = new HashMap<>();
    private final Label cartTotalLabel;
    private final InvoicePreview invoicePreview;

    public RetailerPage(ObservableList<Product> allProducts) {
        this.productTable = createProductTable(allProducts);
        this.cartTable = createCartTable();
        this.invoicePreview = new InvoicePreview(this.getScene() != null ? this.getScene().getWindow() : null);

        Label title = new Label("🏪 Retailer Product Catalog");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Product table with quick add controls
        VBox productSection = new VBox(5);
        Label productLabel = new Label("Available Products (Double-click to add 1 item)");
        productLabel.setStyle("-fx-font-weight: bold;");

        // Quick add controls
        HBox quickAddControls = new HBox(10);
        quickAddControls.setAlignment(Pos.CENTER_LEFT);
        quickAddControls.setPadding(new Insets(5, 0, 5, 0));

        Spinner<Integer> quantitySpinner = new Spinner<>(1, 999, 1);
        quantitySpinner.setPrefWidth(80);
        quantitySpinner.setEditable(true);

        Button addButton = new Button("➕ Add to Cart");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(e -> {
            Product selected = productTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                addToCart(selected, quantitySpinner.getValue());
                quantitySpinner.getValueFactory().setValue(1); // Reset to 1
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product first.");
            }
        });

        Button addMultipleButton = new Button("📦 Quick Add Multiple");
        addMultipleButton.setOnAction(e -> showQuickAddDialog());

        quickAddControls.getChildren().addAll(
                new Label("Quantity:"),
                quantitySpinner,
                addButton,
                new Separator(),
                addMultipleButton
        );

        productSection.getChildren().addAll(productLabel, productTable, quickAddControls);
        VBox.setVgrow(productTable, Priority.ALWAYS);

        // Double-click to add 1 item quickly
        productTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Product selected = productTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    addToCart(selected, 1);
                }
            }
        });

        // Cart section
        VBox cartSection = new VBox(5);
        Label cartLabel = new Label("🛒 Shopping Cart");
        cartLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        this.cartTotalLabel = new Label("Total: $0.00 (0 items)");
        cartTotalLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        HBox cartControls = new HBox(10);
        cartControls.setAlignment(Pos.CENTER_LEFT);
        cartControls.setPadding(new Insets(5, 0, 5, 0));

        Button removeButton = new Button("➖ Remove Selected");
        removeButton.setOnAction(e -> removeFromCart());

        Button clearCartButton = new Button("🗑️ Clear Cart");
        clearCartButton.setOnAction(e -> clearCart());

        Button checkoutButton = new Button("🧾 Checkout & Generate Invoice");
        checkoutButton.getStyleClass().add("primary-button");
        checkoutButton.setOnAction(e -> checkout());

        cartControls.getChildren().addAll(removeButton, clearCartButton, checkoutButton);

        cartSection.getChildren().addAll(cartLabel, cartTotalLabel, cartTable, cartControls);
        VBox.setVgrow(cartTable, Priority.ALWAYS);

        // Layout setup
        this.setPadding(new Insets(10));
        this.setSpacing(15);
        this.getChildren().addAll(title, productSection, new Separator(), cartSection);
    }

    private TableView<Product> createProductTable(ObservableList<Product> products) {
        TableView<Product> table = new TableView<>(products);
        table.setPrefHeight(250);

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameCol.setPrefWidth(150);

        TableColumn<Product, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        descCol.setPrefWidth(250);

        TableColumn<Product, Number> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        priceCol.setPrefWidth(80);

        TableColumn<Product, Number> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(cellData -> cellData.getValue().stockProperty());
        stockCol.setPrefWidth(80);

        table.getColumns().addAll(nameCol, descCol, priceCol, stockCol);
        return table;
    }

    private TableView<CartItem> createCartTable() {
        TableView<CartItem> table = new TableView<>();
        table.setPrefHeight(200);

        TableColumn<CartItem, String> nameCol = new TableColumn<>("Product");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        nameCol.setPrefWidth(200);

        TableColumn<CartItem, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setPrefWidth(80);

        TableColumn<CartItem, Double> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        priceCol.setPrefWidth(100);

        TableColumn<CartItem, Double> subtotalCol = new TableColumn<>("Subtotal");
        subtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        subtotalCol.setPrefWidth(100);

        table.getColumns().addAll(nameCol, qtyCol, priceCol, subtotalCol);
        return table;
    }

    private void addToCart(Product product, int quantity) {
        if (quantity <= 0) {
            showAlert(Alert.AlertType.WARNING, "Invalid Quantity", "Quantity must be greater than 0.");
            return;
        }

        int currentInCart = cart.getOrDefault(product, 0);
        int newTotal = currentInCart + quantity;

        if (newTotal > product.getStock()) {
            showAlert(Alert.AlertType.WARNING, "Insufficient Stock",
                    String.format("Cannot add %d items. Only %d in stock (you have %d in cart).",
                            quantity, product.getStock(), currentInCart));
            return;
        }

        cart.merge(product, quantity, Integer::sum);
        updateCartDisplay();

        // Visual feedback
        showToast("✓ Added " + quantity + "x " + product.getName());
    }

    private void removeFromCart() {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cart.remove(selected.getProduct());
            updateCartDisplay();
            showToast("✓ Removed " + selected.getProductName());
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to remove.");
        }
    }

    private void clearCart() {
        if (cart.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Empty Cart", "Your cart is already empty.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Clear Cart");
        confirm.setHeaderText("Clear entire cart?");
        confirm.setContentText("This will remove all " + cart.size() + " items from your cart.");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            cart.clear();
            updateCartDisplay();
            showToast("✓ Cart cleared");
        }
    }

    private void updateCartDisplay() {
        ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
        double total = 0.0;
        int totalItems = 0;

        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double subtotal = product.getPrice() * quantity;

            cartItems.add(new CartItem(product, quantity));
            total += subtotal;
            totalItems += quantity;
        }

        cartTable.setItems(cartItems);
        cartTotalLabel.setText(String.format("Total: $%.2f (%d items)", total, totalItems));
    }

    private void showQuickAddDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Quick Add Multiple Products");
        dialog.setHeaderText("Select products and quantities to add");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        ListView<ProductQuantityPair> listView = new ListView<>();
        listView.setPrefHeight(300);

        for (Product product : productTable.getItems()) {
            listView.getItems().add(new ProductQuantityPair(product));
        }

        listView.setCellFactory(lv -> new ListCell<ProductQuantityPair>() {
            private final Spinner<Integer> spinner = new Spinner<>(0, 999, 0);
            private final HBox container = new HBox(10);

            {
                spinner.setPrefWidth(80);
                spinner.setEditable(true);
                container.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(ProductQuantityPair pair, boolean empty) {
                super.updateItem(pair, empty);
                if (empty || pair == null) {
                    setGraphic(null);
                } else {
                    spinner.getValueFactory().setValue(0);
                    spinner.valueProperty().addListener((obs, old, newVal) -> pair.setQuantity(newVal));

                    Label label = new Label(pair.getProduct().getName() + " - $" +
                            String.format("%.2f", pair.getProduct().getPrice()) +
                            " (Stock: " + pair.getProduct().getStock() + ")");
                    label.setPrefWidth(300);

                    container.getChildren().clear();
                    container.getChildren().addAll(label, spinner);
                    setGraphic(container);
                }
            }
        });

        content.getChildren().add(listView);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int addedCount = 0;
            for (ProductQuantityPair pair : listView.getItems()) {
                if (pair.getQuantity() > 0) {
                    addToCart(pair.getProduct(), pair.getQuantity());
                    addedCount++;
                }
            }
            if (addedCount > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Added " + addedCount + " products to cart!");
            }
        }
    }

    private void checkout() {
        if (cart.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Cart", "Your cart is empty!");
            return;
        }

        // Generate Invoice Preview
        invoicePreview.showInvoice(cart);

        // Update stock (Simulation of a completed purchase)
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            product.setStock(product.getStock() - quantity);
        }

        // Clear cart and refresh
        cart.clear();
        updateCartDisplay();
        productTable.refresh();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Purchase complete! Stock updated.");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showToast(String message) {
        // Simple feedback - could be enhanced with a custom toast notification
        System.out.println(message);
    }

    // Helper class for cart display
    public static class CartItem {
        private final Product product;
        private final int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() { return product; }
        public String getProductName() { return product.getName(); }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return product.getPrice(); }
        public double getSubtotal() { return product.getPrice() * quantity; }
    }

    // Helper class for quick add dialog
    private static class ProductQuantityPair {
        private final Product product;
        private int quantity = 0;

        public ProductQuantityPair(Product product) {
            this.product = product;
        }

        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}