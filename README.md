# Banking Queue Simulation

A Java desktop application that simulates a **single-server banking queue**. It models customer arrivals and teller service times using uniform random numbers, then reports per-customer timings and overall queue performance statistics.

---

## What It Simulates

| Parameter | Default Value | Configurable? |
|-----------|--------------|---------------|
| Customers | 100 | Yes (1–1000) |
| Inter-arrival times | Uniform(1, 8) min | Yes |
| Service times | Uniform(1, 6) min | Yes |
| Servers | 1 (single teller) | No |

Random numbers between 0 and 1 are converted to actual times using:

```
Time = min + (max − min) × R
```

---

## Requirements

- **Java 17** or higher
- **Maven 3.6+**

```bash
java -version
mvn -version
```

---

## How to Run

```bash
cd "CSM Assignment"
mvn clean package
java -jar target/banking-queue-simulation-1.0.0.jar
```

Or without building a JAR:

```bash
mvn compile exec:java -Dexec.mainClass="com.banking.Main"
```

---

## Application Tabs

The app has **5 tabs** in the left sidebar:

| Tab | Purpose |
|-----|---------|
| **Guide** | In-app walkthrough and tips |
| **Configuration** | Set customer count and time bounds, then auto-run |
| **Simulation** | Manually enter/generate/import random numbers and run |
| **Results** | Per-customer simulation table |
| **Analytics** | Aggregate queue statistics |

---

## How to Use

### Quick way — Configuration tab

1. Open **Configuration** tab
2. Set number of customers, arrival bounds, and service bounds
3. Click **▶ Run Simulation** — random numbers are generated automatically
4. View results in **Results** and **Analytics** tabs

### Advanced way — Simulation tab

1. Open **Simulation** tab
2. Generate random numbers or paste your own (or import from Excel)
3. Click **Run Simulation**
4. View results in **Results** and **Analytics** tabs

### Export & Clear

- Click **Export to Excel** in the header to save results as `.xlsx`
- Click **Clear Simulation** to reset everything and start fresh

---

## Row Colour Coding (Results Table)

| Colour | Meaning |
|--------|---------|
| Green | Customer had **no wait** (server was free on arrival) |
| Yellow | Customer **waited** before being served |

---

## Output Explained

### Results table columns

| Column | Meaning |
|--------|---------|
| Customer # | Sequence number |
| IAT (min) | Inter-arrival time since previous customer |
| Arrival (min) | Clock time customer joins the queue |
| Service (min) | Time teller spends with customer |
| Start (min) | When service begins |
| Wait (min) | Time spent waiting |
| Departure (min) | When customer leaves |
| Time in System (min) | Total time from arrival to departure |

### Analytics statistics

- Average waiting, service, inter-arrival, and time-in-system
- Server utilization (busy ÷ total simulation time)
- Probability of waiting and count who waited
- Total simulation time and idle time
- Average and maximum queue length

---

## Excel File Format

### Input file (Simulation tab import)

- **Sheet 1** — IAT random numbers in column A (values 0–1)
- **Sheet 2** — Service random numbers in column A (values 0–1)

### Output file (Export)

- **Sheet 1** — Customer simulation table
- **Sheet 2** — Queue statistics summary

---

## Project Structure

```
CSM Assignment/
├── docs/                        ← place report PDFs here
│   └── report.pdf
├── src/main/java/com/banking/
│   ├── Main.java
│   ├── model/                   Customer.java, QueueStatistics.java
│   ├── simulation/              QueueSimulator.java
│   ├── excel/                   ExcelHandler.java
│   └── gui/
│       ├── MainFrame.java
│       ├── GameTheme.java
│       ├── ConfigurationPanel.java
│       ├── InputPanel.java
│       ├── TableOutputPanel.java
│       ├── StatisticsOutputPanel.java
│       └── InstructionsPanel.java
├── pom.xml
└── README.md
```

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `mvn: command not found` | Install Maven or use an IDE with built-in Maven |
| `java: command not found` | Install Java 17+ and add to PATH |
| Config error on Run | Check all fields are numeric; lower bound must be < upper bound |
| Export does nothing | Run a simulation first |
| Excel import fails | Use `.xlsx` with two sheets as described above |

---

## Authors

Kanyi Sharon Wambui · Kamau Edwin · Kariuki Rurigi Maina  
Strathmore University — Computer Simulation and Modelling, Assignment 4.1B
