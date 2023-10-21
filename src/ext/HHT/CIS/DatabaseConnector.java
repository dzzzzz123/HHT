package ext.HHT.CIS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import ext.ait.util.PropertiesUtil;

public class DatabaseConnector {
	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("config.properties");

	public static Connection getConnection() throws SQLException, ClassNotFoundException {
		String JDBC_URL = pUtil.getValueByKey("JDBC_URL");
		String USERNAME = pUtil.getValueByKey("USERNAME");
		String PASSWORD = pUtil.getValueByKey("PASSWORD");
		System.out.println("JDBC_URL=" + JDBC_URL + "USERNAME=" + USERNAME + "PASSWORD=" + PASSWORD);
		return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

	}

}