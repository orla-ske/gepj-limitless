package isep.inventory.app.FX;

import isep.inventory.app.entity.Product;
import javafx.geometry.Insets;
import javafx.print.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class InvoicePreview {
    private final Stage dialogStage;
    private Map<Product, Integer> currentCartItems;
    private String invoiceContent;

    public InvoicePreview(Window owner) {
        this.dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            dialogStage.initOwner(owner);
        }
        dialogStage.setTitle("Generated Invoice Preview");
    }

    public void showInvoice(Map<Product, Integer> cartItems) {
        this.currentCartItems = cartItems;

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label title = new Label("🧾 RETAILER PURCHASE INVOICE");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Invoice details area
        TextArea invoiceText = new TextArea();
        invoiceText.setEditable(false);
        invoiceText.setPrefRowCount(15);
        invoiceText.setWrapText(true);

        this.invoiceContent = generateInvoiceContent(cartItems);
        invoiceText.setText(invoiceContent);

        // Button container
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button printButton = new Button("🖨️ Print");
        printButton.setOnAction(e -> printInvoice());

        Button pdfButton = new Button("📄 Print to PDF");
        pdfButton.setOnAction(e -> printToPDF());

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> dialogStage.close());

        buttonBox.getChildren().addAll(printButton, pdfButton, closeButton);

        root.getChildren().addAll(title, invoiceText, buttonBox);

        // Set the scene and show the stage
        dialogStage.setScene(new Scene(root, 600, 450));
        dialogStage.showAndWait();
    }

    private String generateInvoiceContent(Map<Product, Integer> cartItems) {
        StringBuilder sb = new StringBuilder();
        double totalAmount = 0.0;

        // Add date and invoice number
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String invoiceNumber = "INV-" + System.currentTimeMillis();

        sb.append("═══════════════════════════════════════════════════════════════════════\n");
        sb.append("\t\t\tRETAILER PURCHASE INVOICE\n");
        sb.append("═══════════════════════════════════════════════════════════════════════\n\n");

        sb.append("Invoice Number:\t").append(invoiceNumber).append("\n");
        sb.append("Date:\t\t").append(date).append("\n\n");

        sb.append("───────────────────────────────────────────────────────────────────────\n");
        sb.append("Item\t\t\tQuantity\tPrice/Unit\tSubtotal\n");
        sb.append("───────────────────────────────────────────────────────────────────────\n");

        for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double subtotal = product.getPrice() * quantity;
            totalAmount += subtotal;

            // Truncate product name if too long
            String productName = product.getName();
            if (productName.length() > 20) {
                productName = productName.substring(0, 17) + "...";
            }

            // Use tabs with padding for alignment
            sb.append(String.format("%-20s\t%d\t\t$%.2f\t\t$%.2f\n",
                    productName,
                    quantity,
                    product.getPrice(),
                    subtotal));
        }

        sb.append("───────────────────────────────────────────────────────────────────────\n\n");
        sb.append(String.format("TOTAL AMOUNT:\t\t\t\t\t\t$%.2f\n", totalAmount));
        sb.append("\n═══════════════════════════════════════════════════════════════════════\n");
        sb.append("\t\t\tThank you for your business!\n");
        sb.append("═══════════════════════════════════════════════════════════════════════\n");

        return sb.toString();
    }

    private void printInvoice() {
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        if (printerJob != null && printerJob.showPrintDialog(dialogStage)) {
            boolean success = printContent(printerJob);

            if (success) {
                printerJob.endJob();
                showAlert(Alert.AlertType.INFORMATION, "Print Successful",
                        "Invoice sent to printer successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Print Failed",
                        "Failed to print the invoice.");
            }
        }
    }

    private void printToPDF() {
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        if (printerJob != null) {
            // Show print dialog with a note about PDF printing
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Print to PDF");
            info.setHeaderText("Save as PDF");
            info.setContentText("In the print dialog, select 'Microsoft Print to PDF' or 'Save as PDF' as your printer.\n\n" +
                    "This will allow you to save the invoice as a PDF file.");
            info.initOwner(dialogStage);
            info.showAndWait();

            if (printerJob.showPrintDialog(dialogStage)) {
                boolean success = printContent(printerJob);

                if (success) {
                    printerJob.endJob();
                    showAlert(Alert.AlertType.INFORMATION, "PDF Creation",
                            "Invoice has been sent to PDF printer. Check your default save location.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "PDF Creation Failed",
                            "Failed to create PDF.");
                }
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Printer Error",
                    "No printer service available.");
        }
    }

    private boolean printContent(PrinterJob printerJob) {
        // Create a VBox for better layout control
        VBox printNode = new VBox(5);
        printNode.setPadding(new Insets(20));
        printNode.setStyle("-fx-background-color: white;");

        // Title
        Text titleText = new Text("RETAILER PURCHASE INVOICE");
        titleText.setFont(Font.font("Arial", 16));
        titleText.setStyle("-fx-font-weight: bold;");

        // Invoice content
        Text contentText = new Text(invoiceContent);
        contentText.setFont(Font.font("Courier New", 10));

        printNode.getChildren().addAll(titleText, contentText);

        // Get page layout
        PageLayout pageLayout = printerJob.getJobSettings().getPageLayout();

        // Calculate scale to fit page
        double scaleX = pageLayout.getPrintableWidth() / printNode.getBoundsInParent().getWidth();
        double scaleY = pageLayout.getPrintableHeight() / printNode.getBoundsInParent().getHeight();
        double scale = Math.min(scaleX, scaleY);

        if (scale < 1.0) {
            Scale scaleTransform = new Scale(scale, scale);
            printNode.getTransforms().add(scaleTransform);
        }

        return printerJob.printPage(printNode);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(dialogStage);
        alert.showAndWait();
    }
}