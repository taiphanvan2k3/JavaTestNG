package dut.udn.TestNGMavenExample;

import java.io.IOException;
import java.util.Date;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class TestData {
    private String action;

    private Date logTime;

    private String testMethod;

    private String expected;

    private String actual;

    private String status;

    private String exception = null;

    private String imagePath = null;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    public String getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(String testMethod) {
        this.testMethod = testMethod;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Tiến hành ghi lần lượt các dữ liệu của TestData vào các cell của row
     * 
     * @author TaiPV
     * @since 2024/11/10
     * @param sheet      Sheet chứa dữ liệu
     * @param row        Row chứa dữ liệu
     * @param startIndex
     * @throws IOException
     */
    public void writeTestData(XSSFSheet sheet, Row row, int startIndex, CellStyle currentStyle) throws IOException {
        CreationHelper creationHelper = sheet.getWorkbook().getCreationHelper();
        CellStyle globalCellStyle;

        if (currentStyle == null) {
            globalCellStyle = row.getRowStyle();
        } else {
            globalCellStyle = currentStyle;
        }

        // Đối tượng để ghi dữ liệu vào cell
        Cell cell;
        cell = row.createCell(startIndex);
        cell.setCellValue(getAction());
        cell.setCellStyle(globalCellStyle);

        cell = row.createCell(startIndex + 1);
        cell.setCellValue(getLogTime());
        CellStyle datetimeStyle = globalCellStyle;
        datetimeStyle.setDataFormat(creationHelper.createDataFormat().getFormat("hh:mm:ss dd/MM/yyyy"));
        cell.setCellStyle(datetimeStyle);

        cell = row.createCell(startIndex + 2);
        cell.setCellValue(getTestMethod());
        cell.setCellStyle(globalCellStyle);

        cell = row.createCell(startIndex + 3);
        cell.setCellValue(getExpected());
        cell.setCellStyle(globalCellStyle);

        cell = row.createCell(startIndex + 4);
        cell.setCellValue(getActual());
        cell.setCellStyle(globalCellStyle);

        cell = row.createCell(startIndex + 5);
        cell.setCellValue(getStatus());
        cell.setCellStyle(globalCellStyle);

        if (getException() != null) {
            cell = row.createCell(startIndex + 6);
            cell.setCellValue(getException());
            cell.setCellStyle(globalCellStyle);
        }

        if (getImagePath() != null) {
            cell = row.createCell(startIndex + 7);
            cell.setCellValue(getImagePath());
            ExcelUtils.writeImage(getImagePath(), sheet, cell);

            cell = row.createCell(startIndex + 8);
            cell.setCellValue("Click here to view image");
            cell.setCellStyle(globalCellStyle);

            XSSFHyperlink hyperlink = (XSSFHyperlink) creationHelper.createHyperlink(HyperlinkType.URL);
            hyperlink.setAddress(getImagePath().replace("\\", "/"));
            cell.setHyperlink(hyperlink);
        }
    }
}
