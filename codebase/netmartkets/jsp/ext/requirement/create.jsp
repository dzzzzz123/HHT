<%@page import="com.ptc.netmarkets.util.beans.NmContextBean"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page language="java" session="true" pageEncoding="UTF-8"%>

<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

    String contextPath=request.getContextPath(); 
    NmCommandBean nmCommandBean = new NmCommandBean();
    nmCommandBean.setInBeginJsp        (true);
    nmCommandBean.setOpenerCompContext (request.getParameter("compContext"));
    nmCommandBean.setOpenerElemAddress (NmCommandBean.convert(request.getParameter("openerElemAddress")));
    nmCommandBean.setCompContext       (NmCommandBean.convert(request.getParameter("compContext")));
    nmCommandBean.setElemAddress       (NmCommandBean.convert(request.getParameter("elemAddress")));
    nmCommandBean.setRequest           (request);
    nmCommandBean.setResponse          (response);
    nmCommandBean.setOut               (out);
    nmCommandBean.setContextBean       (new NmContextBean());

    String context = NmCommandBean.convert(request.getParameter("ContainerOid"));
    String folder = NmCommandBean.convert(request.getParameter("oid"));
%>

<!DOCTYPE HTML>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>新建需求 </title>
  <link rel="stylesheet" href="<%= basePath %>/netmarkets/jsp/ext/requirement/layui-v2.7.6/layui/css/layui.css">
  <link rel="stylesheet" href="<%= basePath %>/netmarkets/jsp/ext/requirement/css/milligram.css">
  <style>
   *{
        margin: 0;
        padding: 0;
    }
    .requirement {
        /* width: 1000px; */
        width: 100%;
        /* height: 900px; */
        border: 1px solid gray;
        /* top: 0;
        right: 0;
        bottom: 0;
        left: 0;
        margin: auto; */
        /* margin-left: 10px; */
    }
    .lefts{
        /* margin-left: 10%; */
        display: flex;
        padding-left: 4%;
    }
    .form-group{
        margin-top: 10px;
        /* float: left; */
        display: flex;
    }
    .linsd{
     margin-left: 4%; 
        margin-top: 20%; 
       clear: both;
    }
    .selet{
        margin-top:5%;
        margin-right: 20%;
        /* padding-right: 60%; */
        float:right;
    }
    .let{
        margin-top:10px;
        display: flex;
    }
    .Suggestion{
        /* padding: 0;
        margin: 0; */
        /* float:left; */
        width: 95%;
        margin-top:10px;
    }
    .layui-textarea{
        margin-top: 10px;
    }
    .buttons{
        /* float: right; */
        margin-left: 61%;
        margin-top: 10px;
    }
    label{
      width: 90px;
    }
</style>
</head>
<body>
<div>
  <%= context %>
  <%= folder %>
</div>
<div class="layui-container">  
  <div class="layui-row">
    <div class="layui-col-md9 boders">
     <div class="requirement">
      <div class="layui-col-md9 lefts">
        <!-- <form class="form-inline"> -->
          <div>
          <div class="form-group">
            <!-- <label for="exampleInputName2" id="number">编号</label> -->
          </div>
          <div class="form-group" style="width:110%;">
          <label for="exampleInputEmail2">名称&emsp;&emsp;</label>
            <input class="form-control3" id="name" placeholder="请输入名称" style="margin-left: 24px;" autocomplete='off'/>
          </div>
          <div class="form-group">
		           
            <label for="exampleInputEmail">需求类别</label>
            <select name="" id="HHT_ReqCategory"    style="width: 100px" >
			

              <!-- <option value="">请选择</option>
              <option value="条目">条目</option> -->
            </select>
          </div>
          <div class="form-group">
            <label for="exampleInputEmail2">优先级&emsp;</label>
            <select name="" id="HHT_Priority" class="form-control4"    style="width: 100px">
              <!-- <option value="">请选择</option>
              <option value="">EVT</option> -->
            </select>
          </div>
          <div class="form-group">
            <label for="exampleInputEmail2">客户角色</label>
            <select name="" id="HHT_CustomerRole" class="form-control4"    style="width: 100px">
              <!-- <option value="">请选择</option>
              <option value="">影响着</option> -->
            </select>
          </div>
      </div>
    <!-- </form> -->
  </div>
      <div class="layui-col-md3">
        <div class="selet">
          <div class="let">
            <label for="exampleInputEmail2">需求IPD类</label>
            <select
              class="form-control input-lg"
              id="HHT_ipdReq"
              style="width: 100px"
            >
              <!-- <option value="">请选择</option>
              <option value="P包装">P包装</option> -->
            </select>
          </div>
            <div class="let">
            <label for="exampleInputEmail2">需求归属&nbsp;&nbsp;</label>
            <select
              class="form-control input-lg"
              id="HHT_ReqBelong"
              style="width: 100px"
            >
              <!-- <option value="">请选择</option>
              <option value="平板">平板</option> -->
            </select>
          </div>
              <div class="let">
            <label for="exampleInputEmail2"
              >需求组&emsp;&nbsp;&nbsp;</label
            >
            <select
              class="form-control input-lg"
              id="HHT_ReqGroup"
              style="width: 100px"
            >
              <!-- <option value="">请选择</option>
              <option value="外观组">外观组</option> -->
            </select>
          </div>
            <div class="let">
            <label for="exampleInputEmail2">需求来源&nbsp;&nbsp;</label>
            <select
              class="form-control input-lg"
              id="HHT_ReqSource"
              style="width: 100px"
            >
              <!-- <option value="">请选择</option>
              <option value="客户">客户</option> -->
            </select>
          </div>
        </div>
      </div>
        
      <div class="linsd">
        <div class="form-group">
          <label for="exampleInputEmail4">需求描述</label>
          <div class="mytextareas">
            <form method="post">
              <textarea id="mytextarea"></textarea>
            </form>
          </div>
        </div>
        <div class="Suggestion">
          <div class="form-group">
          <label for="exampleInputEmail4">客户意见</label>
          <!-- <div class="layui-input-block"> -->
            <textarea name="desc" placeholder="请输入内容" class="layui-textarea" id="HHT_CustomerComment"></textarea>
          <!-- </div> -->
        </div>
      </div>
      </div>
      <div class="buttons">
        <div class="layui-input-block rightsd">
          <button class="layui-btn" lay-submit lay-filter="formDemo"  id="submit">立即提交</button>
          <button type="reset" class="layui-btn layui-btn-primary" id="Cancel">重置</button>
        </div>
      </div>
      </div>
    </div>
  </div>
  <script type="text/javascript" src="<%= basePath %>/netmarkets/jsp/ext/requirement/js/axios.js"></script>
  <!-- <script type="text/javascript" src="<%= basePath %>/netmarkets/jsp/ext/requirement/js/initialize.js"></script> -->
  <!-- <script type="text/javascript" src="<%= basePath %>/netmarkets/jsp/ext/requirement/js/requirement.js"></script> -->
  <script type="text/javascript" src="<%= basePath %>/netmarkets/jsp/ext/requirement/js/template-web.js"></script>
  <script type="text/javascript" src="<%= basePath %>/netmarkets/jsp/ext/requirement/tinymce/tinymce.js"></script>
    <script type="text/javascript">
	  // 创建一个Axios实例并配置代理
    fetch('http://uat.honghe-tech.com/Windchill/netmarkets/jsp/ext/requirement/json/data.json').then(response=>response.json()).then(data=>{
      //需求条目
      for (let index = 0; index < data.postsd[2]["HHT_ReqCategorySelt"].length; index++) {
        var newoption2 = document.createElement("option");
        newoption2.value=data.postsd[2]["HHT_ReqCategorySelt"][index]
        newoption2.innerHTML=data.postsd[2]["HHT_ReqCategorySelt"][index]
        HHT_ReqCategory.appendChild(newoption2);
      }
      //优先级
      for (let index = 0; index < data.postsd[1]["HHT_PrioritySelet"].length; index++) {
        var newoption2 = document.createElement("option");
        newoption2.value=data.postsd[1]["HHT_PrioritySelet"][index]
        newoption2.innerHTML=data.postsd[1]["HHT_PrioritySelet"][index]
        HHT_Priority.appendChild(newoption2);	
      }
      //客户角色
      for (let index = 0; index < data.postsd[3]["HHT_CustomerRoleSelt"].length; index++) {
        var newoption5 = document.createElement("option");
        newoption5.value=data.postsd[3]["HHT_CustomerRoleSelt"][index]
        newoption5.innerHTML=data.postsd[3]["HHT_CustomerRoleSelt"][index]
        HHT_CustomerRole.appendChild(newoption5);	
      }
      //需求IPD类
      for (let index = 0; index <data.postsd[4]["HHT_ipdReqSelet"].length; index++) {
        var newoption4 = document.createElement("option");
        newoption4.value=data.postsd[4]["HHT_ipdReqSelet"][index]
        newoption4.innerHTML=data.postsd[4]["HHT_ipdReqSelet"][index]
        HHT_ipdReq.appendChild(newoption4);	
      }
      //需求归属
      for(const key in data.postsd[0]["HHT_ReqBelong"]){
        var newoption8 = document.createElement("option");
        newoption8.value=data.postsd[0]["HHT_ReqBelong"][key]
        newoption8.innerHTML=data.postsd[0]["HHT_ReqBelong"][key]
        HHT_ReqBelong.appendChild(newoption8);
      }
      //需求组
      for (let index = 0; index <data.postsd[5]["HHT_ReqGroupSelet"].length; index++) {
        var newoption6 = document.createElement("option");
        newoption6.value=data.postsd[5]["HHT_ReqGroupSelet"][index]
        newoption6.innerHTML=data.postsd[5]["HHT_ReqGroupSelet"][index]
        HHT_ReqGroup.appendChild(newoption6);	
      }
      //需求来源
      for (let index = 0; index < data.postsd[6]["HHT_ReqSourceSelet"].length; index++) {
        var newoption7 = document.createElement("option");
        newoption7.value=data.postsd[6]["HHT_ReqSourceSelet"][index]
        newoption7.innerHTML=data.postsd[6]["HHT_ReqSourceSelet"][index]
        HHT_ReqSource.appendChild(newoption7);
      }
    })


    tinymce.init({
      selector: "#mytextarea,#editor,#tinydemo,#textarea3",
      language: "zh-Hans", // 配置语言包
      promotion: true,
      plugins:
        "lists,advlist,emoticons,table ,fullscreen ,preview,image,template,quickbars,insertdatetime, autolink,link",
      branding: false,
      menubar: false,
      // forced_root_block:'  ',
      toolbar: [
        'undo redo | styleselect | bold italic forecolor backcolor link image alignleft aligncenter alignright "emoticons,numlist bullist table insertdatetime emoticons hr preview removeformat fullscreen',
      ],
      statusbar: false,
    });
    
    var currentfr = document.getElementById("mytextarea");
    currentfr.style.width = "95%";
    let urls = window.location.href;
    let submit = document.querySelector("#submit");
    let Cancel = document.querySelector("#Cancel");
    let names = document.querySelector("#name");

    //需求类别
    let HHT_ReqCategorys = "";
    HHT_ReqCategory.onchange = () => {
      HHT_ReqCategorys = HHT_ReqCategory.value;
    };
    //优先级
    let HHT_Prioritys = "";
    HHT_Priority.onchange = () => {
      HHT_Prioritys = HHT_Priority.value;
    };
    //需求IPD类
    let HHT_ipdReqs = "";
    HHT_ipdReq.onchange = () => {
      HHT_ipdReqs = HHT_ipdReq.value;
    };
    //需求归属
    let HHT_ReqBelongs = "";
    HHT_ReqBelong.onchange = () => {
      HHT_ReqBelongs = HHT_ReqBelong.value;
    };
    //需求组
    let HHT_ReqGroups = "";
    HHT_ReqGroup.onchange = () => {
      HHT_ReqGroups = HHT_ReqGroup.value;
    };
    //需求来源
    let HHT_ReqSources = "";
    HHT_ReqSource.onchange = () => {
      HHT_ReqSources = HHT_ReqSource.value;
    };
    //客户意见
    let HHT_CustomerRoles = "";
    HHT_CustomerRole.onchange = () => {
      HHT_CustomerRoles = HHT_CustomerRole.value;
    };

    //接口代理
    submit.onclick = () => {
      let name = document.querySelector("#name");
      let data = {
          Name: name.value,
          Context: "Containers('OR:wt.pdmlink.PDMLinkProduct:186635')",
          Folder: "Folders('OR:wt.folder.SubFolder:187192')",
          Description: tinymce.activeEditor.getContent(),
          HHT_Priority: "EVT",
          HHT_ReqCategory: "R2",
          HHT_ReqBelong: "平板",
          HHT_ReqSource: "客户",
          HHT_ipdReq: "P包装",
          HHT_CustomerComment: "客户意见",
          HHT_CustomerRole: "决策者"
      };
      axios({
        method: "POST",
        url: "http://uat.honghe-tech.com/Windchill/servlet/Navigation/requirement/insert",
        data: { data },
        suncess: function (formation) {
          console.log(formation);
        },
      });
    };

    Cancel.onclick = () => {
      tinymce.activeEditor.setContent("");
      HHT_CustomerComment.value = "";
      names.value = "";
    };
  </script>
</body>
</html>
