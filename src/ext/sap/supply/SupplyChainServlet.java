package ext.sap.supply;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import ext.ait.util.CommonUtil;
import ext.ait.util.Result;
import ext.sap.Config;

public class SupplyChainServlet implements Controller {

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		try {
			BufferedReader reader = request.getReader();
			StringBuilder jsonInput = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonInput.append(line);
			}

			String result = requestSupplyChain(jsonInput.toString());
			response.getWriter().write(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
			response.getWriter().write(Result.error().toString());
		}
		return null;
	}

	public static String requestSupplyChain(String jsonInput) {
		String url = Config.getSupplyUrl();
		String username = Config.getUsername();
		String password = Config.getPassword();

		return CommonUtil.requestInterface(url, username, password, jsonInput.toString(), "POST", null);
	}
}
