package isep.inventory.app.FX;

import isep.inventory.app.entity.Product;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.util.Map;

public class InvoicePreview {
    private final Stage dialogStage;

    public InvoicePreview(Window owner) {
        this.dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            dialogStage.initOwner(owner);
        }
        dialogStage.setTitle("Generated Invoice Preview");
    }

    public void showInvoice(Map<Product, Integer> cartItems) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label title = new Label("🧾 RETAILER PURCHASE INVOICE");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Invoice details area
        TextArea invoiceText = new TextArea();
        invoiceText.setEditable(false);
        invoiceText.setPrefRowCount(15);
        invoiceText.setWrapText(true);

        StringBuilder sb = new StringBuilder();
        double totalAmount = 0.0;

        sb.append(String.format("%-25s %10s %10s %15s\n", "Item", "Quantity", "Price/Unit", "Subtotal"));
        sb.append("------------------------------------------------------------------------\n");

        for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double subtotal = product.getPrice() * quantity;
            totalAmount += subtotal;

            sb.append(String.format("%-25s %10d %10.2f %15.2f\n",
                    product.getName(),
                    quantity,
                    product.getPrice(),
                    subtotal));
        }

        sb.append("------------------------------------------------------------------------\n");
        sb.append(String.format("%-47s %15.2f\n", "TOTAL AMOUNT:", totalAmount));

        invoiceText.setText(sb.toString());

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> dialogStage.close());

        root.getChildren().addAll(title, invoiceText, closeButton);

        // Set the scene and show the stage
        dialogStage.setScene(new Scene(root, 600, 400));
        dialogStage.showAndWait();
    }
}