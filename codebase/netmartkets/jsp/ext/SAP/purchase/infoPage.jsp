<%@page import="com.ptc.netmarkets.util.beans.NmContextBean"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="wt.part.WTPart"%>
<%@page import="java.util.List"%>
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
    List<String> supplyChainList = SupplyChainServlet.requestSupplyChainList(oid);

    String jsonInput = "{ \"I_MATNR\": \"230820014A\" }";
    String supplyChain = SupplyChainServlet.requestSupplyChain(jsonInput);
%>


<!DOCTYPE html>
<html>
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>需求信息</title>
    <%-- <link rel="stylesheet" href="<%= basePath %>/netmarkets/jsp/ext/SAP/DataTables/datatables.css" /> --%>
    <%-- <link rel="stylesheet" href="<%= basePath %>/core-ui/2.2/lib/bootstrap/bootstrap.min.css" /> --%>
    <style>
        #jsonTable {
            border-collapse: collapse;
            width: 100%;
        }
        #jsonTable th, #jsonTable td {
            border: 1px solid black;
            padding: 8px;
            text-align: left;
        }
        .show-factory-button {
            margin: 0; /* Reset margin for the button */
        }

        /* Optional: Add some spacing between the table and the button */
        .table-container {
            margin-top: 20px;
        }
    </style>
    </head>
    <body>
    <%-- <div><%= oid %></div> --%>
    <%-- <div><%= jsonInput %></div> --%>
    <div><%= supplyChain %></div>
    <%-- <button id="submit">获取信息</button> --%>
    <%-- <div><%= supplyChainList %></div> --%>

    <div class="container">
        <h1>Data Display</h1>
        <table id="jsonTable" class="table table-bordered">
        </table>
    </div>

    <button class="btn btn-primary" data-toggle="modal" data-target="#myModal">展示工厂数据</button>

    <!-- 模态框 -->
    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel">工厂数据</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <table class="table table-bordered" id="factoryTable">
                        <!-- 工厂数据将在此处动态显示 -->
                    </table>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
                </div>
            </div>
        </div>
    </div>
    <script type="text/javascript" src="<%= basePath %>/netmarkets/jsp/ext/requirement/insert/js/axios.js" defer></script>
    <script type="text/javascript">
        PTC.navigation.loadScript('core-ui/2.2/lib/bootstrap/bootstrap.min.js');
        PTC.navigation.loadScript('core-ui/2.2/lib/jquery.js');
        PTC.navigation.loadScript('netmarkets/jsp/ext/SAP/DataTables/datatables.js');
        $(document).ready(function() {
            var jsonData = <%= supplyChain %>;
            var keys = Object.keys(jsonData.IS_MRP1);
            var it_mrp2 = jsonData.IS_MRP1.IT_MRP2;

            var table = document.getElementById('jsonTable');
            var thead = table.createTHead();
            var tbody = table.createTBody();
            var headerRow = thead.insertRow(0);
            var row = tbody.insertRow(0);

            for (var i = 0; i < keys.length; i++) {
                var th = document.createElement('th');
                th.innerHTML = keys[i];
                headerRow.appendChild(th);
                
                if(i===keys.length-1){
                    // 为每行添加一个展示工厂数据的按钮
                    var showFactoryButton = document.createElement('button');
                    showFactoryButton.innerHTML = '展示工厂数据';
                    showFactoryButton.classList.add('btn', 'btn-primary', 'show-factory-button');
                    showFactoryButton.onclick = function() {
                        // 在按钮点击时显示工厂数据模态框
                        $('#myModal').modal('show');
                    };

                    var buttonCell = row.insertCell();
                    buttonCell.appendChild(showFactoryButton);
                    break;
                }
                var cell = row.insertCell(i);
                cell.innerHTML = jsonData.IS_MRP1[keys[i]];

            }

            // 创建工厂数据表格
            var factoryTable = document.getElementById('factoryTable');
            var factoryThead = factoryTable.createTHead();
            var factoryTbody = factoryTable.createTBody();
            var factoryHeaderRow = factoryThead.insertRow(0);

            for (var key in it_mrp2[0]) {
                var th = document.createElement('th');
                th.innerHTML = key;
                factoryHeaderRow.appendChild(th);
            }

            // 将工厂数据填充到模态框中
            it_mrp2.forEach(function(item) {
                var factoryRow = factoryTbody.insertRow();
                for (var key in item) {
                    var factoryCell = factoryRow.insertCell();
                    factoryCell.innerHTML = item[key];
                }

            });

            // 点击展示工厂数据按钮时，获取并显示相关工厂数据
            $('.show-factory-button').click(function() {
                var row = $(this).closest('tr');
                var rowData = it_mrp2[row.index()];

                // 清空工厂数据模态框中的表格
                $('#myModal .modal-body table tbody').empty();

                // 动态填充工厂数据到模态框中
                var modalTable = document.querySelector('#myModal .modal-body table tbody');
                var modalRow = modalTable.insertRow();
                for (var key in rowData) {
                    var modalCell = modalRow.insertCell();
                    modalCell.innerHTML = rowData[key];
                }
            });
        });
    </script>
    </body>
</html>