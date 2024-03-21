package ext.classification.service;

import org.apache.commons.lang3.StringUtils;

import ext.ait.util.CommonUtil;
import ext.ait.util.PropertiesUtil;
import wt.fc.PersistenceHelper;
import wt.part.WTPart;
import wt.part.WTPartMaster;

/**
 * 根据部件的分类属性的内部名称来获取新的物料名称并设置值
 * 
 * @author dz
 *
 */
public class ClassificationName {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("nameConfig.properties");

	public static String process(WTPart part) {
		String result = "";
		String classInternalName = pUtil.getValueByKey(part, "iba.internal.HHT_Classification");
		String pattern = pUtil.getValueByKey(classInternalName);
		if (StringUtils.isBlank(pattern)) {
			return "当前分类 " + classInternalName + " 在配置文件中不存在!\r\n";
		}
		String newName = Util.processPartten(pattern, part);
		changePartName(part, newName);
		return result;
	}

	/**
	 * 因为暂不清楚的原因,现在修改部件名称的方法直接对数据库进行操作
	 * 
	 * @param WTPart
	 * @param String
	 */
	private static void changePartName(WTPart part, String newName) {
		try {
			String sql = "UPDATE WTPARTMASTER SET NAME = ? WHERE IDA2A2 = ?";
			WTPartMaster master = part.getMaster();
			String oid = String.valueOf(master.getPersistInfo().getObjectIdentifier().getId());
			CommonUtil.excuteUpdate(sql, newName, oid);
			PersistenceHelper.manager.refresh(part);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
