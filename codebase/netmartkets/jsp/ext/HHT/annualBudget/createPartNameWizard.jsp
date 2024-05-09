<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<jca:wizard  buttonList="DefaultWizardButtons" title="新建年度预算" formProcessorController="ext.HHT.model.annualBudget.mvc.service.CreateController">
	<jca:wizardStep action="createAnnualBudget" type="HHTWizard" label="新建年度预算" />
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
