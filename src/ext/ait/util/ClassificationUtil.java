package ext.ait.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.ptc.core.lwc.common.dynamicEnum.EnumerationEntryInfo;
import com.ptc.core.lwc.server.LWCEnumerationEntryValuesFactory;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.DataSet;
import com.ptc.core.meta.common.EnumeratedSet;
import com.ptc.core.meta.common.EnumerationEntryIdentifier;
import com.ptc.core.meta.common.OperationIdentifier;
import com.ptc.core.meta.common.OperationIdentifierConstants;
import com.ptc.core.meta.container.common.AttributeTypeSummary;
import com.ptc.windchill.csm.common.CsmConstants;

import wt.facade.classification.ClassificationFacade;
import wt.meta.LocalizedValues;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.pom.WTConnection;
import wt.session.SessionHelper;
import wt.util.WTException;

public class ClassificationUtil implements RemoteAccess {

	/**
	 * 获取部件的分类属性的枚举值属性值的显示名称（在之前调用中出现了一些问题，但是其他程序调用是没有问题的）
	 * 
	 * @param part    需要获取属性的部件
	 * @param ibaName 分类属性值的内部名称
	 * @return 显示名称
	 */
	public static String getEnumDisplay(WTPart part, String ibaName) {
		String displayName = "";
		try {
			Locale loc = SessionHelper.manager.getLocale();
			PersistableAdapter adapter = new PersistableAdapter(part, null, loc,
					OperationIdentifier.newOperationIdentifier(OperationIdentifierConstants.VIEW));
			SessionHelper.manager.setAdministrator();
			// HHT_Classification是分类属性映射到部件上面的内部名称
			adapter.load(new String[] { "HHT_Classification", ibaName });
			Object obj = (String) adapter.get(ibaName);

			AttributeTypeSummary ats_csm = adapter.getAttributeDescriptor(ibaName);
			DataSet lvs_csm = ats_csm.getLegalValueSet();
			if (lvs_csm != null) {
				if (lvs_csm instanceof EnumeratedSet) {
					EnumerationEntryIdentifier eei = ((EnumeratedSet) lvs_csm).getElementByKey(obj.toString());
					LWCEnumerationEntryValuesFactory eevf = new LWCEnumerationEntryValuesFactory();
					LocalizedValues valueLocale = eevf.get(eei, loc);
					displayName = valueLocale.getDisplay();
				}
			} else {
				System.out.println("lvs_csm is null");
				return displayName;
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return displayName;
	}

	/**
	 * 根据查询数据库从的内部名称获取到部件对应枚举值的显示名称 Manufacturing->ZeroStickers->零贴
	 * 
	 * @param part     数据源部件
	 * @param internal 分类属性
	 * @return 显示名称
	 */
	public static String getDisplayByInternal(WTPart part, String internal) {
		String display = "";
		try {
			Locale loc = SessionHelper.manager.getLocale();
			PersistableAdapter adapter = new PersistableAdapter(part, null, loc,
					OperationIdentifier.newOperationIdentifier(OperationIdentifierConstants.VIEW));
			SessionHelper.manager.setAdministrator();
			adapter.load(new String[] { "HHT_Classification", internal });
			Object obj = (String) adapter.get(internal);
			display = getDisplayByInternal(obj.toString());
		} catch (WTException e) {
			e.printStackTrace();
		}
		return display;
	}

	/**
	 * 根据查询数据库从全局枚举的内部名称获取显示名称 ZeroStickers->零贴
	 * 
	 * @param internal 内部名称
	 * @return display 显示名称
	 */
	public static String getDisplayByInternal(String internal) {
		String display = internal;
		String sql = "SELECT VALUE FROM LWCLOCALIZABLEPROPERTYVALUE WHERE IDA3B4 IN ( SELECT IDA2A2 FROM LWCENUMERATIONENTRY WHERE NAME= ? )";
		try {
			ResultSet resultSet = CommonUtil.excuteSelect(sql, internal.toString());
			while (resultSet.next()) {
				display = resultSet.getString("VALUE");
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return display;
	}

	/**
	 * 根据查询数据库从全局枚举的内部名称获取显示名称 ZeroStickers->零贴
	 * 
	 * @param internal 内部名称
	 * @return display 显示名称
	 */
	public static String getDisplayByInternal(String internal, WTConnection connection) {
		System.out.println("参数2:" + internal);

		String display = "";
		String sql = "SELECT VALUE FROM LWCLOCALIZABLEPROPERTYVALUE WHERE IDA3B4 IN ( SELECT IDA2A2 FROM LWCENUMERATIONENTRY WHERE NAME = '"
				+ internal + "' )";

		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				display = resultSet.getString("VALUE");
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return display;
	}

	/**
	 * 根据分类的内部名称获取分类的显示名称
	 * 
	 * @param classificationCode 分类内部名称
	 * @return 分类外部名称
	 */
	public static String getClassificationdDisPlayName(String classificationCode) {
		String sql = "SELECT VALUE FROM LWCLOCALIZABLEPROPERTYVALUE WHERE IDA3B4 IN ( SELECT IDA2A2 FROM LWCStructEnumAttTemplate WHERE NAME= ? )";
		String classificationName = "";
		try {
			ResultSet resultSet = CommonUtil.excuteSelect(sql, classificationCode);
			while (resultSet.next()) {
				classificationName = resultSet.getString("value");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classificationName;
	}

	/**
	 * 根据分类节点内部名称获取完整分类路径
	 * 
	 * @param internalName 内部名称
	 * @return 分类完整路径
	 */
	public static String getFullPathByInternal(String internalName) {
		StringBuilder result = new StringBuilder();
		Class<?> clazz = ClassificationFacade.class;
		try {
			// 通过Constructor破坏单例模式来完成多层级的获取
			Constructor<?> clazzDeclaredConstructor = clazz.getDeclaredConstructor(null);
			clazzDeclaredConstructor.setAccessible(true);

			while (internalName != null) {
				ClassificationFacade facadeInstance = (ClassificationFacade) clazzDeclaredConstructor.newInstance();
				Object classificationNodeInfo = facadeInstance.getClassificationNodeInfo(internalName,
						CsmConstants.NAMESPACE);
				EnumerationEntryInfo entryInfo = (EnumerationEntryInfo) classificationNodeInfo;
				Set<String> set = entryInfo.getNonLocalizablePropertyNames();

				// 添加显示名称在最前端
				String displayName = entryInfo.getLocalizablePropertyValue(EnumerationEntryInfo.DISPLAY_NAME,
						Locale.CHINA);
				result.insert(0, "\\" + displayName);

				// 判断是否存在父节点，不存在则跳出循环
				if (set.contains("parent")) {
					String parentName = (String) entryInfo.getNonLocalizablePropertyValue(EnumerationEntryInfo.PARENT);
					internalName = parentName;
				} else {
					internalName = null;
				}
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	/**
	 * 根据部件和部件绑定的分类属性的全局字符属性来得到分类的内部名称
	 * 
	 * @param part           部件
	 * @param bind_attr_name 部件绑定的属性
	 * @return 部件分类属性的内部名称
	 */
	public static String getClassificationInternal(WTPart part, String bind_attr_name) {
		String bind_attr_value = null;
		try {
			if (bind_attr_name != null) {
				PersistableAdapter obj = new PersistableAdapter(part, null, Locale.US, null);
				obj.load(bind_attr_name);
				bind_attr_value = (String) obj.get(bind_attr_name);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return bind_attr_value;
	}

	/**
	 * 通过SQL语句来获取某个分类中的所有部件
	 * 
	 * @param IBAName   部件绑定的属性
	 * @param classNode 分类节点内部名称
	 * @return classNode这个分类节点中的所有部件
	 */
	public static List<WTPart> getClassPart(String IBAName, String classNode) {
		List<WTPart> partList = new ArrayList<>();
//		String sql = "SELECT DISTINCT WM.WTPARTNUMBER FROM WTPART W INNER JOIN STRINGVALUE A1 ON W.IDA2A2 = A1.IDA3A4 INNER JOIN STRINGDEFINITION SD ON A1.IDA3A6 = SD.IDA2A2 INNER JOIN WTPARTMASTER WM ON W.IDA3MASTERREFERENCE = WM.IDA2A2 WHERE SD.NAME LIKE ? AND A1.VALUE = ? ";
		String sql = "SELECT DISTINCT IDA3A4 FROM STRINGVALUE SV WHERE SV.IDA3A4 IN ( SELECT SV2.IDA3A4 FROM STRINGVALUE SV2 WHERE SV2.IDA3A6 = (SELECT SD2.IDA2A2 FROM STRINGDEFINITION SD2 WHERE SD2.NAME = ? ) AND SV2.VALUE = ?)";
		try {
			ResultSet resultSet = CommonUtil.excuteSelect(sql, IBAName, classNode);
			while (resultSet.next()) {
				String oid = resultSet.getString("IDA3A4");
				WTPart part = (WTPart) PersistenceUtil.oid2Object("OR:wt.part.WTPart:" + oid);
				partList.add(part);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return partList;
	}
}
