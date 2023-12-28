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
import ext.sap.Config;
import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;

public class AlterAttrController implements FormProcessorController {

	@Override
	public FormResult execute(NmCommandBean nmCommandBean) throws WTException {
		Map<String, Object> paramMap = nmCommandBean.getParameterMap();
		String[] numbers = new String[] {};
		String[] newPrices = new String[] {};
		String[] newPriceUnits = new String[] {};
		for (String key : paramMap.keySet()) {
			Object value = paramMap.get(key);
//			String singleValue = value instanceof String[] ? ((String[]) value)[0] : value.toString();
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
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(oid);

			if (matcher.find()) {
				try {
					ReferenceFactory factory = new ReferenceFactory();
					WTPart part = (WTPart) factory.getReference(matcher.group()).getObject();
					partList.add(part);
				} catch (WTRuntimeException e) {
					e.printStackTrace();
				} catch (WTException e) {
					e.printStackTrace();
				}
			}
		}
		return partList;
	}

}
