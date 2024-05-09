<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments"%>
<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<jca:initializeItem baseTypeName="ext.HHT.model.annualBudgetEntry.AnnualBudgetEntry" operation="${createBean.create}" attributePopulatorClass="com.ptc.core.components.forms.DefaultAttributePopulator"/>
<jca:wizard>
    <jca:wizardStep action="createAnnualBudgetEntry" type="HHTWizard"/>
</jca:wizard>
<%@include file="/netmarkets/jsp/util/end.jspf"%>