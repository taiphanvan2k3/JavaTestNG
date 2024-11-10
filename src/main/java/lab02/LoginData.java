package lab02;

import java.io.IOException;
import java.util.Set;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class LoginData extends TestData {
    public String email;
    public String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static void writeLog(String excelFilePath, String sheetName, Set<LoginData> logs) {
        try {
            XSSFWorkbook workbook = ExcelUtils.getWorkbook(excelFilePath);
            XSSFSheet sheet = ExcelUtils.getSheet(workbook, sheetName);

            int startRow = 0;
            int lastRow = sheet.getPhysicalNumberOfRows();
            if (lastRow < startRow) {
                lastRow = startRow;
            }

            CellStyle cellStyle = ExcelUtils.getRowStyle(workbook);

            for (LoginData log : logs) {
                Row row = sheet.createRow(lastRow);

                // Set chiều cao để dễ nhìn hơn
                row.setHeightInPoints(60);
                row.setRowStyle(cellStyle);

                log.writeDataRow(sheet, row, log);
                lastRow++;
            }

            ExcelUtils.export(workbook, excelFilePath);
            System.out.println("Export workbox successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void writeDataRow(XSSFSheet sheet, Row row, LoginData log) throws IOException {
        CellStyle globalStyle = row.getRowStyle();
        CellStyle yellowStyle = sheet.getWorkbook().createCellStyle();
        yellowStyle.cloneStyleFrom(globalStyle); // Sao chép định dạng từ `globalStyle`

        // Đặt màu nền cho `yellowStyle`
        yellowStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        yellowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        yellowStyle.setBorderTop(BorderStyle.THIN);
        yellowStyle.setBorderBottom(BorderStyle.THIN);
        yellowStyle.setBorderLeft(BorderStyle.THIN);
        yellowStyle.setBorderRight(BorderStyle.THIN);

        CellStyle currentStyle = log.getStatus().equals("PASSED") ? globalStyle : yellowStyle;

        Cell cell;

        cell = row.createCell(0);
        cell.setCellValue(log.getEmail());
        cell.setCellStyle(currentStyle);

        cell = row.createCell(1);
        cell.setCellValue(log.getPassword());
        cell.setCellStyle(currentStyle);

        // startIndex: 2 vì đã có 2 cell ở trước
        super.writeTestData(sheet, row, 2, currentStyle);
    }
}
