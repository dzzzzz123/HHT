package ext.classification.service;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ptc.core.lwc.server.LWCEnumerationEntryValuesFactory;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.DataSet;
import com.ptc.core.meta.common.DisplayOperationIdentifier;
import com.ptc.core.meta.common.EnumeratedSet;
import com.ptc.core.meta.common.EnumerationEntryIdentifier;
import com.ptc.core.meta.container.common.AttributeTypeSummary;

import ext.ait.util.CommonUtil;
import ext.ait.util.IBAUtil;
import ext.ait.util.PropertiesUtil;
import wt.meta.LocalizedValues;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.pom.WTConnection;
import wt.session.SessionHelper;
import wt.util.WTException;

public class Util implements RemoteAccess, Serializable {

	private static final long serialVersionUID = 1L;
	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("nameConfig.properties");

	/**
	 * 根据分类的内部名称（编号的前五位）获取同分类的所有编号
	 * 
	 * @param classInternalName
	 * @return
	 */
	public static List<String> getPartNumbersByPrefix(String classInternalName) {
		List<String> partNumbers = new ArrayList<>();
		String selectQuery = "SELECT WTPARTNUMBER FROM WTPARTMASTER WHERE WTPARTNUMBER LIKE ?";
		try {
			WTConnection connection = CommonUtil.getWTConnection();
			PreparedStatement statement = connection.prepareStatement(selectQuery);
			statement.setString(1, classInternalName + "%");
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				String partNumber = resultSet.getString("WTPARTNUMBER");
				partNumbers.add(partNumber);
			}
			resultSet.close();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return partNumbers;
	}

	/**
	 * 根据从配置文件中读取分类所对应的新名称/描述规则
	 * 
	 * @param partten
	 * @param part
	 * @return String 新名称/描述
	 */
	public static String processPartten(String partten, WTPart part) {
		List<String> parttens = Util.extractParttens(partten);
		String newStr = "";
		try {
			IBAUtil ibaUtil = new IBAUtil(part);
			Hashtable hashtable = ibaUtil.getAllIBAValues();
			Set set = hashtable.keySet();
			for (String word : parttens) {
				if (word.startsWith("[")) {
					newStr += word.substring(1);
				} else {
					if (set.contains(word)) {
						String displayName = getEnumDisplay(part, word);
						System.out.println("displayName" + displayName);
						if (displayName.length() > 0) {
							newStr += displayName;
						} else {
							newStr += ibaUtil.getIBAValue(word);
						}
					} else {
						newStr += "";
					}
				}
				System.out.println("newName：" + newStr);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return newStr;
	}

	public static String getEnumDisplay(WTPart part, String ibaName) {
		String displayName = "";
		System.out.println("ibaName：" + ibaName);
		System.out.println("HHT_Classification：" + pUtil.getValueByKey("iba.internal.HHT_Classification"));
		try {
			Locale loc = SessionHelper.manager.getLocale();
			PersistableAdapter adapter = new PersistableAdapter(part, null, loc, new DisplayOperationIdentifier());
			adapter.load(new String[] { pUtil.getValueByKey("iba.internal.HHT_Classification"), ibaName });
			Object obj = (String) adapter.get(ibaName);

			AttributeTypeSummary ats_csm = adapter.getAttributeDescriptor(ibaName);
			DataSet lvs_csm = ats_csm.getLegalValueSet();
			if (lvs_csm != null) {
				if (lvs_csm instanceof EnumeratedSet) {
					EnumerationEntryIdentifier eei = ((EnumeratedSet) lvs_csm).getElementByKey(obj.toString());
					LWCEnumerationEntryValuesFactory eevf = new LWCEnumerationEntryValuesFactory();
					LocalizedValues valueLocale = eevf.get(eei, loc);
					displayName = valueLocale.getDisplay();
				} else {
					System.out.println("lvs_csm is not instanceof EnumeratedSet");
				}
			} else {
				System.out.println("lvs_csm is null");
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return displayName;
	}

	/**
	 * 解析给出的规则将其解析为一个list中并按照原本顺序排列好
	 * 
	 * @param input 输入的长字符串规则
	 * @return List<String> 解析得到的结果
	 */
	public static List<String> extractParttens(String input) {
		List<String> result = new ArrayList<>();
		Pattern pattern = Pattern.compile("\\[([^\\]]+)\\]|([^\\[\\]]+)");
		Matcher matcher = pattern.matcher(input);

		while (matcher.find()) {
			String symbol = matcher.group(1);
			String word = matcher.group(2);

			if (word != null) {
				result.add(word);
			}

			if (symbol != null) {
				result.add("[" + symbol);
			}
		}

		return result;
	}
}
