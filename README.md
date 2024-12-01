# Retailer Reward Points Calculation
This Spring Boot application calculates reward points for customers of a retailer based on their purchase transactions. The reward points are calculated using the following rules:
- 2 points for every dollar spent over $100** in each transaction.
- 1 point for every dollar spent between $50 and $100** in each transaction.

### Technologies Used:
- **Java 17**
- **Spring Boot 3.4.0**
- **MySQL 8.0.40-0**
- **Maven** for project management
- **Lombok** for reducing boilerplate code
- **Global Exception Handling**
- **Spring Profiles** (Dev and Prod)
- **Internationalization (i18n)** for multi-language support

### Database Setup:

1. **Create the Database**:
   - Open MySQL and run the following command to create the database:
     CREATE DATABASE rewards_dev;
     USE rewards_dev;
     
2. **Configure the MySQL Connection**:
   - Update the `application.properties` file in the `src/main/resources` directory to match your local MySQL setup:
     spring.datasource.url=jdbc:mysql://localhost:3306/rewards_dev
     spring.datasource.username=root
     spring.datasource.password=your_password
     spring.jpa.hibernate.ddl-auto=update
     spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

### Running the Project Locally:
1. Clone the repository:
   ```bash
   git clone https://github.com/nalinijayavelu/RetailerRewardPointsCalculator.git
   cd RetailerRewardPointsCalculator
   git checkout master
   
2. Install dependencies using Maven:
   mvn clean install

3. Configure the MySQL database as mentioned above.

4. mvn spring-boot:run
5. The application will be available at http://localhost:8080

API Endpoints:
1. Create a New Reward Transaction
    Method: POST
    URL: /retail/rewards
    Request Body:
    {
      "customerId": "100",
      "purchaseAmount": 120.00,
      "purchaseDate": "2024-12-01"
    }
    Response: 201 Created with a success message.
2. Get Reward Points by Customer ID and Date Range
    Method: GET
    URL: /retail/rewards?customerId=106&fromDate=2024-07-01&toDate=2024-12-01
    Response: A map of reward points for each month within the provided date range. If Date not provide by default CurrentDate rewards retrieved for the given customerId
    {
      "106": {
          "December": 50,
          "Total": 490,
          "July": 350,
          "August": 90
      }
    }
3. Get Reward Points by Customer ID
    Method: GET
    URL: /retail/rewards/106
    Response: A map of reward points for the customer.
    {
      "106": {
          "December": 50,
          "Total": 490,
          "July": 350,
          "August": 90
      }
    }

**Validation Cases Handled:**
Transaction Amount: Ensures the purchase amount is positive and greater than zero.
Customer ID: Ensures the customer ID is provided and valid.
Transaction Date: Ensures the transaction date is not in the future and is not null.
Date Range: Ensures that both fromDate and toDate are valid, with no future dates and that fromDate is before toDate.