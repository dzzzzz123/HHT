package ext.part.duplicateCheck;

import java.util.List;
import java.util.Map;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.forms.CreatePartAndCADDocFormProcessor;

import wt.fc.WTObject;
import wt.folder.Folder;
import wt.util.WTException;

public class CustomCreatePartAndCADDocFormProcessor extends CreatePartAndCADDocFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean nmcommandbean, List<ObjectBean> list) throws WTException {
		System.out.println("----------------------测试CustomCreatePartAndCADDocFormProcessor.doOperation是否正确执行");
		Map<String, String> taMap = nmcommandbean.getTextArea();
		taMap.forEach((key, value) -> {
			System.out.println("doOperation TextArea-----key:" + key + " value:" + value);
		});
		Map<String, Object> comMap = nmcommandbean.getComboBox();
		comMap.forEach((key, value) -> {
			System.out.println("preProcess ComboBox-----key:" + key + " value:" + value.toString());
		});
		return super.doOperation(nmcommandbean, list);
	}

	@Override
	public FormResult preProcess(NmCommandBean nmcommandbean, List<ObjectBean> list) throws WTException {
		System.out.println("----------------------测试CustomCreatePartAndCADDocFormProcessor.preProcess是否正确执行");
		Map<String, String> taMap = nmcommandbean.getTextArea();
		taMap.forEach((key, value) -> {
			System.out.println("preProcess TextArea-----key:" + key + " value:" + value);
		});
		Map<String, Object> comMap = nmcommandbean.getComboBox();
		comMap.forEach((key, value) -> {
			System.out.println("preProcess ComboBox-----key:" + key + " value:" + value.toString());
		});
		return super.preProcess(nmcommandbean, list);
	}

	@Override
	public FormResult postProcess(NmCommandBean nmCommandBean, List<ObjectBean> list) throws WTException {
		NmOid nmOid = nmCommandBean.getActionOid();
		WTObject object = (WTObject) nmOid.getRefObject();
		String folderNamePrefix = "";
		if (object instanceof Folder) {
			Folder folder = (Folder) object;
			System.out.println("folder.getFolderPath():" + folder.getFolderPath());
			if (!"/Default".equals(folder.getFolderPath())) {
				folderNamePrefix = folder.getName();
			} else {
				throw new WTException("不能在跟目录下创建");
			}
		}
		return super.postProcess(nmCommandBean, list);
	}

}
