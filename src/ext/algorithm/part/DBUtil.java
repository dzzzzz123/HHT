package ext.algorithm.part;

import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;

import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.util.WTException;

public class DBUtil {

	public static ResultSet commonQuery(String sql) throws Exception {
		System.out.println(sql);
		WTConnection con = getWTConnection();
		Statement statement = con.prepareStatement(sql);
		ResultSet rs = statement.executeQuery(sql);
		return rs;
	}

	public static String generateNumber(String prefix) throws WTException {
		System.out.println("------------------编号前缀:" + prefix);
		String querySql = "SELECT MAX(TO_NUMBER(WTCHGORDERNUMBER)) AS MAXPARTNUMBER FROM WTCHANGEORDER2MASTER WHERE WTCHGORDERNUMBER LIKE '"
				+ prefix + "%' AND LENGTH(WTCHGORDERNUMBER) = 12";
		int seqLength = 12 - prefix.length();
		System.out.println("seqLength===" + seqLength);
		try {
			ResultSet rs = DBUtil.commonQuery(querySql);
			System.out.println("rs===" + rs);
			String maxNumber = "";
			String newSeq = "1";
			if (rs.next()) {
				maxNumber = rs.getString("MAXPARTNUMBER");
				System.out.println("maxNumber===" + maxNumber);
			}
			String newNumber = "";
			if (StringUtils.isNotBlank(maxNumber)) {
				newNumber = maxNumber;
			}
			System.out.println("最大编号：" + newNumber);
			if (StringUtils.isNotBlank(newNumber)) {
				String maxSeq = StringUtils.mid(newNumber, prefix.length(), seqLength);
				Integer maxSeqI = Integer.valueOf(maxSeq);
				newSeq = (++maxSeqI).toString();
			}
			while (newSeq.length() < seqLength) {
				newSeq = "0" + newSeq;
				System.out.println("newSeq===" + newSeq);
			}
			return prefix + newSeq;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException("生成编号出错。" + e.getMessage());
		}
	}

	public static WTConnection getWTConnection() throws Exception {
		MethodContext methodcontext = MethodContext.getContext();
		WTConnection wtconnection = (WTConnection) methodcontext.getConnection();
		return wtconnection;
	}
}
