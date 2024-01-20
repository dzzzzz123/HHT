package ext.sap.masterData;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ext.ait.util.CommonUtil;
import ext.ait.util.PersistenceUtil;
import ext.sap.Config;
import ext.sap.masterData.mvc.Result;
import wt.fc.WTObject;
import wt.part.WTPart;

public class PartSenderHelper {

	/**
	 * 在工作流中发送物料主数据到SAP
	 * 
	 * @param pbo
	 * @param self
	 * @throws Exception
	 */
	public static String sendParts2SAPInProgress(WTObject pbo) throws Exception {
		List<WTPart> list = CommonUtil.getListFromPBO(pbo, WTPart.class);
		List<Result> data2Present = list.stream().map(PartSenderHelper::getResFromSAP).collect(Collectors.toList());
		return CommonUtil.getJsonFromObject(data2Present);
	}

	/**
	 * 发送物料主数据到SAP的主方法
	 * 
	 * @param pbo
	 * @return
	 * @throws Exception
	 */
	public static List<String> sendParts2SAP(WTObject pbo) {
		List<WTPart> list = CommonUtil.getListFromPBO(pbo, WTPart.class);
		List<String> msg = new ArrayList<>();
		for (WTPart part : list) {
			if (PersistenceUtil.isCheckOut(part)) {
				msg.add("该部件是检出状态!请先检入该部件后操作!");
			}
			SendSAPPartEntity entity = SendSAPService.SendSAPPart(part);
			String json = SendSAPService.entityToJson(entity);
			String result = SendSAPService.sendPartSAP(json);
			String SAPResult = SendSAPService.getResultFromJson(result);
			if (StringUtils.isNotBlank(SAPResult)) {
				msg.add(part.getNumber() + "未发送成功，错误信息为： " + SAPResult);
			} else {
				Config.setHHT_SapMark(part, "X");
			}
		}
		return msg;
	}

	/**
	 * 从部件中获取需要展示在Windchill页面上的信息
	 * 
	 * @param part
	 * @return
	 * @throws Exception
	 */
	public static Result getResFromSAP(WTPart part) {

		SendSAPPartEntity entity = SendSAPService.SendSAPPart(part);
		String json = SendSAPService.entityToJson(entity);
		String result = SendSAPService.sendPartSAP(json);
		String SAPResult = SendSAPService.getResultFromJson(result);

		// 构建显示在流程中的数据对象
		Result resultData = new Result();
		resultData.setNumber(part.getNumber());
		resultData.setName(part.getName());
		resultData.setSapMark(Config.getHHT_SapMark(part));
		if (StringUtils.isNotBlank(SAPResult)) {
			resultData.setResult("ERROR");
			resultData.setMsg(SAPResult);
		} else {
			resultData.setResult("SUCCESS");
			resultData.setMsg(part.getNumber() + "物料创建或修改成功！");
			Config.setHHT_SapMark(part, "X");
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