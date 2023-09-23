package ext.signature;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.ptc.netmarkets.model.NmOid;

import wt.content.ApplicationData;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.config.LatestConfigSpec;

/**
 * 对指定目录的文件夹进行压缩打包成zip文件
 */

public class BatchDownloadZipPDFServlet implements Controller {
	public static String PDF_TEMP = "";
	static {
		String wtHome = null;
		try {
			WTProperties props = WTProperties.getLocalProperties();
			wtHome = props.getProperty("wt.temp");
			PDF_TEMP = wtHome + File.separator + "pdf";
			File pdfFolder = new File(PDF_TEMP);
			if (!pdfFolder.exists()) {
				pdfFolder.mkdir();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		batchDownloadZipPDF(request, response);
		return null;
	}

	@SuppressWarnings("deprecation")
	private void batchDownloadZipPDF(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {

			WTPart part = null;
			String partnumber = request.getParameter("Value");// wtpart partnumber
			String Version = request.getParameter("Version");// wtpart Version
			if (StringUtils.isNotBlank(partnumber) && StringUtils.isNotBlank(Version)) {
				part = getPartByNumber(partnumber, Version.toUpperCase());
			} else {
				String oid = request.getParameter("oid");// wtpart oid
				NmOid nmoid = NmOid.newNmOid(oid);
				if (nmoid != null && nmoid.getRef() != null) {
					Object ref = nmoid.getRef();
					if (ref instanceof WTPart) {
						part = (WTPart) ref;
					}
				}
			}
			if (part != null) {
				response.reset();
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", "attachment; filename=" + part.getNumber() + ".zip");
				Object[] returnData = PartPDFHelper.findBOMPDFRepFromPart(part);
				List<WTPart> noPDFPartList = (List<WTPart>) returnData[0];
				List<ApplicationData> pdfRefList = (List<ApplicationData>) returnData[1];
				ZipOutputStream zos = new ZipOutputStream(
						new CheckedOutputStream(response.getOutputStream(), new CRC32()));
				for (ApplicationData applicationData : pdfRefList) {
					String fileName = applicationData.getFileName();
					InputStream is = ContentServerHelper.service.findContentStream(applicationData);
					ZipEntry entry = new ZipEntry(fileName);
					zos.putNextEntry(entry);
					int len = -1;
					byte[] buffer = new byte[1024];
					while ((len = is.read(buffer)) != -1) {
						zos.write(buffer, 0, len);
					}
					zos.flush();
					is.close();
				}
				// txt列表保存没有pdf表示法的partnumber
				String fileName = "no-pdf-part-list.txt";
				ZipEntry entry = new ZipEntry(fileName);
				StringBuffer sb = new StringBuffer("");
				int index = 1;
				for (WTPart p : noPDFPartList) {
					String temp = index + " | ";
					temp += p.getNumber() + " | ";
					temp += p.getName() + "\r\n";
					sb.append(temp);
					index++;
				}
				InputStream is = new ByteArrayInputStream(sb.toString().getBytes("utf-8"));
				zos.putNextEntry(entry);
				int len = -1;
				byte[] buffer = new byte[1024];
				while ((len = is.read(buffer)) != -1) {
					zos.write(buffer, 0, len);
				}
				zos.flush();
				is.close();
				zos.close();
				response.flushBuffer();
			} else {
				response.reset();
				response.setContentType("text/html;charset=UTF-8"); // 这句太重要了
				response.getWriter().print("无法获取WTPart数据");
				response.flushBuffer();
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.reset();
				response.setContentType("text/html;charset=UTF-8"); // 这句太重要了
				response.getWriter().print("批量导出PDF出错:" + e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
		}
	}

	public static WTPart getPartByNumber(String number, String version) throws WTException {
		WTPart part = null;
		QuerySpec qs = new QuerySpec(WTPart.class);
		SearchCondition sc = new SearchCondition(WTPart.class, "master>number", "=", number);
		qs.appendWhere(sc);
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPart.class, "interopInfo.iopState", "<>", "terminal"));
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPart.class, "versionInfo.identifier.versionId", "=", version));
		QueryResult qr = PersistenceHelper.manager.find(qs);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);

		if (qr.size() > 0) {
			part = (WTPart) qr.nextElement();
		}
		return part;
	}

	private boolean savePDF2TempFolder(InputStream is, String outFilePath) throws Exception {
		File file = new File(outFilePath);
		if (file.exists()) {
			return false;
		}
		FileOutputStream fos = new FileOutputStream(file);
		int len = 0;
		byte[] buffer = new byte[1024];
		while ((len = is.read(buffer)) > 0) {
			fos.write(buffer, 0, len);
			fos.flush();
		}
		fos.close();
		return true;
	}

}