/*
 * Copyright (c) 2006-2009 JW Innovation Software(Shen Zhen) Ltd..
 * Nanshan, ShenZhen, China
 * All rights reserved.
 *
 * @IBAUtil.java
 *
 * This software is the confidential and proprietary information of JWIS. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with JWIS.
 */
package ext.ait.util.back;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.common.view.PropertyHolderHelper;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;

//import wt.csm.businessentity.BusinessEntity;
//import wt.csm.navigation.CSMClassificationNavigationException;
//import wt.csm.navigation.litenavigation.ClassificationStructDefaultView;
//import wt.csm.navigation.service.ClassificationHelper;
//import wt.csm.navigation.service.ClassificationService;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.iba.constraint.IBAConstraintException;
import wt.iba.definition.BooleanDefinition;
import wt.iba.definition.DefinitionLoader;
import wt.iba.definition.FloatDefinition;
import wt.iba.definition.IntegerDefinition;
import wt.iba.definition.StringDefinition;
import wt.iba.definition.TimestampDefinition;
import wt.iba.definition.URLDefinition;
import wt.iba.definition.UnitDefinition;
import wt.iba.definition.litedefinition.AbstractAttributeDefinizerView;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.litedefinition.AttributeDefNodeView;
import wt.iba.definition.litedefinition.BooleanDefView;
import wt.iba.definition.litedefinition.FloatDefView;
import wt.iba.definition.litedefinition.IntegerDefView;
import wt.iba.definition.litedefinition.RatioDefView;
import wt.iba.definition.litedefinition.ReferenceDefView;
import wt.iba.definition.litedefinition.StringDefView;
import wt.iba.definition.litedefinition.TimestampDefView;
import wt.iba.definition.litedefinition.URLDefView;
import wt.iba.definition.litedefinition.UnitDefView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.AttributeContainer;
import wt.iba.value.BooleanValue;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.FloatValue;
import wt.iba.value.IBAContainerException;
import wt.iba.value.IBAHolder;
import wt.iba.value.IBAValueException;
import wt.iba.value.IBAValueUtility;
import wt.iba.value.IntegerValue;
import wt.iba.value.StringValue;
import wt.iba.value.TimestampValue;
import wt.iba.value.URLValue;
import wt.iba.value.UnitValue;
import wt.iba.value.litevalue.AbstractContextualValueDefaultView;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.BooleanValueDefaultView;
import wt.iba.value.litevalue.FloatValueDefaultView;
import wt.iba.value.litevalue.IntegerValueDefaultView;
import wt.iba.value.litevalue.ReferenceValueDefaultView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.litevalue.TimestampValueDefaultView;
import wt.iba.value.litevalue.URLValueDefaultView;
import wt.iba.value.litevalue.UnitValueDefaultView;
import wt.iba.value.service.IBAValueDBService;
import wt.iba.value.service.IBAValueHelper;
import wt.iba.value.service.LoadValue;
import wt.iba.value.service.StandardIBAValueService;
import wt.lite.AbstractLiteObject;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.WhereExpression;
import wt.session.SessionHelper;
import wt.units.service.QuantityOfMeasureDefaultView;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.wip.NonLatestCheckoutException;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;

/**
 * IBA Utility Class
 * 
 * @author wide @version：2020-10-23
 */
public class IBAUtil {

	/**
	 * @Description LOG:Logger
	 */
	private static final Logger LOGGER = LogManager.getLogger(IBAUtil.class);

	/**
	 * @Description ibaContainer:Hashtable
	 */
	Hashtable ibaContainer;

	/**
	 * @Description ibaOrigContainer:Hashtable
	 */
	Hashtable ibaOrigContainer;

	Persistable persistable;
	/**
	 * @Description UNITS:String
	 */
	final static String UNITS = "SI";

	/**
	 * @Description VERBOSE:boolean
	 */
	boolean VERBOSE = false;

	/**
	 * Can not be called directly by the end user
	 */
	public IBAUtil() {
		ibaContainer = new Hashtable();
	}

	/**
	 * The only constrator can be called by the end user
	 * 
	 * @param ibaHolder IBAHolder
	 * @throws WTException
	 */
	public IBAUtil(IBAHolder ibaHolder) throws WTException {
		super();
		try {
			initializeIBAValue(ibaHolder);
			persistable = (Persistable) ibaHolder;
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 *
	 * @description
	 * @date 2009-4-13 上午10:51:21
	 * @return Enumeration
	 */
	public Enumeration getAttributeDefinitions() {
		return ibaContainer.keys();
	}

	/**
	 *
	 * @description
	 * @date 2009-4-13 上午10:51:29
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void removeAllAttributes() throws WTException, WTPropertyVetoException {
		ibaContainer.clear();
	}

	/**
	 *
	 * @description
	 * @date 2009-4-13 上午10:52:30
	 * @param name String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void removeAttribute(String name) throws WTException, WTPropertyVetoException {
		ibaContainer.remove(name);
	}

	/**
	 * return single IBA value
	 *
	 * @param name String
	 * @return String
	 */
	public String getIBAValue(String name) {
		String value = "";
		try {
			if (ibaContainer.get(name) != null) {
				AbstractValueView theValue = (AbstractValueView) ((Object[]) ibaContainer.get(name))[1];
				value = (IBAValueUtility.getLocalizedIBAValueDisplayString(theValue,
						SessionHelper.manager.getLocale()));
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * return multiple IBA values
	 *
	 * @param name String
	 * @return Vector
	 */
	public Vector getIBAValues(String name) {
		Vector vector = new Vector();
		try {
			if (ibaContainer.get(name) != null) {
				Object[] objs = (Object[]) ibaContainer.get(name);
				for (int i = 1; i < objs.length; i++) {
					AbstractValueView theValue = (AbstractValueView) objs[i];
					vector.addElement(IBAValueUtility.getLocalizedIBAValueDisplayString(theValue,
							SessionHelper.manager.getLocale()));
				}
			}
		} catch (WTException e) {
			LOGGER.info("get iba  values error:" + e);
		}
		return vector;
	}

	/**
	 * return ALL IBA values
	 *
	 * @return Hashtable
	 */
	public Hashtable getAllIBAValues() {
		return ibaContainer;
	}

	public HashMap<String, String> getAllAttrValue() throws WTException {
		HashMap<String, String> map = new HashMap<String, String>();
		Enumeration keys = ibaContainer.keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object[] objs = (Object[]) ibaContainer.get(key);
			AbstractValueView theValue = (AbstractValueView) objs[1];
			String value = "";
			if (theValue instanceof BooleanValueDefaultView) {
				BooleanValueDefaultView stringvaluedefaultview = (BooleanValueDefaultView) theValue;
				ObjectIdentifier objectidentifier = stringvaluedefaultview.getObjectID();
				Persistable persistable = ObjectReference.newObjectReference(objectidentifier).getObject();
				BooleanValue booleanValue = (BooleanValue) persistable;
				value = booleanValue.getValueObject().toString();
			} else {
				value = IBAValueUtility.getLocalizedIBAValueDisplayString(theValue, SessionHelper.manager.getLocale());
				if (value == null || value.trim().length() == 0) {
					value = "";
				}
			}
			map.put((String) key, value);
		}
		return map;
	}

	/***
	 * 
	 * @param locale 地区枚举，例如Locale.ENGLISH，或者根据用户系统来例如SessionHelper.manager.getLocale()
	 * @return
	 * @throws Exception
	 */
	public TreeMap<String, String> getAllAttrValueKeyByLocale(Locale locale) throws Exception {
		TreeMap<String, String> map = new TreeMap<String, String>();
		Enumeration keys = ibaContainer.keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object[] objs = (Object[]) ibaContainer.get(key);
			AbstractValueView theValue = (AbstractValueView) objs[1];
			String value = IBAValueUtility.getLocalizedIBAValueDisplayString(theValue, locale);
			if (value == null && value.trim().length() == 0) {
				value = "";
			}
			// AttributeDefDefaultView view = getAttributeDefinition((String)key);
			// String keyLocal = view.getLocalizedDisplayString();
			String keyLocal = getInternalDisplayName((String) key);
			// LOGGER.debug("getAllAttrValueKeyShowLocale>>>>当前获取到的IBA key>>>" + key
			// + " 当前获取到的IBA keyLocal>>>keyLocal" + keyLocal
			// + " 当前获取到的IBA keyLocal>>>keyLocal" + value);
			map.put(keyLocal, value);
		}
		return map;
	}

	/**
	 * return multiple IBA values & dependency relationship
	 *
	 * @param name String
	 * @return Vector
	 */
	public Vector getIBAValuesWithDependency(String name) {
		Vector vector2 = new Vector();
		final int ARRAY_SIZE = 3;
		try {
			if (ibaContainer.get(name) != null) {
				Object[] objs = (Object[]) ibaContainer.get(name);
				for (int i = 1; i < objs.length; i++) {
					AbstractValueView theValue = (AbstractValueView) objs[i];
					String[] temp = new String[ARRAY_SIZE];
					temp[0] = IBAValueUtility.getLocalizedIBAValueDisplayString(theValue,
							SessionHelper.manager.getLocale());
					if ((theValue instanceof AbstractContextualValueDefaultView)
							&& ((AbstractContextualValueDefaultView) theValue).getReferenceValueDefaultView() != null) {
						temp[1] = ((AbstractContextualValueDefaultView) theValue).getReferenceValueDefaultView()
								.getReferenceDefinition().getName();
						temp[2] = ((AbstractContextualValueDefaultView) theValue).getReferenceValueDefaultView()
								.getLocalizedDisplayString();
					} else {
						temp[1] = null;
						temp[2] = null;
					}
					vector2.addElement(temp);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return vector2;
	}

	/**
	 * @description getIBAValuesWithBusinessEntity
	 * @date 2009-4-13 上午10:53:31
	 * @param name String
	 * @return Vector
	 */
	// public Vector getIBAValuesWithBusinessEntity(String name) {
	// Vector vector1 = new Vector();
	// try {
	// if (ibaContainer.get(name) != null) {
	// Object[] objs = (Object[]) ibaContainer.get(name);
	// for (int i = 1; i < objs.length; i++) {
	// AbstractValueView theValue = (AbstractValueView) objs[i];
	// Object[] temp = new Object[2];
	// temp[0] = IBAValueUtility
	// .getLocalizedIBAValueDisplayString(theValue,
	// SessionHelper.manager.getLocale());
	// if ((theValue instanceof AbstractContextualValueDefaultView)
	// && ((AbstractContextualValueDefaultView) theValue)
	// .getReferenceValueDefaultView() != null) {
	// ReferenceValueDefaultView referencevaluedefaultview =
	// ((AbstractContextualValueDefaultView) theValue)
	// .getReferenceValueDefaultView();
	// ObjectIdentifier objectidentifier =
	// ((wt.iba.value.litevalue.DefaultLiteIBAReferenceable)
	// referencevaluedefaultview
	// .getLiteIBAReferenceable()).getObjectID();
	// Persistable persistable = ObjectReference
	// .newObjectReference(objectidentifier)
	// .getObject();
	// temp[1] = (BusinessEntity) persistable;
	// } else {
	// temp[1] = null;
	// }
	// vector1.addElement(temp);
	// }
	// }
	// } catch (WTException e) {
	// e.printStackTrace();
	// }
	// return vector1;
	// }

	/**
	 * @description getIBABusinessEntity
	 * @date 2009-4-13 上午10:54:08
	 * @param name String
	 * @return BusinessEntity
	 */
	// public BusinessEntity getIBABusinessEntity(String name) {
	// BusinessEntity value = null;
	// try {
	// if (ibaContainer.get(name) != null) {
	// AbstractValueView theValue = (AbstractValueView) ((Object[]) ibaContainer
	// .get(name))[1];
	// ReferenceValueDefaultView referencevaluedefaultview =
	// (ReferenceValueDefaultView) theValue;
	// ObjectIdentifier objectidentifier =
	// ((wt.iba.value.litevalue.DefaultLiteIBAReferenceable)
	// referencevaluedefaultview
	// .getLiteIBAReferenceable()).getObjectID();
	// Persistable persistable = ObjectReference.newObjectReference(
	// objectidentifier).getObject();
	// value = (BusinessEntity) persistable;
	// }
	// } catch (WTException e) {
	// e.printStackTrace();
	// }
	// return value;
	// }

	/**
	 * @description getIBABusinessEntities
	 * @date 2009-4-13 上午10:54:40
	 * @param name String
	 * @return Vector
	 */
	public Vector getIBABusinessEntities(String name) {
		Vector vector = new Vector();
		try {
			if (ibaContainer.get(name) != null) {
				Object[] objs = (Object[]) ibaContainer.get(name);
				for (int i = 1; i < objs.length; i++) {
					AbstractValueView theValue = (AbstractValueView) objs[i];
					ReferenceValueDefaultView referencevaluedefaultview = (ReferenceValueDefaultView) theValue;
					ObjectIdentifier objectidentifier = ((wt.iba.value.litevalue.DefaultLiteIBAReferenceable) referencevaluedefaultview
							.getLiteIBAReferenceable()).getObjectID();
					Persistable persistable = ObjectReference.newObjectReference(objectidentifier).getObject();
					vector.addElement(persistable);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return vector;
	}

	/**
	 *
	 * @description
	 * @date 2009-4-13 上午10:54:48
	 * @param theDef AttributeDefDefaultView
	 * @param value  String
	 * @return AbstractValueView
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private AbstractValueView getAbstractValueView(AttributeDefDefaultView theDef, String value)
			throws WTException, WTPropertyVetoException {
		String name = theDef.getName();
		String value2 = null;
		AbstractValueView ibaValue = null;

		if (theDef instanceof UnitDefView) {
			value = value + " " + getDisplayUnits((UnitDefView) theDef, UNITS);
			// LOG.info(value);
		} else if (theDef instanceof ReferenceDefView) {
			value2 = value;
			value = ((ReferenceDefView) theDef).getReferencedClassname();
		} else if (theDef instanceof FloatDefView) {
			if (value != null)
				value = value.trim();
			if (value != null && value.length() > 0 && value.indexOf(".") < 0)
				value = value + ".0";
		}

		ibaValue = internalCreateValue(theDef, value, value2);
		if (ibaValue == null) {
			LOGGER.info("IBA value:" + value + " is illegal. Add IBA value failed!!");
			throw new WTException("Trace.. name = " + theDef.getName() + ", identifier = " + value + " not found.");
			// return;
		}

		if (ibaValue instanceof ReferenceValueDefaultView) {
			if (VERBOSE) {
				LOGGER.info("Before find original reference : " + name + " has key=" + ibaValue.getKey());
			}
			ibaValue = getOriginalReferenceValue(name, ibaValue);
			if (VERBOSE) {
				LOGGER.info("After find original reference : " + name + " has key=" + ibaValue.getKey());
			}
		}
		ibaValue.setState(AbstractValueView.NEW_STATE);
		return ibaValue;
	}

	/**
	 *
	 * @description
	 * @date 2009-4-13 上午11:35:15
	 * @param name     String
	 * @param ibaValue AbstractValueView
	 * @return AbstractValueView
	 * @throws IBAValueException
	 */
	private AbstractValueView getOriginalReferenceValue(String name, AbstractValueView ibaValue)
			throws IBAValueException {
		Object[] objs = (Object[]) ibaOrigContainer.get(name);
		if (objs != null && (ibaValue instanceof ReferenceValueDefaultView)) {
			int businessvaluepos = 1;
			for (businessvaluepos = 1; businessvaluepos < objs.length; businessvaluepos++) {
				if (((AbstractValueView) objs[businessvaluepos]).compareTo(ibaValue) == 0) {
					ibaValue = (AbstractValueView) objs[businessvaluepos];
					break;
				}
			}
		}
		return ibaValue;
	}

	/**
	 * @author wide
	 * @description
	 * @date 2009-4-13 上午11:35:54
	 * @param name String
	 * @return AttributeDefDefaultView
	 * @throws WTException
	 */
	public AttributeDefDefaultView getDefDefaultView(String name) throws WTException {
		AttributeDefDefaultView theDef = null;
		Object[] obj = (Object[]) ibaContainer.get(name);
		if (obj != null) {
			theDef = (AttributeDefDefaultView) obj[0];
		} else {
			theDef = getAttributeDefinition(name);
		}
		if (theDef == null) {
			LOGGER.info("IBA name:" + name + " is illegal. Add IBA value failed!!");
			throw new WTException("Trace.. name = " + name + " not existed.");
		}
		return theDef;
	}

	/**
	 *
	 * @description
	 * @date 2009-4-13 上午11:36:10
	 * @param name  String
	 * @param value String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void setIBAValue(String name, String value) throws WTException, WTPropertyVetoException {
		AttributeDefDefaultView theDef = getDefDefaultView(name);
		Object theValue = getAbstractValueView(theDef, value);
		// LOG.info(name + " put
		// "+((AbstractValueView)theValue).getLocalizedDisplayString());
		Object[] temp = new Object[2];
		temp[0] = theDef;
		temp[1] = theValue;
		ibaContainer.put(name, temp);
	}

	/**
	 * 给对象设置IBA的属性名称及值,包含浮点型
	 * 
	 * @param s  IBA的属性名称
	 * @param s1 IBA值
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	public void setIBAValueNew(String s, String s1) throws WTPropertyVetoException, WTException {
		AbstractValueView abstractvalueview = null;
		AttributeDefDefaultView attributedefdefaultview = null;
		Object aobj[] = (Object[]) ibaContainer.get(s);
		if (aobj != null) {
			abstractvalueview = (AbstractValueView) aobj[1];
			attributedefdefaultview = (AttributeDefDefaultView) aobj[0];
		}
		if (abstractvalueview == null)
			attributedefdefaultview = getDefDefaultView(s);
		if (attributedefdefaultview == null) {
			// System.out.println("definition is null ...");
			return;
		}
		if (attributedefdefaultview instanceof UnitDefView) {
			s1 = s1 + " " + getDisplayUnits((UnitDefView) attributedefdefaultview, UNITS);
		} else if (attributedefdefaultview instanceof FloatDefView) {
			LOGGER.debug(" 设置软属性的类型为--->" + FloatDefView.class.getName());
			if (s1 != null)
				s1 = s1.trim();
			if (s1 != null && s1.length() > 0 && s1.indexOf(".") < 0)
				s1 = s1 + ".0";
		} else if (attributedefdefaultview instanceof TimestampDefView) {

		}

		abstractvalueview = internalCreateValue(attributedefdefaultview, s1, null);
		if (abstractvalueview == null) {
			// System.out.println("after creation, iba value is null ..");
			return;
		} else {
			abstractvalueview.setState(1);
			Object aobj1[] = new Object[2];
			aobj1[0] = attributedefdefaultview;
			aobj1[1] = abstractvalueview;
			ibaContainer.put(attributedefdefaultview.getName(), ((Object) (aobj1)));
			// System.out.println("Set
			// :"+attributedefdefaultview.getName()+"\t"+ ((Object)
			// (aobj1[0])).toString()+"\t"+((Object) (aobj1[1])).toString());
			return;
		}
	}

	/**
	 * Set the attribute with multiple values from the list
	 *
	 * @param name   String
	 * @param values Vector
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	public void setIBAValues(String name, Vector values) throws WTPropertyVetoException, WTException {
		AttributeDefDefaultView theDef = getDefDefaultView(name);
		Object[] temp = new Object[values.size() + 1];
		temp[0] = theDef;
		for (int i = 0; i < values.size(); i++) {
			String value = (String) values.get(i);
			Object theValue = getAbstractValueView(theDef, value);
			temp[i + 1] = theValue;
		}
		ibaContainer.put(name, temp);
	}

	/**
	 *
	 * @description
	 * @date 2009-4-13 上午11:36:39
	 * @param name  String
	 * @param value String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void addIBAValue(String name, String value) throws WTException, WTPropertyVetoException {
		Object[] obj = (Object[]) ibaContainer.get(name);
		AttributeDefDefaultView theDef = getDefDefaultView(name);
		Object theValue = getAbstractValueView(theDef, value);

		Object[] temp;
		if (obj == null) {
			temp = new Object[2];
			temp[0] = theDef;
			temp[1] = theValue;
		} else {
			temp = new Object[obj.length + 1];
			int i;
			for (i = 0; i < obj.length; i++) {
				temp[i] = obj[i];
			}
			temp[i] = theValue;
		}

		ibaContainer.put(name, temp);
	}
	// public Vector a(String name) throws WTRuntimeException, WTException{
	// Vector<URLValue> values = new Vector<>();
	// Object[] objs = (Object[]) ibaContainer.get(name);
	//
	// if(objs!=null && objs.length>0) {
	// objs = ArrayUtils.remove(objs, 0);
	// for(Object obj : objs) {
	// AbstractValueView view = (AbstractValueView) obj;
	// URLValueDefaultView urlValuedefaultview =(URLValueDefaultView) view;
	// ObjectIdentifier objectidentifier = urlValuedefaultview.getObjectID();
	// Persistable persistable =
	// ObjectReference.newObjectReference(objectidentifier).getObject();
	// values.add((URLValue) persistable);
	// }
	// }
	// return values;
	// }

	public void addURLValue(String name, String value, String des) throws WTException, WTPropertyVetoException {
		Object[] obj = (Object[]) ibaContainer.get(name);
		AttributeDefDefaultView theDef = getDefDefaultView(name);
		Object theValue = getAbstractValueView(theDef, value);
		URLValueDefaultView urlValuedefaultview = (URLValueDefaultView) theValue;
		urlValuedefaultview.setDescription(des);
		urlValuedefaultview.setValue(value);
		Object[] temp;
		if (obj == null) {
			temp = new Object[2];
			temp[0] = theDef;
			temp[1] = urlValuedefaultview;
		} else {
			temp = new Object[obj.length + 1];
			int i;
			for (i = 0; i < obj.length; i++) {
				temp[i] = obj[i];
			}
			temp[i] = urlValuedefaultview;
		}

		ibaContainer.put(name, temp);
	}

	/**
	 *
	 * @description
	 * @date 2009-4-13 上午11:36:48
	 * @param sourceDef     AttributeDefDefaultView
	 * @param sourceValue   AbstractValueView
	 * @param businessDef   AttributeDefDefaultView
	 * @param businessValue AbstractValueView
	 * @return AbstractValueView
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	private AbstractValueView setDependency(AttributeDefDefaultView sourceDef, AbstractValueView sourceValue,
			AttributeDefDefaultView businessDef, AbstractValueView businessValue)
			throws WTPropertyVetoException, WTException {
		String sourcename = sourceDef.getName();
		String businessname = businessDef.getName();

		if (businessValue == null) {
			throw new WTException("This Business Entity:" + businessname + " value doesn't exist in System Business "
					+ "Entity. Add IBA dependancy failed!!");
		}
		Object[] businessobj = (Object[]) ibaContainer.get(businessname);
		if (businessobj == null) {
			throw new WTException("Part IBA:" + businessname + " Value is null. Add IBA dependancy failed!!");
		}
		int businessvaluepos = 1;
		for (businessvaluepos = 1; businessvaluepos < businessobj.length; businessvaluepos++) {
			if (((AbstractValueView) businessobj[businessvaluepos]).compareTo(businessValue) == 0) {
				businessValue = (AbstractValueView) businessobj[businessvaluepos];
				break;
			}
		}
		if (businessvaluepos == businessobj.length) {
			throw new WTException(
					"This Business Entity:" + businessname + " value:" + businessValue.getLocalizedDisplayString()
							+ " is not existed in Part IBA values. " + "Add IBA dependancy failed!!");
		}

		if (!(businessValue instanceof ReferenceValueDefaultView)) {
			throw new WTException(
					"This Business Entity:" + businessname + " value:" + businessValue.getLocalizedDisplayString()
							+ " is not a ReferenceValueDefaultView. " + "Add IBA dependancy failed!!");
		}
		((AbstractContextualValueDefaultView) sourceValue)
				.setReferenceValueDefaultView((ReferenceValueDefaultView) businessValue);
		if (VERBOSE)
			LOGGER.info("ref obj=" + ((AbstractContextualValueDefaultView) sourceValue).getReferenceValueDefaultView()
					.getLocalizedDisplayString());
		if (VERBOSE)
			LOGGER.info("ref key="
					+ ((AbstractContextualValueDefaultView) sourceValue).getReferenceValueDefaultView().getKey());
		if (VERBOSE)
			LOGGER.info("This IBA:" + sourcename + " value:" + sourceValue.getLocalizedDisplayString()
					+ " add dependancy with Business Entity:" + businessname + " value:"
					+ businessValue.getLocalizedDisplayString() + " successfully with state=" + sourceValue.getState()
					+ " !!");
		return sourceValue;
	}

	/**
	 *
	 * @description
	 * @date 2009-4-13 上午11:39:20
	 * @param sourcename    String SourceName
	 * @param sourcevalue   String SourceValue
	 * @param businessname  String businessname
	 * @param businessvalue String businessvalue
	 * @throws IBAValueException
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	public void setIBAValue(String sourcename, String sourcevalue, String businessname, String businessvalue)
			throws IBAValueException, WTPropertyVetoException, WTException {

		AttributeDefDefaultView sourceDef = getDefDefaultView(sourcename);
		AttributeDefDefaultView businessDef1 = getDefDefaultView(businessname);
		AbstractValueView sourceValue = getAbstractValueView(sourceDef, sourcevalue);
		AbstractValueView businessValue = getAbstractValueView(businessDef1, businessvalue);
		sourceValue = setDependency(sourceDef, sourceValue, businessDef1, businessValue);
		Object[] temp = new Object[2];
		temp[0] = sourceDef;
		temp[1] = sourceValue;
		ibaContainer.put(sourcename, temp);
	}

	/**
	 * Add an IBA value with dependency relation
	 *
	 * @param sourcename    String
	 * @param sourcevalue   String
	 * @param businessname  String
	 * @param businessvalue String
	 * @throws IBAValueException
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */

	public void addIBAValue(String sourcename, String sourcevalue, String businessname, String businessvalue)
			throws IBAValueException, WTPropertyVetoException, WTException {
		AttributeDefDefaultView sourceDef = getDefDefaultView(sourcename);
		AttributeDefDefaultView businessDef = getDefDefaultView(businessname);
		AbstractValueView sourceValue = getAbstractValueView(sourceDef, sourcevalue);
		AbstractValueView businessValue = getAbstractValueView(businessDef, businessvalue);
		sourceValue = setDependency(sourceDef, sourceValue, businessDef, businessValue);

		Object[] obj = (Object[]) ibaContainer.get(sourcename);
		Object[] temp;
		if (obj == null) {
			temp = new Object[2];
			temp[0] = sourceDef;
			temp[1] = sourceValue;
		} else {
			temp = new Object[obj.length + 1];
			int i;
			for (i = 0; i < obj.length; i++) {
				temp[i] = obj[i];
			}

			temp[i] = sourceValue;
		}
		ibaContainer.put(sourcename, temp);
	}

	/**
	 * initializePart() with this signature is designed to pre-populate values from
	 * an existing IBA holder.
	 * 
	 * @description
	 * @date 2009-4-13 上午11:39:44
	 * @param ibaHolder ibaHolder
	 * @throws WTException
	 * @throws RemoteException
	 */
	private void initializeIBAValue(IBAHolder ibaHolder) throws WTException, RemoteException {
		ibaContainer = new Hashtable();
		ibaOrigContainer = new Hashtable();
		ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, null, SessionHelper.manager.getLocale(),
				null);
		DefaultAttributeContainer theContainer = (DefaultAttributeContainer) ibaHolder.getAttributeContainer();
		if (theContainer != null) {
			AttributeDefDefaultView[] theAtts = theContainer.getAttributeDefinitions();
			for (int i = 0; i < theAtts.length; i++) {
				AbstractValueView[] theValues = theContainer.getAttributeValues(theAtts[i]);
				if (theValues != null) {
					// Add by Somesh
					Object[] temp = new Object[theValues.length + 1];
					temp[0] = theAtts[i];
					for (int j = 1; j <= theValues.length; j++) {
						temp[j] = theValues[j - 1];
					}
					// End Add by Somesh
					ibaContainer.put(theAtts[i].getName(), temp);
					ibaOrigContainer.put(theAtts[i].getName(), temp);
				}
			}
		}
		// LOG.info("initializeIBAValue : ibaContainer = " +
		// ibaContainer);
		// LOG.info("initializeIBAValue : ibaOrigContainer = " +
		// ibaOrigContainer);
	}

	/**
	 *
	 * @description
	 * @date 2009-4-13 上午11:40:26
	 * @param theContainer DefaultAttributeContainer
	 * @param s            String
	 * @return DefaultAttributeContainer
	 * @throws WTException
	 */
	// private DefaultAttributeContainer suppressCSMConstraint(
	// DefaultAttributeContainer theContainer, String s)
	// throws WTException {
	// ClassificationStructDefaultView defStructure = null;
	// defStructure = getClassificationStructDefaultViewByName(s);
	// if (defStructure != null) {
	// // ReferenceDefView ref = defStructure.getReferenceDefView();
	// Vector cgs = theContainer.getConstraintGroups();
	// Vector newCgs = new Vector();
	// // AttributeConstraint immutable = null;
	// try {
	// // if (VERBOSE)
	// // LOG.info("cgs size="+cgs.size());
	// for (int i = 0; i < cgs.size(); i++) {
	// ConstraintGroup cg = (ConstraintGroup) cgs.elementAt(i);
	// if (cg != null) {
	// // LOG.info(cg.getConstraintGroupLabel());
	// if (!cg
	// .getConstraintGroupLabel()
	// .equals(
	// wt.csm.constraint.CSMConstraintFactory.
	// CONSTRAINT_GROUP_LABEL)) {
	// newCgs.addElement(cg);
	// } else {
	// // Enumeration enum = cg.getConstraints();
	// ConstraintGroup newCg = new ConstraintGroup();
	// newCg.setConstraintGroupLabel(cg
	// .getConstraintGroupLabel());
	// newCgs.addElement(newCg);
	// }
	// }
	// }
	// theContainer.setConstraintGroups(newCgs);
	// } catch (wt.util.WTPropertyVetoException e) {
	// e.printStackTrace();
	// }
	// }
	// // end of CSM constraint removal, rjla 2000-11-17
	// return theContainer;
	// }
	/**
	 *
	 * @description
	 * @date 2009-4-13 上午11:41:26
	 * @param attributecontainer DefaultAttributeContainer
	 * @return DefaultAttributeContainer
	 */
	private DefaultAttributeContainer removeCSMConstraint(DefaultAttributeContainer attributecontainer) {
		Object obj = attributecontainer.getConstraintParameter();
		if (obj == null) {
			obj = new String("CSM");
		} else if (obj instanceof Vector) {
			((Vector) obj).addElement(new String("CSM"));
		} else {
			Vector vector1 = new Vector();
			vector1.addElement(obj);
			obj = vector1;
			((Vector) obj).addElement(new String("CSM"));
		}
		try {
			attributecontainer.setConstraintParameter(obj);
		} catch (WTPropertyVetoException wtpropertyvetoexception) {
			wtpropertyvetoexception.printStackTrace();

		}
		return attributecontainer;
	}

	/**
	 * Update the IBAHolder's attribute container from the hashtable
	 *
	 * @param ibaHolder IBAHolder
	 * @return IBAHolder
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 */
	// public IBAHolder updateAttributeContainer(IBAHolder ibaHolder)
	// throws WTException, WTPropertyVetoException, RemoteException {
	// if (ibaHolder.getAttributeContainer() == null){
	// ibaHolder = IBAValueHelper.service.refreshAttributeContainer(
	// ibaHolder, null, SessionHelper.manager.getLocale(), null);
	// }
	// DefaultAttributeContainer defaultattributecontainer =
	// (DefaultAttributeContainer) ibaHolder
	// .getAttributeContainer();
	//
	// defaultattributecontainer = suppressCSMConstraint(
	// defaultattributecontainer, getIBAHolderClassName(ibaHolder));
	//
	// AttributeDefDefaultView[] theAtts = defaultattributecontainer
	// .getAttributeDefinitions();
	// // Delete existed iba if they aren't in the hashtable of this class
	// for (int i = 0; i < theAtts.length; i++) {
	// AttributeDefDefaultView theDef = theAtts[i];
	// if (ibaContainer.get(theDef.getName()) == null) {
	// createOrUpdateAttributeValuesInContainer(
	// defaultattributecontainer, theDef, null);
	// }
	// }
	//
	// Enumeration enum1 = ibaContainer.elements();
	// while (enum1.hasMoreElements()) {
	// Object[] temp = (Object[]) enum1.nextElement();
	// AttributeDefDefaultView theDef = (AttributeDefDefaultView) temp[0];
	// AbstractValueView abstractvalueviews[] = new AbstractValueView[temp.length -
	// 1];
	// for (int i = 0; i < temp.length - 1; i++) {
	// abstractvalueviews[i] = (AbstractValueView) temp[i + 1];
	// }
	// createOrUpdateAttributeValuesInContainer(defaultattributecontainer,
	// theDef, abstractvalueviews);
	// }
	//
	// defaultattributecontainer = removeCSMConstraint(defaultattributecontainer);
	// ibaHolder.setAttributeContainer(defaultattributecontainer);
	//
	// return ibaHolder;
	// }

	/**
	 * Update without checkout/checkin
	 *
	 * @param ibaholder IBAHolder
	 * @return boolean
	 */
	public static boolean updateIBAHolder(IBAHolder ibaholder) throws WTException {
		IBAValueDBService ibavaluedbservice = new IBAValueDBService();
		boolean flag = true;
		try {
			PersistenceServerHelper.manager.update((Persistable) ibaholder);
			AttributeContainer attributecontainer = ibaholder.getAttributeContainer();
			Object obj = ((DefaultAttributeContainer) attributecontainer).getConstraintParameter();
			AttributeContainer attributecontainer1 = ibavaluedbservice.updateAttributeContainer(ibaholder, obj, null,
					null);
			ibaholder.setAttributeContainer(attributecontainer1);
		} catch (WTException wtexception) {
			LOGGER.info("updateIBAHOlder: Couldn't update. " + wtexception);
			flag = false;
			wtexception.printStackTrace();
			throw new WTException(wtexception);
		}
		return flag;
	}

	/**
	 * Referenced from method "createOrUpdateAttributeValueInContainer" of
	 * wt.iba.value.service.LoadValue.java -> modified to have multi-values support
	 *
	 * @param defaultattributecontainer DefaultAttributeContainer
	 * @param theDef                    AttributeDefDefaultView
	 * @param abstractvalueviews        AbstractValueView[]
	 * @throws WTException
	 */
	private void createOrUpdateAttributeValuesInContainer(DefaultAttributeContainer defaultattributecontainer,
			AttributeDefDefaultView theDef, AbstractValueView[] abstractvalueviews)
			throws WTException, WTPropertyVetoException {
		if (defaultattributecontainer == null)
			throw new IBAContainerException("wt.iba.value.service.LoadValue.createOrUpdateAttributeValueInContainer"
					+ " :  DefaultAttributeContainer passed in is null!");
		AbstractValueView abstractvalueviews0[] = defaultattributecontainer.getAttributeValues(theDef);
		try {
			if (abstractvalueviews0 == null || abstractvalueviews0.length == 0) {
				// Original valus is empty
				for (int j = 0; j < abstractvalueviews.length; j++) {
					AbstractValueView abstractvalueview = abstractvalueviews[j];
					defaultattributecontainer.addAttributeValue(abstractvalueview);
					// LOG.info("IBAUtil:"+abstractvalueview.getLocalizedDisplayString()+"
					// in "+abstractvalueview.getDefinition().getName());
				}
			} else if (abstractvalueviews == null || abstractvalueviews.length == 0) {
				// New value is empty, so delete all existed values
				for (int j = 0; j < abstractvalueviews0.length; j++) {
					AbstractValueView abstractvalueview = abstractvalueviews0[j];
					defaultattributecontainer.deleteAttributeValue(abstractvalueview);
				}
			} else if (abstractvalueviews0.length <= abstractvalueviews.length) {

				// More new valuss than (or equal to) original values,
				// So update existed values and add new values
				for (int j = 0; j < abstractvalueviews0.length; j++) {
					abstractvalueviews0[j] = LoadValue.cloneAbstractValueView(abstractvalueviews[j],
							abstractvalueviews0[j]);
					// abstractvalueviews0[j] = abstractvalueviews[j];
					abstractvalueviews0[j] = cloneReferenceValueDefaultView(abstractvalueviews[j],
							abstractvalueviews0[j]);

					defaultattributecontainer.updateAttributeValue(abstractvalueviews0[j]);
				}
				for (int j = abstractvalueviews0.length; j < abstractvalueviews.length; j++) {
					AbstractValueView abstractvalueview = abstractvalueviews[j];
					// abstractvalueview.setState(AbstractValueView.CHANGED_STATE);
					defaultattributecontainer.addAttributeValue(abstractvalueview);
				}
			} else if (abstractvalueviews0.length > abstractvalueviews.length) {
				// Less new values than original values,
				// So delete some values
				for (int j = 0; j < abstractvalueviews.length; j++) {
					abstractvalueviews0[j] = LoadValue.cloneAbstractValueView(abstractvalueviews[j],
							abstractvalueviews0[j]);
					abstractvalueviews0[j] = cloneReferenceValueDefaultView(abstractvalueviews[j],
							abstractvalueviews0[j]);
					// abstractvalueviews0[j] = abstractvalueviews[j];
					defaultattributecontainer.updateAttributeValue(abstractvalueviews0[j]);
				}
				for (int j = abstractvalueviews.length; j < abstractvalueviews0.length; j++) {
					AbstractValueView abstractvalueview = abstractvalueviews0[j];
					defaultattributecontainer.deleteAttributeValue(abstractvalueview);
				}
			}
		} catch (IBAConstraintException ibaconstraintexception) {
			ibaconstraintexception.printStackTrace();
		}
	}

	/**
	 *
	 * @description For dependency used.
	 * @date 2009-4-13 上午11:45:05
	 * @param abstractvalueview  AbstractValueView
	 * @param abstractvalueview1 AbstractValueView
	 * @return AbstractValueView
	 * @throws IBAValueException
	 */
	AbstractValueView cloneReferenceValueDefaultView(AbstractValueView abstractvalueview,
			AbstractValueView abstractvalueview1) throws IBAValueException {
		if (abstractvalueview instanceof AbstractContextualValueDefaultView) {
			if (VERBOSE) {
				LOGGER.info(abstractvalueview1.getLocalizedDisplayString() + ":"
						+ abstractvalueview.getLocalizedDisplayString());
				if (((AbstractContextualValueDefaultView) abstractvalueview1).getReferenceValueDefaultView() != null)
					System.out.println("Key before set=" + ((AbstractContextualValueDefaultView) abstractvalueview1)
							.getReferenceValueDefaultView().getKey());
			}

			try {
				((AbstractContextualValueDefaultView) abstractvalueview1).setReferenceValueDefaultView(
						((AbstractContextualValueDefaultView) abstractvalueview).getReferenceValueDefaultView());
			} catch (WTPropertyVetoException wtpropertyvetoexception) {
				throw new IBAValueException("can't get ReferenceValueDefaultView from the Part in the database");
			}
			if (VERBOSE) {
				if (((AbstractContextualValueDefaultView) abstractvalueview1).getReferenceValueDefaultView() != null)
					System.out.println("Key after set=" + ((AbstractContextualValueDefaultView) abstractvalueview1)
							.getReferenceValueDefaultView().getKey());
			}

		}
		return abstractvalueview1;
	}

	/**
	 * another "black-box": pass in a string, and get back an IBA value object. Copy
	 * from wt.iba.value.service.LoadValue.java -> please don't modify this method
	 *
	 * @param abstractattributedefinizerview AbstractAttributeDefinizerView
	 * @param s                              String
	 * @param s1                             String
	 * @return AbstractValueView
	 */
	private static AbstractValueView internalCreateValue(AbstractAttributeDefinizerView abstractattributedefinizerview,
			String s, String s1) {
		AbstractValueView abstractvalueview = null;
		if (abstractattributedefinizerview instanceof FloatDefView)
			abstractvalueview = LoadValue.newFloatValue(abstractattributedefinizerview, s, s1);
		else if (abstractattributedefinizerview instanceof StringDefView)
			abstractvalueview = LoadValue.newStringValue(abstractattributedefinizerview, s);
		else if (abstractattributedefinizerview instanceof IntegerDefView)
			abstractvalueview = LoadValue.newIntegerValue(abstractattributedefinizerview, s);
		else if (abstractattributedefinizerview instanceof RatioDefView)
			abstractvalueview = LoadValue.newRatioValue(abstractattributedefinizerview, s, s1);
		else if (abstractattributedefinizerview instanceof TimestampDefView)
			abstractvalueview = LoadValue.newTimestampValue(abstractattributedefinizerview, s);
		else if (abstractattributedefinizerview instanceof BooleanDefView)
			abstractvalueview = LoadValue.newBooleanValue(abstractattributedefinizerview, s);
		else if (abstractattributedefinizerview instanceof URLDefView)
			abstractvalueview = LoadValue.newURLValue(abstractattributedefinizerview, s, s1);
		else if (abstractattributedefinizerview instanceof ReferenceDefView)
			abstractvalueview = LoadValue.newReferenceValue(abstractattributedefinizerview, s, s1);
		else if (abstractattributedefinizerview instanceof UnitDefView)
			abstractvalueview = LoadValue.newUnitValue(abstractattributedefinizerview, s, s1);

		return abstractvalueview;
	}

	/**
	 * This method is a "black-box": pass in a string, like "Electrical/Resistance/
	 * ResistanceRating" and get back a IBA definition object.
	 *
	 * @param ibaPath String
	 * @return AttributeDefDefaultView
	 */
	public AttributeDefDefaultView getAttributeDefinition(String ibaPath) {

		AttributeDefDefaultView ibaDef = null;
		try {
			ibaDef = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaPath);
			if (ibaDef == null) {
				AbstractAttributeDefinizerView ibaNodeView = DefinitionLoader.getAttributeDefinition(ibaPath);
				if (ibaNodeView != null)
					ibaDef = IBADefinitionHelper.service.getAttributeDefDefaultView((AttributeDefNodeView) ibaNodeView);
			}
		} catch (Exception wte) {
			wte.printStackTrace();
		}

		return ibaDef;
	}

	/**
	 *
	 * @description
	 * @date 2009-4-13 上午11:46:00
	 * @param unitdefview UnitDefView
	 * @return String
	 */
	public static String getDisplayUnits(UnitDefView unitdefview) {
		return getDisplayUnits(unitdefview, UNITS);
	}

	/**
	 *
	 * @description
	 * @date 2009-4-13 上午11:46:10
	 * @param unitdefview UnitDefView
	 * @param s           String
	 * @return String
	 */
	public static String getDisplayUnits(UnitDefView unitdefview, String s) {
		QuantityOfMeasureDefaultView quantityofmeasuredefaultview = unitdefview.getQuantityOfMeasureDefaultView();
		String s1 = quantityofmeasuredefaultview.getBaseUnit();
		if (s != null) {
			String s2 = unitdefview.getDisplayUnitString(s);
			if (s2 == null)
				s2 = quantityofmeasuredefaultview.getDisplayUnitString(s);
			if (s2 == null)
				s2 = quantityofmeasuredefaultview.getDefaultDisplayUnitString(s);
			if (s2 != null)
				s1 = s2;
		}
		if (s1 == null)
			return "";
		else
			return s1;
	}
	/**
	 *
	 * @description
	 * @date 2009-4-13 上午11:46:22
	 * @param ibaHolder IBAHolder
	 * @return String
	 * @throws IBAConstraintException
	 */
	// public static String getClassificationStructName(IBAHolder ibaHolder)
	// throws IBAConstraintException {
	// String s = getIBAHolderClassName(ibaHolder);
	// ClassificationService classificationservice1 = ClassificationHelper.service;
	// ClassificationStructDefaultView aclassificationstructdefaultview[] = null;
	// try {
	// aclassificationstructdefaultview = classificationservice1
	// .getAllClassificationStructures();
	// } catch (RemoteException remoteexception1) {
	// remoteexception1.printStackTrace();
	// throw new IBAConstraintException(remoteexception1);
	// } catch (CSMClassificationNavigationException
	// csmclassificationnavigationexception1) {
	// csmclassificationnavigationexception1.printStackTrace();
	// throw new IBAConstraintException(
	// csmclassificationnavigationexception1);
	// } catch (WTException wtexception1) {
	// wtexception1.printStackTrace();
	// throw new IBAConstraintException(wtexception1);
	// }
	// for (int i = 0; aclassificationstructdefaultview != null
	// && i < aclassificationstructdefaultview.length; i++)
	// if (s.equals(aclassificationstructdefaultview[i]
	// .getPrimaryClassName())) {
	// return s;
	// }
	//
	// try {
	// for (Class class1 = Class.forName(s).getSuperclass(); !class1
	// .getName().equals((wt.fc.WTObject.class).getName())
	// && !class1.getName().equals(
	// (java.lang.Object.class).getName()); class1 = class1
	// .getSuperclass()) {
	// for (int j = 0; aclassificationstructdefaultview != null
	// && j < aclassificationstructdefaultview.length; j++)
	// if (class1.getName().equals(
	// aclassificationstructdefaultview[j]
	// .getPrimaryClassName())) {
	// return class1.getName();
	// }
	// }
	// } catch (ClassNotFoundException classnotfoundexception) {
	// classnotfoundexception.printStackTrace();
	// }
	// return null;
	// }

	/**
	 * Please refer to the method "getIBAHolderClassName" of class
	 * "wt.csm.constraint.CSMConstraintFactory"
	 *
	 * @param ibaholder IBAHolder
	 * @return String
	 */
	private static String getIBAHolderClassName(IBAHolder ibaholder) {
		String s = null;
		if (ibaholder instanceof AbstractLiteObject)
			s = ((AbstractLiteObject) ibaholder).getHeavyObjectClassname();
		else
			s = ibaholder.getClass().getName();
		return s;
	}

	/**
	 * Please refer to the method "getClassificationStructDefaultViewByName" of
	 * class "wt.csm.constraint.CSMConstraintFactory"
	 *
	 * @param s String
	 * @return ClassificationStructDefaultView
	 * @throws IBAConstraintException
	 */
	// private ClassificationStructDefaultView
	// getClassificationStructDefaultViewByName(
	// String s) throws IBAConstraintException {
	// ClassificationService classificationservice = ClassificationHelper.service;
	// ClassificationStructDefaultView aclassificationstructdefaultview[] = null;
	// try {
	// aclassificationstructdefaultview = classificationservice
	// .getAllClassificationStructures();
	// } catch (RemoteException remoteexception) {
	// remoteexception.printStackTrace();
	// throw new IBAConstraintException(remoteexception);
	// } catch (CSMClassificationNavigationException
	// csmclassificationnavigationexception) {
	// csmclassificationnavigationexception.printStackTrace();
	// throw new IBAConstraintException(
	// csmclassificationnavigationexception);
	// } catch (WTException wtexception) {
	// wtexception.printStackTrace();
	// throw new IBAConstraintException(wtexception);
	// }
	// for (int i = 0; aclassificationstructdefaultview != null
	// && i < aclassificationstructdefaultview.length; i++)
	// if (s.equals(aclassificationstructdefaultview[i]
	// .getPrimaryClassName())) {
	// return aclassificationstructdefaultview[i];
	// }
	//
	// try {
	// for (Class class2 = Class.forName(s).getSuperclass(); !class2
	// .getName().equals((wt.fc.WTObject.class).getName())
	// && !class2.getName().equals(
	// (java.lang.Object.class).getName()); class2 = class2
	// .getSuperclass()) {
	// for (int j = 0; aclassificationstructdefaultview != null
	// && j < aclassificationstructdefaultview.length; j++)
	// if (class2.getName().equals(
	// aclassificationstructdefaultview[j]
	// .getPrimaryClassName())) {
	// return aclassificationstructdefaultview[j];
	// }
	// }
	// } catch (ClassNotFoundException classnotfoundexception) {
	// classnotfoundexception.printStackTrace();
	// }
	// return null;
	// }

	/**
	 * 根据属性名称获取其软属性对象 by Wide
	 * 
	 * @param Attribute Name
	 * @return BooleanDefinition
	 * @throws WTException
	 */
	public static StringDefinition findStringDefinition(String attributeName) throws WTException {
		StringDefinition sDef = null;
		QueryResult result = null;
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		int index = qs.addClassList(StringDefinition.class, true);
		WhereExpression where = new SearchCondition(StringDefinition.class, "name", SearchCondition.EQUAL,
				attributeName);
		qs.appendWhere(where, new int[] { index });
		result = PersistenceHelper.manager.find((StatementSpec) qs);
		while (result != null && result.hasMoreElements()) {
			Object[] objs = (Object[]) result.nextElement();
			for (Object obj : objs) {
				if (obj instanceof StringDefinition) {
					sDef = (StringDefinition) obj;
				}
			}
		}
		return sDef;
	}

	/**
	 * 根据属性名称获取其软属性对象 by Wide
	 * 
	 * @param Attribute Name
	 * @return BooleanDefinition
	 * @throws WTException
	 */
	public static FloatDefinition findFloatDefinition(String attributeName) throws WTException {
		FloatDefinition fDef = null;
		QueryResult result = null;
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		int index = qs.addClassList(FloatDefinition.class, true);
		WhereExpression where = new SearchCondition(FloatDefinition.class, "name", SearchCondition.EQUAL,
				attributeName);
		qs.appendWhere(where, new int[] { index });
		result = PersistenceHelper.manager.find((StatementSpec) qs);
		while (result != null && result.hasMoreElements()) {
			Object[] objs = (Object[]) result.nextElement();
			for (Object obj : objs) {
				if (obj instanceof FloatDefinition) {
					fDef = (FloatDefinition) obj;
				}
			}
		}
		return fDef;
	}

	/**
	 * 根据属性名称获取其软属性对象 by Wide
	 * 
	 * @param Attribute Name
	 * @return BooleanDefinition
	 * @throws WTException
	 */
	public static TimestampDefinition findTimestampDefinition(String attributeName) throws WTException {
		TimestampDefinition fDef = null;
		QueryResult result = null;
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		int index = qs.addClassList(TimestampDefinition.class, true);
		WhereExpression where = new SearchCondition(TimestampDefinition.class, "name", SearchCondition.EQUAL,
				attributeName);
		qs.appendWhere(where, new int[] { index });
		result = PersistenceHelper.manager.find((StatementSpec) qs);
		while (result != null && result.hasMoreElements()) {
			Object[] objs = (Object[]) result.nextElement();
			for (Object obj : objs) {
				if (obj instanceof TimestampDefinition) {
					fDef = (TimestampDefinition) obj;
				}
			}
		}
		return fDef;
	}

	/**
	 * 根据属性名称获取其软属性对象 by Wide
	 * 
	 * @param Attribute Name
	 * @return BooleanDefinition
	 * @throws WTException
	 */
	public static UnitDefinition findUnitDefinition(String attributeName) throws WTException {
		UnitDefinition fDef = null;
		QueryResult result = null;
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		int index = qs.addClassList(UnitDefinition.class, true);
		WhereExpression where = new SearchCondition(UnitDefinition.class, "name", SearchCondition.EQUAL, attributeName);
		qs.appendWhere(where, new int[] { index });
		result = PersistenceHelper.manager.find((StatementSpec) qs);
		while (result != null && result.hasMoreElements()) {
			Object[] objs = (Object[]) result.nextElement();
			for (Object obj : objs) {
				if (obj instanceof UnitDefinition) {
					fDef = (UnitDefinition) obj;
				}
			}
		}
		return fDef;
	}

	/**
	 * 根据属性名称获取其软属性对象 by Wide
	 * 
	 * @param Attribute Name
	 * @return BooleanDefinition
	 * @throws WTException
	 */
	public static URLDefinition findURLDefinition(String attributeName) throws WTException {
		URLDefinition fDef = null;
		QueryResult result = null;
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		int index = qs.addClassList(URLDefinition.class, true);
		WhereExpression where = new SearchCondition(URLDefinition.class, "name", SearchCondition.EQUAL, attributeName);
		qs.appendWhere(where, new int[] { index });
		result = PersistenceHelper.manager.find((StatementSpec) qs);
		while (result != null && result.hasMoreElements()) {
			Object[] objs = (Object[]) result.nextElement();
			for (Object obj : objs) {
				if (obj instanceof URLDefinition) {
					fDef = (URLDefinition) obj;
				}
			}
		}
		return fDef;
	}

	/**
	 * 根据属性名称获取其软属性对象 by Wide
	 * 
	 * @param Attribute Name
	 * @return BooleanDefinition
	 * @throws WTException
	 */
	public static IntegerDefinition findIntDefinition(String attributeName) throws WTException {
		IntegerDefinition sDef = null;
		QueryResult result = null;
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		int index = qs.addClassList(IntegerDefinition.class, true);
		WhereExpression where = new SearchCondition(IntegerDefinition.class, "name", SearchCondition.EQUAL,
				attributeName);
		qs.appendWhere(where, new int[] { index });
		result = PersistenceHelper.manager.find((StatementSpec) qs);
		while (result != null && result.hasMoreElements()) {
			Object[] objs = (Object[]) result.nextElement();
			for (Object obj : objs) {
				if (obj instanceof IntegerDefinition) {
					sDef = (IntegerDefinition) obj;
				}
			}
		}
		return sDef;
	}

	/**
	 * 根据属性名称获取其软属性对象 by Wide
	 * 
	 * @param Attribute Name
	 * @return BooleanDefinition
	 * @throws WTException
	 */
	public static BooleanDefinition findBooleanDefinition(String attributeName) throws WTException {
		BooleanDefinition sDef = null;
		QueryResult result = null;
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		int index = qs.addClassList(BooleanDefinition.class, true);
		WhereExpression where = new SearchCondition(BooleanDefinition.class, "name", SearchCondition.EQUAL,
				attributeName);
		qs.appendWhere(where, new int[] { index });
		result = PersistenceHelper.manager.find((StatementSpec) qs);
		while (result != null && result.hasMoreElements()) {
			Object[] objs = (Object[]) result.nextElement();
			for (Object obj : objs) {
				if (obj instanceof BooleanDefinition) {
					sDef = (BooleanDefinition) obj;
				}
			}
		}
		return sDef;
	}

	/**
	 * 获取软属性对象
	 * 
	 * @return
	 * @throws WTException
	 * @throws WTRuntimeException
	 */
	public StringValue getStringValueByName(String attributeName) throws WTException {
		StringValue value = null;
		Object[] objs = (Object[]) ibaContainer.get(attributeName);
		if (objs != null && objs.length > 0) {

			AbstractValueView view = (AbstractValueView) objs[1];
			StringValueDefaultView stringvaluedefaultview = (StringValueDefaultView) view;
			ObjectIdentifier objectidentifier = stringvaluedefaultview.getObjectID();
			Persistable persistable = ObjectReference.newObjectReference(objectidentifier).getObject();
			value = (StringValue) persistable;
		}
		return value;
	}

	private Persistable getValueByName(String attributeName) throws WTException {
		Persistable value = null;
		try {
			Object[] objs = (Object[]) ibaContainer.get(attributeName);
			if (objs != null && objs.length > 0) {

				AbstractValueView view = (AbstractValueView) objs[1];
				view.getObjectID();
				ObjectIdentifier objectidentifier = (ObjectIdentifier) view.getObjectID();
				value = ObjectReference.newObjectReference(objectidentifier).getObject();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WTException(e);
		}
		return value;
	}

	/**
	 * 获取软属性对象
	 * 
	 * @return
	 * @throws WTException
	 * @throws WTRuntimeException
	 */
	public FloatValue getFloatValueByName(String attributeName) throws WTException {
		FloatValue value = null;
		Object[] objs = (Object[]) ibaContainer.get(attributeName);
		if (objs != null && objs.length > 0) {

			AbstractValueView view = (AbstractValueView) objs[1];
			FloatValueDefaultView floatvaluedefaultview = (FloatValueDefaultView) view;

			ObjectIdentifier objectidentifier = floatvaluedefaultview.getObjectID();
			Persistable persistable = ObjectReference.newObjectReference(objectidentifier).getObject();
			value = (FloatValue) persistable;
		}
		return value;
	}

	/**
	 * 获取时间格式的数据
	 * 
	 * @param attributeName
	 * @return
	 * @throws WTException
	 */
	public TimestampValue getTimestampValue(String attributeName) throws WTException {
		TimestampValue value = null;
		Object[] objs = (Object[]) ibaContainer.get(attributeName);
		if (objs != null && objs.length > 0) {
			AbstractValueView view = (AbstractValueView) objs[1];
			if (view instanceof TimestampValueDefaultView) {
				TimestampValueDefaultView tsvaluedefaultview = (TimestampValueDefaultView) view;
				ObjectIdentifier objectidentifier = tsvaluedefaultview.getObjectID();
				Persistable persistable = ObjectReference.newObjectReference(objectidentifier).getObject();
				if (persistable instanceof TimestampValue) {
					value = (TimestampValue) persistable;
				}
			}
		}
		return value;
	}

	/**
	 * 获取软属性对象
	 * 
	 * @return
	 * @throws WTException
	 * @throws WTRuntimeException
	 */
	public URLValue getURLValueByName(String attributeName) throws WTException {
		URLValue value = null;
		Object[] objs = (Object[]) ibaContainer.get(attributeName);
		if (objs != null && objs.length > 0) {

			AbstractValueView view = (AbstractValueView) objs[1];
			URLValueDefaultView urlValuedefaultview = (URLValueDefaultView) view;

			ObjectIdentifier objectidentifier = urlValuedefaultview.getObjectID();
			Persistable persistable = ObjectReference.newObjectReference(objectidentifier).getObject();
			value = (URLValue) persistable;

		}
		return value;
	}

	public Vector getURLValues(String name) throws WTRuntimeException, WTException {
		Vector<URLValue> values = new Vector<>();
		Object[] objs = (Object[]) ibaContainer.get(name);

		if (objs != null && objs.length > 0) {
			objs = ArrayUtils.remove(objs, 0);
			for (Object obj : objs) {
				AbstractValueView view = (AbstractValueView) obj;
				URLValueDefaultView urlValuedefaultview = (URLValueDefaultView) view;
				ObjectIdentifier objectidentifier = urlValuedefaultview.getObjectID();
				Persistable persistable = ObjectReference.newObjectReference(objectidentifier).getObject();
				values.add((URLValue) persistable);
			}
		}
		return values;
	}

	/**
	 * 获取软属性对象
	 * 
	 * @return
	 * @throws WTException
	 * @throws WTRuntimeException
	 */
	public IntegerValue getIntValueByName(String attributeName) throws WTException {
		IntegerValue value = null;
		Object[] objs = (Object[]) ibaContainer.get(attributeName);
		if (objs != null && objs.length > 0) {

			AbstractValueView view = (AbstractValueView) objs[1];
			IntegerValueDefaultView intvalue = (IntegerValueDefaultView) view;

			ObjectIdentifier objectidentifier = intvalue.getObjectID();
			Persistable persistable = ObjectReference.newObjectReference(objectidentifier).getObject();
			value = (IntegerValue) persistable;
		}
		return value;
	}

	/**
	 * 获取软属性对象
	 * 
	 * @return
	 * @throws WTException
	 * @throws WTRuntimeException
	 */
	public BooleanValue getBooleanValueByName(String attributeName) throws WTException {
		BooleanValue value = null;
		Object[] objs = (Object[]) ibaContainer.get(attributeName);
		if (objs != null && objs.length > 0) {

			AbstractValueView view = (AbstractValueView) objs[1];
			BooleanValueDefaultView intvalue = (BooleanValueDefaultView) view;

			ObjectIdentifier objectidentifier = intvalue.getObjectID();
			Persistable persistable = ObjectReference.newObjectReference(objectidentifier).getObject();
			value = (BooleanValue) persistable;
		}
		return value;
	}

	/**
	 * 设置对象软属性的值，但不检出对象
	 * 
	 * @param holder
	 * @param typeName
	 * @param value
	 * @throws WTException
	 */
	public static void newIBATimeAttribute(IBAHolder holder, String typeName, Object value) throws WTException {
		try {
			if (value instanceof Timestamp) {
				Timestamp ts = (Timestamp) value;
				IBAUtil util = new IBAUtil(holder);
				TimestampDefinition sd = findTimestampDefinition(typeName);
				if (sd != null) {
					TimestampValue sv = util.getTimestampValue(typeName);
					if (sv != null) {
						LOGGER.debug("对象[" + holder.toString() + "]属性[" + sd.getName() + "]旧值为[" + sv.getValue() + "]");
						if (value == null) {
							PersistenceHelper.manager.delete(sv);
						} else {
							sv.setValue(ts);
							PersistenceServerHelper.manager.update(sv);
						}
					} else {
						sv = TimestampValue.newTimestampValue(sd, holder, ts);
						PersistenceServerHelper.manager.insert(sv);
					}
				}

			}
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 设置对象软属性的值，但不检出对象
	 * 
	 * @param holder
	 * @param typeName
	 * @param value
	 * @throws WTException
	 */
	public static void newIBAUnitAttribute(IBAHolder holder, String typeName, Object value) throws WTException {
		try {
			if (value instanceof Double) {
				double f = (double) value;
				IBAUtil util = new IBAUtil(holder);
				UnitDefinition sd = findUnitDefinition(typeName);
				if (sd != null) {
					UnitValue sv = util.getUnitFloatValueByName(typeName);
					if (sv != null) {
						LOGGER.debug("对象[" + holder.toString() + "]属性[" + sd.getName() + "]旧值为[" + sv.getValue() + "]");
						if (value == null) {
							PersistenceHelper.manager.delete(sv);
						} else {
							sv.setValue(f);
							PersistenceServerHelper.manager.update(sv);
						}
					} else {
						int paramInt = 0;
						String t = Double.toString(f);
						if (t.indexOf(".") > -1) {
							t = t.substring(t.indexOf(".") + 1, t.length());
							while (t.startsWith("0")) {
								t = t.replaceFirst("0", "");
							}
							paramInt = t.length();
						} else {
							// 没有小数点，则保留0位
							paramInt = 0;
						}
						sv = UnitValue.newUnitValue(sd, holder, f, paramInt);
						PersistenceServerHelper.manager.insert(sv);
					}
				}

			}
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 获取软属性对象
	 * 
	 * @return
	 * @throws WTException
	 * @throws WTRuntimeException
	 */
	public UnitValue getUnitFloatValueByName(String attributeName) throws WTException {
		UnitValue value = null;
		Object[] objs = (Object[]) ibaContainer.get(attributeName);
		if (objs != null && objs.length > 0) {

			AbstractValueView view = (AbstractValueView) objs[1];
			UnitValueDefaultView unitfloatvaluedefaultview = (UnitValueDefaultView) view;

			ObjectIdentifier objectidentifier = unitfloatvaluedefaultview.getObjectID();
			Persistable persistable = ObjectReference.newObjectReference(objectidentifier).getObject();
			value = (UnitValue) persistable;
		}
		return value;
	}

	public static void setIBATimestampValue(IBAHolder obj, String ibaName, Timestamp newValue) throws WTException {
		String ibaClass = "wt.iba.definition.TimestampDefinition";
		try {
			IBAHolder ibaHolder = (IBAHolder) obj;
			DefaultAttributeContainer defaultattributecontainer = getContainer(ibaHolder);
			if (defaultattributecontainer == null) {
				defaultattributecontainer = new DefaultAttributeContainer();
				ibaHolder.setAttributeContainer(defaultattributecontainer);
			}
			TimestampValueDefaultView abstractvaluedefaultview = (TimestampValueDefaultView) getIBAValueView(
					defaultattributecontainer, ibaName, ibaClass);
			if (abstractvaluedefaultview != null) {
				abstractvaluedefaultview.setValue(newValue);

				defaultattributecontainer.updateAttributeValue(abstractvaluedefaultview);
			} else {
				AttributeDefDefaultView attributedefdefaultview = getAttributeDefinition(ibaName, false);
				TimestampValueDefaultView abstractvaluedefaultview1 = new TimestampValueDefaultView(
						(TimestampDefView) attributedefdefaultview, newValue);
				defaultattributecontainer.addAttributeValue(abstractvaluedefaultview1);
			}
			ibaHolder.setAttributeContainer(defaultattributecontainer);
			StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaHolder, null, null, null);
			ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, "CSM", null, null);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static DefaultAttributeContainer getContainer(IBAHolder ibaHolder) throws WTException, RemoteException {
		ibaHolder = IBAValueHelper.service.refreshAttributeContainerWithoutConstraints(ibaHolder);
		DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) ibaHolder
				.getAttributeContainer();
		return defaultattributecontainer;
	}

	public static AttributeDefDefaultView getAttributeDefinition(String s, boolean flag1) {
		AttributeDefDefaultView attributedefdefaultview = null;
		try {
			attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(s);
			if (attributedefdefaultview == null) {
				AbstractAttributeDefinizerView abstractattributedefinizerview = DefinitionLoader
						.getAttributeDefinition(s);
				if (abstractattributedefinizerview != null)
					attributedefdefaultview = IBADefinitionHelper.service
							.getAttributeDefDefaultView((AttributeDefNodeView) abstractattributedefinizerview);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return attributedefdefaultview;
	}

	public static AbstractValueView getIBAValueView(DefaultAttributeContainer dac, String ibaName, String ibaClass)
			throws WTException {
		AbstractValueView aabstractvalueview[] = null;
		AbstractValueView avv = null;
		aabstractvalueview = dac.getAttributeValues();
		for (int j = 0; j < aabstractvalueview.length; j++) {
			String thisIBAName = aabstractvalueview[j].getDefinition().getName();
			String thisIBAValue = IBAValueUtility.getLocalizedIBAValueDisplayString(aabstractvalueview[j],
					Locale.CHINA);
			String thisIBAClass = (aabstractvalueview[j].getDefinition()).getAttributeDefinitionClassName();
			if (thisIBAName.equals(ibaName) && thisIBAClass.equals(ibaClass)) {
				avv = aabstractvalueview[j];
				break;
			}
		}
		return avv;
	}

	/**
	 * 设置对象软属性的值，但不检出对象
	 * 
	 * @param holder
	 * @param typeName
	 * @param value
	 * @throws WTException
	 */
	public static void newIBAAttribute(IBAHolder holder, String typeName, String value) throws WTException {
		try {
			IBAUtil util = new IBAUtil(holder);
			StringDefinition sd = findStringDefinition(typeName);
			if (sd != null) {
				StringValue sv = util.getStringValueByName(typeName);
				if (sv != null) {
					// LOGGER.debug("对象["+holder.toString()+"]属性["+sd.getName()+"]旧值为["+sv.getValue()+"]");
					if (value == null || value.trim().length() == 0) {
						PersistenceHelper.manager.delete(sv);
					} else {
						sv.setValue(value);
						PersistenceServerHelper.manager.update(sv);
					}
				} else {
					sv = StringValue.newStringValue(sd, holder, value);
					PersistenceServerHelper.manager.insert(sv);
				}
			} else {
				LOGGER.error("属性[" + typeName + "]不存在");
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 设置对象软属性的值，但不检出对象
	 * 
	 * @param holder
	 * @param typeName
	 * @param value
	 * @throws WTException
	 */
	public static void newIBAAttributeNoUpdate(IBAHolder holder, String typeName, String value) throws WTException {
		try {
			IBAUtil util = new IBAUtil(holder);
			StringDefinition sd = findStringDefinition(typeName);
			if (sd != null) {
				StringValue sv = StringValue.newStringValue(sd, holder, value);
				PersistenceServerHelper.manager.insert(sv);
			} else {
				LOGGER.error("属性[" + typeName + "]不存在");
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	public static void deleteIBAAttribute(IBAHolder holder, String typeName) throws WTException {
		LOGGER.debug("删除对象[" + holder.toString() + "]属性[" + typeName + "]");
		StringDefinition sd = findStringDefinition(typeName);
		IBAUtil util = new IBAUtil(holder);
		if (sd != null) {
			StringValue sv = util.getStringValueByName(typeName);
			if (sv != null) {
				LOGGER.debug("对象[" + holder.toString() + "]属性[" + sd.getName() + "]旧值为[" + sv.getValue() + "]");
				PersistenceHelper.manager.delete(sv);
			}
		}
	}

	/**
	 * 记录多值的字符串软属性
	 * 
	 * @param holder
	 * @param typeName
	 * @param value
	 * @throws WTException
	 */
	public static void newIBAAttributes(IBAHolder holder, String typeName, ArrayList<String> values)
			throws WTException {
		IBAUtil util = new IBAUtil(holder);
		/**
		 * 先删除所有的值
		 */
		Object[] objs = (Object[]) util.ibaContainer.get(typeName);
		if (objs != null && objs.length > 0) {
			for (int i = 1; i < objs.length; i++) {
				AbstractValueView view = (AbstractValueView) objs[i];
				StringValueDefaultView stringvaluedefaultview = (StringValueDefaultView) view;
				ObjectIdentifier objectidentifier = stringvaluedefaultview.getObjectID();
				Persistable persistable = ObjectReference.newObjectReference(objectidentifier).getObject();
				StringValue theValue = (StringValue) persistable;
				PersistenceHelper.manager.delete(theValue);
			}
		}
		StringDefinition sd = findStringDefinition(typeName);
		/**
		 * 再增加传入的值，如果没有值，则清空软属性
		 */
		for (String value : values) {
			StringValue sv = StringValue.newStringValue(sd, holder, value);
			PersistenceServerHelper.manager.insert(sv);
		}
	}

	/**
	 * 设置对象软属性的值，但不检出对象
	 * 
	 * @param holder
	 * @param typeName
	 * @param value
	 * @throws WTException
	 */
	public static void newIBAFloatAttribute(IBAHolder holder, String typeName, double value) throws WTException {
		try {
			LOGGER.debug("newIBAFloatAttribute type --->" + typeName + " value --->" + value);
			IBAUtil util = new IBAUtil(holder);
			FloatDefinition fd = findFloatDefinition(typeName);
			if (fd != null) {
				// 默认保留三位小数
				int paramInt = 3;
				if (value == 0 || value == 1) {
					paramInt = 0;
				} else {
					String t = Double.toString(value);
					if (t.indexOf(".") > -1) {
						t = t.substring(t.indexOf(".") + 1, t.length());
						while (t.startsWith("0")) {
							t = t.replaceFirst("0", "");
						}
						paramInt = t.length();
					} else {
						// 没有小数点，则保留0位
						paramInt = 0;
					}
				}
				FloatValue fv = util.getFloatValueByName(typeName);
				if (fv != null) {
					fv.setValue(value);
					fv.setPrecision(paramInt);
					PersistenceServerHelper.manager.update(fv);
					// PersistenceHelper.manager.save(sv);
				} else {
					fv = FloatValue.newFloatValue(fd, holder, value, paramInt);
					PersistenceServerHelper.manager.insert(fv);
				}
			} else {
				LOGGER.error("属性[" + typeName + "]不存在");
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 设置对象软属性的值，但不检出对象
	 * 
	 * @param holder
	 * @param typeName
	 * @param value
	 * @throws WTException
	 */
	public static void newIBAFloatAttribute(IBAHolder holder, String typeName, float value) throws WTException {
		try {
			LOGGER.debug("newIBAFloatAttribute type --->" + typeName + " value --->" + value);
			IBAUtil util = new IBAUtil(holder);
			FloatDefinition fd = findFloatDefinition(typeName);
			if (fd != null) {
				// 默认保留三位小数
				int paramInt = 3;
				if (value == 0 || value == 1) {
					paramInt = 0;
				} else {
					String t = Float.toString(value);
					if (t.indexOf(".") > -1) {
						t = t.substring(t.indexOf(".") + 1, t.length());
						while (t.startsWith("0")) {
							t = t.replaceFirst("0", "");
						}
						paramInt = t.length();
					} else {
						// 没有小数点，则保留0位
						paramInt = 0;
					}
				}
				FloatValue fv = util.getFloatValueByName(typeName);
				if (fv != null) {
					fv.setValue(value);
					fv.setPrecision(paramInt);
					PersistenceServerHelper.manager.update(fv);
				} else {
					fv = FloatValue.newFloatValue(fd, holder, value, paramInt);
					PersistenceServerHelper.manager.insert(fv);
				}
			} else {
				LOGGER.error("属性[" + typeName + "]不存在");
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 设置对象软属性的值，但不检出对象
	 * 
	 * @param holder
	 * @param typeName
	 * @param value
	 * @param des      : 连接描述
	 * @throws WTException
	 */
	public static void newIBAURLAttribute(IBAHolder holder, String typeName, String value, String des)
			throws WTException {
		try {
			LOGGER.debug("newIBAFloatAttribute type --->" + typeName + " value --->" + value);
			IBAUtil util = new IBAUtil(holder);
			URLDefinition fd = findURLDefinition(typeName);
			if (fd != null) {

				URLValue fv = util.getURLValueByName(typeName);
				if (fv != null) {
					if (value == null || value.trim().length() == 0) {
						PersistenceHelper.manager.delete(fv);
					} else {
						fv.setDescription(des);
						fv.setValue(value);
						PersistenceServerHelper.manager.update(fv);
					}
				} else {
					fv = URLValue.newURLValue(fd, holder, value, des);
					PersistenceServerHelper.manager.insert(fv);
				}
			} else {
				LOGGER.error("属性[" + typeName + "]不存在");
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 设置对象软属性的值，但不检出对象
	 * 
	 * @param holder
	 * @param typeName
	 * @param value
	 * @throws WTException
	 */
	public static void newIBAIntAttribute(IBAHolder holder, String typeName, long value) throws WTException {
		try {
			LOGGER.debug("newIBAFloatAttribute type --->" + typeName + " value --->" + value);
			IBAUtil util = new IBAUtil(holder);
			IntegerDefinition fd = findIntDefinition(typeName);
			LOGGER.debug("获取到的属性类型为--->" + fd.getDisplayName());
			if (fd != null) {
				IntegerValue iv = util.getIntValueByName(typeName);
				if (iv != null) {
					iv.setValue(value);
					PersistenceServerHelper.manager.update(iv);
					// PersistenceHelper.manager.save(sv);
				} else {
					iv = IntegerValue.newIntegerValue(fd, holder, value);
					PersistenceServerHelper.manager.insert(iv);
				}
			} else {
				LOGGER.error("Can't find IntegerDefinition by name --->" + typeName);
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 设置对象软属性的值，但不检出对象
	 * 
	 * @param holder
	 * @param typeName
	 * @param value
	 * @throws WTException
	 */
	public static void newIBABooleanAttribute(IBAHolder holder, String typeName, boolean value) throws WTException {
		try {
			LOGGER.debug("newIBAFloatAttribute type --->" + typeName + " value --->" + value);
			IBAUtil util = new IBAUtil(holder);
			BooleanDefinition fd = findBooleanDefinition(typeName);
			LOGGER.debug("获取到的属性类型为--->" + fd.getDisplayName());
			if (fd != null) {
				BooleanValue iv = util.getBooleanValueByName(typeName);
				if (iv != null) {
					iv.setValue(value);
					PersistenceServerHelper.manager.update(iv);
					// PersistenceHelper.manager.save(sv);
				} else {
					iv = BooleanValue.newBooleanValue(fd, holder, value);
					PersistenceServerHelper.manager.insert(iv);
				}
			} else {
				LOGGER.error("Can't find IntegerDefinition by name --->" + typeName);
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 设置对象软属性的值（当值不存在时才写入，如果原来有，则保留原来的值），但不检出对象
	 * 
	 * @param holder
	 * @param typeName
	 * @param value
	 * @throws WTException
	 */
	public static void insertIBAAttribute(IBAHolder holder, String typeName, String value) throws WTException {
		try {
			IBAUtil util = new IBAUtil(holder);
			StringDefinition sd = findStringDefinition(typeName);
			if (sd != null) {
				StringValue sv = util.getStringValueByName(typeName);
				if (sv == null) {
					sv = StringValue.newStringValue(sd, holder, value);
					PersistenceServerHelper.manager.insert(sv);
				}
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	public ArrayList<String> getStringValues(String key) throws WTException {
		ArrayList<String> list = new ArrayList<String>();
		String value = "";
		try {
			if (ibaContainer.get(key) != null) {
				Object[] objs = (Object[]) ibaContainer.get(key);
				for (int i = 1; i < objs.length; i++) {
					AbstractValueView theValue = (AbstractValueView) ((Object[]) ibaContainer.get(key))[i];
					value = (IBAValueUtility.getLocalizedIBAValueDisplayString(theValue,
							SessionHelper.manager.getLocale()));
					list.add(value);
				}

			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<StringValue> getAllStringValues(String key) throws WTException {
		ArrayList<StringValue> list = new ArrayList<StringValue>();
		try {
			if (ibaContainer.get(key) != null) {
				Object[] objs = (Object[]) ibaContainer.get(key);
				for (int i = 1; i < objs.length; i++) {
					AbstractValueView theValue = (AbstractValueView) ((Object[]) ibaContainer.get(key))[i];
					StringValueDefaultView stringvaluedefaultview = (StringValueDefaultView) theValue;
					ObjectIdentifier objectidentifier = stringvaluedefaultview.getObjectID();
					Persistable persistable = ObjectReference.newObjectReference(objectidentifier).getObject();
					StringValue value = (StringValue) persistable;
					list.add(value);
				}

			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 查询某个软属性值为指定值的所有对象(需过滤掉非最新小版本)
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static HashMap<String, IBAHolder> getIBAHolder(String key, String value) {
		HashMap<String, IBAHolder> holdMap = new HashMap<String, IBAHolder>();
		try {
			StringDefinition sd = findStringDefinition(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return holdMap;
	}

	/***
	 * 不检出修改IBA属性，且无需指定类型
	 * 
	 * @param holder
	 * @param typeName
	 * @param value
	 * @throws Exception
	 */
	public void setIBAAttribute4AllType(IBAHolder holder, String typeName, Object value) throws Exception {

		AttributeDefDefaultView view = getAttributeDefinition(typeName);
		if (view == null) {
			LOGGER.error("属性[" + typeName + "]不存在，不设置该IBA属性");
			// throw new Exception("属性[" + typeName + "]不存在");
			return;
		}
		LOGGER.debug("当前需设置的IBA内部名称为=>" + typeName + " 软属性类型为=>[" + view.getClass().getName() + "]" + " 值为=>[" + value
				+ "]");
		if (view instanceof FloatDefView) {
			if (value != null) {
				if (value instanceof Double) {
					IBAUtil.newIBAFloatAttribute(holder, typeName, (double) value);
				} else {
					double d = Double.parseDouble(value.toString());
					IBAUtil.newIBAFloatAttribute(holder, typeName, d);
				}
			}
		} else if (view instanceof StringDefView) {
			if (value != null) {
				if (value instanceof String) {
					IBAUtil.newIBAAttribute(holder, typeName, (String) value);
				} else {
					IBAUtil.newIBAAttribute(holder, typeName, value.toString());
				}
			}
		} else if (view instanceof URLDefView) {
			if (value != null) {
				IBAUtil.newIBAURLAttribute(holder, typeName, value.toString(), value.toString());
			} else {
				IBAUtil.newIBAURLAttribute(holder, typeName, "", "");
			}
		} else if (view instanceof IntegerDefView) {
			if (value != null) {
				if (value instanceof Integer) {
					IBAUtil.newIBAIntAttribute(holder, typeName, (Integer) value);
				} else {
					int it = Integer.parseInt(value.toString());
					IBAUtil.newIBAIntAttribute(holder, typeName, it);
				}
			}
		} else if (view instanceof TimestampDefView) {
			if (value != null) {
				if (value instanceof Date) {
					Date date = (Date) value;
					Timestamp ts = new Timestamp(date.getTime());
					IBAUtil.newIBATimeAttribute(holder, typeName, ts);
				} else {
					LOGGER.error("待导入的数据类型不是日期格式");
				}
			}
		} else if (view instanceof BooleanDefView) {
			if (value != null) {
				if (value instanceof Boolean) {
					IBAUtil.newIBABooleanAttribute(holder, typeName, (Boolean) value);
				} else {
					IBAUtil.newIBABooleanAttribute(holder, typeName, Boolean.parseBoolean((String) value));
				}
			}
		} else {
			LOGGER.error("失败，不支持的软属性类型");
		}
	}

	/***
	 * 检出，修改部件对象IBA值，检入
	 * 
	 * @param part
	 * @param dataMap
	 * @throws WTException
	 * @throws PersistenceException
	 * @throws WTPropertyVetoException
	 * @throws WorkInProgressException
	 * @throws NonLatestCheckoutException
	 */
	public void checkAndSetIBAValues(WTPart part, Map<String, String> dataMap) throws Exception {
		WTPart part_work = (WTPart) WorkInProgressHelper.service
				.checkout(part, WorkInProgressHelper.service.getCheckoutFolder(), null).getWorkingCopy();

		for (String key : dataMap.keySet()) {
			AttributeDefDefaultView view = getAttributeDefinition(key);
			if (view == null) {
				LOGGER.error("属性[" + key + "]不存在，不设置该IBA属性");
				continue;
			}

			if (dataMap.get(key) != null) {
				LOGGER.debug("Begin set IBA key " + key + " ====> " + dataMap.get(key));
				setIBAAttribute4AllType(part_work, key, dataMap.get(key));
			}
		}
		WorkInProgressHelper.service.checkin(part_work, null);
	}

	/***
	 * 获取全局属性在类型上的显示名称
	 * 
	 * @param ibaName
	 * @return
	 * @throws Exception
	 */
	public String getInternalDisplayName(String ibaName) throws Exception {
		String displayName = "";
		TypeIdentifier typeIden = TypeIdentifierUtility.getTypeIdentifier(persistable); // unsupported API
		TypeDefinitionReadView tdrv = TypeDefinitionServiceHelper.service.getTypeDefView(typeIden);
		if (tdrv != null) {
			AttributeDefinitionReadView attView = tdrv.getAttributeByName(ibaName); // att1 is attribute name
			if (attView != null) {
				displayName = PropertyHolderHelper.getDisplayName(attView, SessionHelper.manager.getLocale());
				// LOGGER.debug("display Name:" + displayName);
				// LOGGER.debug("attView Name:" + attView.getName());
			}
		}
		return displayName;
	}

	// 获取枚举值显示名称
	public static String getEnumDisplayValue(WTObject obj, String key) {
		String value = "";
		if (obj == null || StringUtils.isBlank(key)) {
			return value;
		}
		try {
			java.util.Locale locale = wt.session.SessionHelper.manager.getLocale();
			com.ptc.core.lwc.server.PersistableAdapter pbo = new com.ptc.core.lwc.server.PersistableAdapter(obj, null,
					locale, new com.ptc.core.meta.common.DisplayOperationIdentifier());
			pbo.load(key);
			String codeOfAccountsValue = (String) pbo.get(key);
			com.ptc.core.meta.container.common.AttributeTypeSummary ats = pbo.getAttributeDescriptor(key);
			value = com.ptc.core.components.util.AttributeHelper.getEnumeratedEntryDisplayValue(codeOfAccountsValue,
					ats, locale);
			// System.out.println(codeOfAccountsValue+"<<<<"+key+"--3------->"+value);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return value;
	}

}