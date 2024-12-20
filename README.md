<img width="900" alt="one-place-logo" src="https://github.com/user-attachments/assets/e5dd1d59-62d3-4feb-b2ac-cf8c0410ab97" height="500"/>

A robust and secure backend API built using Spring Boot and Java and serves as the foundation for the e-commerce website,
offering a user-friendly platform for online shopping.

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
Java 11+ (JDK 11 or higher)
Maven (for dependency management and build automation)
MySQL (or other supported database)
Postman or any API client (for testing the API) 

# Setup and Installation
1. Clone the repository
   git clone https://github.com/yourusername/easyshop-backend.git
2. Set up database
   Create a MySQL database named easyshop.
   Update the application.properties 
   spring.datasource.url=jdbc:mysql://localhost:3306/easyshop
   spring.datasource.username=root
   spring.datasource.password= <yourpassword>
3. Run the application
The API will be accessible at http://localhost:8080
4. Open PostMan
   use path /api/login with a body request { "username":"admin", "password":"password" } to
   generate bearer token for authentication  

# API Endpoints
Here is a list of the key API endpoints:
19
- Authentication

- POST /api/register - Register a new user 1
- POST /api/login - Log in and get a JWT token2
- 
- Products

- GET /api/products - View a list of all products 
- GET /api/products/{id} - Views a product by productId
- POST /api/products - Add a new product (requires admin role) 
- PUT /api/products/{id} - Update product information (requires admin role) 
- DELETE /api/products/{id} - Delete a product (requires admin role) 

- Categories

- GET /api/categories - View all categories 
- GET /api/categories/{id} View categories by categoryId
- POST /api/categories - Create a new product category (requires admin role) 
- PUT /api/categories/{id} - Update categories by categoryId
- DELETE /api/categories/{id} - Delete categories by categoryId

- Profile
- GET /api/profile - View profiles
- PUT /api/profile - Update a profile 

- Cart

- GET /api/cart - View the current shopping cart 
- POST /api/cart/products/{id} - Add a product to the shopping cart 12
- PUT /api/cart/products/{id} - Update a product by productId  12
- DELETE /api/cart/{itemId} - Remove an item from the cart 13

- Orders

- POST /api/orders - Confirm order (from the cart) 14
- GET /api/orders - Get all orders for the logged in user 15

# Author 
- Tosh

# Acknowledgement 

- https://manytools.org/hacker-tools/ascii-banner/
- https://www.vistaprint.com/logomaker
- https://www.geeksforgeeks.org/rest-api-introduction/
