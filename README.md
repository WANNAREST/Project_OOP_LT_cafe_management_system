# Café Management System

A comprehensive Java-based application for managing café operations, built with OOP principles and JavaFX.

## Overview

This project is a full-featured café management system designed to streamline operations for both staff and customers. The system provides separate interfaces for employees and customers, handling everything from order processing to inventory management.

## Features

### For Customers
- User account creation and authentication
- Product browsing and searching
- Shopping cart functionality
- Order placement and tracking
- Multiple payment options (Cash, Bank Transfer)
- QR code payment integration
- Loyalty points system

### For Employees
- Secure login system with role-based access
- Order management and status updates
- Shift scheduling and attendance tracking
- Sales reporting and analytics
- Inventory management
- Customer management

## Technical Stack

- **Language**: Java
- **UI Framework**: JavaFX
- **Database**: SQL (with DAO pattern implementation)
- **External APIs**: Payment processing integration
- **Architecture**: Object-Oriented with MVC pattern

## Project Structure

- **src/main**: Contains application entry points
- **src/Controller**: MVC Controllers for handling business logic
- **src/obj**: Model classes representing business entities
- **src/view**: FXML files for UI components

## Key Components

### Product Management
The system supports multiple product categories including coffee and bakery items, each with customizable properties.

### User Management
- **Customer**: Account management, order history, loyalty points
- **Employee**: Role-based access, shift management, performance tracking

### Order Processing
Complete order lifecycle management from creation to fulfillment, with support for different payment methods and statuses.

### Payment Integration
Secure payment processing with QR code generation for bank transfers.

## Getting Started

1. Clone the repository
2. Set up the database using the provided schema
3. Configure payment API credentials in `config.properties`
4. Run the application using `src/main/Shop.java`

## Dependencies

- JavaFX
- Apache HTTP Client
- JSON Libraries
- SQL Database connector
