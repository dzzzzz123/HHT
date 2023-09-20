package ext.srm.pdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ptc.wvs.common.ui.VisualizationHelper;

import ext.ait.util.DocumentUtil;
import ext.ait.util.PartUtil;
import ext.ait.util.PropertiesUtil;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.representation.Representation;
import wt.util.FileUtil;

public class SendPDF2SRMServlet implements Controller {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("config.properties");

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("utf-8");
		response.setContentType("appliction/json;charset=utf-8");
		PrintWriter out = response.getWriter();
		try {
			List<PartInfo> list = new Gson().fromJson(getJsonStringFromRequest(request),
					new TypeToken<List<PartInfo>>() {
					}.getType());

			for (PartInfo info : list) {
				String number = info.getNumber();
				String version = info.getVersion();
				WTPart part = PartUtil.getWTPartByNumberAndView(number, version);
				if (part == null) {
					throw new Exception(number + "，" + version + "部件不存在。");
				}
				ApplicationData appData = getAppData(part);

				if (appData == null) {
					info.setPath("该部件无PDF文件。");
					continue;
				}

				String filename = number + "_" + part.getName() + "_" + version + "_"
						+ part.getLifeCycleState().getDisplay(Locale.CHINA);
				info.setPath(zip(filename, appData));
			}
			out.print(Result.sucess("下载成功", list));
		} catch (Exception e) {
			e.printStackTrace();
			out.print(Result.error(301, e.getMessage()));
		}
		return null;
	}

	// 获取WTPART关联的2D CAD文档的表示法中的PDF，如果没有获取到则获取说明方文档的表示法中的PDF
	private ApplicationData getAppData(WTPart part) throws Exception {
		List<WTDocument> drawingList = new ArrayList<>();

		drawingList = DocumentUtil.getDescribedDocumentsFromPart(part);
		VisualizationHelper vizHelper = new VisualizationHelper();
		ApplicationData found = null;
		for (Persistable persistable : drawingList) {
			QueryResult result = vizHelper.getRepresentations(persistable);
			while (result.hasMoreElements()) {
				Representation rep = (Representation) result.nextElement();
				ContentHolder ch = ContentHelper.service.getContents(rep);
				Boolean defaultRepresentation = rep.getDefaultRepresentation();
				if (!defaultRepresentation) {
					continue;
				}
				Vector<ApplicationData> appDatas = ContentHelper.getContentListAll(ch);
				for (ApplicationData appData : appDatas) {
					String fileName = appData.getFileName();
					ContentRoleType role = appData.getRole();
					if (role.equals(ContentRoleType.ADDITIONAL_FILES) || role.equals(ContentRoleType.SECONDARY)
							&& FileUtil.getExtension(fileName).equalsIgnoreCase("PDF")) {
						found = appData;
					}
				}
			}
			if (found != null)
				break;
		}
		return found;
	}

	private String zip(String filename, ApplicationData app) throws Exception {
		String zipPath = "C:/" + filename + ".zip";
		File file = new File(zipPath);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		ZipOutputStream zos = new ZipOutputStream(new CheckedOutputStream(new FileOutputStream(file), new CRC32()));
		byte[] buffer = new byte[1024];
		InputStream is = ContentServerHelper.service.findContentStream(app);
		zos.putNextEntry(new ZipEntry(filename + ".pdf"));
		int len = 0;
		while ((len = is.read(buffer)) > 0) {
			zos.write(buffer, 0, len);
		}
		zos.flush();
		zos.closeEntry();
		zos.close();
		is.close();
		return zipPath;
	}

	public String getJsonStringFromRequest(HttpServletRequest request) throws IOException {
		StringBuffer sb = new StringBuffer();
		InputStream is = request.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String s = "";
		while ((s = br.readLine()) != null) {
			sb.append(s);
		}
		String str = sb.toString();
		return str;
	}

}
