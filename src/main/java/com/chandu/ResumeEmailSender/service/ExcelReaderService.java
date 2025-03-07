package com.chandu.ResumeEmailSender.service;

import com.chandu.ResumeEmailSender.model.HrDetails;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelReaderService {
    private static final Logger logger = LoggerFactory.getLogger(ExcelReaderService.class);

    @Value("${excel.file.path}")
    private String filePath;

    public List<HrDetails> readHrDetails() {
        List<HrDetails> hrDetailsList = new ArrayList<>();

        logger.info("Reading HR details from Excel file: {}", filePath);

        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getPhysicalNumberOfRows();

            logger.info("Total Rows Found: {}", totalRows);

            // Validate at least one data row exists
            if (totalRows <= 1) {
                logger.warn("Excel file has no data rows (only header)");
                return hrDetailsList;
            }

            for (int i = 1; i < totalRows; i++) { // Start from row 1 (skip header)
                Row row = sheet.getRow(i);
                if (row == null) {
                    logger.debug("Skipped empty row at index {}", i);
                    continue;
                }

                String hrName = getCellValue(row, 1); // Column B
                String hrEmail = getCellValue(row, 2).trim(); // Column C (Email)
                String companyName = getCellValue(row, 4); // Column E

                if (hrEmail.isEmpty() || !isValidEmail(hrEmail)) {
                    logger.warn("Skipping invalid email at row {}: {} | Email: {}", i, hrName, hrEmail);
                    continue;
                }

                logger.debug("Processing row {}: {} | Email: {} | Company: {}", i, hrName, hrEmail, companyName);
                hrDetailsList.add(new HrDetails(hrName, hrEmail, companyName));
            }

            logger.info("Successfully read {} valid HR contacts from Excel", hrDetailsList.size());

        } catch (IOException e) {
            logger.error("Error reading Excel file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read HR details from Excel file", e);
        }

        return hrDetailsList;
    }

    private String getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";

        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();
                case NUMERIC:
                    return String.valueOf((long) cell.getNumericCellValue());
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    try {
                        return cell.getStringCellValue();
                    } catch (Exception e) {
                        try {
                            return String.valueOf((long) cell.getNumericCellValue());
                        } catch (Exception ex) {
                            return "";
                        }
                    }
                default:
                    return "";
            }
        } catch (Exception e) {
            logger.warn("Error reading cell [{}]: {}", columnIndex, e.getMessage());
            return "";
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }
}