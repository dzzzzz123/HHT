package ext.HHT.part.duplicateCheck.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.HHT.Config;

public class BondedCheckService {

	public static String process(NmCommandBean nmCommandBean) {
		Map<String, Object> paramMap = nmCommandBean.getParameterMap();
		String NonbondedNumber = Config.getIBA_NonbondedNumber();
		String HHT_Bonded = Config.getIBA_HHT_Bonded();
		String NonbondedNumberValue = "";
		String HHT_BondedValue = "";

		for (String key : paramMap.keySet()) {
			Object value = paramMap.get(key);
			String strValue = value instanceof String[] ? ((String[]) value)[0] : value.toString();
			if (key.contains(HHT_Bonded) && !key.endsWith("old")) {
				HHT_BondedValue = strValue;
			} else if (key.contains(NonbondedNumber) && !key.endsWith("old")) {
				NonbondedNumberValue = strValue;
			}
		}

		if (HHT_BondedValue.equals("True") && StringUtils.isBlank(NonbondedNumberValue)) {
			return "当是否保税为'是'时，非保税料号为必填项！";
		}

		return "";
	}

}
