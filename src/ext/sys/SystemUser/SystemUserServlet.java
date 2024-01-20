package ext.sys.SystemUser;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import ext.ait.util.CommonUtil;
import wt.org.OrganizationServicesHelper;
import wt.org.WTUser;

/**
 * 用户操作相关接口
 * 
 * @author Administrator
 *
 */
public class SystemUserServlet implements Controller {

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String userCode = request.getParameter("userCode");
		System.out.println("========上传参数：" + userCode);
		String code = new String(Base64.decodeBase64(userCode.getBytes()));
		System.out.println("========员工工号：" + code);
		List<WTUser> userList = new ArrayList<>();
		Enumeration result = OrganizationServicesHelper.manager.findUser(WTUser.NAME, code);
		while (result.hasMoreElements()) {
			userList.add((WTUser) result.nextElement());
		}
		// 使用setCharacterEncoding方法设置输出内容使用UTF-8进行编码
		response.setCharacterEncoding("UTF-8");
		// 使用setHeader方法设置浏览器使用UTF-8进行解码
		response.setHeader("Content-Type", "text/html;charset=UTF-8");
		if (userList.isEmpty()) {
			response.getWriter().write(result(400, "该工号在PLM中不存在"));
			return null;
		}
		if (userList.size() > 1) {
			response.getWriter().write(result(500, "PLM数据维护异常"));
			return null;
		}
		OrganizationServicesHelper.manager.delete(userList.get(0));
		response.getWriter().write(result(200, "success"));
		return null;
	}

	private String result(int code, String msg) {
		Map<String, Object> result = new HashMap<>(2);
		result.put("code", code);
		result.put("msg", msg);
		return CommonUtil.getJsonFromObject(result);
	}

}
