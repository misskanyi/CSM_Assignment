package com.banking.excel;

import com.banking.model.Customer;
import com.banking.model.QueueStatistics;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading input random numbers from Excel and exporting simulation results.
 */
public class ExcelHandler {

    /**
     * Reads random numbers from an Excel file.
     * Expects column A to contain values (skipping header row if present).
     */
    public static double[] readRandomNumbers(String filePath, int expectedCount) throws IOException {
        List<Double> values = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Cell cell = row.getCell(0);
                if (cell == null) {
                    continue;
                }

                double value = getNumericCellValue(cell);
                if (value >= 0 && value <= 1) {
                    values.add(value);
                }
            }
        }

        if (values.size() < expectedCount) {
            throw new IOException("Expected at least " + expectedCount + " random numbers, found " + values.size());
        }

        double[] result = new double[expectedCount];
        for (int i = 0; i < expectedCount; i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    /**
     * Reads two datasets from an Excel file with two sheets:
     * Sheet 1: Inter-arrival random numbers
     * Sheet 2: Service time random numbers
     */
    public static double[][] readTwoDatasets(String filePath, int expectedCount) throws IOException {
        double[] iatRandoms;
        double[] serviceRandoms;

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            iatRandoms = readSheet(workbook.getSheetAt(0), expectedCount);
            serviceRandoms = workbook.getNumberOfSheets() > 1
                    ? readSheet(workbook.getSheetAt(1), expectedCount)
                    : new double[expectedCount];
        }

        return new double[][]{iatRandoms, serviceRandoms};
    }

    private static double[] readSheet(Sheet sheet, int expectedCount) throws IOException {
        List<Double> values = new ArrayList<>();
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            Cell cell = row.getCell(0);
            if (cell == null) {
                continue;
            }
            double value = getNumericCellValue(cell);
            if (value >= 0 && value <= 1) {
                values.add(value);
            }
        }

        if (values.size() < expectedCount) {
            throw new IOException("Sheet '" + sheet.getSheetName() + "' needs at least "
                    + expectedCount + " values, found " + values.size());
        }

        double[] result = new double[expectedCount];
        for (int i = 0; i < expectedCount; i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    private static double getNumericCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    yield -1;
                }
            }
            default -> -1;
        };
    }

    /**
     * Exports simulation results to an Excel file with two sheets:
     * Sheet 1: Customer table
     * Sheet 2: Queue statistics
     */
    public static void exportResults(String filePath, List<Customer> customers,
                                     QueueStatistics stats) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            createCustomerTableSheet(workbook, customers);
            createStatisticsSheet(workbook, stats);

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
    }

    private static void createCustomerTableSheet(Workbook workbook, List<Customer> customers) {
        Sheet sheet = workbook.createSheet("Customer Table");
        CellStyle headerStyle = createHeaderStyle(workbook);

        String[] headers = {
                "Customer #", "Inter-Arrival Time", "Arrival Time", "Service Time",
                "Service Start", "Waiting Time", "Departure Time", "Time in System"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (Customer c : customers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(c.getCustomerNumber());
            row.createCell(1).setCellValue(round(c.getInterArrivalTime()));
            row.createCell(2).setCellValue(round(c.getArrivalTime()));
            row.createCell(3).setCellValue(round(c.getServiceTime()));
            row.createCell(4).setCellValue(round(c.getServiceStartTime()));
            row.createCell(5).setCellValue(round(c.getWaitingTime()));
            row.createCell(6).setCellValue(round(c.getDepartureTime()));
            row.createCell(7).setCellValue(round(c.getTimeInSystem()));
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static void createStatisticsSheet(Workbook workbook, QueueStatistics stats) {
        Sheet sheet = workbook.createSheet("Queue Statistics");
        CellStyle headerStyle = createHeaderStyle(workbook);

        String[][] data = {
                {"Statistic", "Value"},
                {"Total Customers", String.valueOf(stats.getTotalCustomers())},
                {"Average Waiting Time (min)", format(stats.getAverageWaitingTime())},
                {"Average Service Time (min)", format(stats.getAverageServiceTime())},
                {"Average Inter-Arrival Time (min)", format(stats.getAverageInterArrivalTime())},
                {"Average Time in System (min)", format(stats.getAverageTimeInSystem())},
                {"Server Utilization", formatPercent(stats.getServerUtilization())},
                {"Probability of Waiting", formatPercent(stats.getProbabilityOfWaiting())},
                {"Customers Who Waited", String.valueOf(stats.getCustomersWhoWaited())},
                {"Total Simulation Time (min)", format(stats.getTotalSimulationTime())},
                {"Total Idle Time (min)", format(stats.getTotalIdleTime())},
                {"Average Queue Length", format(stats.getAverageQueueLength())},
                {"Maximum Queue Length", String.valueOf(stats.getMaxQueueLength())}
        };

        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < data[i].length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(data[i][j]);
                if (i == 0) {
                    cell.setCellStyle(headerStyle);
                }
            }
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    /**
     * Creates a template Excel file with two sheets for input random numbers.
     */
    public static void createInputTemplate(String filePath, double[] iatRandoms,
                                           double[] serviceRandoms) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet iatSheet = workbook.createSheet("IAT Random Numbers");
            Sheet serviceSheet = workbook.createSheet("Service Random Numbers");

            writeRandomColumn(iatSheet, "R (IAT)", iatRandoms);
            writeRandomColumn(serviceSheet, "R (Service)", serviceRandoms);

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
    }

    private static void writeRandomColumn(Sheet sheet, String header, double[] values) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue(header);

        for (int i = 0; i < values.length; i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(values[i]);
        }
        sheet.autoSizeColumn(0);
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static String format(double value) {
        return String.format("%.2f", value);
    }

    private static String formatPercent(double value) {
        return String.format("%.2f%%", value * 100);
    }
}
