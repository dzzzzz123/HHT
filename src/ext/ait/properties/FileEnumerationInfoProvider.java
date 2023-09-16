/* 
 * Disclaimer : This is a sample implementation for demonstration purpose.
 * Do not use as it in production.
 */
package ext.ait.properties;

import java.io.File;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import com.ptc.core.lwc.common.dynamicEnum.EnumerationEntryInfo;
import com.ptc.core.lwc.common.dynamicEnum.EnumerationInfo;
import com.ptc.core.lwc.common.dynamicEnum.EnumerationInfoManager;
import com.ptc.core.lwc.common.dynamicEnum.EnumerationInfoProvider;

import wt.util.WTProperties;

public class FileEnumerationInfoProvider implements EnumerationInfoProvider {
	// Default input file, in case the file name is not provided via the
	// initialization string.
	// This sample assumes that the files stored under WT_HOME.
	private static final String DEFAULT_ENUM_FILE_NAME = "customEnum.properties";

	private static String ENUM_SOURCE_PATH;
	private static String WT_HOME;

	/**
	 * Internal name of the enumeration
	 */
	private static final String ENUMERATION_NAME = "customEnum";

	/**
	 * Locales for which values will be generated
	 */
	private static final Locale[] SUPPORTED_LOCALES = { Locale.US, Locale.FRENCH };
	private static final Locale DEFAULT_LOCALE = Locale.US;

	/**
	 * The enumeration info manager to reset enumeration cache in Windchill
	 */
	private EnumerationInfoManager enumInfoManager = null;

	/**
	 * The initialization string (not used in this example)
	 */
	private String parameters = null;

	/**
	 * The enumeration info which include all of the information of this dynamic
	 * enumeration list.
	 */
	private EnumerationInfo enumerationInfo = null;

	static {
		try {
			/* Retrieves the wt.home path */
			WTProperties properties = WTProperties.getLocalProperties();
			WT_HOME = properties.getProperty("wt.home", "E:\\ptc\\WNC102\\wnc");
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	@Override
	public EnumerationInfo getEnumerationInfo() {
		return enumerationInfo;
	}

	@Override
	public EnumerationInfoManager getManager() {
		return enumInfoManager;
	}

	@Override
	public String getParameters() {
		return parameters;
	}

	/**
	 * Instances will be instantiated through reflection. Therefore a public no-arg
	 * constructor is required.
	 */
	public FileEnumerationInfoProvider() {
	}

	/**
	 * The major method of this class. It generates the enumeration info based on a
	 * property file specified as initialization string.
	 * 
	 * @param manager    The enumeration info manager
	 * @param parameters The initialization string <br>
	 *                   The initialization string must contain the property file
	 *                   name relative to wt.home
	 * 
	 *                   This provider only supports 2 locales : Locale.US,
	 *                   Locale.FRENCH. When other locale is chosen on client
	 *                   browser, the default localized entry with suffix Locale.US
	 *                   will be displayed.
	 */
	@Override
	public void initialize(EnumerationInfoManager manager, String params) {
		if (manager == null) {
			String errorMsg = "Argument EnumerationInfoManager must be not null.";
			throw new IllegalArgumentException(errorMsg);
		}

		this.enumInfoManager = manager;
		this.parameters = params;

		if (params == null || params.isEmpty())
			ENUM_SOURCE_PATH = WT_HOME + File.separator + DEFAULT_ENUM_FILE_NAME;
		else
			ENUM_SOURCE_PATH = WT_HOME + File.separator + params;

		/* Populate the enumeration */
		this.enumerationInfo = createEnumerationInfo();

	}

	/**
	 * Populate the EnumerationInfo
	 */
	private EnumerationInfo createEnumerationInfo() {
		// create EnumerationInfo
		EnumerationInfo enumerationInfo = new EnumerationInfo();
		// set localizable properties
		for (Locale locale : SUPPORTED_LOCALES) {
			enumerationInfo.setLocalizableProperty(EnumerationInfo.DISPLAY_NAME, locale, ENUMERATION_NAME);
			enumerationInfo.setLocalizableProperty(EnumerationInfo.DESCRIPTION, locale,
					ENUMERATION_NAME + " description");
			enumerationInfo.setLocalizableProperty(EnumerationInfo.TOOLTIP, locale, ENUMERATION_NAME + " tooltip");
		}
		// set non localizable properties
		enumerationInfo.setNonLocalizableProperty(EnumerationInfo.AUTO_SORT, true);
		enumerationInfo.setNonLocalizableProperty(EnumerationInfo.DEFAULT_LOCALE, DEFAULT_LOCALE);

		Set<EnumerationEntryInfo> enumEntryInfos = createEnumerationEntryInfos();
		enumerationInfo.addEnumerationEntryInfos(enumEntryInfos);

		return enumerationInfo;
	}

	/**
	 * Populate the EnumerationInfo entries from the source file
	 */
	private Set<EnumerationEntryInfo> createEnumerationEntryInfos() {
		Properties props = new Properties();
		try {
			props.load(new java.io.FileInputStream(ENUM_SOURCE_PATH));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		Set<EnumerationEntryInfo> enumEntryInfos = new LinkedHashSet<EnumerationEntryInfo>();

		String key = null;
		Enumeration keys = props.keys();
		while (keys.hasMoreElements()) {
			key = (String) keys.nextElement();
			EnumerationEntryInfo enumEntryInfo = new EnumerationEntryInfo(key);

			// set localizable properties
			String value = null;
			for (Locale locale : SUPPORTED_LOCALES) {
				value = props.getProperty(key) + "_" + locale.getLanguage();
				enumEntryInfo.setLocalizableProperty(EnumerationEntryInfo.DISPLAY_NAME, locale, value);
				enumEntryInfo.setLocalizableProperty(EnumerationEntryInfo.DESCRIPTION, locale, value);
				enumEntryInfo.setLocalizableProperty(EnumerationEntryInfo.TOOLTIP, locale, value);
				enumEntryInfo.setLocalizableProperty(EnumerationEntryInfo.PATH_OVERRIDE, locale, null);
			}
			// set non localizable properties
			// SELECTABLE
			enumEntryInfo.setNonLocalizableProperty(EnumerationEntryInfo.SELECTABLE, Boolean.TRUE);

			// SORT_ORDER
			enumEntryInfo.setNonLocalizableProperty(EnumerationEntryInfo.SORT_ORDER, null);
			// PARENT
			enumEntryInfo.setNonLocalizableProperty(EnumerationEntryInfo.PARENT, null);
			enumEntryInfos.add(enumEntryInfo);
		}
		return enumEntryInfos;
	}
}