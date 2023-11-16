package ext.sap.Custing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import ext.ait.util.PropertiesUtil;

/**
 * 生成成本计算的pdf
 * @author Administrator
 *
 */
public class CustingPdfServlet implements Controller{
	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 从请求中获取JSON数据
		BufferedReader reader = request.getReader();
		StringBuilder jsonBuilder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
		jsonBuilder.append(line);
		}
		String jsonData = jsonBuilder.toString();
		ObjectMapper objectMapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		List<List<String>> list = objectMapper.readValue(jsonData,List.class);
		String path = properties.getValueByKey("pdf.local.path");
		File file = generatePdf(path,path+"\\"+System.currentTimeMillis()+".pdf","","",list);
        FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
		return null;
	}
	
     
	  /**
		 * 生成结算单pdf
		 *
		 * @param folderName  生成文件的文件夹名称
		 * @param fileName    生成文件的全路径文件名
		 * @param titleName   文件内容标题
		 * @param contentName 文件内容
		 * @param data        文件表格数据
		 * @return
		 */
		public static File generatePdf(String folderName, String fileName, String titleName, String contentName,List<List<String>> data) {
			try {
				//页面大小
				Rectangle rect = new Rectangle(PageSize.A4);
				//创建文档对象
				Document document = new Document(rect, 60, 60, 30, 30);
				File folderFile = new File(folderName);
				if (!folderFile.exists()) {
					folderFile.mkdirs();// 如果不存在，创建目录
				}
				//设置输出流
				PdfWriter.getInstance(document, new FileOutputStream(fileName));

				document.open();

				// 本地调试用这个字体
				BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

				Font fontTitle = new Font(bfChinese, 18, Font.BOLD);
				Font fontContent = new Font(bfChinese, 10, Font.NORMAL);

				Paragraph title = new Paragraph(titleName, fontTitle);
				//居中
				title.setAlignment(1);
				document.add(title);

				// 空一行
				Paragraph emptyRow = new Paragraph(10f, " ", fontContent);
				document.add(emptyRow);

				Paragraph content = new Paragraph(contentName, fontContent);
				document.add(content);

				// 空一行
				document.add(emptyRow);

				int columnNum = 7;
				PdfPTable headerTable = new PdfPTable(columnNum);
				headerTable.setWidthPercentage(100);
				int headerwidths[] = {5, 8, 10, 5, 5,5,5};
				headerTable.setWidths(headerwidths);

				// 构建表格头 （根据需求修改，也可作为入参传进来）
				List<String> headList = new ArrayList<>();
				headList.add("父编号");
				headList.add("编号");
				headList.add("名称");
				headList.add("版本");
				headList.add("状态");
				headList.add("数量");
				headList.add("总价");
				for (int i = 0; i < headList.size(); i++) {
					createTableCell(headList.get(i), fontContent, headerTable,false);
				}

				// 外层循环构建行，内层循环构建列
				for (int i = 0; i < data.size()-1; i++) {
					List<String> hList = data.get(i);
					for (int j = 0; j < hList.size(); j++) {
						createTableCell(data.get(i).get(j), fontContent, headerTable,false);
					}
				}

				// 构建表格尾（根据需求修改，也可作为入参传进来）
				List<String> endList = new ArrayList<>();
				endList.add("合计：");
				List<String> lastList = data.get(data.size() -1);
				endList.add(lastList.get(lastList.size() -1));
				for (int i = 0; i < endList.size(); i++) {
					createTableCell(endList.get(i), fontContent, headerTable,true);
				}

				document.add(headerTable);

				document.close();
				File file = new File(fileName);
				return file;
			} catch (DocumentException e) {
				throw new RuntimeException(e);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * 创建表格单元格
		 *
		 * @param content     内容
		 * @param fontContent 字体
		 * @param headerTable 表格对象
		 */
		private static void createTableCell(String content, Font fontContent, PdfPTable headerTable,boolean isHw) {
			Phrase phrase = new Phrase(content, fontContent);
			PdfPCell pdfPCell = new PdfPCell(phrase);
			pdfPCell.setFixedHeight(18);
			pdfPCell.setHorizontalAlignment(1);
			pdfPCell.setVerticalAlignment(1);
			if(isHw) {
				pdfPCell.setColspan(6);
				if("合计：".equals(content)) {
					pdfPCell.setVerticalAlignment(2);
					pdfPCell.setHorizontalAlignment(2);
				}
			}
			headerTable.addCell(pdfPCell);
		}
}
