package ext.sap.SupplierMasterData;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import ext.ait.util.PropertiesUtil;
import ext.ait.util.Result;

public class SupplierMasterDataServlet implements Controller {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("customEnum.properties");

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
		// 写入接收到的数据到properties文件中
		writeToProperties(SupplierEntitiesList);

		PrintWriter out = response.getWriter();
		out.print(Result.success().toString());
		out.close();
		return null;
	}

	private void writeToProperties(List<SupplierEntity> list) {
		Map<String, String> map = new HashMap<>();
		list.forEach(supplier -> {
			String key = supplier.getInternalName();
			String value = supplier.getDisplayName() + "/" + supplier.getCreateTime();
			map.put(key, value);
		});
		int result = pUtil.writeAll(map);
		System.out.println("result" + result);
	}

}
