# Banking Queue Simulation

A Java application that simulates a single-server banking queue for **100 customers** and computes queue statistics.

## Simulation Parameters

| Parameter | Distribution |
|-----------|-------------|
| Inter-Arrival Times | Uniform(1, 8) minutes |
| Service Times | Uniform(1, 6) minutes |
| Number of Customers | 100 |

## Features

- **Input Screen** — Two datasets for random numbers (IAT and Service)
- **Output Screen 1** — Customer simulation table (arrival, service, wait, departure times)
- **Output Screen 2** — Queue statistics summary
- **Excel Integration** — Load input from Excel, save templates, export results

## Requirements

- Java 17 or higher
- Maven 3.6+

## How to Run

```bash
cd "CSM Assignment"
mvn clean package
java -jar target/banking-queue-simulation-1.0.0.jar
```

Or run directly with Maven:

```bash
mvn compile exec:java -Dexec.mainClass="com.banking.Main"
```

## Usage

1. Open the **Input** tab
2. Click **Generate Random Numbers** (or enter 100 values per dataset manually)
3. Optionally use **Load from Excel** or **Save Excel Template**
4. Click **Run Simulation**
5. View results in **Output 1: Table** and **Output 2: Statistics**
6. Click **Export Results to Excel** to save output

## Excel Format

**Input file** (two sheets):
- Sheet 1: IAT random numbers (column A, values 0–1)
- Sheet 2: Service random numbers (column A, values 0–1)

**Output file** (two sheets):
- Sheet 1: Customer simulation table
- Sheet 2: Queue statistics

## Queue Statistics Computed

- Average waiting time
- Average service time
- Average inter-arrival time
- Average time in system
- Server utilization
- Probability of waiting
- Total simulation time and idle time
- Average and maximum queue length

## Project Structure

```
src/main/java/com/banking/
├── Main.java
├── model/          Customer, QueueStatistics
├── simulation/     QueueSimulator
├── excel/          ExcelHandler
└── gui/            InputPanel, TableOutputPanel, StatisticsOutputPanel, MainFrame
```
