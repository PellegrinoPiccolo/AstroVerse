# README AstroVerse

Created: October 10, 2024 11:57 AM  
Trello: [AstroVerseIS](https://trello.com/b/W32LhGw5/astroverseis)  
Slack: [Progetto IS Slack](https://join.slack.com/t/progettoisadchchpl/shared_invite/zt-2rxwjgdhx-P~UW7Ups6JCDrtdBl1lEIw)  
GitHub Repository: [AstroVerse Repository](https://github.com/PellegrinoPiccolo/AstroVerse.git)  
AI Repository: [AstroVerseIA Repository](https://github.com/Adry04/AstroVerseIA/tree/main)  
Last Updated: Monday, 22/12/2025, 18:03

![Spring Boot Social Web Application
A project for Software Engineering course of Computer Science at University of Salerno](AstroVerse_logo.png)

Social Web Application made with Spring Boot and Vue.js  
A project for Software Engineering course of Computer Science at the University of Salerno

# Project Description

## Authors:

- [**Pellegrino Piccolo**](https://github.com/PellegrinoPiccolo)*-Developer-AI Branch*
- [**Adriano De Vita**](https://github.com/Adry04)-*Developer-AI Branch-Quantum Branch*
- [**Christian Fontana**](https://github.com/chriisey)-*Developer*
- [**Christian Bianco**](https://github.com/ChristianUnisa)-*Developer*

## Documents

All the project documents can be found inside the *“Docs”* directory of the repository, the test directory can be found inside *Docs → Testing* divided by test type (blackbox/whitebox).

Those documents include the Statement Of Work (SOW), Requirement Analysis Document (RAD), System Design Document (SDD), some notions on Object Design Document (ODD), Test Plan (TP), Test Case Scenarios (TCS), Test Summary Report (TSR), Test Incident Report (TIR) and Test Incident Report Table (TIRT).

Now there's a new section of the documents in *"Docs"* called *"QuantumMigration"* that contains the thesis of [**Adriano De Vita**](https://github.com/Adry04) that covered the last necessary update on Quantum Migration as part of his internship. The reading of the document is highly recommended to those who are interested in taking action in the process of migration to new PQC algorithms and technologies.

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

## PQC (Post-Quantum Cryptography) System Update

After implementing a new method of Key-exchanhge, based on an hybrid solution using ECDH toghether with ML-KEM/Kyber algorithms the system is now using a standard of post-quantum cryptography approved by the NIST to cipher the user email. The update is available only on the branch `quantum` of the AstroVerse repository so the sensible data submitted will be definitively safe against the risk of future quantum attacks at least until the breakout of a CRQC that can break security level 3 easily according to the latest NIST report.

## Built with

- [Java](https://docs.oracle.com/en/java/javase/21/) - The programming language used for back-end development
- [Spring Framework](https://spring.io/) - The framework used in combination with Java on the back-end development
- [Vue.js](https://vuejs.org/) - Javascript framework used for the front-end dynamic pages development
- [CSS](https://www.w3schools.com/css/default.asp) - For the graphic part of the front-end pages
- [Javascript](https://www.javascript.com/) - Used for accessing to the requests of the back-end server
