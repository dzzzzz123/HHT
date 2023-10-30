package ext.sap.CostCenter;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import ext.ait.util.CommonUtil;
import ext.ait.util.PropertiesUtil;
import ext.ait.util.Result;

public class CostCenterServlet implements Controller {
	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("customEnum.properties");

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<CostCenterEntity> costCenterEntityList = CommonUtil.getEntitiesFromRequest(request, CostCenterEntity.class,
				"");
		// 写入接收到的数据到properties文件中
		writeToProperties(costCenterEntityList);

		response.setCharacterEncoding("utf-8");
		response.setContentType("appliction/json;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(Result.success().toString());
		out.close();
		return null;
	}

	private void writeToProperties(List<CostCenterEntity> list) {
		Map<String, String> map = new HashMap<>();
		list.forEach(costCenter -> {
			String key = costCenter.getInternalName();
			String value = costCenter.getDisplayName() + "/" + costCenter.getFactoryCode();
			map.put(key, value);
		});
		int result = pUtil.writeAll(map);
		System.out.println("result" + result);
	}

}
