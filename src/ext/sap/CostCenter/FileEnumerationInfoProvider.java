package ext.sap.CostCenter;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.ptc.core.lwc.common.dynamicEnum.EnumerationEntryInfo;
import com.ptc.core.lwc.common.dynamicEnum.EnumerationInfo;
import com.ptc.core.lwc.common.dynamicEnum.EnumerationInfoManager;
import com.ptc.core.lwc.common.dynamicEnum.EnumerationInfoProvider;

import ext.ait.util.PropertiesUtil;

public class FileEnumerationInfoProvider implements EnumerationInfoProvider {
	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("customEnum.properties");
	private static final String ENUMERATION_NAME = "CostCenter";

	private static final Locale[] SUPPORTED_LOCALES = { Locale.CHINESE };
	private static final Locale DEFAULT_LOCALE = Locale.CHINESE;

	private EnumerationInfoManager enumInfoManager = null;

	private String parameters = null;

	private EnumerationInfo enumerationInfo = null;

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

	public FileEnumerationInfoProvider() {
	}

	@Override
	public void initialize(EnumerationInfoManager manager, String params) {
		if (manager == null) {
			String errorMsg = "Argument EnumerationInfoManager must be not null.";
			throw new IllegalArgumentException(errorMsg);
		}

		this.enumInfoManager = manager;
		this.parameters = params;
		this.enumerationInfo = createEnumerationInfo();

	}

	private EnumerationInfo createEnumerationInfo() {
		EnumerationInfo enumerationInfo = new EnumerationInfo();
		for (Locale locale : SUPPORTED_LOCALES) {
			enumerationInfo.setLocalizableProperty(EnumerationInfo.DISPLAY_NAME, locale, ENUMERATION_NAME);
			enumerationInfo.setLocalizableProperty(EnumerationInfo.DESCRIPTION, locale,
					ENUMERATION_NAME + " description");
			enumerationInfo.setLocalizableProperty(EnumerationInfo.TOOLTIP, locale, ENUMERATION_NAME + " tooltip");
		}
		enumerationInfo.setNonLocalizableProperty(EnumerationInfo.AUTO_SORT, true);
		enumerationInfo.setNonLocalizableProperty(EnumerationInfo.DEFAULT_LOCALE, DEFAULT_LOCALE);

		Set<EnumerationEntryInfo> enumEntryInfos = createEnumerationEntryInfos();
		enumerationInfo.addEnumerationEntryInfos(enumEntryInfos);

		return enumerationInfo;
	}

	private Set<EnumerationEntryInfo> createEnumerationEntryInfos() {
		Map<String, String> map = pUtil.getAll();
		Set<String> keys = map.keySet();

		Set<EnumerationEntryInfo> enumEntryInfos = new LinkedHashSet<EnumerationEntryInfo>();
		keys.forEach(key -> {
			EnumerationEntryInfo enumEntryInfo = new EnumerationEntryInfo(key);
			for (Locale locale : SUPPORTED_LOCALES) {
				String value = map.get(key);
				String[] values = value.split("/");
				System.out.println("Values:" + values[0] + " : " + values[1]);
				enumEntryInfo.setLocalizableProperty(EnumerationEntryInfo.DISPLAY_NAME, locale, values[0]);
				enumEntryInfo.setLocalizableProperty(EnumerationEntryInfo.DESCRIPTION, locale, values[1]);
				enumEntryInfo.setLocalizableProperty(EnumerationEntryInfo.TOOLTIP, locale, value);
				enumEntryInfo.setLocalizableProperty(EnumerationEntryInfo.PATH_OVERRIDE, locale, null);
			}
			enumEntryInfo.setNonLocalizableProperty(EnumerationEntryInfo.SELECTABLE, Boolean.TRUE);
			enumEntryInfo.setNonLocalizableProperty(EnumerationEntryInfo.SORT_ORDER, null);
			enumEntryInfo.setNonLocalizableProperty(EnumerationEntryInfo.PARENT, null);
			enumEntryInfos.add(enumEntryInfo);
		});
		return enumEntryInfos;
	}
}