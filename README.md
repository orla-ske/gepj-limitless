# Team  gepj-limitless 
Project Inventory Management System.

Description of the Project
The  Inventory Management System (IMS) is a Java - based application designed to facilitate Business - to - Business (B2B) operations between Wholesalers (Suppliers) and Retailers, overseen by a System Administrator. The application streamlines the supply chain process by allowing suppliers to list  products and retailers to purchase them.
The core problem this project solves is the manual tracking of inventory and invoicing. Bydigitizingthisprocess,thesystemensuresaccuratestocklevels,automatesinvoice generation, and enforces security through Role -B ased Access Control (RBAC).

List of Features
 The application features distinct functionality based on the user's logged -i n role:
Core Features
 Authentication: Secure login system with role detection (Admin, Supplier, Retailer).
 CSV Integration: Ability to bulk import product data from  .csv files.
 PDF Reporting: Generation of downloadable and printable PDF invoices for completed transactions.

Role-Specifc Features
• Administrator:
  Full system access (Superuser privileges).
  User Management: Create and manage accounts for Suppliers and Retailers.
  System Dashboard: View high - level metrics of system activity.

• Supplier (Wholesaler):
  Inventory Management: Add new products manually or via CSV import.
  Company Association: Accounts are linked to specific companies.
  Stock Overview: View and manage only the products associated with their specific company.

• Retailer: Marketplace View: Browse all available products on the platform from all suppliers.
  Purchasing: Add items to a cart and "Buy" items.
  Invoicing: Automatically generate a standard or PDF invoice upon purchase.
