package com.banking.gui;

import com.banking.simulation.QueueSimulator;

import javax.swing.*;
import java.awt.*;

/**
 * In-app guide for running the simulation and interpreting results.
 */
public class InstructionsPanel extends JPanel {

    public InstructionsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("How to Use This Simulation", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JTextArea instructions = new JTextArea();
        instructions.setEditable(false);
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        instructions.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        instructions.setMargin(new Insets(10, 10, 10, 10));
        instructions.setText(buildInstructions());

        JScrollPane scrollPane = new JScrollPane(instructions);
        scrollPane.setBorder(BorderFactory.createTitledBorder("User Guide & Lessons Learned"));

        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private static String buildInstructions() {
        int customerCount = QueueSimulator.CUSTOMER_COUNT;
        return """
                WHAT THIS APP SIMULATES
                -----------------------
                This models a single bank teller serving customers one at a time (a single-server queue).
                For each of %d customers, the app:
                  - Draws an inter-arrival time from Uniform(1, 8) minutes
                  - Draws a service time from Uniform(1, 6) minutes
                  - Tracks when each customer arrives, waits, starts service, and leaves

                STEP-BY-STEP: HOW TO RUN A SIMULATION
                -----------------------------------
                1. Go to the "Input" tab.
                2. Provide two datasets of exactly 100 random numbers each (values between 0 and 1):
                   - Dataset 1: used to generate inter-arrival times (IAT)
                   - Dataset 2: used to generate service times
                   You can:
                   - Click "Generate Random Numbers" to fill both datasets automatically
                   - Type or paste numbers manually (one per line, or comma/space separated)
                   - Click "Load from Excel" to import from a .xlsx file (see README for format)
                3. Click "Run Simulation".
                4. Open "Output 1: Table" to see each customer's timing details.
                5. Open "Output 2: Statistics" to see summary queue performance metrics.
                6. Optionally click "Export Results to Excel" at the bottom to save both outputs.

                UNDERSTANDING THE OUTPUT TABLE (Output 1)
                -----------------------------------------
                - Customer #: customer sequence in the simulation
                - IAT (min): time since the previous customer arrived
                - Arrival (min): clock time when the customer joins the queue
                - Service (min): how long the teller spends with this customer
                - Start (min): when service actually begins (after any wait)
                - Wait (min): time spent waiting before service starts
                - Departure (min): when the customer leaves the system
                - Time in System (min): total time from arrival to departure (wait + service)

                KEY STATISTICS (Output 2)
                -------------------------
                - Average Waiting Time: how long customers wait on average
                - Server Utilization: fraction of time the teller is busy (busy / total simulation time)
                - Probability of Waiting: share of customers who had to wait (wait > 0)
                - Average Queue Length: average number of customers waiting over the simulation
                - Maximum Queue Length: worst-case backlog observed

                LESSONS LEARNED FROM QUEUE SIMULATION
                ------------------------------------
                1. Arrival rate vs. service rate drives congestion
                   When customers arrive faster than they can be served, queues build up.
                   Here, average IAT is about 4.5 min while average service is about 3.5 min -
                   close enough that waiting still occurs regularly.

                2. A single server creates bottlenecks
                   With only one teller, any burst of arrivals forces later customers to wait,
                   even if average demand seems manageable.

                3. Utilization near 100%% means long waits
                   High server utilization looks efficient, but it usually increases waiting time
                   and queue length. Banks often add staff before utilization gets too high.

                4. Variability matters as much as averages
                   Two runs with the same average times can produce very different queues because
                   random variation in arrivals and service times creates unpredictable peaks.

                5. Waiting time affects customer experience
                   Time in system = waiting time + service time. Even short service times feel
                   poor if waiting is long - a key insight for service design.

                6. Simulation helps test decisions safely
                   Before hiring more tellers or changing hours, you can model "what if" scenarios
                   by changing arrival/service patterns and comparing statistics - without
                   disrupting real customers.

                TIP: Run the simulation multiple times with different random numbers and compare
                Output 2 statistics. Notice how wait times and queue lengths change even though
                the underlying distributions stay the same.
                """.formatted(customerCount);
    }
}
