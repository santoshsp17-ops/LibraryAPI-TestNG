# Library API Automation Project
Automated testing suite for a Library REST API — designed to perform CRUD operations (excluding Update) using Java, TestNG, and RestAssured.

## Overview
This project automates interactions with a Library API to validate the Add, Get, and Delete operations. It uses dynamic test data via Faker to generate realistic book information, making the tests robust and scalable.

---
## Features
- Create (Add) multiple books with fake/dynamic data  
- Fetch (Get) book details  
- Delete books dynamically using IDs generated in the tests  
- Assertion-based validation to verify API behavior  
- Detailed logging to simplify debugging  
---

## Tech Stack
- **Language:** Java  
- **Test Framework:** TestNG  
- **HTTP Client:** RestAssured  
- **Data Generation:** Faker  
- **Build Tool:** Maven  
---

## Setup & Installation

1. Clone this repository:  git clone https://github.com/santoshsp17-ops/LibraryAPI-TestNG.git  
   
-Open the project in your preferred IDE (IntelliJ / Eclipse).
-No additional setup is required because pom.xml includes:
-The build configuration
-Test framework setup
-Plugins (if any)

You can directly run: mvn test.
