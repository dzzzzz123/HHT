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
    // String jsonInput = "{ \"I_MATNR\": \" + number +\" }";
    String supplyChain = SupplyChainServlet.requestSupplyChain(jsonInput);
%>

<!DOCTYPE html>
<html>
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>需求信息</title>
    <%-- <link rel="stylesheet" href="<%= basePath %>/core-ui/2.2/lib/bootstrap/bootstrap.min.css" /> --%>
    <style>
        .table{
          height: 400px;
          width: 400px;
          position: relative;
          top: -90px;
          margin: 0 auto; 
        }
    </style>
    </head>
    <body>
    <div><%= oid %></div>
    <div><%= number %></div>
    <div><%= supplyChain %></div>

        <button id="submit">获取信息</button>

    <div style=" margin-top: 100px;">
      <table id="myTable">
        <caption>List of users</caption>
        <thead>
          <tr>
            <th scope="col">#</th>
            <th scope="col">First</th>
            <th scope="col">Last</th>
            <th scope="col">Handle</th>
          </tr>
        </thead>
        <tbody class="abctest">
          <tr>
            <th scope="row">1</th>
            <td>Mark</td>
            <td>Otto</td>
            <td>@mdo</td>
          </tr>
          <tr>
            <th scope="row">2</th>
            <td>Jacob</td>
            <td>Thornton</td>
            <td>@fat</td>
          </tr>
          <tr>
            <th scope="row">3</th>
            <td>Larry</td>
            <td>the Bird</td>
            <td>@twitter</td>
          </tr>
        </tbody>
      </table>
    </div>
    <script type="text/javascript" src="<%= basePath %>/netmarkets/jsp/ext/requirement/insert/js/axios.js" defer></script>
    <script type="text/javascript">
            PTC.navigation.loadScript('core-ui/2.2/lib/jquery.js');
            PTC.navigation.loadScript('core-ui/2.2/lib/bootstrap/bootstrap.min.js');
            PTC.navigation.loadScript('core-ui/2.2/lib/bootstrap/bootstrap.min.css');

            // 在这里执行你的Bootstrap JavaScript代码
            // 使用 Bootstrap 的 JavaScript API 添加样式
          var table = document.getElementById('myTable');
          $(table).addClass('table'); 

          $("#submit").click(function(){
            // let I_MATNR = "<%= number %>"
            let I_MATNR = "230820014A";
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
          });

    </script>
    </body>
</html>
