<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%> 
<jca:wizard title="跟踪工时"  formProcessorController="ext.HHT.project.TrackHours.TrackHoursController">
	<jca:wizardStep action="trackHours" type="HHTWizard" />
</jca:wizard>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>