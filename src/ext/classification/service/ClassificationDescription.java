package ext.classification.service;

import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.ptc.core.lwc.server.LWCEnumerationEntryValuesFactory;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.DataSet;
import com.ptc.core.meta.common.DisplayOperationIdentifier;
import com.ptc.core.meta.common.EnumeratedSet;
import com.ptc.core.meta.common.EnumerationEntryIdentifier;
import com.ptc.core.meta.container.common.AttributeTypeSummary;

import ext.ait.util.IBAUtil;
import ext.ait.util.PropertiesUtil;
import wt.meta.LocalizedValues;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

public class ClassificationDescription {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("descriptionConfig.properties");

	/**
	 * 处理物料描述主方法
	 * 
	 * @param part
	 */
	public static void process(WTPart part) {
		String classInternalName = pUtil.getValueByKey(part, "iba.internal.HHT_Classification");
		String partten = pUtil.getValueByKey(classInternalName);
		String newDescription = processPartten(partten, part);
		try {
			IBAUtil ibaUtil = new IBAUtil(part);
			ibaUtil.setIBAAttribute4AllType(part, pUtil.getValueByKey("iba.internal.HHT_LongtDescription"),
					newDescription);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将配置文件中读取所对应的分类
	 * 
	 * @param partten
	 * @return String 新物料描述
	 */
	private static String processPartten(String partten, WTPart part) {
		List<String> parttens = Util.extractParttens(partten);
		String newDescription = "";
		try {
			IBAUtil ibaUtil = new IBAUtil(part);
			Hashtable hashtable = ibaUtil.getAllIBAValues();
			Set set = hashtable.keySet();
			for (String word : parttens) {
				if (word.startsWith("[")) {
					newDescription += word.substring(1);
				} else {
					if (set.contains(word)) {
						String displayName = getEnumDisplay(part, word);
						if (displayName.length() > 0) {
							newDescription += displayName;
						} else {
							newDescription += ibaUtil.getIBAValue(word);
						}
					} else {
						newDescription += "";
					}
				}
				System.out.println("newDescription：" + newDescription);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return newDescription;
	}

	public static String getEnumDisplay(WTPart part, String ibaName) {
		System.out.println("ibaName:" + ibaName);
		String displayName = "";
		try {
			Locale loc = SessionHelper.manager.getLocale();
			PersistableAdapter adapter = new PersistableAdapter(part, null, loc, new DisplayOperationIdentifier());
			adapter.load(new String[] { pUtil.getValueByKey("iba.internal.HHT_Classification"), ibaName });
			String obj = (String) adapter.get(ibaName);
			AttributeTypeSummary ats_csm = adapter.getAttributeDescriptor(ibaName);
			DataSet lvs_csm = ats_csm.getLegalValueSet();
			if (lvs_csm != null) {
				if (lvs_csm instanceof EnumeratedSet) {
					EnumerationEntryIdentifier eei = ((EnumeratedSet) lvs_csm).getElementByKey(obj.toString());
					LWCEnumerationEntryValuesFactory eevf = new LWCEnumerationEntryValuesFactory();
					LocalizedValues valueLocale = eevf.get(eei, loc);
					displayName = valueLocale.getDisplay();
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

		return displayName;
	}
}
