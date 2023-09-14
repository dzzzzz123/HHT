package ext.sap.SupplierMasterData;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import ext.ait.util.Result;

public class SupplierMasterDataServlet implements Controller {

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("utf-8");
		response.setContentType("appliction/json;charset=utf-8");
		// 从请求中获取JSON数据
		BufferedReader reader = request.getReader();
		StringBuilder jsonBuilder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			jsonBuilder.append(line);
		}
		String jsonData = jsonBuilder.toString();

		// 使用Jackson库解析JSON数据并存储在List<SupplierEntity>中
		ObjectMapper objectMapper = new ObjectMapper();
		SupplierEntity[] SupplierEntities = objectMapper.readValue(jsonData, SupplierEntity[].class);
		List<SupplierEntity> SupplierEntitiesList = Arrays.asList(SupplierEntities);

		String jsonOutput = objectMapper.writeValueAsString(SupplierEntitiesList);

		// 将JSON写入文件
		try (FileWriter fileWriter = new FileWriter("supplierEntity.json")) {
			fileWriter.write(jsonOutput);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter out = response.getWriter();
		out.print(Result.success().toString());
		out.close();
		return null;
	}

}