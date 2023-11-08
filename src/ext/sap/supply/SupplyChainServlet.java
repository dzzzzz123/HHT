package ext.sap.supply;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import ext.ait.util.CommonUtil;
import ext.ait.util.Result;
import ext.ait.util.WorkflowUtil;
import ext.sap.Config;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.workflow.work.WorkItem;

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

	public static List<String> requestSupplyChainList(String workItemID) {
		List<String> result = new ArrayList<>();
		try {
			ReferenceFactory rf = new ReferenceFactory();
			WorkItem workItem = (WorkItem) rf.getReference(workItemID).getObject();
			WTObject pbo = WorkflowUtil.getPBOByWorkItem(workItem);
			ArrayList<WTPart> parts = WorkflowUtil.getTargerObject(pbo, "AffectedObjects", WTPart.class);
			parts.forEach(part -> {
				result.add(requestSupplyChain("{ \"I_MATNR\": \"" + part.getNumber() + "\" }"));
			});
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return result;
	}

}
