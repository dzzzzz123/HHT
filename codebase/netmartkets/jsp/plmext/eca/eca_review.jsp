<%@page import="org.apache.commons.lang3.time.DateUtils"%>
<%@page import="org.apache.commons.beanutils.ConvertUtils"%>

<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="wt.fc.PersistentReference"%>
<%@page import="wt.change2.*" %>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.fc.WTReference"%>
<%@page import="ext.plm.util.CommUtil"%>
<%@page import="java.text.SimpleDateFormat" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%> 

<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String oid = request.getParameter("oid");

	ReferenceFactory rf = new ReferenceFactory();
	WorkItem workItem = (WorkItem) rf.getReference(oid).getObject();
	PersistentReference persistentReference = workItem.getPrimaryBusinessObject();
	WTChangeActivity2 eca = (WTChangeActivity2)persistentReference.getObject();
	QueryResult qr = ChangeHelper2.service.getChangeOrder(eca);
	WTChangeOrder2 ecn = null;
	if(qr.size()>0){
		ecn = (WTChangeOrder2)qr.nextElement();
	}
	String ecnCt = CommUtil.getFormatDate(ecn.getCreateTimestamp(),"");
	
	List<Object[]> messList = ext.plm.change.ECRExcelUtil.getEcaFileMsg(eca);
	
	String btStr ="序号;替换组;更改类型;父件编码;物料编号;物料描述;版本;数量;属性;物料编号;物料描述;版本;数量;属性;未清PO;在制品;产成品;备注";
	String[] tits = btStr.split(";");
	
 %>

 
<fieldset id="create_div" class="x-fieldset x-form-label-left"
	style="width: 99%; "><legend
	class="x-fieldset-header x-unselectable"><span
	class="attributePanel-fieldset-title">更改通知单内容：</span></legend>
 
<table border="0" cellpadding="0" cellspacing="0" id="ecaAttTable" width="98%" class="gridtable2">
    <tr>
   		 <th width="100px">更改通告编号：</th>
	   	 <td colspan=4>
	   	 	<%=ecn.getNumber()%>
	   	 </td>
		 <th colspan=2>更改通告名称：</th>
	   	 <td colspan=6>
	   	 	<%=ecn.getName()%>
	   	 </td>
		 <th colspan=2>创建日期:</th>
	   	 <td colspan=3>
	   	 	<%=ecnCt%>
	   	 </td>
    </tr>
 
    <tr>
		<th>更改通告描述：</th>
		<td colspan=<%=tits.length-1%> >
	   	 	<%=ecn.getDescription()==null?"":ecn.getDescription()%>
	   	</td>
	</tr>
	
    <tr>
		<%
			for(String tname : tits){
		%>
		<th align="center"><%=tname%></th>
		<%}%>
	</tr>
	<%
	if(messList!=null) {
		for(Object[] data : messList){
	%>
	<tr>
		<% 
		for(int i=0;i<data.length;i++){
			String alStr = "align='left'";
			if(i==0){
				alStr = "align='center'";
			}
		%>
		<td <%=alStr%>>
	   	 	<%=data[i]==null?"":data[i].toString()%>
	   	 </td>
		<%}%>
	</tr>
	<%
		}}
	%>
		
</table>
</fieldset>
 
<script type="text/javascript">
 
</script>

<style type="text/css">
table.gridtable2 {
	font-family: verdana, arial, sans-serif;
	font-size: 12px;
	color: #333333;
	border-width: 1px;
	border-color: #D0D0D0;
	border-collapse: collapse;
}

table.gridtable2 th {
	padding: 5px;
	font-size: 14px;
	border-width: 1px;
	border-style: solid;
	border-color: #D0D0D0;
	background-color: #F1F1F1;
}

table.gridtable2 td {
	padding: 3px;
	border-width: 1px;
	border-style: solid;
	border-color: #D0D0D0;
}

input.cust_input {
	cursor: pointer; 
	border-style: none none solid none; 
	border-width: 1px;
}
</style>
