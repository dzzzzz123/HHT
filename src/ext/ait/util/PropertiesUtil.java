package ext.ait.util;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import wt.log4j.LogR;

public class PropertiesUtil {

	private static PropertiesUtil instance;
	private Properties properties;
	private static Logger LOGGER = LogR.getLogger(CommonUtil.class.getName());

	private PropertiesUtil(Class<?> callingClass, String configFileName) {
		try {
			String propertiefile = callingClass.getResource(configFileName).getFile();
			properties = new Properties();
			properties.load(new InputStreamReader(new FileInputStream(propertiefile), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static PropertiesUtil getInstance(Class<?> callingClass, String configFileName) {
		if (instance == null) {
			instance = new PropertiesUtil(callingClass, configFileName);
		}
		return instance;
	}

	public String getStr(String key) {
		String strinfo = properties.getProperty(key);
		if (strinfo != null) {
			strinfo = strinfo.trim();
		}
		LOGGER.info("----->得到config文件中的[" + key + ":" + strinfo + "]");
		return strinfo;
	}
}
