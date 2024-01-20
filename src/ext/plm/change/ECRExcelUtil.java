package ext.plm.change;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ext.plm.util.CommUtil;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;

public class ECRExcelUtil {

	public static String WT_HOME = null;
	static {
		try {
			WTProperties wtproperties = WTProperties.getLocalProperties();
			WT_HOME = wtproperties.getProperty("wt.home", "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取模板文件 @return String @throws
	 */
	private static String getTemplateFile() {
		String strTemplate = WT_HOME + File.separatorChar + "codebase" + File.separatorChar + "ext" + File.separatorChar
				+ "plm" + File.separatorChar + "change" + File.separatorChar + "model" + File.separatorChar
				+ "template.xlsx";
		File tmpFile = new File(strTemplate);
		if (!(tmpFile.exists())) {
			System.out.println(strTemplate + " 文件不存在!!");
		}
		return strTemplate;
	}

	public static String writeInfo2Excel(WTChangeOrder2 ecn, String ecaNum, String fileName,
			List<CompareMessage> messList) {
		String outFilePath = "";
		OutputStream os = null;
		try {
			String filePath = getTemplateFile();
			FileInputStream fileinputstream = new FileInputStream(filePath);
			XSSFWorkbook xssfwb = new XSSFWorkbook(fileinputstream);
			XSSFSheet sheet = xssfwb.getSheetAt(0);
			XSSFCellStyle style = getLeftStyle(xssfwb, false);
			XSSFRow xrow = null;

			String ecnNum = ecn.getNumber();
			String ecnName = ecn.getName();
			String ecnCt = CommUtil.getFormatDate(ecn.getCreateTimestamp(), "");
			String ecnDesc = ecn.getDescription();

			xrow = sheet.getRow(1);
			if (xrow == null) {
				xrow = sheet.createRow(1);
			}
			writeToCell(sheet, style, xrow, 4, ecnNum);
			writeToCell(sheet, style, xrow, 10, ecnName);
			writeToCell(sheet, style, xrow, 16, ecnCt);
			xrow = sheet.getRow(2);
			if (xrow == null) {
				xrow = sheet.createRow(2);
			}
			writeToCell(sheet, style, xrow, 4, ecnDesc);

			int count = 1;
			int row = 5;
			for (CompareMessage mess : messList) {
				xrow = sheet.createRow(row);
				writeToCell(sheet, style, xrow, 0, count + "");
				writeToCell(sheet, style, xrow, 1, mess.getGroup());
				writeToCell(sheet, style, xrow, 2, mess.getChangeType());
				writeToCell(sheet, style, xrow, 3, mess.getFatherPartNumber());
				writeToCell(sheet, style, xrow, 4, mess.getSonPartNumber_old());
				writeToCell(sheet, style, xrow, 5, mess.getSonPartDesc_old());
				writeToCell(sheet, style, xrow, 6, mess.getFatherPartVer_old());
				writeToCell(sheet, style, xrow, 7, mess.getQuantity_old());
				writeToCell(sheet, style, xrow, 8, mess.getAtt_old());
				writeToCell(sheet, style, xrow, 9, mess.getSonPartNumber_new());
				writeToCell(sheet, style, xrow, 10, mess.getSonPartDesc_new());
				writeToCell(sheet, style, xrow, 11, mess.getFatherPartVer_new());
				writeToCell(sheet, style, xrow, 12, mess.getQuantity_new());
				writeToCell(sheet, style, xrow, 13, mess.getAtt_new());

				writeToCell(sheet, style, xrow, 14, mess.getZzView());
				writeToCell(sheet, style, xrow, 15, mess.getZtView());
				writeToCell(sheet, style, xrow, 16, mess.getKcView());
				writeToCell(sheet, style, xrow, 17, mess.getRemark());

				row++;
				count++;
			}
			row++;

			// 写入签名行
			writeView(xssfwb, sheet, row);

			// TODO 文件名称
//			String fileName = ecaNum+"_变更通知单_V"+version+".xlsx";
			String out = WT_HOME + File.separatorChar + "codebase" + File.separatorChar + "temp" + File.separatorChar;
			File dir = new File(out);
			if (!dir.exists()) {
				dir.mkdir();
			}
			outFilePath = out + fileName;
			os = new FileOutputStream(outFilePath);
			xssfwb.write(os);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return outFilePath;
	}

	private static void writeView(XSSFWorkbook xssfwb, XSSFSheet sheet, int row) {
		// 签名
		XSSFCellStyle style = getCenterStyle(xssfwb, true);
		String viewStr = "发起人;项目;产品;策采;计划;批准;SAP";
		String[] views = viewStr.split(";");
		XSSFRow xrow = sheet.createRow(row);
		writeNullRow(sheet, style, xrow);
		writeToCell(sheet, style, xrow, 0, "");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 0, 1));
		writeToCell(sheet, style, xrow, 2, views[0]);
		sheet.addMergedRegion(new CellRangeAddress(row, row, 2, 3));
		writeToCell(sheet, style, xrow, 4, views[1]);
		sheet.addMergedRegion(new CellRangeAddress(row, row, 4, 5));
		writeToCell(sheet, style, xrow, 6, views[2]);
		sheet.addMergedRegion(new CellRangeAddress(row, row, 6, 7));
		writeToCell(sheet, style, xrow, 8, views[3]);
		sheet.addMergedRegion(new CellRangeAddress(row, row, 8, 9));
		writeToCell(sheet, style, xrow, 10, views[4]);
		sheet.addMergedRegion(new CellRangeAddress(row, row, 10, 11));
		writeToCell(sheet, style, xrow, 12, views[5]);
		sheet.addMergedRegion(new CellRangeAddress(row, row, 12, 13));
		writeToCell(sheet, style, xrow, 14, views[6]);
		sheet.addMergedRegion(new CellRangeAddress(row, row, 14, 17));

		row++;
		xrow = sheet.createRow(row);
		writeNullRow(sheet, style, xrow);
		writeToCell(sheet, style, xrow, 0, "签名：");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 0, 1));
		writeToCell(sheet, style, xrow, 2, "");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 2, 3));
		writeToCell(sheet, style, xrow, 4, "");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 4, 5));
		writeToCell(sheet, style, xrow, 6, "");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 6, 7));
		writeToCell(sheet, style, xrow, 8, "");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 8, 9));
		writeToCell(sheet, style, xrow, 10, "");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 10, 11));
		writeToCell(sheet, style, xrow, 12, "");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 12, 13));
		writeToCell(sheet, style, xrow, 14, "");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 14, 17));

		row++;
		xrow = sheet.createRow(row);
		writeNullRow(sheet, style, xrow);
		writeToCell(sheet, style, xrow, 0, "日期：");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 0, 1));
		writeToCell(sheet, style, xrow, 2, "");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 2, 3));
		writeToCell(sheet, style, xrow, 4, "");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 4, 5));
		writeToCell(sheet, style, xrow, 6, "");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 6, 7));
		writeToCell(sheet, style, xrow, 8, "");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 8, 9));
		writeToCell(sheet, style, xrow, 10, "");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 10, 11));
		writeToCell(sheet, style, xrow, 12, "");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 12, 13));
		writeToCell(sheet, style, xrow, 14, "");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 14, 17));

	}

	private static void writeNullRow(XSSFSheet sheet, XSSFCellStyle dataStyle, XSSFRow xlsrow) {
		// TODO 列数
		int allCol = 18;
		for (int i = 0; i < allCol; i++) {
			writeToCell(sheet, dataStyle, xlsrow, i, "");
		}
	}

	public static void writeToCell(XSSFSheet sheet, XSSFCellStyle dataStyle, XSSFRow xlsrow, int c_i, String v) {
		try {
			if (StringUtils.isBlank(v)) {
				v = "";
			}
			XSSFCell cell = xlsrow.getCell(c_i);
			if (cell == null) {
				cell = xlsrow.createCell(c_i);
			}
			xlsrow.setHeightInPoints(22.5f);
			cell.setCellType(CellType.STRING);
			cell.setCellStyle(dataStyle);
			cell.setCellValue(v);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static XSSFCellStyle getLeftStyle(XSSFWorkbook xssfwb, boolean bold) {
		XSSFCellStyle cellStyle = xssfwb.createCellStyle();
		// 字体
		XSSFFont font = xssfwb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setFontName("宋体");
		if (bold) {// 加粗
			font.setBold(true);
		}
		cellStyle.setFont(font);
		// 边框
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		// 对齐
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 自动换行
		cellStyle.setWrapText(true);
		return cellStyle;
	}

	private static XSSFCellStyle getCenterStyle(XSSFWorkbook xssfwb, boolean bold) {
		XSSFCellStyle cellStyle = xssfwb.createCellStyle();
		// 字体
		XSSFFont font = xssfwb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setFontName("宋体");
		if (bold) {// 加粗
			font.setBold(true);
		}
		cellStyle.setFont(font);
		// 边框
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		// 对齐
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 自动换行
		cellStyle.setWrapText(true);
		return cellStyle;
	}

	/**
	 * 获取ECA最新更改单的更改信息 任务表单jsp调用
	 * 
	 * @param eca
	 */
	public static List<Object[]> getEcaFileMsg(WTChangeActivity2 eca) {
		try {
			ApplicationData app = ChangeUtil.getEcaAppData(eca);
			if (app == null) {
				return null;
			}
			InputStream inputStream = ContentServerHelper.service.findContentStream(app);
			XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
			List<Object[]> dataList = getDataFromExcel(workbook, 0, 5, 18);
			return dataList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<Object[]> getDataFromExcel(XSSFWorkbook workbook, int sheetIndex, int startRowIndex,
			int cellCount) throws WTException {
		List<Object[]> data = new ArrayList<Object[]>();
		if (workbook == null || sheetIndex < 0) {
			return data;
		}
		XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
		if (null == sheet) {
			return data;
		}
		int rowIndex = startRowIndex;
		while (rowIndex <= sheet.getLastRowNum()) {
			XSSFRow row = sheet.getRow(rowIndex++);
			if (row == null || row.getCell(0) == null) {
				XSSFCell cell = row.getCell(0);
				String val = getCellValueByCell(cell);
				if (StringUtils.isBlank(val)) {
					break;
				}
			}
			Object[] rowData = new Object[cellCount];
			for (int i = 0; i < rowData.length; i++) {
				XSSFCell cell = row.getCell(i);
				rowData[i] = getCellValueByCell(cell);

			}
			data.add(rowData);
		}
		return data;
	}

	public static String getCellValueByCell(Cell cell) {
		// 判断是否为null或空串
		if (cell == null || cell.toString().trim().equals("")) {
			return "";
		}
		String cellValue = "";
		int cellType = cell.getCellType().getCode();
		switch (cellType) {
		case 0: // 数字
			short format = cell.getCellStyle().getDataFormat();
			SimpleDateFormat sdf = null;
			if (format == 20 || format == 32) {
				sdf = new SimpleDateFormat("HH:mm");
			} else if (format == 14 || format == 31 || format == 57 || format == 58) {
				// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
				sdf = new SimpleDateFormat("yyyy-MM-dd");
				double value = cell.getNumericCellValue();
				Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
				cellValue = sdf.format(date);
			} else {// 数字
				BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
				cellValue = bd.toPlainString();// 数值 这种用BigDecimal包装再获取plainString，可以防止获取到科学计数值
			}
			break;
		case 1: // 字符串
			cellValue = cell.getStringCellValue();
			break;
		case 4: // Boolean
			cellValue = cell.getBooleanCellValue() + "";
			break;
		case 2: // 公式
			try {
				cellValue = String.valueOf(cell.getNumericCellValue());
			} catch (Exception e) {
				cellValue = String.valueOf(cell.getRichStringCellValue());
			}
			break;
		case 3: // 空值
			cellValue = "";
			break;
		case 5: // 故障
			cellValue = "";
			break;
		default:
			cellValue = "";
			break;
		}
		return cellValue;
	}

}
