
# 📘 Customer Management Application

This is a Spring Boot RESTful web service for managing customer records. It allows you to create, retrieve, update, and delete customer data. The application uses an in-memory H2 database and provides an interactive Swagger UI for API testing.

---

## 🚀 How to Build and Run the Application

### 🔧 Prerequisites
- Java 17 or higher
- Maven 3.8+

### 🛠️ Build and Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/harinath-ediga/customer-api.git
   cd customer-app
   ```

2. **Build the application**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The application will start on: `http://localhost:8080`

---

## 📂 Sample Requests

### ➕ Create Customer
```http
POST /customers
Content-Type: application/json

{
  "name": "Harinath",
  "email": "harinath.ediga23@gmail.com",
  "annualSpend": 24000,
  "lastPurchaseDate": "2025-05-24",
  "tier": "1"
}
```

### 📄 Get All Customers
```http
GET /customers/all
```

### 🔍 Get Customer by ID
```http
GET /customers/{uuid}
```

### 🔍 Get Customer by Name
```http
GET /customers?name=Harinath
```

### 🔍 Get Customer by Email
```http
GET /customers?email=harinath.ediga@gmail.com
```

### ✏️ Update Customer
```http
PUT /customers/{uuid}
Content-Type: application/json

{
  "name": "Harinath",
  "email": "harinath.ediga23@gmail.com",
  "annualSpend": 24000,
  "lastPurchaseDate": "2025-05-24",
  "tier": "1"
}
```

### ❌ Delete Customer
```http
DELETE /customers/{uuid}
```

---

## 💻 Accessing the H2 Database Console

1. Open your browser and go to:  
   `http://localhost:8080/h2-console`

2. Use the following credentials:
   - **JDBC URL**: `jdbc:h2:mem:testdb`
   - **Username**: `admin`
   - **Password**: *admin*
   - **Driver Class**: `org.h2.Driver`

3. Click **Connect** to view or query the database.

---

## 🧾 Assumptions Made

- Customer ID is generated using `UUID`.
- Both `name` and `email` are required fields.
- An in-memory H2 database is used for simplicity and fast development.
- `GET` endpoints for name and email return a single result.
- Basic validation is applied using Jakarta annotations.
- Logging is configured at the controller level to track request handling.
- Swagger UI is enabled for interactive documentation and testing.

---

## 📑 API Documentation

Swagger UI is available at:
- `http://localhost:8080/swagger-ui.html`  
- or `http://localhost:8080/swagger-ui/index.html`

Use this interface to test all API endpoints interactively.

---

## ✅ Running Unit Tests

Run the test cases using Maven:

```bash
mvn test
```

---

## 🧑‍💻 Author

- **Harinath** – Principal Engineer

---

## License

This project is licensed under the MIT License.
