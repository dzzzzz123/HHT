package ext.classification.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ext.ait.util.ClassificationUtil;
import ext.ait.util.CommonUtil;
import ext.ait.util.IBAUtil;
import wt.part.WTPart;
import wt.util.WTException;

public class Util {

	/**
	 * 根据分类的内部名称（编号的前五位）获取同分类的所有编号
	 * 
	 * @param classInternalName 部件分类属性内部名称
	 * @return 流水号序列
	 */
	public static List<String> getPartNumbersByPrefix(String classInternalName) {
		List<String> partNumbers = new ArrayList<>();
		String sql = "SELECT WTPARTNUMBER FROM WTPARTMASTER WHERE WTPARTNUMBER LIKE ?";
		ResultSet resultSet = CommonUtil.excuteSelect(sql, classInternalName);
		try {
			while (resultSet.next()) {
				String partNumber = resultSet.getString("WTPARTNUMBER");
				partNumbers.add(partNumber);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return partNumbers;
	}

	/**
	 * 根据从配置文件中读取分类所对应的新名称/描述规则
	 * 
	 * @param partten config中获取的规则
	 * @param part 获取数据源部件
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
						String displayName = ClassificationUtil.getDisplayByInternal(part, word);
						if (displayName.length() > 0) {
							newStr += displayName;
						} else {
							newStr += ibaUtil.getIBAValue(word);
						}
					} else {
						newStr += "";
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return newStr;
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
