# EasyShop - Backend API
Welcome to the EasyShop backend API, built using Spring Boot and Java. 
This API serves as the backend for the EasyShop e-commerce website, 
providing a robust and secure platform for users to shop online.

# Features
User Authentication: Register, login, and authenticate using JWT-based security.
Product Management: Manage products, categories, and product filtering.
Order Management: Create, view, update, and manage customer orders 
Shopping Cart: Add, update, or remove products from the shopping cart.
Security: Full protection using Spring Security and JWT for stateless authentication.

# Technologies Used
Java 
Spring Boot 
Spring Security (for JWT-based authentication)
MySQL (database)
JUnit & JSON (for unit testing)

# Requirements
Before running the project, ensure that you have the following installed:
Java 11+ (JDK 11 or higher)
Maven (for dependency management and build automation)
MySQL (or other supported database)
Postman or any API client (for testing the API) 

# Setup and Installation
1. Clone the repository
   bash
   Copy code
   git clone https://github.com/yourusername/easyshop-backend.git
   open project
2. Set up your database
   Create a MySQL database named easyshop.
   Update the application.properties file with your MySQL connection details:
   properties
   Copy code
   spring.datasource.url=jdbc:mysql://localhost:3306/easyshop
   spring.datasource.username=root
   spring.datasource.password= <yourpassword>
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

3. Run the application
The API will be accessible at http://localhost:8080.

# API Endpoints
Here is a list of the key API endpoints for EasyShop:

Authentication
POST /api/register - Register a new user
POST /api/login - Log in and get a JWT token
Products
GET /api/products - Get a list of all products
GET /api/products/{id} - Get details of a single product by ID
POST /api/products - Add a new product (requires admin role)
PUT /api/products/{id} - Update product information (requires admin role)
DELETE /api/products/{id} - Delete a product (requires admin role)
Categories
GET /api/categories - Get all product categories
POST /api/categories - Create a new product category (requires admin role)
Cart
POST /api/cart - Add a product to the shopping cart
GET /api/cart - View the current shopping cart
DELETE /api/cart/{itemId} - Remove an item from the cart
Orders
POST /api/orders - Confirm order (from the cart)
GET /api/orders - Get all orders for the logged in user