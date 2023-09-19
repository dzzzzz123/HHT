package ext.ait.util;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.ptc.core.lwc.server.LWCLocalizablePropertyValue;
import com.ptc.core.lwc.server.LWCPropertyDefinition;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;

import wt.fc.ObjectIdentifier;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.StringValue;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.service.IBAValueHelper;
import wt.part.WTPart;
import wt.query.CompositeWhereExpression;
import wt.query.ConstantExpression;
import wt.query.LogicalOperator;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.session.SessionServerHelper;
import wt.util.WTException;

public class ClassificationUtil {

	private static String CLASSIFICATIONROOT = "05_成品";

	/**
	 * @param part 根据part对象得到据体part分类路径
	 * @return
	 */
	public static String getClassificationFullPathByPart(WTPart part) {
		String path = "";
		StringValue classNode = getClassNodeByPart(part);
		if (classNode == null)
			return "";
		// 内部名称
		String value = classNode.getValue();
		// 根据内部名称获取分类节点对象
		LWCStructEnumAttTemplate structureEum = getStructureEum(value);
		// 向上递归，获取分类路径
		if (structureEum != null) {
			path = getClassificationFullPath(structureEum);
			for (structureEum = getParentStructEnum(
					structureEum); structureEum != null; structureEum = getParentStructEnum(structureEum)) {
				String classificationFullPath = getClassificationFullPath(structureEum);
				path = classificationFullPath + "/" + path;
			}
		}
		return path;
	}

	/**
	 * 
	 * @param wtpart
	 * @return
	 */
	public static StringValue getClassNodeByPart(WTPart wtpart) {
		StringValue clsNode = null;
		try {
			if (wtpart == null) {
				return clsNode;
			}
			IBAHolder ibaHolder = IBAValueHelper.service.refreshAttributeContainerWithoutConstraints(wtpart);
			DefaultAttributeContainer theContainer = (DefaultAttributeContainer) ibaHolder.getAttributeContainer();
			if (theContainer != null) {
				AttributeDefDefaultView[] theAtts = theContainer.getAttributeDefinitions();
				for (int i = 0; i < theAtts.length; ++i) {
					AbstractValueView[] theValues = theContainer.getAttributeValues(theAtts[i]);
					if (theValues != null) {
						Object[] temp = new Object[2];
						temp[0] = theAtts[i];
						temp[1] = theValues[0];

						if (temp[1] instanceof StringValueDefaultView) {
							if (theAtts[i].getName().equalsIgnoreCase(CLASSIFICATIONROOT)) {
								String sNodeId = (theValues[0]).getObjectID().getStringValue();
								ObjectIdentifier nodeOID = ObjectIdentifier.newObjectIdentifier(sNodeId);
								clsNode = (StringValue) PersistenceHelper.manager.refresh(nodeOID);
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clsNode;
	}

	/**
	 * 根据名称获取分类节点
	 * 
	 * @param name
	 * @return
	 */
	public static LWCStructEnumAttTemplate getStructureEum(String name) {
		LWCStructEnumAttTemplate result = null;
		try {
			QuerySpec queryspec = new QuerySpec(LWCStructEnumAttTemplate.class);
			queryspec.appendWhere(
					new SearchCondition(LWCStructEnumAttTemplate.class, LWCStructEnumAttTemplate.NAME, "=", name),
					new int[] {});
			queryspec.appendAnd();
			queryspec.appendWhere(
					new SearchCondition(LWCStructEnumAttTemplate.class, LWCStructEnumAttTemplate.DELETED_ID, true),
					new int[] {});
			QueryResult qr = PersistenceServerHelper.manager.query(queryspec);
			if (qr.hasMoreElements()) {
				result = (LWCStructEnumAttTemplate) qr.nextElement();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param structureEum
	 * @return
	 */
	public static String getClassificationFullPath(LWCStructEnumAttTemplate structureEum) {
		String part = "";
		try {
			QuerySpec queryspec = new QuerySpec();
			int a = queryspec.appendClassList(LWCLocalizablePropertyValue.class, true);
			int b = queryspec.appendClassList(LWCPropertyDefinition.class, false);
			queryspec.setAdvancedQueryEnabled(true);
			String[] aliases = new String[2];
			aliases[0] = queryspec.getFromClause().getAliasAt(a);
			aliases[1] = queryspec.getFromClause().getAliasAt(b);
			TableColumn tc1 = new TableColumn(aliases[0], "IDA3C4");
			TableColumn tc3 = new TableColumn(aliases[0], "CLASSNAMEKEYC4");
			TableColumn tc11 = new TableColumn(aliases[0], "IDA3B4");
			TableColumn tc33 = new TableColumn(aliases[0], "CLASSNAMEKEYB4");
			TableColumn tc2 = new TableColumn(aliases[0], "IDA3A4");
			TableColumn tc4 = new TableColumn(aliases[1], "IDA2A2");
			TableColumn tc5 = new TableColumn(aliases[1], "NAME");
			TableColumn tc6 = new TableColumn(aliases[1], "CLASSNAME");
			CompositeWhereExpression andExpression = new CompositeWhereExpression(LogicalOperator.AND);
			andExpression.append(new SearchCondition(tc1, "=",
					new ConstantExpression(structureEum.getPersistInfo().getObjectIdentifier().getId())));
			andExpression.append(new SearchCondition(tc3, "=",
					new ConstantExpression("com.ptc.core.lwc.server.LWCStructEnumAttTemplate")));
			andExpression.append(new SearchCondition(tc11, "=",
					new ConstantExpression(structureEum.getPersistInfo().getObjectIdentifier().getId())));
			andExpression.append(new SearchCondition(tc33, "=",
					new ConstantExpression("com.ptc.core.lwc.server.LWCStructEnumAttTemplate")));
			andExpression.append(new SearchCondition(tc2, "=", tc4));
			andExpression.append(new SearchCondition(tc5, "=", new ConstantExpression("displayName")));
			andExpression.append(new SearchCondition(tc6, "=",
					new ConstantExpression("com.ptc.core.lwc.server.LWCAbstractAttributeTemplate")));
			queryspec.appendWhere(andExpression, null);
			QueryResult qr = PersistenceHelper.manager.find(queryspec);
			if (qr.hasMoreElements()) {
				Object[] nextElement = (Object[]) qr.nextElement();
				LWCLocalizablePropertyValue value = (LWCLocalizablePropertyValue) nextElement[0];
				String zh = value.getValue(Locale.SIMPLIFIED_CHINESE);
				if (StringUtils.isBlank(zh)) {
					return value.getValue();
				} else {
					return zh;
				}
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return part;

	}

	/**
	 * 获取父节点
	 * 
	 * @param structureEum
	 * @return
	 * @throws WTException
	 */
	private static LWCStructEnumAttTemplate getParentStructEnum(LWCStructEnumAttTemplate structureEum) {
		LWCStructEnumAttTemplate result = null;
		try {
			if (structureEum.getParent() == null) {
				return result;
			}
			QuerySpec queryspec = new QuerySpec(LWCStructEnumAttTemplate.class);
			queryspec.appendWhere(
					new SearchCondition(LWCStructEnumAttTemplate.class, "thePersistInfo.theObjectIdentifier", "=",
							structureEum.getParentReference().getObject().getPersistInfo().getObjectIdentifier()),
					new int[] {});
			queryspec.appendAnd();
			queryspec.appendWhere(
					new SearchCondition(LWCStructEnumAttTemplate.class, LWCStructEnumAttTemplate.DELETED_ID, true),
					new int[] {});
			QueryResult qr = PersistenceServerHelper.manager.query(queryspec);
			if (qr.hasMoreElements()) {
				result = (LWCStructEnumAttTemplate) qr.nextElement();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 分类中文名，查出对应的英文名称 select LST."NAME" from lwclocalizablepropertyvalue
	 * lv,lwcstructenumatttemplate lst where lv.zh_cn='电机支架_分体机室外机' and
	 * lv.ida3b4=lst.ida2a2;
	 */
	@SuppressWarnings("deprecation")
	public static String getClassificationName(String name) {
		try {
			boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
			QuerySpec qs = new QuerySpec();
			int a = qs.appendClassList(LWCLocalizablePropertyValue.class, false);
			int b = qs.appendClassList(LWCStructEnumAttTemplate.class, true);
			String[] str = new String[3];
			str[0] = qs.getFromClause().getAliasAt(a);
			str[1] = qs.getFromClause().getAliasAt(b);
			TableColumn t1 = new TableColumn(str[0], "zh_cn");
			TableColumn t2 = new TableColumn(str[0], "ida3b4");
			TableColumn t3 = new TableColumn(str[1], "ida2a2");
			qs.appendWhere(new SearchCondition(t1, SearchCondition.EQUAL, new ConstantExpression(name)));
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(t2, SearchCondition.EQUAL, t3));
			qs.appendAnd();
			qs.appendWhere(
					new SearchCondition(LWCStructEnumAttTemplate.class, LWCStructEnumAttTemplate.DELETED_ID, true),
					new int[] {});
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				LWCStructEnumAttTemplate lsat = (LWCStructEnumAttTemplate) obj[0];
				return lsat.getName();
			}
			SessionServerHelper.manager.setAccessEnforced(enforce);
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 分类中文名，查出对应的英文名称 select LST."NAME" from lwclocalizablepropertyvalue
	 * lv,lwcstructenumatttemplate lst where lv.zh_cn='电机支架_分体机室外机' and
	 * lv.ida3b4=lst.ida2a2;
	 */
	public static String getClassificationCNName(String name) {
		try {
			boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
			QuerySpec qs = new QuerySpec();
			int a = qs.appendClassList(LWCLocalizablePropertyValue.class, true);
			int b = qs.appendClassList(LWCStructEnumAttTemplate.class, false);
			int c = qs.appendClassList(LWCPropertyDefinition.class, false);
			String[] str = new String[3];
			str[0] = qs.getFromClause().getAliasAt(a);
			str[1] = qs.getFromClause().getAliasAt(b);
			str[2] = qs.getFromClause().getAliasAt(c);
			TableColumn t1 = new TableColumn(str[2], "ida2a2");
			TableColumn t2 = new TableColumn(str[0], "ida3b4");
			TableColumn t3 = new TableColumn(str[1], "ida2a2");
			TableColumn t4 = new TableColumn(str[0], "ida3a4");
			qs.appendWhere(new SearchCondition(t2, SearchCondition.EQUAL, t3), new int[] { a, b });
			qs.appendAnd();
			qs.appendWhere(
					new SearchCondition(LWCStructEnumAttTemplate.class, LWCStructEnumAttTemplate.DELETED_ID, true),
					new int[] { b });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(LWCStructEnumAttTemplate.class, LWCStructEnumAttTemplate.NAME,
					SearchCondition.EQUAL, name), new int[] { b });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(t4, SearchCondition.EQUAL, t1), new int[] { a, c });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(LWCPropertyDefinition.class, LWCPropertyDefinition.NAME,
					SearchCondition.EQUAL, "displayName"), new int[] { c });
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				LWCLocalizablePropertyValue lsat = (LWCLocalizablePropertyValue) obj[0];
				return lsat.getValue();
			}
			SessionServerHelper.manager.setAccessEnforced(enforce);
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}
}
