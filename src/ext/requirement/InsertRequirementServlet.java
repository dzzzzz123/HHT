package ext.requirement;

import java.io.BufferedReader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ext.ait.util.CommonUtil;

public class InsertRequirementServlet implements Controller {

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 获取传入的JSON数据
		BufferedReader reader = request.getReader();
		StringBuilder jsonInput = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			jsonInput.append(line);
		}

		// 使用Jackson库将JSON数据解析为 JsonNode 对象
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(jsonInput.toString());
		String datasNode = rootNode.get("datas").asText();

		System.out.println("datasNode:" + datasNode);
		Requirement dataEntity = objectMapper.readValue(datasNode, Requirement.class);

		// 现在，您可以访问和操作解析后的数据
		String number = dataEntity.getNumber();
		String name = dataEntity.getName();
		List<Requirement.Post> posts = dataEntity.getPosts();
		String postsJson = objectMapper.writeValueAsString(posts);

		String sql = "INSERT INTO CUSTOMREQUIREMENT (\"NUMBER\", \"NAME\", \"RICHTEXT\") VALUES ( ? , ? , ? )";
		CommonUtil.excuteInsert(sql, number, name, postsJson);

		// 在此处添加您的处理逻辑
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(ext.ait.util.Result.success().toString());
		return null;
	}

}
