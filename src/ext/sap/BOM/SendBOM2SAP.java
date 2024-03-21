package ext.sap.BOM;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ext.ait.util.CommonUtil;
import ext.ait.util.PartUtil;
import ext.sap.Config;
import ext.sap.BOM.mvc.Result;
import wt.fc.WTObject;
import wt.part.WTPart;

public class SendBOM2SAP {

	/**
	 * 在工作流中发送BOM到SAP
	 * 
	 * @param pbo
	 * @param self
	 * @throws Exception
	 */
	public static String sendParts2SAPInProgress(WTObject pbo) throws Exception {
		List<WTPart> list = CommonUtil.getListFromPBO(pbo, WTPart.class);
		List<WTPart> listIncludeBOM = new ArrayList<>();
		List<WTPart> listFiltered = new ArrayList<>();
		// 将BOM和下层BOM都放入其中
		listIncludeBOM.addAll(list);
		// 因为稀奇古怪的问题（下层BOM与上层BOM的历史导致ECN编号获取存在问题）所以现在先屏蔽递归下层子BOM的代码
		// list.forEach(part -> {
		// listIncludeBOM.addAll(PartUtil.getAllBomByPart(part));
		// });
		// 过滤部件，判断是否为BOM
		listIncludeBOM.stream().filter(SendBOM2SAP::checkBOM).forEach(listFiltered::add);
		// 将分类属性开头为7的先发送,开头为5的后发送
		listFiltered.sort(new Comparator<WTPart>() {
			@Override
			public int compare(WTPart o1, WTPart o2) {
				int class1 = Integer.valueOf(Config.getHHT_Classification(o1).substring(0, 1));
				int class2 = Integer.valueOf(Config.getHHT_Classification(o2).substring(0, 1));
				if (class1 == 7 && class2 != 7) {
					return -1;
				} else if (class1 != 7 && class2 == 7) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		List<Result> data2Present = listFiltered.stream().map(SendBOM2SAP::getResFromSAP).collect(Collectors.toList());
		return CommonUtil.getJsonFromObject(data2Present);
	}

	/**
	 * 向SAP发送BOM数据的主方法
	 * 
	 * @param WTObject obj
	 */
	public static List<String> sendListBOM2SAP(WTObject obj) {
		List<WTPart> list = CommonUtil.getListFromPBO(obj, WTPart.class);
		List<WTPart> listIncludeBOM = new ArrayList<>();
		List<WTPart> listFiltered = new ArrayList<>();
		List<String> msg = new ArrayList<>();
		// 将BOM和下层BOM都放入其中
		listIncludeBOM.addAll(list);
		// 因为稀奇古怪的问题（下层BOM与上层BOM的历史导致ECN编号获取存在问题）所以现在先屏蔽递归下层子BOM的代码
		// list.forEach(part -> {
		// listIncludeBOM.addAll(PartUtil.getAllBomByPart(part));
		// });
		// 过滤部件，判断是否为BOM
		listIncludeBOM.stream().filter(SendBOM2SAP::checkBOM).forEach(listFiltered::add);
		// 从部件中获取BOM实体类，并逐个发送给SAP
		listFiltered.forEach(part -> {
			BOMEntity bomEntity = SendBOM2SAPService.getBOMEntity(part);
			String json = SendBOM2SAPService.getJsonByEntity(bomEntity);
			String result = SendBOM2SAPService.SendBOM2SAPUseUrl(json);
			msg.add(SendBOM2SAPService.getResultFromJson(result));
		});
		return msg;
	}

	/**
	 * 判断部件是否是BOM
	 * 
	 * @param part
	 * @return
	 */
	public static boolean checkBOM(WTPart part) {
		List<WTPart> BOMList = PartUtil.getBomByPart(part);
		return BOMList.size() > 0;
	}

	/**
	 * 从部件中获取需要展示在Windchill页面上的信息
	 * 
	 * @param part
	 * @return
	 * @throws Exception
	 */
	public static Result getResFromSAP(WTPart part) {

		BOMEntity bomEntity = SendBOM2SAPService.getBOMEntity(part);
		String json = SendBOM2SAPService.getJsonByEntity(bomEntity);
		String result = SendBOM2SAPService.SendBOM2SAPUseUrl(json);
		String SAPResult = SendBOM2SAPService.getResultFromJson(result);

		// 构建显示在流程中的数据对象
		Result resultData = new Result();
		resultData.setNumber(part.getNumber());
		resultData.setName(part.getName());
		if (StringUtils.isNotBlank(SAPResult)) {
			resultData.setResult("ERROR");
			resultData.setMsg(SAPResult);
		} else {
			resultData.setResult("SUCCESS");
			resultData.setMsg(part.getNumber() + "BOM创建或修改成功！");
		}

		ZoneId zoneId = ZoneId.of("Asia/Shanghai");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime currentTime = LocalDateTime.now(zoneId);
		String formattedTime = currentTime.format(formatter);
		resultData.setTime(formattedTime);
		return resultData;
	}

	/**
	 * 校验sap给出的信息中是否存在错误信息
	 * 
	 * @param res
	 * @return error/ok
	 */
	public static String checkSAPRes(String res) {
		List<Result> results = CommonUtil.getEntitiesFromJson(res, Result.class, "");
		for (Result result : results) {
			if (result.getResult().equalsIgnoreCase("ERROR")) {
				return "error";
			}
		}
		return "ok";
	}

}
