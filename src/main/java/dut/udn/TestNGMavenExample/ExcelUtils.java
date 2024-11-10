package dut.udn.TestNGMavenExample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ExcelUtils {
	private static final String CLASS_NAME = ExcelUtils.class.getName();
	public static final String DATA_SRC = System.getProperty("user.dir") + "/test_resources/";
	private static final String IMAGES_SRC = System.getProperty("user.dir") + "/test_resources/images/";

	public static XSSFWorkbook getWorkbook(String filePath) throws IOException {
		File file = new File(filePath);
		try {
			if (!file.exists()) {
				throw new IOException("File not found: " + filePath);
			}

			FileInputStream fis = new FileInputStream(file);
			XSSFWorkbook workbook = new XSSFWorkbook(fis);

			fis.close();
			return workbook;
		} catch (Exception e) {
			System.out.println(CLASS_NAME + " - getWorkbook: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Get sheet by its name
	 * 
	 * @param workbook
	 * @param sheetName
	 * @author TaiPV
	 * @since 2024-11-05
	 * @return XSSFSheet | null
	 */
	public static XSSFSheet getSheet(XSSFWorkbook workbook, String sheetName) {
		try {
			XSSFSheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				throw new Exception("Sheet not found: " + sheetName);
			}

			return sheet;
		} catch (Exception e) {
			System.out.println(CLASS_NAME + " - getSheet: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Định dạng style cho các cell trong sheet
	 * 
	 * @param workbook
	 * @return
	 */
	public static CellStyle getRowStyle(XSSFWorkbook workbook) {
		CellStyle rowStyle = workbook.createCellStyle();
		rowStyle.setWrapText(true); // Xuống dòng khi text quá dài
		rowStyle.setAlignment(HorizontalAlignment.CENTER);
		rowStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		return rowStyle;
	}

	/**
	 * Get cell value by row and column
	 * 
	 * @author TaiPV
	 * 
	 * @since 2024-11-05
	 * @param sheet the XSSFSheet from which the cell value is retrieved
	 * @param row   the index of the row (0-based)
	 * @param col   the index of the column (0-based)
	 * @return the cell value as a String
	 * 
	 */
	public static String getCellValue(XSSFSheet sheet, int row, int col) {
		XSSFCell cell = sheet.getRow(row).getCell(col);
		String returnValue;

		if (cell == null)
			return "";

		if (cell.getCellType() == CellType.STRING) {
			returnValue = cell.getStringCellValue();
		} else if (cell.getCellType() == CellType.NUMERIC) {
			// Chỉ trả về phần nguyên của số nếu nó có phần thập phân
			returnValue = String.format("%.0f", cell.getNumericCellValue());
		} else if (cell.getCellType() == CellType.BOOLEAN) {
			returnValue = String.valueOf(cell.getBooleanCellValue());
		} else {
			returnValue = "";
		}

		return returnValue;
	}

	/**
	 * Take screenshot and save it to the specified location
	 * 
	 * @author TaiPV
	 * @since 2024-11-05
	 * 
	 * @param driver    WebDriver instance
	 * @param outputSrc the location where the screenshot is saved
	 * @throws IOException if the file is not found
	 */
	public static void takeScreenshot(WebDriver driver, String outputSrc) throws IOException {
		File screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(screenShot, new File(outputSrc));
	}

	public static Object[][] readSheetData(XSSFSheet sheet) {
		int rowCount = sheet.getPhysicalNumberOfRows();
		int colCount = sheet.getRow(0).getLastCellNum();

		// Do not include the header row
		Object[][] data = new Object[rowCount - 1][colCount];
		for (int row = 1; row < rowCount; row++) {
			for (int col = 0; col < colCount; col++) {
				data[row - 1][col] = getCellValue(sheet, row, col);
			}
		}

		return data;
	}

	public static void writeImage(String imagePath, XSSFSheet sheet, Cell cell) throws IOException {
		InputStream inputStream = new FileInputStream(imagePath);
		byte[] bytes = IOUtils.toByteArray(inputStream);

		int pictureId = sheet.getWorkbook().addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_PNG);
		inputStream.close();

		XSSFDrawing drawing = sheet.createDrawingPatriarch();
		ClientAnchor anchor = new XSSFClientAnchor();

		// Định vị 4 góc của ảnh
		anchor.setRow1(cell.getRowIndex());
		anchor.setCol1(cell.getColumnIndex());
		anchor.setRow2(cell.getRowIndex() + 1);
		anchor.setCol2(cell.getColumnIndex() + 1);

		drawing.createPicture(anchor, pictureId);
	}

	public static void export(XSSFWorkbook workbook, String outputPath) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(outputPath);
		workbook.write(outputStream);
		outputStream.close();
	}

	public static void main(String[] args) {
		try {
			XSSFWorkbook workbook = getWorkbook(DATA_SRC);
			XSSFSheet sheet = getSheet(workbook, "LOGIN_DATA");

			Object[][] data = readSheetData(sheet);
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data[i].length; j++) {
					System.out.print(data[i][j] + " ");
				}
				System.out.println();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
