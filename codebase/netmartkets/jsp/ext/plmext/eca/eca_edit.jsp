<%@page import="org.apache.commons.lang.time.DateUtils"%>
<%@page import="org.apache.commons.beanutils.ConvertUtils"%>
<%@page import="net.haige.util.PartUtil"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
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
	//栏位id与CompareMessage属性值匹配
	String btStr="序号;替换组;更改类型<font color='red'>*</font>;父件编码;物料编号;物料描述;版本;数量;属性;物料编号;物料描述;版本;数量;属性;在制处理意见;在途处理意见;库存处理意见;备注";
	String keyStr="xh,group,changeType,fatherPartNumber,sonPartNumber_old,sonPartDesc_old,fatherPartVer_old,quantity_old,att_old,sonPartNumber_new,sonPartDesc_new,fatherPartVer_new,quantity_new,att_new,zzView,ztView,kcView,remark";
	String[] tits = btStr.split(";");
	String[] keys = keyStr.split(",");
	int messCount = 0;
 %>

 
<fieldset id="create_div" class="x-fieldset x-form-label-left"
	style="width: 96%; "><legend
	class="x-fieldset-header x-unselectable"><span
	class="attributePanel-fieldset-title">更改通知单内容：</span></legend>
 
<table border="0" cellpadding="0" cellspacing="0" id="ecaAttTable" width="95%" class="gridtable2">
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
	   	 <td colspan=4>
	   	 	<%=ecnCt%>
	   	 </td>
    </tr>
 
    <tr>
		<th>更改通告描述：</th>
		<td colspan=<%=tits.length%> >
	   	 	<%=ecn.getDescription()%>
	   	</td>
	</tr>
	
    <tr>
		<%
			for(String tname : tits){
		%>
		<th align="center"><%=tname%></th>
		<%}%>
		<th></th>
	</tr>
	<%
	if(messList!=null) {
		messCount = messList.size();
		int row = 0;
		for(Object[] data : messList){
			row++;
	%>
	<tr>
		<% 
		for(int i=0;i<data.length;i++){
			String alStr = "align='left'";
			String isReq = "";
			if(i==0){
				alStr = "align='center'";
			}
			if(i==2){
				isReq="class='required'";
			}
		%>
		<td <%=alStr%>>
			<%
			if(i==0){
			%>
				<%=data[i]==null?"":data[i].toString()%>
			<%}else{%>
				<input type="text" id="<%=keys[i]%>_<%=row%>" name="<%=keys[i]%>_<%=row%>" value="<%=data[i]==null?"":data[i].toString()%>" style="width:100%" <%=isReq%>/>
			<%}%>
	   	 </td>
		<%}%>
		<td><input type='button' class='button' onclick="deleteItemRow(this)" value='&nbsp;删除&nbsp;'/></td>
	</tr>
	<%
		}}
	%>
		
</table>
<div>
	<input type='button' class='button' value='&nbsp;&nbsp;新增一行&nbsp;&nbsp;' onclick="addRow()" />
	<input type="hidden" id="ecaKeyStr" name="ecaKeyStr" value="<%=keyStr%>"/>
	<input type="hidden" id="ecaMessCount" name="ecaMessCount" value="<%=messCount%>"/>
</div>

</fieldset>
 
<script type="text/javascript">

	function addRow(){
		var table = document.getElementById("ecaAttTable");//获取表格对象
		var currentRow = document.all.ecaAttTable.insertRow(-1);
		var index = table.rows.length-3;
		var keyStr = document.getElementById("ecaKeyStr").value;
	 
		var col1 = currentRow.insertCell(0);
		col1.innerHTML = "&nbsp;&nbsp;"+index;
		var keys = keyStr.split(",");
		for(var i=1;i<keys.length;i++){
			var col = currentRow.insertCell(i);
			col.innerHTML = "<input type='text' id='"+keys[i]+"_"+index+"' name='"+keys[i]+"_"+index+"' style='width:100%' />";
		}
		col1 = currentRow.insertCell(keys.length);
		col1.innerHTML = "<input type='button' class='button' onclick='deleteItemRow(this)' value='&nbsp;删除&nbsp;'/>";
		
		var ecaMessCount = document.getElementById("ecaMessCount").value;
		
		document.getElementById("ecaMessCount").value=(parseInt(ecaMessCount)+10);
	}
	
	function deleteItemRow(obj){
		if(!confirm('确认删除该行？')){
			return false;
		}
		var row_tr = obj.parentNode.parentNode.rowIndex;//获取当前行数
		var table = document.getElementById('ecaAttTable');//获取表格对象
		table.deleteRow(row_tr);
		var ecaMessCount = document.getElementById("ecaMessCount").value;
		document.getElementById("ecaMessCount").value=(parseInt(ecaMessCount)-1);
	}
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
	padding: 2px;
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
