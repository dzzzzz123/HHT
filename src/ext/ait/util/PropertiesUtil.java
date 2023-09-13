package ext.ait.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import wt.iba.value.IBAHolder;
import wt.log4j.LogR;
import wt.util.WTException;

public class PropertiesUtil {

	// 单例模式实例对象
	private static PropertiesUtil instance;
	// 资源变量
	private Properties properties;
	// 存储调用类
	private Class<?> callingClass;
	// properties文件名称
	private String configFileName;
	private static Logger LOGGER = LogR.getLogger(PropertiesUtil.class.getName());

	private PropertiesUtil(Class<?> callingClass, String configFileName) {
		this.callingClass = callingClass; // 存储调用类
		this.configFileName = configFileName;
		loadProperties();
	}

	// 单例
	public static PropertiesUtil getInstance(String configFileName) {
		if (instance == null) {
			// 使用 Thread.currentThread().getStackTrace() 获取调用者的类
			Class<?> callingClass = getCallingClass();
			instance = new PropertiesUtil(callingClass, configFileName);
		}
		return instance;
	}

	// 获取调用当前方法的方法的class
	private static Class<?> getCallingClass() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		try {
			if (stackTrace.length >= 4) {
				String className = stackTrace[3].getClassName();
				return Class.forName(className);
			} else {
				LOGGER.error("----->当前文件夹找不到配置文件: ");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 载入properties文件中的内容
	private void loadProperties() {
		try {
			String propertiefile = callingClass.getResource(configFileName).getFile();
			properties = new Properties();
			properties.load(new InputStreamReader(new FileInputStream(propertiefile), "UTF-8"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 读取properties文件中对应的value值
	public String getStr(String key) {
		loadProperties(); // 在每次调用 getStr 时重新加载
		String strinfo = properties.getProperty(key);
		if (strinfo != null) {
			strinfo = strinfo.trim();
		}
		LOGGER.info("----->得到config文件中的[" + key + ":" + strinfo + "]");
		return strinfo;
	}

	// 读取properties文件中对应的value值
	// 并直接获取对应对象的IBA属性对应值
	public String getStr(IBAHolder ibaHolder, String key) {
		String IBAKey = getStr(key);
		String IBAValue = "";
		try {
			IBAUtil ibaUtil = new IBAUtil(ibaHolder);
			IBAValue = ibaUtil.getIBAValue(IBAKey);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return IBAValue;
	}
}
