package com.chandu.ResumeEmailSender.service;

import com.chandu.ResumeEmailSender.model.HrDetails;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelReaderService {

    @Value("${excel.file.path}")
    private String filePath; // Load file path from application.properties

    public List<HrDetails> readHrDetails() {
        List<HrDetails> hrDetailsList = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getPhysicalNumberOfRows();

            System.out.println("Total Rows Found: " + totalRows);

            for (int i = 1; i < totalRows; i++) { // Start from row 1 (skip header)
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String hrName = getCellValue(row, 1); // Column B
                String hrEmail = getCellValue(row, 2).trim(); // Column C (Email)
                String companyName = getCellValue(row, 4); // Column E

                if (hrEmail.isEmpty() || !isValidEmail(hrEmail)) {
                    System.out.println("Skipping invalid email: " + hrName + " | Email: " + hrEmail);
                    continue;
                }

                System.out.println("Processing: " + hrName + " | Email: " + hrEmail + " | Company: " + companyName);
                hrDetailsList.add(new HrDetails(hrName, hrEmail, companyName));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return hrDetailsList;
    }

    private String getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue()); // Convert numeric to string
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }
}
