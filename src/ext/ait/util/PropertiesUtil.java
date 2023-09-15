package ext.ait.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
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
	public String getValueByKey(String key) {
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
	public String getValueByKey(IBAHolder ibaHolder, String key) {
		String IBAKey = getValueByKey(key);
		String IBAValue = "";
		try {
			IBAUtil ibaUtil = new IBAUtil(ibaHolder);
			IBAValue = ibaUtil.getIBAValue(IBAKey);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return IBAValue;
	}

	/**
	 * 获取properties文件中所有的key/value所对应的map
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, String> getAll() {
		loadProperties(); // 在每次调用 getStr 时重新加载
		Map<String, String> map = new HashMap<>();

		for (String key : properties.stringPropertyNames()) {
			String value = properties.getProperty(key);
			value = value.trim();
			map.put(key, value);
		}
		return map;
	}

	/**
	 * 将map中的key/value值写入properties文件中
	 * 
	 * @param map 写入properties文件值
	 * @return int 修改的条数：判断是否修改正确
	 */
	public int writeAll(Map<String, String> map) {
		loadProperties(); // 在每次调用 getStr 时重新加载
		int affectedRows = 0;

		// 保存到与加载时相同的文件
		String propertiefile = callingClass.getResource(configFileName).getFile();

		try (OutputStream outputStream = new FileOutputStream(propertiefile)) {
			Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
			// 使用map中的值修改现有属性
			for (String key : map.keySet()) {
				properties.setProperty(key, map.get(key));
				affectedRows++;
			}

			// 将已修改的属性存储回文件
			properties.store(writer, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return affectedRows;
	}
}
