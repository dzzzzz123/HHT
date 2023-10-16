package ext.requirement.create;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ait.util.CommonUtil;
import wt.util.WTException;

public class CreateRequirement extends DefaultObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> list) throws WTException {
		String url = "";
		String context = "";
		String folder = "";

		Map<String, Object> map = nmCommandBean.getParameterMap();
		for (String key : map.keySet()) {
			if (key.equals("ContainerOid")) {
				Object value = map.get(key);
				String[] stringArray = (String[]) value;
				Optional<String> firstMatch = Arrays.stream(stringArray).filter(s -> s.startsWith("OR")).findFirst();
				if (firstMatch.isPresent()) {
					context = "Containers('" + firstMatch.get() + "')";
				}
			} else if (key.equals("oid")) {
				Object value = map.get(key);
				String[] stringArray = (String[]) value;
				Optional<String> firstMatch = Arrays.stream(stringArray).filter(s -> s.startsWith("OR")).findFirst();
				if (firstMatch.isPresent()) {
					folder = "Folders('" + firstMatch.get() + "')";
				}
			}
		}
		String json = generateJson(context, folder);
		CommonUtil.requestInterface(url, "", "", json, "POST");
		return super.doOperation(nmCommandBean, list);
	}

	/**
	 * 根据产品和文件夹的oid来生成json
	 * @param context
	 * @param folder
	 * @return
	 */
	public String generateJson(String context, String folder) {
		String json = "";
		ObjectMapper objectMapper = new ObjectMapper();
		Container container = new Container(context, folder);
		ContainerData containerData = new ContainerData(container);
		try {
			json = objectMapper.writeValueAsString(containerData);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return json;
	}
}
