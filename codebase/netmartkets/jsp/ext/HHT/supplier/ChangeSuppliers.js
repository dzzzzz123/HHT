
//---------------------------------只需要修改以下配置-----------------------------------------------------

//全局变量1，定义【供应商类型】的IBA的内部名称
var SuppliersTypeIBA = "HHT_Supplier";

//全局变量2，定义【供应商全称】的IBA的内部名称
var SuppliersFullNameIBA = "SupplierFullName";

//全局变量3，定义【供应商类型】的IBA值触发
var SuppliersTypeIBAValue1="零散供应商";

//全局变量4，定义【供应商类型】的IBA值触发
var SuppliersTypeIBAValue2="零散供应商-美金";

//---------------------------------以下正文不需要修改-----------------------------------------------------

//编辑部件页面初始化
var numberItem = document.getElementById("number");
if(numberItem){
	console.log("number="+numberItem.innerHTML);
	initEditPart();
}else {
//新建部件页面初始化
	initCreateChangeSuppliers();
}

//编辑场景
//20秒内【供应商类型】下拉列表注册onchange事件
function initEditPart(){
	console.log("初始化供应商类型开始 ...");
	var count = 20;//20秒等待供应商类型的下拉框加载完成，如果20秒内加载就放弃了
	var interval = setInterval(function(){
		var SuppliersTypeIBAItem = document.getElementById(SuppliersTypeIBA);
		console.log("count:"+count);
		console.log(SuppliersTypeIBAItem);
		if(SuppliersTypeIBAItem){
			//防止另外1个输入框的加载太慢，也定义在5秒内检查加载完成
			if(supplierButtonItem==undefined){
				var buttonElement = document.createElement('button');
				buttonElement.id = 'supplierButton';
				buttonElement.innerHTML = '搜索';
				buttonElement.onclick=function(){
					openPage();
				}
				SuppliersTypeIBAItem.insertAdjacentElement('afterend',buttonElement);
			}
			
			//下拉输入框加载完成后，注册onchange事件给【供应商类型】的下拉选择框
			var ootbChange = SuppliersTypeIBAItem.getAttribute("onchange");
			if(ootbChange){
				ootbChange = ootbChange +";handleOnChange();";
			}else {
				ootbChange = "handleOnChange(this);";
			}
			SuppliersTypeIBAItem.setAttribute("onchange",ootbChange);
			clearInterval(interval);//停止定时器，不检查加载
		}
		count--;
		if(count < 0 ){
			clearInterval(interval);
		}
	},1000);
}

//新建部件的场景，会选择部件类型，会涉及到可能存在多个子类型切换，切换子类型之后，IBA属性输入框都会被重新初始化，注册的onchange事件会失效
//所以新建页面的解决方案是:定时器一直运行，只要检查到onchange事件中没有客制化定义的function就注册
function initCreateChangeSuppliers(){
	console.log("初始化更改供应商 Start ...");
	var interval = setInterval(function(){
		//供应商类型注册onchange事件	
		var SuppliersTypeIBAItem = document.getElementById(SuppliersTypeIBA);
		var supplierButtonItem = document.getElementById('supplierButton');
		console.log("SuppliersTypeIBAItem:"+SuppliersTypeIBAItem);
		console.log("buttonElement:"+buttonElement);
		if(SuppliersTypeIBAItem){
			if(supplierButtonItem==undefined){
				var buttonElement = document.createElement('button');
				buttonElement.id = 'supplierButton';
				buttonElement.innerHTML = '搜索';
				buttonElement.onclick=function(){
					openPage();
				}
				SuppliersTypeIBAItem.insertAdjacentElement('afterend',buttonElement);
			}
			var ootbChange = SuppliersTypeIBAItem.getAttribute("onchange");
			if(!ootbChange){
				console.log("添加onchange事件111");
				//如果未注册onchange事件，则添加客制化处理onchange的函数
				ootbChange = "handleOnChange(this);";
				SuppliersTypeIBAItem.setAttribute("onchange",ootbChange);
			}else if(ootbChange.indexOf("handleOnChange") < 0){
				console.log("添加onchange事件222");
				//如果已有注册onchange事件，但未添加客制化处理onchange的函数，则添加
				ootbChange = ootbChange+";handleOnChange(this);";
				SuppliersTypeIBAItem.setAttribute("onchange",ootbChange);
			}
			//如果已有注册onchange事件，并且已添加客制化处理onchange的函数，则不处理，继续运行定时器
		}
	},1000);
}


//处理【供应商全名】onchange事件
//将【供应商全名】输入框清空，并且设置为“必需的”
function handleOnChange(targetElement){
	console.log("handleOnChange Start...");
	var SuppliersTypeIBAValueItem = document.getElementById(SuppliersTypeIBA);
	var SuppliersFullNameIBAItem = document.getElementById(SuppliersFullNameIBA);
	console.log(targetElement.value+"="+SuppliersTypeIBAValueItem +";SuppliersFullNameIBAItem:"+SuppliersFullNameIBAItem);
	if(SuppliersTypeIBAValue1 == targetElement.value || SuppliersTypeIBAValue2==targetElement.value){
		console.log("设置供应商全称 Start1 ...");
		if(SuppliersFullNameIBAItem){
			SuppliersFullNameIBAItem.value="";
			console.log("设置供应商全称 Start2 ...");
			SuppliersFullNameIBAItem.setAttribute("class","required ");
			console.log("设置供应商全称 Start3 ...");
		}
	}else {
		if(SuppliersFullNameIBAItem){
			SuppliersFullNameIBAItem.value="";
			SuppliersFullNameIBAItem.removeAttribute("class");
		}
	}
}

