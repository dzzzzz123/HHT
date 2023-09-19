package ext.classification.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ext.ait.util.CommonUtil;
import wt.pom.WTConnection;

public class Util {

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
