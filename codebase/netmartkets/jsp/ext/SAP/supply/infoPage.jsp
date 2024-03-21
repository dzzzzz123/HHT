<%@page import="com.ptc.netmarkets.util.beans.NmContextBean"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="ext.sap.supply.SupplyChainService"%>
<%@page import="ext.sap.supply.Entity"%>
<%@page import="ext.sap.supply.Entity.IT_MRP2"%>
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

    // String jsonInput = "{ \"I_MATNR\": \"230820014A\" }";
    // Entity supplyInfo = SupplyChainService.requestSupplyChain(jsonInput);

    String oid = NmCommandBean.convert(request.getParameter("oid"));
    List<Entity> supplyChainList = new ArrayList<>();
    supplyChainList = SupplyChainService.getVar(oid);

    List<IT_MRP2> factory2000 = SupplyChainService.getIT_MRP2(supplyChainList,"2000");
    List<IT_MRP2> factory2100 = SupplyChainService.getIT_MRP2(supplyChainList,"2100");

    String time = SupplyChainService.getVar2(oid);


    // Map<String, String[]> paramMap = request.getParameterMap();
    // for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
    //    String key = entry.getKey();
    //    String[] values = entry.getValue();
    //    out.println("<p>Key: " + key + "</p>");
    //    out.println("<ul>");
    //    for (String value : values) {
    //        out.println("<li>value: " + value + "</li>");
    //    }
    //   out.println("</ul>");
    // }
    %>

    
<!DOCTYPE html>
<html>
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>需求信息</title>
    <style>
        #priceTable th,#priceTable td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }

        #priceTable th {
            background-color: #f2f2f2;
        }

        #priceTable {
            border-collapse: collapse;
            width: 95%;
            margin-bottom: 30px;
        }
    </style>
    </head>
    <body>
        <div>请求供应链信息的时间为：<%= time %><div>
        <% if (supplyChainList.size() > 0) { %>
            <table id="priceTable">
                <tr>
                    <th>部件编号</th>
                    <th>MRP平衡数量</th>
                    <th>单价</th>
                    <th>需求数量</th>
                    <th>库存</th>
                    <th>PR数量</th>
                    <th>未清PO数量</th>
                    <th>多余库存</th>
                    <th>多余PO</th>
                    <th>多余PR</th>
                    <th>VMI库存</th>
                </tr>
        <% for (Entity entity : supplyChainList) { %>
            <% if (entity == null ) { %>
                <tr>
                    <td>访问SAP接口出现问题，请联系管理员！</td>
                </tr>
            <% } else{ %>
            <tr>
                <td name="PartNumber"  value="<%= entity.getPartNumber() %>"><%= entity.getPartNumber() %></td>
                <td name="MRPBalanceQuantity"  value="<%= entity.getMRPBalanceQuantity() %>"><%= entity.getMRPBalanceQuantity() %></td>
                <td name="UnitPrice"  value="<%=  entity.getUnitPrice() %>"><%=  entity.getUnitPrice() %></td>
                <td name="RequiredQuantity"  value="<%= entity.getRequiredQuantity() %>"><%= entity.getRequiredQuantity() %></td>
                <td name="STOCK"  value="<%= entity.getSTOCK() %>"><%= entity.getSTOCK() %></td>
                <td name="PRQuantity"  value="<%= entity.getPRQuantity() %>"><%= entity.getPRQuantity() %></td>
                <td name="OpenPOQuantity"  value="<%= entity.getOpenPOQuantity() %>"><%= entity.getOpenPOQuantity() %></td>
                <td name="RedundantInventory"  value="<%= entity.getRedundantInventory() %>"><%= entity.getRedundantInventory() %></td>
                <td name="RedundantPO"  value="<%= entity.getRedundantPO() %>"><%= entity.getRedundantPO() %></td>
                <td name="RedundantPR"  value="<%= entity.getRedundantPR() %>"><%= entity.getRedundantPR() %></td>
                <td name="VMIInventory"  value="<%= entity.getVMIInventory() %>"><%= entity.getVMIInventory() %></td>
            </tr>
        <% } } %>
            </table>
            <table id="priceTable">
            <tr>
                <th>物料编码</th>
                <th>工厂</th>
                <th>MRP平衡数量</th>
                <th>多余PO</th>
                <th>多余库存</th>
                <th>多余PR</th>
                <th>库存</th>
                <th>固定采购申请</th>
                <th>采购申请</th>
                <th>计划订单</th>
                <th>固定计划订单</th>
                <th>采购订单</th>
                <th>生产订单</th>
                <th>计划独立需求</th>
                <th>相关需求</th>
                <th>订单预留</th>
                <th>预留</th>
                <th>安全库存</th>
                <th>转储采购申请</th>
                <th>转储订单需求</th>
                <th>销售订单需求</th>
                <th>外向交货需求</th>
            </tr>
            <% for (IT_MRP2 entity : factory2000) { %>
                <tr>
                    <td name="PartNumber"  value="<%= entity.getPartNumber() %>"><%= entity.getPartNumber() %></td>
                    <td name="Factory"  value="<%= entity.getFactory() %>"><%= entity.getFactory() %></td>
                    <td name="MRPBalancingQuantity"  value="<%= entity.getMRPBalancingQuantity() %>"><%= entity.getMRPBalancingQuantity() %></td>
                    <td name="RedundantPO"  value="<%= entity.getRedundantPO() %>"><%= entity.getRedundantPO() %></td>
                    <td name="RedundantInventory"  value="<%= entity.getRedundantInventory() %>"><%= entity.getRedundantInventory() %></td>
                    <td name="RedundantPR"  value="<%= entity.getRedundantPR() %>"><%= entity.getRedundantPR() %></td>
                    <td name="STOCK"  value="<%= entity.getSTOCK() %>"><%= entity.getSTOCK() %></td>
                    <td name="FixedPurchaseRequisition"  value="<%= entity.getFixedPurchaseRequisition() %>"><%= entity.getFixedPurchaseRequisition() %></td>
                    <td name="PurchaseRequisition"  value="<%= entity.getPurchaseRequisition() %>"><%= entity.getPurchaseRequisition() %></td>
                    <td name="PlannedOrder"  value="<%= entity.getPlannedOrder() %>"><%= entity.getPlannedOrder() %></td>
                    <td name="FixedPlannedOrder"  value="<%= entity.getFixedPlannedOrder() %>"><%= entity.getFixedPlannedOrder() %></td>
                    <td name="PurchaseOrder"  value="<%= entity.getPurchaseOrder() %>"><%= entity.getPurchaseOrder() %></td>
                    <td name="ProductionOrder"  value="<%= entity.getProductionOrder() %>"><%= entity.getProductionOrder() %></td>
                    <td name="PlanIndependentReq"  value="<%= entity.getPlanIndependentReq() %>"><%= entity.getPlanIndependentReq() %></td>
                    <td name="RelatedRequirement"  value="<%= entity.getRelatedRequirement() %>"><%= entity.getRelatedRequirement() %></td>
                    <td name="OrderReservation"  value="<%= entity.getOrderReservation() %>"><%= entity.getOrderReservation() %></td>
                    <td name="Reservation"  value="<%= entity.getReservation() %>"><%= entity.getReservation() %></td>
                    <td name="SafetyStock"  value="<%= entity.getSafetyStock() %>"><%= entity.getSafetyStock() %></td>
                    <td name="TransPurchaseRequisition"  value="<%= entity.getTransPurchaseRequisition() %>"><%= entity.getTransPurchaseRequisition() %></td>
                    <td name="TransOrderRequirement"  value="<%= entity.getTransOrderRequirement() %>"><%= entity.getTransOrderRequirement() %></td>
                    <td name="SalesOrderRequirement"  value="<%= entity.getSalesOrderRequirement() %>"><%= entity.getSalesOrderRequirement() %></td>
                    <td name="OutDeliveryRequirement"  value="<%= entity.getOutDeliveryRequirement() %>"><%= entity.getOutDeliveryRequirement() %></td>
                </tr>
            <% } %>
            </table>
            <table id="priceTable">
            <tr>
                <th>物料编码</th>
                <th>工厂</th>
                <th>MRP平衡数量</th>
                <th>多余PO</th>
                <th>多余库存</th>
                <th>多余PR</th>
                <th>库存</th>
                <th>固定采购申请</th>
                <th>采购申请</th>
                <th>计划订单</th>
                <th>固定计划订单</th>
                <th>采购订单</th>
                <th>生产订单</th>
                <th>计划独立需求</th>
                <th>相关需求</th>
                <th>订单预留</th>
                <th>预留</th>
                <th>安全库存</th>
                <th>转储采购申请</th>
                <th>转储订单需求</th>
                <th>销售订单需求</th>
                <th>外向交货需求</th>
            </tr>
            <% for (IT_MRP2 entity : factory2100) { %>
                <tr>
                    <td name="PartNumber"  value="<%= entity.getPartNumber() %>"><%= entity.getPartNumber() %></td>
                    <td name="Factory"  value="<%= entity.getFactory() %>"><%= entity.getFactory() %></td>
                    <td name="MRPBalancingQuantity"  value="<%= entity.getMRPBalancingQuantity() %>"><%= entity.getMRPBalancingQuantity() %></td>
                    <td name="RedundantPO"  value="<%= entity.getRedundantPO() %>"><%= entity.getRedundantPO() %></td>
                    <td name="RedundantInventory"  value="<%= entity.getRedundantInventory() %>"><%= entity.getRedundantInventory() %></td>
                    <td name="RedundantPR"  value="<%= entity.getRedundantPR() %>"><%= entity.getRedundantPR() %></td>
                    <td name="STOCK"  value="<%= entity.getSTOCK() %>"><%= entity.getSTOCK() %></td>
                    <td name="FixedPurchaseRequisition"  value="<%= entity.getFixedPurchaseRequisition() %>"><%= entity.getFixedPurchaseRequisition() %></td>
                    <td name="PurchaseRequisition"  value="<%= entity.getPurchaseRequisition() %>"><%= entity.getPurchaseRequisition() %></td>
                    <td name="PlannedOrder"  value="<%= entity.getPlannedOrder() %>"><%= entity.getPlannedOrder() %></td>
                    <td name="FixedPlannedOrder"  value="<%= entity.getFixedPlannedOrder() %>"><%= entity.getFixedPlannedOrder() %></td>
                    <td name="PurchaseOrder"  value="<%= entity.getPurchaseOrder() %>"><%= entity.getPurchaseOrder() %></td>
                    <td name="ProductionOrder"  value="<%= entity.getProductionOrder() %>"><%= entity.getProductionOrder() %></td>
                    <td name="PlanIndependentReq"  value="<%= entity.getPlanIndependentReq() %>"><%= entity.getPlanIndependentReq() %></td>
                    <td name="RelatedRequirement"  value="<%= entity.getRelatedRequirement() %>"><%= entity.getRelatedRequirement() %></td>
                    <td name="OrderReservation"  value="<%= entity.getOrderReservation() %>"><%= entity.getOrderReservation() %></td>
                    <td name="Reservation"  value="<%= entity.getReservation() %>"><%= entity.getReservation() %></td>
                    <td name="SafetyStock"  value="<%= entity.getSafetyStock() %>"><%= entity.getSafetyStock() %></td>
                    <td name="TransPurchaseRequisition"  value="<%= entity.getTransPurchaseRequisition() %>"><%= entity.getTransPurchaseRequisition() %></td>
                    <td name="TransOrderRequirement"  value="<%= entity.getTransOrderRequirement() %>"><%= entity.getTransOrderRequirement() %></td>
                    <td name="SalesOrderRequirement"  value="<%= entity.getSalesOrderRequirement() %>"><%= entity.getSalesOrderRequirement() %></td>
                    <td name="OutDeliveryRequirement"  value="<%= entity.getOutDeliveryRequirement() %>"><%= entity.getOutDeliveryRequirement() %></td>
                </tr>
            <% } %>
            </table>
        <% }  else { %>
            <p>没有符合条件的部件。</p>
        <% } %>
    </body>
</html>