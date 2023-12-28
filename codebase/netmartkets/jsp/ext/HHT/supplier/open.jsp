<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="ext.sap.SupplierMasterData.SupplierEntity"%>
<%
	String supplierParam = request.getParameter("supplier");
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	List<SupplierEntity> list = ext.sap.SupplierMasterData.SupplierMasterDataServlet.getSuppliers(supplierParam);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>" target="_self">
    <title>My JSP 'open.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script type="text/javascript">
		function closePage(val){
			if(val == undefined){}else{
				window.returnValue=val;
			}
			window.close();
		}
		
		function closePages2(){
			window.close();
		}
	
		function closePages(){
			var radios = document.getElementsByName("radioDemo");
			for(var i=0; i<radios.length; i++){
				if(radios[i].checked == true){					
					window.opener.document.getElementById('HHT_Supplier').value=radios[i].value;
					window.close();
				}
			}
		}
	</script>
  </head>
  
  <body align="center">
  	<form action="netmarkets/jsp/ext/HHT/supplier/open.jsp">
  	
  	<table border="0" align="center" width="600px">
  		<tr height="40px;" width="600px;" align = "left">
  			<td width="100px">供应商名称：</td>
  			<td width="200px" align="left">
				<input type="text" value="" name="supplier" style="width:200px;" >&nbsp;&nbsp;&nbsp;&nbsp;
			</td>
  			<td width="300px" align="left">
				<input type="submit" value="搜索">&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" onclick="closePages()" value="确定">&nbsp;&nbsp;&nbsp;
				<input type="button" onclick="closePages2()" value="取消">
			</td>
  		</tr>
  	</table>
  	
  	<table border="1" align="center" width="600px">
  		<tr width="600px">
  			<td width="50px"></td>
  			<td width="100px">供应商名称</td>
  			<td width="250px">供应商编码</td>
  			<td width="150px">供应商创建时间</td>
  		</tr>
	 
	    <%  for (int i = 0; i < list.size(); i++) {
				SupplierEntity supplier = list.get(i);
				String InternalName = supplier.getInternalName();
				String DisplayName = supplier.getDisplayName();
				String CreateTime = supplier.getCreateTime(); %>
		<tr width="120px">
			<td width="50px"><%=i+1 %>
				<input type="radio" name="radioDemo" id="<%=InternalName%>" 
					ondblclick="closePage('A<%=i+1%>')" value="<%=InternalName%>">
			</td>
			<td width="100px"> <%=InternalName%> </td>
			<td width="250px"> <%=DisplayName%></td>
			<td width="150px"> <%=CreateTime%></td>
		</tr>
	    <% } %>
  	</table>
  	</form>
  </body>
</html>
