# Enhanced Car Rental System

**Table of Contents**

1. [Overview](#overview)
2. [Features](#features)
3. [Getting Started](#getting-started)

   * [Prerequisites](#prerequisites)
   * [Compilation](#compilation)
   * [Running the Application](#running-the-application)
4. [Usage](#usage)

   * [Main Menu Options](#main-menu-options)
   * [User Menu Options](#user-menu-options)
5. [Design & Technical Highlights](#design--technical-highlights)
6. [Persistence & Cleanup](#persistence--cleanup)

---

## Overview

This Java-based command-line **Enhanced Car Rental System** demonstrates a full-fledged OOP application, complete with dynamic memory management, secure user authentication, loan request/transfer, rental history management, and robust input validation. The system uses serialization for persistence and leverages Java’s built-in logging for operational insight.

## Features

* **Dynamic Data Structures**: Manages vehicles, customers, and rental history via `HashMap` and `ArrayList`.
* **Data Hiding & Encapsulation**: All class fields are private with public getters/setters; passwords are stored securely with SHA-256 hashing.
* **Inheritance & Polymorphism**: Abstract `Vehicle` base class with concrete `Car` and `Truck` subclasses overriding pricing logic.
* **Authentication**: User login and registration with credential validation.
* **Loan Management**: Request and transfer loans between accounts with input checks.
* **Rental Operations**: Rent and return vehicles; each rental is timestamped and logged.
* **History Management**: View, sort (by date), and delete rental history.
* **Account Management**: Create new accounts and delete existing ones.
* **Input Validation**: Ensures all numeric inputs fall within valid ranges.
* **Persistence**: State is saved/loaded via Java serialization (`.dat` files).
* **Logging**: `java.util.logging` provides info-level logs for rental processing and returns.
* **Resource Cleanup**: Proper closure of I/O resources (`Scanner`) and no memory leaks.

## Getting Started

### Prerequisites

* Java Development Kit (JDK) 8 or higher
* Terminal/Command Prompt

### Compilation

1. Clone or download the project files.
2. In the project directory, compile all `.java` files:

   ```bash
   javac *.java
   ```

### Running the Application

Launch the CLI app:

```bash
java EnhancedCarRentalApp
```

State files (`veh.dat`, `cus.dat`) will be created in the working directory on exit.

## Usage

### Main Menu Options

1. **Login** – Authenticate with existing user credentials.
2. **Register** – Create a new user account.
3. **Exit** – Save state and terminate.

### User Menu Options

1. **Rent** – View available vehicles, choose one by ID, and specify rental days.
2. **Return** – Return the most recent rental.
3. **RequestLoan** – Request additional loan funds.
4. **TransferLoan** – Transfer loan balance to another user.
5. **ShowHistory** – Display all past rentals.
6. **SortHistory** – Sort rental history by date.
7. **DeleteAccount** – Permanently remove your user account.
8. **Logout** – Return to the main menu.

## Design & Technical Highlights

* **OOP Principles**: Demonstrates Abstraction, Encapsulation, Inheritance, and Polymorphism.
* **Security**: Passwords are never stored in plain text.
* **Scalability**: Dynamic collections allow easy scaling of vehicles and users.
* **Error Handling**: Custom exceptions (`AuthenticationException`, `EntityNotFoundException`) for clear feedback.

## Persistence & Cleanup

* **State Persistence**: Vehicles and customer maps are serialized to `veh.dat` and `cus.dat` on exit.
* **Cleanup**: `Scanner` input stream is closed; all file I/O uses try-with-resources to prevent resource leaks.

