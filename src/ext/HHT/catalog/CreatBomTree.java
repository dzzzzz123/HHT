package ext.HHT.catalog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ext.ait.util.CommonUtil;
import ext.ait.util.PartUtil;
import ext.ait.util.PersistenceUtil;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.LineNumber;
import wt.part.Quantity;
import wt.part.QuantityUnit;
import wt.part.SubstituteQuantity;
import wt.part.SubstitutesReplacementType;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.util.WTRuntimeException;

/**
 * bom树形结构导入
 * 
 * @author root
 *
 */
public class CreatBomTree implements RemoteAccess, Serializable {

	private static final Logger logger = LogManager.getLogger(CreatBomTree.class);
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws WTRuntimeException, WTException {
		invoke("ImportData", CreatBomTree.class.getName(), null, new Class[] {}, new Object[] {});
	}

	public static Object invoke(String methodName, String className, Object instance, Class[] cla, Object[] obj) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		try {
			return rms.invoke(methodName, className, instance, cla, obj);
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void ImportData() throws PersistenceException {
		File file = new File("/data/log/warningPart.txt");
		if (file.exists()) {
			file.delete();
		}
		// 查询数据库内容
		List<Map<String, String>> resultList = excuteSql();
		Transaction t = new Transaction();
		try {
			t.start();
			if (resultList == null || resultList.isEmpty()) {
				System.out.println("数据库数据为空");
				return;
			}
			// 根据父编号分组
			Map<String, List<Map<String, String>>> dataMap = resultList.stream()
					.collect(Collectors.groupingBy(obj -> obj.get("WLH") + "|" + obj.get("GC")));
			for (String key : dataMap.keySet()) {
				String[] keys = key.split("\\|");
				// 获取该附件下的子件列表
				List<Map<String, String>> sonList = dataMap.get(key);

				System.out.println("--------number:" + keys[0]);
				System.out.println("--------size:" + sonList.size());

				// 如果找不到父件就跳过处理逻辑
				WTPart part = PartUtil.getWTPartByNumber(keys[0]);
				// || keys[1].equals(part.getViewName())
				if (part == null || sonList == null || sonList.isEmpty()) {
					appendLog("（父）系统中没有此组件:" + key);
					System.out.println("系统中无此编号||视图中无此编号:" + key);
				} else {
					// 查询该组件下的子件
					List<Map<String, String>> sonList100 = sonList.stream().filter(obj -> "100".equals(obj.get("KNX")))
							.collect(Collectors.toList());
					if (sonList100 == null || sonList100.isEmpty()) {
						appendLog("（父）此父件下面无编号:" + key);
						System.out.println("此父件下面无编号" + key);
					} else {
						System.out.println("开始创建需要替代的子件======" + keys[0] + " size:" + sonList100.size());
						for (int i = 0; i < sonList100.size(); i++) {
							Map<String, String> obj = sonList100.get(i);
							// 判断子件是否存在
							WTPart bomZj = PartUtil.getWTPartByNumber(obj.get("BOMZJ"));
							if (bomZj == null) {
								appendLog("（父）:" + key + " （子）:" + obj.get("BOMZJ"));
								System.out.println("子件不存在，不做处理");
							} else {
								System.out.println("100的子件：" + bomZj.getNumber());
								// 查询是否存在关联关系
								WTPartUsageLink uLink = PartUtil.getLinkByPart(part, bomZj);
								System.out.println("是否有连接：" + (uLink == null));

								Double yl = StringUtils.isBlank(obj.get("YL")) ? null
										: Double.parseDouble(obj.get("YL"));
								Long hhLong = StringUtils.isBlank(obj.get("BOMXM")) ? null
										: Long.parseLong(obj.get("BOMXM"));
								String unit = obj.get("DW");
								if (uLink == null) {
									uLink = createWtpartToLink(part, bomZj, hhLong, yl, unit);
								} else {
									UpdateLink(part, uLink, hhLong, yl, unit);
								}

								// 查询是否有替代项
								List<Map<String, String>> tdxList = sonList.stream()
										.filter(tdx -> StringUtils.equals(tdx.get("DTX"), obj.get("DTX"))
												&& "0".equals(tdx.get("KNX")))
										.collect(Collectors.toList());
								System.out.println("是否有代替项：" + tdxList.size());

								if (tdxList != null && !tdxList.isEmpty()) {
									for (int j = 0; j < tdxList.size(); j++) {
										Map<String, String> objDtx = tdxList.get(j);
										WTPart bomZj1 = PartUtil.getWTPartByNumber(objDtx.get("BOMZJ"));
										if (bomZj1 != null) {
											List<WTPartSubstituteLink> dtLink = PartUtil
													.getWTPartSubstituteLinks(uLink);
											System.out.println("是否有替换连接：" + dtLink.isEmpty());

											Double ylTd = StringUtils.isBlank(objDtx.get("YL")) ? null
													: Double.parseDouble(objDtx.get("YL"));
											String hhLongTd = objDtx.get("BOMXM");
											String unitTd = objDtx.get("DW");
											if (dtLink == null || dtLink.isEmpty()) {
												System.out.println("============f:" + keys[0]);

												System.out.println("=============s:" + objDtx.get("BOMZJ"));
												createWTPartAlternateLink(uLink, bomZj1, ylTd, unitTd, hhLongTd);
											} else {
												// 判断替换件是否有当前物料
												List<WTPartSubstituteLink> count = dtLink.stream().filter(objLink -> {
													WTPartMaster master = (WTPartMaster) objLink.getRoleBObject();
													System.out.println("==1:" + master.getNumber());
													System.out.println("==2:" + objDtx.get("BOMZJ"));

													return StringUtils.equals(master.getNumber(), bomZj1.getNumber());
												}).collect(Collectors.toList());
												if (count != null && count.size() > 0) {
													// 存在更新逻辑
													UpdateSubstituteLink(count.get(0), hhLongTd, ylTd, unitTd);

												} else {
													// 不存在创建逻辑
													createWTPartAlternateLink(uLink, bomZj1, ylTd, unitTd, hhLongTd);
												}

											}
										} else {
											appendLog("（父）:" + key + " （子）:" + objDtx.get("BOMZJ"));
										}
									}
								}
							}

						}
					}

					List<Map<String, String>> sonListNull = sonList.stream()
							.filter(obj -> StringUtils.isBlank(obj.get("KNX"))).collect(Collectors.toList());
					if (sonListNull != null && !sonListNull.isEmpty()) {
						System.out.println("开始创建无需替代的子件======" + keys[0]);

						for (int i = 0; i < sonListNull.size(); i++) {
							Map<String, String> obj = sonListNull.get(i);
							// 判断子件是否存在
							WTPart bomZj = PartUtil.getWTPartByNumber(obj.get("BOMZJ"));
							if (bomZj == null) {
								appendLog("（父）:" + key + " （子）:" + obj.get("BOMZJ"));
								System.out.println("子件不存在，不做处理");
							} else {
								// 查询是否存在关联关系
								WTPartUsageLink uLink = PartUtil.getLinkByPart(part, bomZj);
								Double yl = StringUtils.isBlank(obj.get("YL")) ? null
										: Double.parseDouble(obj.get("YL"));
								Long hhLong = StringUtils.isBlank(obj.get("BOMXM")) ? null
										: Long.parseLong(obj.get("BOMXM"));
								String unit = obj.get("DW");
								if (uLink == null) {
									createWtpartToLink(part, bomZj, hhLong, yl, unit);
								} else {
									UpdateLink(part, uLink, hhLong, yl, unit);
								}
							}

						}
					}
					if (PersistenceUtil.isCheckOut(part)) {
						// 检入
						PersistenceUtil.checkinObj(part);
					}

				}
			}

			System.out.println("执行完毕，清查看日志是否有错误信息");
			appendLog("===================success===================");
		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
			System.out.println("程序异常:" + e);
		} finally {
			t.commit();
		}
	}

	public static void appendLog(String log) {
		SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String path = "/data/log/warningPart.txt";
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

	public static WTPartUsageLink createWtpartToLink(WTPart parent, WTPart son, Long number, Double amount, String unit)
			throws Exception {
		System.out.println("======创建子件======" + "父：" + parent.getNumber() + " 子:" + son.getNumber());
		try {
			parent = PartUtil.getWTPartByNumber(parent.getNumber());
			WTPartUsageLink link = new WTPartUsageLink();

			if (number != null) {
				LineNumber lineNumber = new LineNumber();
				lineNumber.setValue(number);
				link.setLineNumber(lineNumber);
			}
			Quantity quantity = new Quantity();
			if (amount != null) {
				quantity.setAmount(amount);
			}
			if (StringUtils.isNotBlank(unit)) {
				QuantityUnit quantityUnit = QuantityUnit.toQuantityUnit(unit);
				quantity.setUnit(quantityUnit);
			}

			link.setQuantity(quantity);
			if (!PersistenceUtil.isCheckOut(parent)) {
				parent = (WTPart) PersistenceUtil.checkoutObj(parent);
			}
			link.setUsedBy(parent);
			link.setUses(son.getMaster());
			link = (WTPartUsageLink) wt.fc.PersistenceHelper.manager.save(link);
			return link;
		} catch (Exception e) {
			throw e;
		}
	}

	public static void UpdateLink(WTPart parent, WTPartUsageLink usageLink, Long number, Double amount, String dw)
			throws Exception {
		try {
			System.out.println("修改单位：" + dw);
			if (number != null) {
				LineNumber lineNumber = new LineNumber();
				lineNumber.setValue(number);
				usageLink.setLineNumber(lineNumber);
			}
			if (amount != null || StringUtils.isNotBlank(dw)) {
				Quantity quantity = new Quantity();
				if (amount != null) {
					quantity.setAmount(amount);
				}
				if (StringUtils.isNotBlank(dw)) {
					QuantityUnit quantityUnit = QuantityUnit.toQuantityUnit(dw);
					quantity.setUnit(quantityUnit);
				}

				usageLink.setQuantity(quantity);
			}
			if (!PersistenceUtil.isCheckOut(parent)) {
				PersistenceUtil.checkoutObj(parent);
			}
			PersistenceServerHelper.update(usageLink);
		} catch (Exception e) {
			throw e;
		}
	}

	public static void createWTPartAlternateLink(WTPartUsageLink usageLink, WTPart part, Double amout, String unit,
			String wh) throws Exception {
		System.out.println("======创建子件======" + "父：" + usageLink.getFindNumber() + " 子:" + part.getNumber());

		try {
			WTPartSubstituteLink link = new WTPartSubstituteLink();
			link.setRoleAObject(usageLink);
			link.setRoleBObject(part.getMaster());
			SubstitutesReplacementType defaultType = link.getReplacementType().getSubstitutesReplacementTypeDefault();
			link.setReplacementType(defaultType);
			if (amout != null || StringUtils.isNotBlank(unit)) {
				SubstituteQuantity quantity = new SubstituteQuantity();
				if (amout != null) {
					quantity.setAmount(amout);
				}
				if (StringUtils.isNotBlank(unit)) {
					QuantityUnit quantityUnit = QuantityUnit.toQuantityUnit(unit);
					quantity.setUnit(quantityUnit);
				}

				link.setQuantity(quantity);
			}
			if (StringUtils.isNotBlank(wh)) {
				link.setReferenceDesignator(wh);
			}

			PersistenceHelper.manager.save(link);
		} catch (Exception e) {
			throw e;
		}
	}

	public static void UpdateSubstituteLink(WTPartSubstituteLink link, String number, Double amount, String dw)
			throws Exception {
		try {
			System.out.println("修改替换link:" + dw);
			if (amount != null || StringUtils.isNotBlank(dw)) {
				SubstituteQuantity quantity = new SubstituteQuantity();
				quantity.setAmount(amount);
				QuantityUnit quantityUnit = QuantityUnit.toQuantityUnit(dw);
				quantity.setUnit(quantityUnit);
				link.setQuantity(quantity);
			}
			if (StringUtils.isNotBlank(number)) {
				link.setReferenceDesignator(number);
			}
//			if (!PersistenceUtil.isCheckOut(parent)) {
//				PersistenceUtil.checkoutObj(parent);
//			}
			PersistenceServerHelper.update(link);
		} catch (Exception e) {
			throw e;
		}
	}

	public static List<Map<String, String>> excuteSql() {
		// 查询数据库内容
		String sql = "SELECT GC,WLH,BOMXM,BOMZJ,YL,DW,DTX,KNX from BOMDATAINFO";
		ResultSet resultSet = CommonUtil.excuteSelect(sql);
		List<Map<String, String>> resultList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				Map<String, String> result = new HashMap<String, String>();
				result.put("GC", resultSet.getString("GC"));
				result.put("WLH", resultSet.getString("WLH"));
				result.put("BOMXM", resultSet.getString("BOMXM"));
				result.put("BOMZJ", resultSet.getString("BOMZJ"));
				result.put("YL", resultSet.getString("YL"));
				result.put("DW", resultSet.getString("DW"));
				result.put("DTX", resultSet.getString("DTX"));
				result.put("KNX", resultSet.getString("KNX"));
				resultList.add(result);
			}
			resultSet.close();
		} catch (Exception e) {
			return null;
		}
		return resultList;
	}

}
