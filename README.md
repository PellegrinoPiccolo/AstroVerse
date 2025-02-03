# README AstroVerse

Created: October 10, 2024 11:57 AM  
Trello: [AstroVerseIS](https://trello.com/b/W32LhGw5/astroverseis)  
Slack: [Progetto IS Slack](https://join.slack.com/t/progettoisadchchpl/shared_invite/zt-2rxwjgdhx-P~UW7Ups6JCDrtdBl1lEIw)  
GitHub Repository: [AstroVerse Repository](https://github.com/PellegrinoPiccolo/AstroVerse.git)  
AI Repository: [AstroVerseIA Repository](https://github.com/Adry04/AstroVerseIA/tree/main)  
Last Updated: January 5, 2025 2:21 PM

![Spring Boot Social Web Application
A project for Software Engineering course of Computer Science at University of Salerno](AstroVerse_logo.png)

Social Web Application made with Spring Boot and Vue.js  
A project for Software Engineering course of Computer Science at the University of Salerno

# Project Description

## Authors:

- [**Pellegrino Piccolo**](https://github.com/PellegrinoPiccolo)*-Developer*
- [**Adriano De Vita**](https://github.com/Adry04)-*Developer*
- [**Christian Fontana**](https://github.com/chriisey)-*Developer*
- [**Christian Bianco**](https://github.com/ChristianUnisa)-*Developer*

## Documents

All the project documents can be found inside the “*Docs”* directory of the repository, the test directory can be found inside *Docs→Testing* divided by test type (blackbox/whitebox).

Those documents include the Statement Of Work (SOW), Requirement Analysis Document (RAD), System Design Document (SDD), some notions on Object Design Document (ODD), Test Plan (TP), Test Case Scenarios (TCS), Test Summary Report (TSR), Test Incident Report (TIR) and Test Incident Report Table (TIRT).

# Technical Information

In this section you can find the technical information on how to run the AstroVerse app on your machine

## Clone and run the project locally

To clone the project locally follow these steps:

1. Install IntelliJ and MySQL 8+ on your PC;
2. Clone this repo with `git clone` on your command line prompt;
3. Open cloned repository with IntelliJ;
4. Login on MySQL Workbench and open a connection on port 3306 (usually is 3306 by default)
5. Open the folder "*backend"* and run the backend with the `mvn spring-boot:run` command line on the IntelliJ prompt;
6. Then open the folder "*frontend"* and run the frontend with the `npm install` and then `npm run` commands;
7. Open your browser on `localhost:5173`.

## AI Update

After the Artificial Intelligence module update and deployment into AstroVerse you have now the possibility to express your preferences freely through the macro_arguments in the autentication phase, when registering a new account, that will automatically suggest you relevant AstroVerse spaces, based on your current interests. Just make sure to use the `dev-ai/main` branch to see and use the new updates or the new release v2.1 and the Artificial Intellingence module pinned on top, inside the relative repository you will find the instruction on how to run it.

## Built with

- [Java](https://docs.oracle.com/en/java/javase/21/) - The programming language used for back-end development
- [Spring Framework](https://spring.io/) - The framework used in combination with Java on the back-end development
- [Vue.js](https://vuejs.org/) - Javascript framework used for the front-end dynamic pages development
- [CSS](https://www.w3schools.com/css/default.asp) - For the graphic part of the front-end pages
- [Javascript](https://www.javascript.com/) - Used for accessing to the requests of the back-end server
