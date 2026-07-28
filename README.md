# Micro Lending Circle Tracker

A Java Swing desktop application for tracking micro-lending circles (chit funds / ROSCAs):
members, monthly contributions, payout rotation, and reports. No web server required —
runs as a plain desktop app from VS Code.

## Tech Stack
- Java 17+ (Swing for UI)
- MySQL (via JDBC / mysql-connector-j)
- Maven (dependency management)

## Setup

### 1. Install prerequisites
- JDK 17 or later
- VS Code + "Extension Pack for Java" (Microsoft)
- MySQL Server, running locally

### 2. Create the database
Open MySQL (Workbench, CLI, or the VS Code MySQL extension) and run:
```
database/schema.sql
```
This creates the `micro_lending_tracker` database, all tables, and a default login.

### 3. Configure your DB credentials
Open `src/main/java/com/microlending/DBConnection.java` and update:
```java
private static final String USER = "root";
private static final String PASSWORD = "your_mysql_password";
```
to match your local MySQL setup.

### 4. Open in VS Code
- Open the `MicroLendingTracker` folder in VS Code.
- The Java extension should auto-detect the Maven project and download
  `mysql-connector-j` automatically (internet required the first time).
- If it doesn't resolve automatically, run: `mvn clean install`

### 5. Run the app
Two ways:
- Open `src/main/java/com/microlending/Main.java` and click **Run** (above the `main` method) in VS Code.
- Or from a terminal in the project folder:
  ```
  mvn compile exec:java
  ```

### Default login
```
Username: admin
Password: admin123
```

## Building a standalone .jar
```
mvn clean package
```
This produces `target/MicroLendingTracker.jar` (includes the MySQL driver bundled in),
which you can double-click or run with:
```
java -jar target/MicroLendingTracker.jar
```

## Project Structure
```
MicroLendingTracker/
  pom.xml
  database/
    schema.sql
  src/main/java/com/microlending/
    Main.java
    DBConnection.java
    model/          User, Circle, Member, Contribution, Payout
    dao/             UserDAO, CircleDAO, MemberDAO, ContributionDAO, PayoutDAO
    ui/              LoginFrame, DashboardFrame, CircleFrame, MemberFrame,
                      ContributionFrame, PayoutFrame, ReportFrame
```

## Notes / Next Steps
- Passwords are stored in plain text in this version for simplicity — for real use,
  hash passwords (e.g. with BCrypt) before storing them.
- The "Generate Payout" button pays out to members in `payout_order` sequence and
  marks them as having received their payout; it computes the payout as
  monthly amount × member count (adjust the logic in `PayoutFrame.java` if your
  circle's payout rules differ).
- Feel free to extend `ReportFrame` to export reports to PDF/CSV if needed later.
