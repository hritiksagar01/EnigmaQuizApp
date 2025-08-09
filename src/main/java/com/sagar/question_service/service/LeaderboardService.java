package com.sagar.question_service.service;

import com.sagar.question_service.Dao.UserDao;
import com.sagar.question_service.model.Student;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    @Autowired
    UserDao userDao;

    public ResponseEntity<byte[]> generateLeaderboardExcel() {
        List<Student> users = userDao.findAll();
        users.sort((u1, u2) -> Integer.compare(u2.getScore(), u1.getScore()));

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Leaderboard");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Rank");
            headerRow.createCell(1).setCellValue("User ID");
            headerRow.createCell(2).setCellValue("Name");
            headerRow.createCell(3).setCellValue("Score");
            headerRow.createCell(4).setCellValue("Played");

            int rowNum = 1;
            int rank = 1;
            for (Student user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rank++);
                row.createCell(1).setCellValue(user.getUniqueId());
                row.createCell(2).setCellValue(user.getName());
                row.createCell(3).setCellValue(user.getScore());
                row.createCell(4).setCellValue(user.isPlayed() ? "Yes" : "No");
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=leaderboard.xlsx");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<byte[]> generateLeaderboardExcelPLayed() {
        List<Student> users = userDao.findAll();

        List<Student> playedStudents = users.stream()
                .filter(Student::isPlayed).sorted((u1, u2) -> Integer.compare(u2.getScore(), u1.getScore())).collect(Collectors.toList());


        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Leaderboard");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Rank");
            headerRow.createCell(1).setCellValue("User ID");
            headerRow.createCell(2).setCellValue("Name");
            headerRow.createCell(3).setCellValue("Score");
            headerRow.createCell(4).setCellValue("Played");

            int rowNum = 1;
            int rank = 1;
            for (Student user : playedStudents) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rank++);
                row.createCell(1).setCellValue(user.getUniqueId());
                row.createCell(2).setCellValue(user.getName());
                row.createCell(3).setCellValue(user.getScore());
                row.createCell(4).setCellValue(user.isPlayed() ? "Yes" : "No");
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=leaderboard.xlsx");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}