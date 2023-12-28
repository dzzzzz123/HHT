package ext.plm.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
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
import org.apache.zookeeper.common.StringUtils;

import ext.plm.change.CompareMessage;

public class TestE {

	public static void main(String[] args) {
		String ecnNum = "ECA123";
		String version = "2";
		List<CompareMessage> messList = new ArrayList<CompareMessage>();
		CompareMessage mess = new CompareMessage();
		mess.setFatherPartNumber("11111");
		mess.setFatherPartDesc("感光透镜_B19");
		mess.setFatherPartVer_old("A");
		mess.setChangeType( "移除用料" );
		mess.setSonPartNumber_old( "t001" );
		mess.setSonPartDesc_old("感光透镜_B19_无_件(PC)_无_透");
		mess.setQuantity_old("11");
		messList.add( mess );
		
		mess = new CompareMessage();
		mess.setFatherPartNumber("2222222");
		mess.setFatherPartDesc("XX_B19");
		mess.setFatherPartVer_old("C");
		mess.setFatherPartVer_new("d");
		mess.setChangeType( "更改数量" );
		mess.setSonPartNumber_old( "t002" );
		mess.setSonPartDesc_old("感光透镜_B19333333333");
		mess.setSonPartNumber_new( "t002" );
		mess.setSonPartDesc_new("感光透镜_B19333333333");
		mess.setQuantity_old("11");
		mess.setQuantity_new("12");
		mess.setAtt_old("DDD");
		mess.setAtt_new("ad d dd的");
		messList.add( mess );
		
		mess = new CompareMessage();
		mess.setFatherPartNumber("2222222");
		mess.setFatherPartDesc("XX_B19");
		mess.setFatherPartVer_old("C");
		mess.setFatherPartVer_new("d");
		mess.setChangeType( "增加数量" );
		mess.setSonPartNumber_new( "t002111111" );
		mess.setSonPartDesc_new("感光dddddddddddd");
		mess.setQuantity_old("11");
		mess.setQuantity_new("12");
		mess.setAtt_old("DDD");
		mess.setAtt_new("ad d dd的");
		messList.add( mess );
		
		writeMess2Excel(ecnNum,version,messList);
	}
	
	public static void writeMess2Excel(String ecaNum,String version,List<CompareMessage> messList) {
		OutputStream os = null;
		try {
			String filePath = "D:\\Desktop\\hh\\";
			String temp = "template.xlsx";//Excel模板
			FileInputStream fileinputstream = new FileInputStream(filePath+temp);
			XSSFWorkbook xssfwb = new XSSFWorkbook( fileinputstream );
			XSSFSheet sheet = xssfwb.getSheetAt(0);
			XSSFCellStyle style = getLeftStyle(xssfwb, false);
			XSSFRow xrow = null;
			
			String ecnNum = "ECN-002";
			String ecnName = "名称xxx";
			String ecnCt = "2023-12-07";
			String ecnDesc = "产品DVT验证完成，需要到PVT状态，进行小批生产和验证。不涉及到历史物理的处理和图纸变更；需要创建新版本的MBOM；";
			
			xrow = sheet.getRow(1);
			if(xrow==null){
				xrow = sheet.createRow(1);
			}
			writeToCell(sheet,style,xrow,4,ecnNum);
			writeToCell(sheet,style,xrow,10,ecnName);
			writeToCell(sheet,style,xrow,16,ecnCt);
			xrow = sheet.getRow(2);
			if(xrow==null){
				xrow = sheet.createRow(2);
			}
			writeToCell(sheet,style,xrow,4,ecnDesc);
			
			int count = 1;
			int row = 5;
			for(CompareMessage mess : messList){
				xrow = sheet.createRow(row);
				writeToCell(sheet,style,xrow,0,count+"");
				writeToCell(sheet,style,xrow,1,mess.getGroup());
				writeToCell(sheet,style,xrow,2,mess.getChangeType());
				writeToCell(sheet,style,xrow,3,mess.getFatherPartNumber());
				writeToCell(sheet,style,xrow,4,mess.getSonPartNumber_old());
				writeToCell(sheet,style,xrow,5,mess.getSonPartDesc_old());
				writeToCell(sheet,style,xrow,6,mess.getFatherPartVer_old());
				writeToCell(sheet,style,xrow,7,mess.getQuantity_old());
				writeToCell(sheet,style,xrow,8,mess.getAtt_old());
				writeToCell(sheet,style,xrow,9,mess.getSonPartNumber_new());
				writeToCell(sheet,style,xrow,10,mess.getSonPartDesc_new());
				writeToCell(sheet,style,xrow,11,mess.getFatherPartVer_new());
				writeToCell(sheet,style,xrow,12,mess.getQuantity_new());
				writeToCell(sheet,style,xrow,13,mess.getAtt_new());
				
				writeToCell(sheet,style,xrow,14,"");
				writeToCell(sheet,style,xrow,15,"");
				writeToCell(sheet,style,xrow,16,"");
				writeToCell(sheet,style,xrow,17,"");
				
				row++;
				count++;
			}
			row++;
			
			//写入签名行
			writeView(xssfwb,sheet,row);
			
			//TODO 文件名称
			String fileName = ecaNum+"变更通知单_V"+version+".xlsx";
			os = new FileOutputStream(filePath+fileName);
			xssfwb.write(os);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    }
	}
	
	private static void writeView(XSSFWorkbook xssfwb,XSSFSheet sheet ,int row){
		//签名
		XSSFCellStyle style = getCenterStyle(xssfwb, true);
		String viewStr = "发起人;项目;产品;策采;计划;批准;SAP";
		String[] views = viewStr.split(";");
		XSSFRow xrow = sheet.createRow(row);
		writeNullRow(sheet,style,xrow);
		writeToCell(sheet,style,xrow,0,"");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 0, 1));
		writeToCell(sheet,style,xrow,2,views[0]);
		sheet.addMergedRegion(new CellRangeAddress(row, row, 2, 3));
		writeToCell(sheet,style,xrow,4,views[1]);
		sheet.addMergedRegion(new CellRangeAddress(row, row, 4, 5));
		writeToCell(sheet,style,xrow,6,views[2]);
		sheet.addMergedRegion(new CellRangeAddress(row, row, 6, 7));
		writeToCell(sheet,style,xrow,8,views[3]);
		sheet.addMergedRegion(new CellRangeAddress(row, row, 8, 9));
		writeToCell(sheet,style,xrow,10,views[4]);
		sheet.addMergedRegion(new CellRangeAddress(row, row, 10, 11));
		writeToCell(sheet,style,xrow,12,views[5]);
		sheet.addMergedRegion(new CellRangeAddress(row, row, 12, 13));
		writeToCell(sheet,style,xrow,14,views[6]);
		sheet.addMergedRegion(new CellRangeAddress(row, row, 14, 17));
		
		row++;
		xrow = sheet.createRow(row);
		writeNullRow(sheet,style,xrow);
		writeToCell(sheet,style,xrow,0,"签名：");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 0, 1));
		writeToCell(sheet,style,xrow,2,"");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 2, 3));
		writeToCell(sheet,style,xrow,4,"");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 4, 5));
		writeToCell(sheet,style,xrow,6,"");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 6, 7));
		writeToCell(sheet,style,xrow,8,"");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 8, 9));
		writeToCell(sheet,style,xrow,10,"");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 10, 11));
		writeToCell(sheet,style,xrow,12,"");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 12, 13));
		writeToCell(sheet,style,xrow,14,"");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 14, 17));
		
		row++;
		xrow = sheet.createRow(row);
		writeNullRow(sheet,style,xrow);
		writeToCell(sheet,style,xrow,0,"日期：");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 0, 1));
		writeToCell(sheet,style,xrow,2,"");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 2, 3));
		writeToCell(sheet,style,xrow,4,"");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 4, 5));
		writeToCell(sheet,style,xrow,6,"");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 6, 7));
		writeToCell(sheet,style,xrow,8,"");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 8, 9));
		writeToCell(sheet,style,xrow,10,"");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 10, 11));
		writeToCell(sheet,style,xrow,12,"");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 12, 13));
		writeToCell(sheet,style,xrow,14,"");
		sheet.addMergedRegion(new CellRangeAddress(row, row, 14, 17));
		
	}
	
	private static void writeNullRow(XSSFSheet sheet, XSSFCellStyle dataStyle,XSSFRow xlsrow){
		//TODO 列数
		int allCol = 18;
		for(int i=0;i<allCol;i++){
			writeToCell(sheet,dataStyle,xlsrow,i,"");
		}
	}
	
	public static void writeToCell(XSSFSheet sheet, XSSFCellStyle dataStyle, XSSFRow xlsrow, int c_i, String v) {
		try {
			if(StringUtils.isBlank(v)){
				v="";
			}
			XSSFCell cell = xlsrow.getCell(c_i);
			if(cell==null){
				cell = xlsrow.createCell(c_i);
			}
			xlsrow.setHeightInPoints( 22.5f );
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
		font.setFontHeightInPoints((short)10);
		font.setFontName("宋体");
		if (bold) {//加粗
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
		font.setFontHeightInPoints((short)10);
		font.setFontName("宋体");
		if (bold) {//加粗
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
	
}
