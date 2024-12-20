<img width="1000" alt="one-place-logo" src="https://github.com/user-attachments/assets/e5dd1d59-62d3-4feb-b2ac-cf8c0410ab97" height="300"/>

A  backend API built using Spring Boot and Java and serves as the foundation for the e-commerce website,
offering a user-friendly platform for online shopping.

# Features
- User Authentication: 
Register, login, and authenticate using JWT-based security.
- Product Management: Manage products, categories, and product filtering.
- Order Management: Create, view, update, and manage customer orders 
- Shopping Cart: Add, update, or remove products from the shopping cart.
- Security: Full protection using Spring Security and JWT for stateless authentication.

# Technologies Used
- Java 
- Spring Boot 
- Spring Security (for JWT-based authentication)
- MySQL (database)
JUnit & JSON (for unit testing)

# Requirements
- Java 11+ (JDK 11 or higher)
- Maven (for dependency management and build automation)
- MySQL (or other supported database)
- Postman or any API client (for testing the API) 

# Setup and Installation
1. Clone the repository
git clone https://github.com/yourusername/easyshop-backend.git

2. Set up database
    
3. Update the application.properties
spring.datasource.password= <yourpassword>

4. Run the application

5. Open PostMan. API will be accessible at http://localhost:8080

6. Log in - /api/login and generate bearer token for authentication { "username":"admin", "password":"password" } 
   

# API Endpoints
Here is a list of the key API endpoints:

Authentication

- POST /api/register - Register a new user 
- POST /api/login - Log in and get a JWT token

Products

- GET /api/products - View a list of all products 
- GET /api/products/{id} - Views a product by productId
- POST /api/products - Add a new product (requires admin role) 
- PUT /api/products/{id} - Update product information (requires admin role) 
- DELETE /api/products/{id} - Delete a product (requires admin role) 

Categories

- GET /api/categories - View all categories
- GET /api/categories/{id} View categories by categoryId
- GET /api/categories/{categoryId}/products - Filter products by category id
- POST /api/categories - Create a new product category (requires admin role) 
- PUT /api/categories/{id} - Update categories by categoryId
- DELETE /api/categories/{id} - Delete categories by categoryId

Profile
- GET /api/profile - View profile of current user logged in
- PUT /api/profile - Update a profile of current user logged in 

Cart

- GET /api/cart - View the shopping cart 
- POST /api/cart/products/{id} - Add a product to the shopping cart 
- PUT /api/cart/products/{id} - Update a product by productId  
- DELETE /api/cart/{itemId} - Remove an item from the cart 

Orders

- POST /api/orders - Confirm order (from the cart) 
- GET /api/orders - Get all orders for the logged in user 

# Author 
- Tosh

# Acknowledgement 

- https://manytools.org/hacker-tools/ascii-banner/
- https://www.vistaprint.com/logomaker
- https://www.geeksforgeeks.org/rest-api-introduction/
