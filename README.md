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
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/rewards_dev
     spring.datasource.username=root
     spring.datasource.password=your_password
     spring.jpa.hibernate.ddl-auto=update
     spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
     ```

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
   - Method: POST
   - URL: /retail/rewards
    ```Request:
     {
        "customerId": "101",
        "customerName": "Neel",
        "amount": 120
    }
   ```
    Response: 201 Created with a success message.
2. Get Reward Points by Customer ID and Date Range -  A map of reward points for each month within the provided date range. If Date not provide by default CurrentDate rewards retrieved for the given customerId
   - Method: GET
   - URL: /retail/rewards?customerId=100&fromDate=2024-07-01&toDate=2024-12-05
    ```Response:
    {
       "customerId": "100",
       "customerName": "John",
       "totalPoints": 441,
       "transactionsByMonth": {
           "Dec2024": {
               "points": 441,
               "transactions": [
                   {
                       "id": 1,
                       "amount": 120.5,
                       "date": "2024-12-10",
                       "rewardPoints": 91
                   },
                   {
                       "id": 5,
                       "amount": 250.0,
                       "date": "2024-12-10",
                       "rewardPoints": 350
                   }
               ]
           }
       }
   }
    ```
3. Get Reward Points by Customer ID
    - Method: GET
    - URL: /retail/rewards/101
    ```Response:
   {
    "customerId": "100",
    "customerName": "John",
    "totalPoints": 491,
    "transactionsByMonth": {
        "Nov2024": {
            "points": 50,
            "transactions": [
                {
                    "id": 4,
                    "amount": 100.0,
                    "date": "2024-11-10",
                    "rewardPoints": 50
                }
            ]
        },
        "Dec2024": {
            "points": 441,
            "transactions": [
                {
                    "id": 1,
                    "amount": 120.5,
                    "date": "2024-12-10",
                    "rewardPoints": 91
                },
                {
                    "id": 5,
                    "amount": 250.0,
                    "date": "2024-12-10",
                    "rewardPoints": 350
                }
            ]
        }
    }
   }
   ```

**Validation Cases Handled:**
- Transaction Amount: Ensures the purchase amount is positive and greater than zero.
- Customer ID: Ensures the customer ID is provided and valid.
- Transaction Date: Ensures the transaction date is not null and not in the future.
- Date Range: Ensures that both fromDate and toDate are valid, with no future dates and that fromDate is before toDate.

###Included Screenshot of test results

**Create a New Reward Transaction**

- Create a New Reward Transaction - success case
![Create a New Reward Transaction - success case](https://github.com/nalinijayavelu/RetailerRewardPointsCalculator/blob/master/assets/CreateNewRewardTransaction-SuccessCase.png)

- Create a New Reward Transaction - Validation for an amount
![](https://github.com/nalinijayavelu/RetailerRewardPointsCalculator/blob/master/assets/CreateNewRewardTransaction-ValidationAmount.png)

- Create a New Reward Transaction - Validation for CustomerId
![Create a New Reward Transaction - Validation for CustomerId](https://github.com/nalinijayavelu/RetailerRewardPointsCalculator/blob/master/assets/CreateNewRewardTransaction-ValidationCustomerId.png)

**Get Reward Points by Customer ID and Date Range**
 
- Get Reward Points by Customer ID and DateRange - Validation case for CustomerId
![Get Reward Points by Customer ID and DateRange - Validation case for CustomerId](https://github.com/nalinijayavelu/RetailerRewardPointsCalculator/blob/master/assets/GetRewardPointsByCustomerIDAndDateRange-ValidationCustomerId.png)

- Get Reward Points by Customer ID and DateRange - Validation case for Future Date 
![et Reward Points by Customer ID and DateRange - Validation case for Future Date](https://github.com/nalinijayavelu/RetailerRewardPointsCalculator/blob/master/assets/GetRewardPointsByCustomerIDDateRange-ValidationcaseForFutureDate.png)

- Get Reward Points by Customer ID and DateRange - Success Case
- If fromDate and toDate are not passed then 3 months transactions are retrieved.
![Get Reward Points by Customer ID and DateRange - Success Case](https://github.com/nalinijayavelu/RetailerRewardPointsCalculator/blob/master/assets/GetRewardPointsByCustomerIDDateRange-SuccessCase.png)

Get Reward Points by Customer ID
- Get Reward Points by Customer ID Success case
![Get Reward Points by Customer ID Success case](https://github.com/nalinijayavelu/RetailerRewardPointsCalculator/blob/master/assets/GetRewardPointsByCustomerID.png)

