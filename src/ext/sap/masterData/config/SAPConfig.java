package ext.sap.masterData.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import wt.util.WTProperties;

public class SAPConfig {
	private static Properties CONIG = null;
	private static String file_path;
	private static long LAST_MODIFIED = 0;
	static {
		CONIG = new Properties();
		try {

			WTProperties wtproperties = WTProperties.getLocalProperties();
			String wt_home = wtproperties.getProperty("wt.home");
			String sep = wtproperties.getProperty("dir.sep");
			StringBuffer tempBuf = new StringBuffer(wt_home);
			tempBuf.append(sep).append("codebase").append(sep).append("ext").append(sep).append("sap").append(sep)
					.append("masterData").append(sep).append("config").append(sep).append("SAP.properties");
			file_path = tempBuf.toString();
			FileInputStream inputStream = new FileInputStream(file_path);
			InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
			CONIG.load(reader);
		} catch (Exception e) {
			System.out.println("------Error:Config file not found !!!!!!!");
		}
	}

	public static String getConfig(String key) {
		isNew();
		return String.valueOf(CONIG.get(key));
	}

	private static boolean isNew() {
		File file = new File(file_path);
		long lastModified = file.lastModified();
		try {
			if (lastModified > LAST_MODIFIED) {
				FileInputStream fs = new FileInputStream(file);
				InputStreamReader reader = new InputStreamReader(fs, "UTF-8");
				CONIG.load(reader);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println("Error:SAP Config file not found !");
			return false;
		}
	}
}
