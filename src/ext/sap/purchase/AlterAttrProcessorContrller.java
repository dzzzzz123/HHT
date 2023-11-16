package ext.sap.purchase;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormProcessorController;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ait.util.PartUtil;
import ext.ait.util.PersistenceUtil;
import ext.sap.Config;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

public class AlterAttrProcessorContrller implements FormProcessorController {

	@Override
	public FormResult execute(NmCommandBean nmCommandBean) throws WTException {
		Map<String, Object> paramMap = nmCommandBean.getParameterMap();
		String[] numbers = new String[] {};
		String[] newPrices = new String[] {};
		String[] newPriceUnits = new String[] {};
		for (String key : paramMap.keySet()) {
			Object value = paramMap.get(key);
			String strValue = value instanceof String[] ? ((String[]) value)[0] : value.toString();
			System.out.println("execute-------key:" + key + " value:" + strValue);
			if (key.equals("number")) {
				numbers = (String[]) value;
			} else if (key.equals("newPrice")) {
				newPrices = (String[]) value;
			} else if (key.equals("newPriceUnit")) {
				newPriceUnits = (String[]) value;
			}
		}
		for (int i = 0; i < numbers.length; i++) {
			String number = numbers[i];
			String newPrice = newPrices[i];
			String newPriceUnit = newPriceUnits[i];
			System.out.println("number:" + number + " newPrice:" + newPrice + " newPriceUnit:" + newPriceUnit);
			if (StringUtils.isNotBlank(newPrice)) {
				Config.setHHT_Price(PartUtil.getWTPartByNumber(number), newPrice);
			}
			if (StringUtils.isNotBlank(newPriceUnit)) {
				Config.setHHT_PriceUnit(PartUtil.getWTPartByNumber(number), newPriceUnit);
			}
		}
		FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
		result.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null, null,
				new String[] { "更新价格属性成功!" }));
		result.setSkipPageRefresh(false);
		return result;
	}

	public static ArrayList<WTPart> processSoid(String[] soid) {
		ArrayList<WTPart> partList = new ArrayList<>();
		for (String oid : soid) {
			String regex = "(?:OR:|VR:)wt\\.part\\.WTPart:(\\d+)";
//			String regex = "(?:OR:)?VR:wt\\.part\\.WTPart:\\d+";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(oid);

			if (matcher.find()) {
				String partOid = matcher.group();
				partOid = partOid.startsWith("VR") ? PartUtil.getORbyVR(partOid) : partOid;
				WTPart part = (WTPart) PersistenceUtil.oid2Object(partOid.split(":")[2]);
				partList.add(part);
			}
		}
		return partList;
	}

}
