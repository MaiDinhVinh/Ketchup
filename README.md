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

<!-- TODO: Fill in the feature list -->

---

## Installation and Usage

- To use this application, make sure that you have installed Java SE with the version 17+
- Then, go to the Github release page in this repository and download the Jar file corresponding to your current Operating System
- Then double click on the Jar file and enjoy !
(Note: You will encounter an prompting window asking you if you want to load in the test dataset, this is for demonstration purposes, so press "No" if you want to use this app normally. This will run when you first run this application)
---

## Developer Guide

For developers, in order to run this project, please follow the steps below
- First, clone this repository to your machine
- Then, open the source code in your favorite text editor or IDE
- If you are using InteliJ IDEA, the run configuraton will be automatically detected by the IDE, otherwise, to run the project, please run `mvn javafx:run` for normal execution of the project (or `mvn javafx:run -P debug` for debug mode execution)

---

*This project was developed as part of COMP1020 - Object-Oriented Programming and Data Structures, Spring 2026, VinUniversity.*
