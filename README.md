# Ketchup - Movie Management System

## Table of Contents

- [About the Project](#about-the-project)
- [Team Members](#team-members)
- [Technology Stack](#technology-stack)
- [Features](#features)
- [Installation and Usage](#installation-and-usage)
- [Developer Guide](#developer-guide)

---

## About the Project

Ketchup is a desktop-based Movie Management System developed as a final project for the course **COMP1020 - Object-Oriented Programming and Data Structures** (Spring 2026). The application provides a comprehensive platform for managing movie listings and cinema operations, targeting both administrative personnel and end users.

For admin users, this system allows users to manage movie records, including adding, updating, and removing titles from the catalog. For ordinary users, registered users can browse available movies, view detailed screening information, select seats for a desired showtime, and complete a booking. Upon confirmation, the system generates a PDF ticket that can be downloaded and printed, providing a complete end-to-end cinema booking experience.

The project demonstrates the practical application of object-oriented programming principles — including encapsulation, inheritance, polymorphism, and abstraction — in the context of a real-world, database-backed desktop application.

---

## Team Members

| Full Name | Student ID | Email |
|---|---|---|
| Hoang Duc Phat | V202502162 | 25phat.hd@vinuni.edu.vn |
| Tran Phan Anh | V202502262 | 25anh.tp@vinuni.edu.vn |
| Nguyen Trong Khoi Nguyen | V202502344 | 25nguyen.ntk@vinuni.edu.vn |
| Nguyen Dinh Quy | V202502731 | 25quy.nd@vinuni.edu.vn |
| Mai Dinh Vinh | V202502959 | 25vinh.md@vinuni.edu.vn |

---

## Technology Stack

| Technology | Version | Role |
|---|---|---|
| Java SE | 17 | Core application language |
| JavaFX | 21 | Desktop GUI framework |
| Maven | 3.x | Build and dependency management |
| MariaDB | Latest stable | Relational database backend |

### Rationale for Technology Choices

**Java SE 17**
Java SE 17 is a Long-Term Support (LTS) release, it is a stable successor of Java SE 8, that was used widely in enterpise systems in the last decades. It contains many new useful features such as switch expressions, sealed classes, and other performance improvement in JVM and Garbage Collection, ensuring the reliability of the system

**JavaFX**
JavaFX is the standard modern GUI toolkit for Java desktop applications, offering a rich set of UI controls, a CSS-based styling engine, and an FXML declarative layout system. This separation of concerns between UI design (FXML/CSS) and application logic (Java controllers) resulting in a maintainable and testable codebase. JavaFX also provides smooth rendering and native-feeling interfaces on both Windows and macOS.

**Maven**
Maven provides a user-friendly and standardized dependency management system using XML file. It eliminates the use of manual JAR libraries management (often referred to as *JAR hell*). It is developed by Apache and therefore integrated seamlessly with various IDE such as InteliJ IDEA and Eclipse. All available 3rd party libraries are all actively supporting Maven

**MariaDB**
MariaDB is a "copy-cat" version developed by the same developers from MySQL that allows embedding for database engine, therefore, users and admins don't have to install a separate database engine like MySQL. This also allows versatility for database engineers with the need to migrate from MariaDB to MySQL

---

## Features
 
### Authentication and Account Management
 
- **User Registration** — New users can self-register by providing an email address, username, and a password. Email addresses are validated against a standardized regex pattern
- **Role-Based Login** — The login screen allows user to pick registed role through a role selector (Admin / User). The system enforces that administrator access is restricted to authorized accounts, preventing external access
- **Input Validation** — All required fields are validated before any authentication attempt is made.
---
 
### Administrator Features
 
#### Movie Catalog Management
 
- **Add Movie** — Administrators can create a new movie record through a dedicated form dialog, providing all required informations. New entries are added to a centralized MariaDB embedded database
- **Edit Movie** — Any existing movie record can be selected from the table and modified. All changes are updated accordingly inside the database
- **Delete Movie** — A selected movie can be permanently removed from the system after the explicit confirmation from the user
#### Search, Sort, and Navigation
 
- **Movie Search** — The catalog can be searched by keyword across supported fields, including title, genre, rating, and showtime. Search operations are executed in a multithreaded manner to keep the interface responsive.
- **Multi-Criterion Sort** — The movie table can be sorted by any of eight criteria: ID, Title, Genre, Duration (minutes), Rating, Showtime, Number of Selected Seats, and Price per Seat.
- **Refresh** — The movie catalog can be reloaded from the database at any time
- **Logout** — Administrators can end their session and return to the Login screen
---
 
### Customer Features
 
#### Movie Browsing
 
- **Filtered Movie List** — The customer home screen displays only movies that the logged-in user has not yet booked, preventing duplicate entries
- **Movie Detail View** — A dedicated detail panel presents the full screening information for a selected movie, including genre, duration, age rating, showtime, and price per seat.
- **Movie Search** — Customers can search the available catalog by keyword. Previously booked movies are excluded from all search results.
- **Movie Sort** — The available movie list can be sorted by Title, Genre, Duration (minutes), or Showtime.
#### Seat Selection and Booking
 
- **Interactive Seat Map** — A fixed cinema grid of 8 rows (A–H) by 12 columns is rendered for each movie, with a visual aisle gap between columns 6 and 7 to reflect a realistic cinema layout.
- **Real-Time Seat Status** — Each seat is color-coded: green for available, red for occupied by another customer, and a highlighted style for seats the current customer has selected.
- **Seat Toggle with Live Pricing** — Clicking an available seat toggles its selection state. The total prices of the booking is updated in real-time in a decicated label
- **Booking Confirmation** — Before a booking is finalized, a confirmation dialog presents a full summary including movie title, showtime, selected seat identifiers, seat count, per-seat price, and total amount due. The customer may confirm the booking or return to the seat map to revise their selection.
#### Booking History
 
- **Booking History Table** — All confirmed bookings are displayed in a centralized view with columns for Booking ID, Movie Title, Showtime, Seats, and Total Price.
- **Booking History Search** — Customers can search across their booking history records.
- **Booking History Sort** — Booking records can be sorted by Booking ID, Movie Title, Showtime, or Total Price.
- **View Booked Seat Map** — A read-only seat map can be opened for any confirmed booking. The customer's reserved seats are highlighted in gold, and the occupancy status of all remaining seats is shown for reference.
- **Cancel Booking** — A confirmed booking can be cancelled after an explicit confirmation prompt. Upon cancellation, the affected movie is restored to the available movie list and becomes bookable again.
#### PDF Ticket Export
 
- **Export from Booking History** — A booking selected from the history table can be exported as a formatted PDF ticket through a native Save dialog.
- **Export from Booking Detail** — The PDF export action is also accessible directly from within the read-only seat map detail view.
- **Ticket Content** — The generated PDF ticket (A5 format) includes the application branding, a shortened ticket ID, movie title, showtime, booked seat list, seat count, per-seat price, and total price, presented in a styled layout with a dark-red branded header.


---

## Installation and Usage

- To use this application, make sure that you have installed Java SE with the version 17+
- Then, go to the Github release page in this repository and download the Jar file corresponding to your current Operating System
- Then double click on the Jar file and enjoy !
> [!CAUTION]
> When running this app for the first time, Windows or any other OS might ask for permission, please press "ALLOW" to proceed, otherwise, the behavior of the app might be unexpected
> You might encounter a situation where the application window is just a blank window, please _"jiggle"_ it a little bit or close the window and open it again, it should be fixed. This is a JavaFX bug related to the Stage changing, and we haven't find a solution for that bug yet

> [!NOTE]
> You will encounter an prompting window asking you if you want to load in the test dataset, this is for demonstration purposes, so press "No" if you want to use this app normally. This will run when you first run this application
> The Admin credential for the test database is _**Email: a@a.com**_ and _**Password: a**_ 
> To factory reset the database, simply delete the folder `~/Ketchup/` 
---

## Developer Guide

For developers, in order to run this project, please follow the steps below
- First, clone this repository to your machine
- Then, open the source code in your favorite text editor or IDE
- If you are using InteliJ IDEA, the run configuraton will be automatically detected by the IDE, otherwise, to run the project, please run `mvn javafx:run` for normal execution of the project (or `mvn javafx:run -P debug` for debug mode execution)

---

*This project was developed as part of COMP1020 - Object-Oriented Programming and Data Structures, Spring 2026, VinUniversity.*
