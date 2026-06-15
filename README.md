# Banking Queue Simulation

A Java desktop application that simulates a **single-server banking queue** for 100 customers. It models customer arrivals and teller service times, then reports per-customer timings and overall queue performance statistics.

## What It Simulates

| Parameter | Value |
|-----------|-------|
| Customers | 100 |
| Inter-arrival times | Uniform(1, 8) minutes |
| Service times | Uniform(1, 6) minutes |
| Servers | 1 (single teller) |

Random numbers between 0 and 1 are converted to actual times using the inverse transform method:

```
Time = min + (max - min) × R
```

## Requirements

- **Java 17** or higher
- **Maven 3.6+** (for building)

Check your versions:

```bash
java -version
mvn -version
```

## How to Run the App

### Option 1: Build and run the JAR (recommended)

```bash
cd "CSM Assignment"
mvn clean package
java -jar target/banking-queue-simulation-1.0.0.jar
```

### Option 2: Run with Maven (no JAR needed)

```bash
cd "CSM Assignment"
mvn compile exec:java -Dexec.mainClass="com.banking.Main"
```

The application opens a window with four tabs: **How to Use**, **Input**, **Output 1: Table**, and **Output 2: Statistics**.

## How to Use the App

### 1. Read the in-app guide

Open the **How to Use** tab for step-by-step instructions and lessons learned from queue simulation.

### 2. Prepare input data (Input tab)

You need **two datasets**, each with exactly **100 random numbers** between 0 and 1:

| Dataset | Purpose | Becomes |
|---------|---------|---------|
| Dataset 1 | Inter-arrival random numbers (R) | Uniform(1, 8) minute gaps between arrivals |
| Dataset 2 | Service random numbers (R) | Uniform(1, 6) minute service times |

Ways to provide data:

- **Generate Random Numbers** — fills both datasets automatically
- **Manual entry** — type or paste numbers (one per line, or comma/space separated)
- **Load from Excel** — import from a `.xlsx` file
- **Save Excel Template** — export current (or sample) data to Excel for editing

### 3. Run the simulation

Click **Run Simulation**. A confirmation dialog appears when processing is complete.

### 4. View results

- **Output 1: Table** — per-customer details (arrival, wait, service start, departure, time in system)
- **Output 2: Statistics** — summary metrics (averages, utilization, queue length, etc.)

### 5. Export (optional)

Click **Export Results to Excel** at the bottom of the window to save both the customer table and statistics to a `.xlsx` file.

## Excel File Format

### Input file (two sheets)

- **Sheet 1** — IAT random numbers in column A (values 0–1, 100 rows)
- **Sheet 2** — Service random numbers in column A (values 0–1, 100 rows)

### Output file (two sheets)

- **Sheet 1** — Customer simulation table
- **Sheet 2** — Queue statistics summary

## Output Explained

### Customer table columns

| Column | Meaning |
|--------|---------|
| Customer # | Customer sequence (1–100) |
| IAT (min) | Inter-arrival time since previous customer |
| Arrival (min) | Clock time when customer joins the queue |
| Service (min) | Time the teller spends with this customer |
| Start (min) | When service begins (after any wait) |
| Wait (min) | Time waiting before service starts |
| Departure (min) | When customer leaves |
| Time in System (min) | Total time from arrival to departure |

### Statistics computed

- Average waiting, service, inter-arrival, and time-in-system
- Server utilization (busy time ÷ total simulation time)
- Probability of waiting and count of customers who waited
- Total simulation time and idle time
- Average and maximum queue length

## Lessons Learned from Queue Simulation

Running this model illustrates several important ideas in operations and customer service:

1. **Arrival rate vs. service rate drives congestion**  
   When customers arrive faster than they can be served, queues grow. Even when average arrival spacing (~4.5 min) is only slightly larger than average service time (~3.5 min), waiting still happens often.

2. **A single server creates bottlenecks**  
   One teller means any burst of arrivals forces later customers to wait. There is no parallel capacity to absorb peaks.

3. **High utilization increases waiting**  
   Server utilization near 100% may look efficient, but it usually means longer queues and wait times. Real banks add staff before utilization becomes excessive.

4. **Variability matters as much as averages**  
   Two simulation runs with the same distributions can produce different wait times and queue lengths because random variation creates unpredictable busy periods.

5. **Customer experience includes waiting**  
   Time in system = waiting time + service time. Short service times still feel poor if the wait is long.

6. **Simulation supports better decisions**  
   You can test staffing or process changes in a model before applying them in a real branch — comparing statistics across runs without affecting real customers.

**Try this:** Run the simulation several times with different random numbers and compare Output 2 statistics each time.

## Project Structure

```
src/main/java/com/banking/
├── Main.java
├── model/          Customer, QueueStatistics
├── simulation/     QueueSimulator
├── excel/          ExcelHandler
└── gui/            MainFrame, InputPanel, TableOutputPanel,
                    StatisticsOutputPanel, InstructionsPanel
```

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `mvn: command not found` | Install Maven or use an IDE with built-in Maven support |
| `java: command not found` | Install Java 17+ and ensure it is on your PATH |
| Input error on Run | Each dataset must have exactly 100 numbers between 0 and 1 |
| Export does nothing | Run the simulation first before exporting |
| Excel load fails | Use `.xlsx` format with two sheets as described above |
