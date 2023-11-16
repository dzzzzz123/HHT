<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%> 
<jca:wizard title="修改部件价格属性"  formProcessorController="ext.sap.purchase.AlterAttrProcessorContrller">
	<jca:wizardStep action="alterProperties" type="SAPWizard" />
</jca:wizard>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>