<%@page import="com.ptc.netmarkets.util.beans.NmContextBean"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="wt.part.WTPart"%>
<%@page import="ext.ait.util.PartUtil"%>
<%@page import="ext.ait.util.PersistenceUtil"%>
<%@page import="ext.sap.supply.SupplyChainServlet"%>
<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    NmCommandBean nmCommandBean = new NmCommandBean();
    nmCommandBean.setInBeginJsp (true);
    nmCommandBean.setOpenerCompContext (request.getParameter("compContext"));
    nmCommandBean.setOpenerElemAddress(NmCommandBean.convert(request.getParameter("openerElemAddress")));
    nmCommandBean.setCompContext(NmCommandBean.convert(request.getParameter("compContext")));
    nmCommandBean.setElemAddress(NmCommandBean.convert(request.getParameter("elemAddress")));
    nmCommandBean.setRequest (request);
    nmCommandBean.setResponse (response);
    nmCommandBean.setOut (out);
    nmCommandBean.setContextBean (new NmContextBean());

    String oid = NmCommandBean.convert(request.getParameter("oid"));
    oid = oid.startsWith("VR") ? PartUtil.getORbyVR(oid) : oid;
    WTPart part = (WTPart) PersistenceUtil.oid2Object(oid.split(":")[2]);
    String number = part.getNumber();
    String jsonInput = "{ \"I_MATNR\": \"230820014A\" }";
    String supplyChain = SupplyChainServlet.requestSupplyChain(jsonInput);
%>

<!DOCTYPE html>
<html>
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>需求信息</title>
    <style>
    </style>
    </head>
    <body>
    <div><%= oid %></div>
    <div><%= number %></div>
    <div><%= supplyChain %></div>
    <div class="layui-input-block rightsd">
        <button id="submit">获取信息</button>
    </div>
    <table class="layui-hide" id="ID-table-demo-data"></table>
    <script type="text/javascript" src="<%= basePath %>/netmarkets/jsp/ext/requirement/insert/js/axios.js" ></script>
    <script type="text/javascript">
      let submit = document.querySelector("#submit");
      submit.onclick = () => {
          // let I_MATNR = "<%= number %>"
          let I_MATNR = "230820014A"
          console.log(I_MATNR);
          debugger;
          axios({
              method: "POST",
              url: "http://uat.honghe-tech.com/Windchill/servlet/Navigation/sap/supplyChain",
              data: { I_MATNR },
          }).then( function (formation) {
            debugger;
              console.log(formation);
          }).catch(function(error){
            debugger;
              console.log("请求失败:", error);
          })
      };
    </script>
    </body>
</html>
