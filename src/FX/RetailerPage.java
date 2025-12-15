import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.HashMap;
import java.util.Map;

public class RetailerPage extends VBox {
    private final TableView<Product> productTable;
    private final Map<Product, Integer> cart = new HashMap<>();
    private final Label cartSummaryLabel;
    private final InvoicePreview invoicePreview;

    public RetailerPage(ObservableList<Product> allProducts) {
        this.productTable = createProductTable(allProducts);
        this.invoicePreview = new InvoicePreview(this.getScene() != null ? this.getScene().getWindow() : null);

        Label title = new Label("🏪 Retailer Product Catalog");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Cart Summary
        this.cartSummaryLabel = new Label("🛒 Cart: 0 items");

        // Quantity and Add to Cart controls
        TextField quantityField = new TextField("1");
        quantityField.setPrefWidth(50);
        Button addToCartButton = new Button("🛒 Add to Cart");

        addToCartButton.setOnAction(e -> {
            Product selected = productTable.getSelectionModel().getSelectedItem();
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                if (selected != null && quantity > 0 && quantity <= selected.getStock()) {
                    addToCart(selected, quantity);
                    quantityField.setText("1"); // Reset
                } else if (quantity > selected.getStock()) {
                    new Alert(Alert.AlertType.WARNING, "Insufficient stock!", ButtonType.OK).showAndWait();
                }
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid quantity number.", ButtonType.OK).showAndWait();
            }
        });

        Button checkoutButton = new Button("🧾 Checkout and Generate Invoice");
        checkoutButton.setOnAction(e -> checkout());

        HBox controls = new HBox(10, new Label("Quantity:"), quantityField, addToCartButton, checkoutButton);
        controls.setPadding(new Insets(5, 0, 0, 0));

        // Layout setup
        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.getChildren().addAll(title, cartSummaryLabel, productTable, controls);
    }

    private TableView<Product> createProductTable(ObservableList<Product> products) {
        TableView<Product> table = new TableView<>(products);

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Product, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        TableColumn<Product, Number> priceCol = new TableColumn<>("Price (Each)");
        priceCol.setCellValueFactory(cellData -> cellData.getValue().priceProperty());

        TableColumn<Product, Number> stockCol = new TableColumn<>("Available Stock");
        stockCol.setCellValueFactory(cellData -> cellData.getValue().stockProperty());

        table.getColumns().addAll(nameCol, descCol, priceCol, stockCol);
        return table;
    }

    private void addToCart(Product product, int quantity) {
        // Add or update the product quantity in the cart
        cart.merge(product, quantity, Integer::sum);
        updateCartSummary();
    }

    private void updateCartSummary() {
        int totalItems = cart.values().stream().mapToInt(Integer::intValue).sum();
        cartSummaryLabel.setText("🛒 Cart: " + totalItems + " items");
    }

    private void checkout() {
        if (cart.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Your cart is empty!", ButtonType.OK).showAndWait();
            return;
        }

        // 1. Generate Invoice Preview
        invoicePreview.showInvoice(cart);

        // 2. Update stock (Simulation of a completed purchase)
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            product.setStock(product.getStock() - quantity); // Decrement stock
        }

        // 3. Clear cart and refresh
        cart.clear();
        updateCartSummary();
        productTable.refresh();
        new Alert(Alert.AlertType.INFORMATION, "Purchase complete! Stock updated.", ButtonType.OK).showAndWait();
    }
}