<%@page import="com.ptc.netmarkets.util.beans.NmContextBean"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="ext.ait.util.PartUtil"%>
<%@page language="java" session="true" pageEncoding="UTF-8"%>

<% 
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/"; 
    String contextPath=request.getContextPath();
    NmCommandBean nmCommandBean = new NmCommandBean();
    nmCommandBean.setInBeginJsp (true);
    nmCommandBean.setOpenerCompContext (request.getParameter("compContext"));
    nmCommandBean.setOpenerElemAddress (NmCommandBean.convert(request.getParameter("openerElemAddress")));
    nmCommandBean.setCompContext (NmCommandBean.convert(request.getParameter("compContext")));
    nmCommandBean.setElemAddress (NmCommandBean.convert(request.getParameter("elemAddress")));
    nmCommandBean.setRequest (request); nmCommandBean.setResponse (response);
    nmCommandBean.setOut (out); nmCommandBean.setContextBean (new NmContextBean());
    String oid =  NmCommandBean.convert(request.getParameter("oid"));
    oid = oid.startsWith("VR") ? PartUtil.getORbyVR(oid) : oid;
%>

<!doctype html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>成本分析</title>
        <link rel="stylesheet" href="<%= basePath %>netmarkets/jsp/ext/requirement/insert/layui-v2.7.6/layui/css/jquery-ui.css"/>
		<style>
.custingTr{
margin: 0 auto;
border-collapse: collapse;
border-spacing: 0;
}
.custingTr, .custingTd{
border: 1px solid #bdc3c7;
padding: 10px;
vertical-align: middle;
}
.custingTr:nth-child(even){
background-color: #f2f2f2;
}
#custingButton,#pdfButton {
    width: 80px;
    margin: 3px 1px 0 5px;
    padding: 0 10px;
    background-color: #16a0d3;
    border: none;
    display: inline-block;
    font-family: "Microsoft Yahei";
    font-size: 12px;
    cursor: pointer;
    height: 27px;
    line-height: 27px;
    color: #FFF;
    border-radius: 5px;
}
#divone {
position: absolute;
top: 40px;
width:100%;
}
        </style>
    </head>
    <body>
	<div>
	<input type="button" name="" value="成本分析" id ="custingButton"/>
	<input type="button" name="" value="导出pdf" id = "pdfButton"/>
	<span >分析时间：</span><span id = "timeFx"></span>
	</div>
	<div id="divone">
	<table cellspacing="0"border="1px" width = "100%" id = "tr">

	</table>
	</div>

  <h1>BOM成本的表格最好按照Windchill展示逻辑显示出来<h1>
  <h1>显示所有部件的成本，只计算上层的成本，不无限向下计算所有部件的成本<h1>

	  <script src="https://cdnjs.cloudflare.com/ajax/libs/html2canvas/0.4.1/html2canvas.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/1.0.272/jspdf.debug.js"></script>

    <script type="text/javascript">
	getCbjs();
	
	
	var btn = document.getElementById("pdfButton");  
    btn.onclick =function(){
		var  table = document.getElementById("tr");
		let allList = new Array();　　
		for (var i = 1; i < table.rows.length; i++) {   
             var cells = table.rows[i].cells;
		let list = new Array();　　
        for (var j = 0; j < cells.length; j++) {
        var value = cells[j].innerHTML;
		list.push(value);
         }
		 allList.push(list);
     }
	 if(allList.length == 0){
		 alert("无成本计算数据");
	 } else {
	   httpPostLocaltion("",allList);
	 }
	 
	}
	
	function httpPostLocaltion(key,params){
	let url = "http://uat.honghe-tech.com/Windchill/servlet/Navigation/sap/CustingPdfServlet";
	// 把参数对象转换为json
	let param = JSON.stringify(params);
	let xhr = new XMLHttpRequest();
	xhr.responseType = 'blob';
	xhr.onreadystatechange = function () {
	    if (xhr.readyState == 4 &&  xhr.status === 200) {
	    	const file = xhr.response;
			  const url = window.URL.createObjectURL(file);

    // 创建一个链接元素并设置下载属性
    const a = document.createElement('a');
    a.href = url;
    a.download = '成本分析.pdf'; // 设置文件名

    // 模拟点击链接以触发下载
    a.click();

    // 释放 Blob URL
    window.URL.revokeObjectURL(url);
	    }
	};
	xhr.open(
		"post",
		url,
		true
	);
	// 注意，设置请求头的信息必须写在下面，否则会报错
	// 设置以json传参
	xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8"); 
	// 解决跨域问题
	xhr.setRequestHeader("Access-Control-Allow-Origin", "*");
	// 设置请求体携带的参数
	xhr.send(param);
}

	

	
	function formatDateTime(date, format) {
  const o = {
    'M+': date.getMonth() + 1, // 月份
    'd+': date.getDate(), // 日
    'h+': date.getHours() % 12 === 0 ? 12 : date.getHours() % 12, // 小时
    'H+': date.getHours(), // 小时
    'm+': date.getMinutes(), // 分
    's+': date.getSeconds(), // 秒
    'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
    S: date.getMilliseconds(), // 毫秒
    a: date.getHours() < 12 ? '上午' : '下午', // 上午/下午
    A: date.getHours() < 12 ? 'AM' : 'PM', // AM/PM
  };
  if (/(y+)/.test(format)) {
    format = format.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
  }
  for (let k in o) {
    if (new RegExp('(' + k + ')').test(format)) {
      format = format.replace(
        RegExp.$1,
        RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length)
      );
    }
  }
  return format;
}
  
  function getCbjs(){
	 var oid ='<%= oid %>';
        var httpRequest = new XMLHttpRequest();//第一步：建立所需的对象
        httpRequest.open('GET', 'http://uat.honghe-tech.com/Windchill/servlet/Navigation/sap/Custing?oid='+oid, true);//第二步：打开连接  将请求参数写在url中  ps:"http://localhost:8080/rest/xxx"
        httpRequest.send();//第三步：发送请求  将请求参数写在URL中
        /**
         * 获取数据后的处理程序
         */
        httpRequest.onreadystatechange = function () {
            if (httpRequest.readyState == 4 && httpRequest.status == 200) {
				var timeFx = document.getElementById("timeFx");
                timeFx.innerHTML = formatDateTime(new Date(),"yyyy年MM月dd日 HH:mm:ss");
                var json = httpRequest.responseText;//获取到json字符串，还需解析
				// if(json == null || json == ''){
				// 	alert("无零件结构");
				// }
                testJson = eval("(" + json + ")");
			var allHtml = "<tr align='center' class = 'custingTr'>" +
			            "<td width = '5%' class = 'custingTd'>父编号</td>" +
						"<td width = '15%' class = 'custingTd'>编号</td>" +
						"<td width = '40%' class = 'custingTd'>名称</td>" +
						"<td width = '10%' class = 'custingTd'>版本</td>" +
						"<td width = '10%' class = 'custingTd'>状态</td>" +
						"<td width = '10%' class = 'custingTd'>数量</td>" +
						"<td width = '10%' class = 'custingTd'>总价</td>" +
                        "</tr>";
			for(let i =0;i<testJson.length;i++){
				var val = testJson[i];
				var html = "<tr align='center' class = 'custingTr'>" +
            "<td class = 'custingTd'>"+(val.parent == null?"":val.parent)+"</td>" +
            "<td class = 'custingTd'>"+val.number+"</td>" +
            "<td class = 'custingTd'>"+val.name+"</td>" +
            "<td class = 'custingTd'>"+val.version+"</td>" +
            "<td class = 'custingTd'>"+val.status+"</td>" +
            "<td class = 'custingTd'>"+(val.amount == null?"":val.amount)+"</td>" +
            "<td class = 'custingTd'>"+(val.price == null ? "":val.price)+"</td>" +
            "</tr>";
			if(i == testJson.length -1){
				html = "<tr class = 'custingTr'>" +
            "<td colspan='6' align='right' class = 'custingTd'>合计：</td>" +
            "<td align='center' class = 'custingTd'>"+val.price+"</td>" +
            "</tr>";
			}
			allHtml += html;
			}
			var tab = document.getElementById("tr");
                tab.innerHTML = allHtml;
            }
        }; 
  };

  var btn = document.getElementById("custingButton");  
    btn.onclick =function(){  
	   getCbjs();
    }  
    </script>
    </body>
</html>