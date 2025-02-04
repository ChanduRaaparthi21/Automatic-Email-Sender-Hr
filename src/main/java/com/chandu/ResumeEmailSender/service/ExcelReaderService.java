package com.chandu.ResumeEmailSender.service;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.chandu.ResumeEmailSender.model.HrDetails;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelReaderService {

    @Value("${excel.file.path}")
    private String excelFilePath;

    public List<HrDetails> readHrDetails() {
        List<HrDetails> hrDetailsList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(excelFilePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                // Adjust column indices based on your Excel structure
                String hrName = row.getCell(1).getStringCellValue(); // Column B: Name
                String hrEmail = row.getCell(2).getStringCellValue(); // Column C: Email
                String companyName = row.getCell(4).getStringCellValue(); // Column E: Company

                hrDetailsList.add(new HrDetails(hrName, hrEmail, companyName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hrDetailsList;
    }
}