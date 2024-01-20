package ext.HHT.catalog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ait.util.CommonUtil;
import ext.ait.util.ContainerUtil;
import ext.ait.util.PartUtil;
import ext.ait.util.PersistenceUtil;
import ext.sap.Config;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;
import wt.session.SessionHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;

public class UpdateProductCatalogProcessor extends DefaultObjectFormProcessor {
	@Override
	public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1) throws WTException {
		FormResult formresult = null;
		WTPart wtPart = WTPart.class.cast((WTObject) arg0.getPrimaryOid().getRef());
		wtPart = PartUtil.getWTPartByNumber(wtPart.getNumber());
		System.out.println("当前组件信息:" + wtPart.getNumber());
		if (!StringUtils.equals("鸿合产品目录", wtPart.getName())) {
			formresult = new FormResult(FormProcessingStatus.FAILURE);
			formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null,
					null, new String[] { "非鸿合产品目录" }));
			return formresult;
		}
		// 查询数据库内容
		String sql = "SELECT CPX_NUMBER,CPX,CPXL_NUMBER,XL,PP,XHGG,CPBM,CPLH,CPWLMS from PRODUCTCATALOG";
		ResultSet resultSet = CommonUtil.excuteSelect(sql);
		List<Map<String, String>> resultList = new ArrayList<>();
		// Transaction t = new Transaction();
		try {
			// t.start();
			while (resultSet.next()) {
				Map<String, String> result = new HashMap<String, String>();
				result.put("CPX_NUMBER", resultSet.getString("CPX_NUMBER"));
				result.put("CPX", resultSet.getString("CPX"));
				result.put("CPXL_NUMBER", resultSet.getString("CPXL_NUMBER"));
				result.put("XL", resultSet.getString("XL"));
				result.put("PP", resultSet.getString("PP"));
				result.put("XHGG", resultSet.getString("XHGG"));
				result.put("CPBM", resultSet.getString("CPBM"));
				result.put("CPLH", resultSet.getString("CPLH"));
				result.put("CPWLMS", resultSet.getString("CPWLMS"));
				resultList.add(result);
			}

			resultSet.close();
			if (resultList == null || resultList.isEmpty()) {
				formresult = new FormResult(FormProcessingStatus.FAILURE);
				formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null,
						null, new String[] { "查询数据库表为空，请维护产品目录数据" }));
				return formresult;
			}
			long count = resultList.stream().filter(obj -> {
				boolean falg = StringUtils.isBlank(obj.get("CPX_NUMBER")) || StringUtils.isBlank(obj.get("CPXL_NUMBER"))
						|| StringUtils.isBlank(obj.get("XHGG")) || StringUtils.isBlank(obj.get("CPX"))
						|| StringUtils.isBlank(obj.get("XL")) || StringUtils.isBlank(obj.get("PP"))
						|| StringUtils.isBlank(obj.get("CPBM"));
				return falg;
			}).count();
			if (count > 0) {
				formresult = new FormResult(FormProcessingStatus.FAILURE);
				formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null,
						null, new String[] { "数据库数据维护错误，请检查" }));
				return formresult;
			}

			// 获取鸿合产品目录下的数据 1:产品线
			List<WTPart> cpxlist = PartUtil.getAllBomByPart(wtPart);
			Map<String, List<Map<String, String>>> dataMap = resultList.stream().filter(
					obj -> StringUtils.isNotBlank(obj.get("CPX_NUMBER")) && StringUtils.isNotBlank(obj.get("CPX")))
					.collect(Collectors.groupingBy(obj -> obj.get("CPX_NUMBER")));
			if (dataMap == null || dataMap.isEmpty()) {
				formresult = new FormResult(FormProcessingStatus.FAILURE);
				formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null,
						null, new String[] { "数据库数据格式维护错误" }));
				return formresult;
			}

			for (String key : dataMap.keySet()) {
				List<Map<String, String>> mapList = dataMap.get(key);
				// 产品线
				List<WTPart> cpxWtList = cpxlist.stream().filter(obj -> obj.getNumber().equals(key))
						.collect(Collectors.toList());

				// 产品线WTPart
				WTPart cpxWtPart = null;
				if (cpxWtList == null || cpxWtList.isEmpty()) {
					// 创建产品线
					WTPart newWtPart = WTPart.newWTPart();
					newWtPart.setName(mapList.get(0).get("CPX") == null ? "" : mapList.get(0).get("CPX"));
					newWtPart.setNumber(key);
					cpxWtPart = createWtpartToLink(newWtPart, wtPart, "com.honghe_tech.HHT_ProductLine",
							mapList.get(0).get("CPX"), mapList.get(0).get("PP"));
				} else {
					// 修改产品线
					cpxWtPart = cpxWtList.get(0);
					WTPartUsageLink link = PartUtil.getLinkByPart(wtPart, cpxWtPart);
					if (link == null) {
						createWtpartToLink(cpxWtPart, wtPart, "com.honghe_tech.HHT_ProductLine",
								mapList.get(0).get("CPX"), mapList.get(0).get("PP"));
					}

					PartUtil.changePartName(cpxWtPart, mapList.get(0).get("CPX"));
				}

				// 产品系列编码
				Map<String, List<Map<String, String>>> cpxlMap = mapList.stream().filter(
						obj -> StringUtils.isNotBlank(obj.get("CPXL_NUMBER")) && StringUtils.isNotBlank(obj.get("XL")))
						.collect(Collectors.groupingBy(obj -> obj.get("CPXL_NUMBER")));
				for (String cpxlKey : cpxlMap.keySet()) {
					List<Map<String, String>> cpxlList = cpxlMap.get(cpxlKey);
					// 产品系列
					List<WTPart> cpxlWtList = cpxlist.stream().filter(obj -> obj.getNumber().equals(cpxlKey))
							.collect(Collectors.toList());
					// 产品系列WTPart
					WTPart cpxlWtPart = null;
					if (cpxlWtList == null || cpxlWtList.isEmpty()) {
						// 创建产品系列
						WTPart newWtPart = WTPart.newWTPart();
						newWtPart.setName(cpxlList.get(0).get("XL") == null ? "" : cpxlList.get(0).get("XL"));
						newWtPart.setNumber(cpxlKey);
						cpxlWtPart = createWtpartToLink(newWtPart, cpxWtPart, "com.honghe_tech.HHT_ProductFamily",
								cpxlList.get(0).get("CPX"), cpxlList.get(0).get("PP"));
					} else {
						// 修改产品系列
						cpxlWtPart = cpxlWtList.get(0);
						WTPartUsageLink link = PartUtil.getLinkByPart(cpxWtPart, cpxlWtPart);
						if (link == null) {
							createWtpartToLink(cpxlWtPart, cpxWtPart, "com.honghe_tech.HHT_ProductFamily",
									cpxlList.get(0).get("CPX"), cpxlList.get(0).get("PP"));
						}
						PartUtil.changePartName(cpxlWtPart, cpxlList.get(0).get("XL"));

					}

					// 品牌+型号+产品编码
					// 产品系列编码
					Map<String, List<Map<String, String>>> cpxMap = cpxlList.stream()
							.filter(obj -> StringUtils.isNotBlank(obj.get("XHGG")))
							.collect(Collectors.groupingBy(obj -> obj.get("XHGG")));

					for (String cpxKey : cpxMap.keySet()) {
						System.out.println("=====创建产品系列编码:" + cpxKey);
						List<Map<String, String>> cpxList = cpxMap.get(cpxKey);
						List<WTPart> phcWtList = cpxlist.stream().filter(obj -> obj.getNumber().equals(cpxKey))
								.collect(Collectors.toList());
						if (phcWtList == null || phcWtList.isEmpty()) {
							// 创建产品类型
							WTPart newWtPart = WTPart.newWTPart();
							wtPart = PartUtil.getWTPartByNumber(wtPart.getNumber());
							newWtPart.setName(cpxList.get(0).get("CPBM") == null ? "" : cpxList.get(0).get("CPBM"));
							newWtPart.setNumber(cpxKey);
							newWtPart = createWtpartToLink(newWtPart, cpxlWtPart, "com.honghe_tech.HHT_ProductType",
									mapList.get(0).get("CPX"), mapList.get(0).get("PP"));

							for (int i = 0; i < cpxList.size(); i++) {
								Map<String, String> cpwlMap = cpxList.get(i);
								String cplh = cpwlMap.get("CPLH");
								WTPart cplhWtPart = PartUtil.getWTPartByNumber(cplh);
								if (cplhWtPart != null) {
									createWtpartToLink(newWtPart, cplhWtPart);
								} else {
									appendLog("异常物料:" + cplh);
								}

							}
							if (PersistenceUtil.isCheckOut(newWtPart)) {
								PersistenceUtil.checkinObj(newWtPart);
							}

						} else {
							// 修改产品类型
							WTPart updatePart = phcWtList.get(0);
							WTPartUsageLink link = PartUtil.getLinkByPart(cpxlWtPart, updatePart);
							if (link == null) {
								createWtpartToLink(updatePart, cpxlWtPart, "com.honghe_tech.HHT_ProductType",
										mapList.get(0).get("CPX"), mapList.get(0).get("PP"));
							}

							for (int i = 0; i < cpxList.size(); i++) {
								Map<String, String> cpwlMap = cpxList.get(i);
								String cplh = cpwlMap.get("CPLH");
								WTPart cplhWtPart = PartUtil.getWTPartByNumber(cplh);
								if (cplhWtPart != null) {
									WTPartUsageLink cplhLink = PartUtil.getLinkByPart(updatePart, cplhWtPart);
									if (cplhLink == null) {
										createWtpartToLink(updatePart, cplhWtPart);
									}

								} else {
									appendLog("异常物料:" + cplh);
								}

							}
							if (PersistenceUtil.isCheckOut(updatePart)) {
								PersistenceUtil.checkinObj(updatePart);
							}
							// PartUtil.changePartType(updatePart, cpxList.get(0).get("PP"));

							PartUtil.changePartName(updatePart, cpxList.get(0).get("CPBM"));

						}
					}

					// 产品类型检入
					if (PersistenceUtil.isCheckOut(cpxlWtPart)) {
						PersistenceUtil.checkinObj(cpxlWtPart);
					}

				}

				// 产品线检入
				if (PersistenceUtil.isCheckOut(cpxWtPart)) {
					PersistenceUtil.checkinObj(cpxWtPart);
				}

			}

			// 判断物料号是否存在
//			resultList.forEach(obj -> {
//				String cplh = obj.get("CPLH");
//				List<WTPart> list = cpxlist.stream().filter(part -> part.getNumber().equals(cplh))
//						.collect(Collectors.toList());
//				if (list == null || list.isEmpty()) {
//					String cpxbm = obj.get("CPX_NUMBER");
//					String cpx = obj.get("CPX");
//					String cpxlbm = obj.get("CPXL_NUMBER");
//					String xl = obj.get("XL");
//					String pp = obj.get("PP");
//					String xhgg = obj.get("XHGG");
//					String cpbm = obj.get("CPBM");
//					String cplh1 = obj.get("CPLH");
//					String cplhms = obj.get("CPWLMS");
//					appendLog("异常物料:" + cpxbm + " " + cpx + " " + cpxlbm + " " + xl + " " + pp + " " + xhgg + " " + cpbm
//							+ " " + cplh1 + " " + cplhms);
//				} else {
//					WTPart part = list.get(0);
//					PartUtil.changePartName(part, obj.get("CPWLMS"));
//				}
//
//			});
			appendLog("===========");
			appendLog("===========");
			appendLog("===========");

			// 产品目录检入
			PersistenceUtil.checkinObj(wtPart);

			formresult = new FormResult(FormProcessingStatus.SUCCESS);
			formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null,
					null, new String[] { "导入成功!" }));
			return formresult;
		} catch (

		Exception e) {
			e.printStackTrace();
			// t.rollback();
		} finally {
			// t.commit();
		}
		return formresult;
	}

	public static WTPartUsageLink createWtpartToLink(WTPart parent, WTPart son) throws Exception {
		System.out.println("======创建子件======" + "父：" + parent.getNumber() + " 子:" + son.getNumber());
		try {
			parent = ext.ait.util.PartUtil.getWTPartByNumber(parent.getNumber());
			if (!PersistenceUtil.isCheckOut(parent)) {
				parent = (WTPart) PersistenceUtil.checkoutObj(parent);
			}
			WTPartUsageLink link = new WTPartUsageLink();
			link.setUsedBy(parent);
			link.setUses(son.getMaster());
			link = (WTPartUsageLink) wt.fc.PersistenceHelper.manager.save(link);
			return link;
		} catch (Exception e) {
			throw e;
		}
	}

	public static void appendLog(String log) {
		SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String path = "" + "/warning.txt";
		FileWriter writer = null;
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			writer = new FileWriter(path, true);
			writer.write(si.format(new Date()) + log + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static WTPart createWtpartToLink(WTPart son, WTPart parent, String type, String path, String pp)
			throws Exception {
		appendLog("父编号:" + parent.getNumber() + "  子编号:" + son.getNumber() + " path:" + path);
		System.out.println("======创建子件======");
		switch (path) {
		case "平板":
			path = "01-平板";
			break;
		case "黑板":
			path = "02-黑板";
			break;
		case "白板":
			path = "03-白板";
			break;
		case "电子班牌":
			path = "04-电子班牌";
			break;
		case "展台":
			path = "05-展台";
			break;
		case "录播":
			path = "06-录播";
			break;
		default:
			path = "09-其他";
			break;
		}
		try {
			parent = PartUtil.getWTPartByNumber(parent.getNumber());
			WTPartUsageLink link = new WTPartUsageLink();

			WTPart oldSon = PartUtil.getWTPartByNumber(son.getNumber());
			if (oldSon != null) {
				son = oldSon;
			} else {
				TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(type);
				son.setTypeDefinitionReference(tdr);
//				String location = FolderHelper.getLocation((CabinetBased) parent);
//				WTContainer container2 = WTContainerHelper.getContainer((WTContained) parent);
//				WTContainerRef containerRef = WTContainerRef.newWTContainerRef(container2);
//				FolderHelper.assignLocation((FolderEntry) son, location, containerRef);
				WTContainer container = parent.getContainer();
				son.setContainer(container);
				Folder folder = ContainerUtil.getFolder(path, container);
				FolderHelper.assignLocation((FolderEntry) son, (Folder) folder);
				son = (WTPart) wt.fc.PersistenceHelper.manager.save(son);
				Config.setHHT_Brand(son, pp);
			}
			if (!PersistenceUtil.isCheckOut(parent)) {
				parent = (WTPart) PersistenceUtil.checkoutObj(parent);
			}
			link.setUsedBy(parent);
			link.setUses(son.getMaster());
			wt.fc.PersistenceHelper.manager.save(link);
			return son;
		} catch (Exception e) {
			throw e;
		}
	}
}
