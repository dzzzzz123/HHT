package ext.sap.masterData.mvc;

import java.util.ArrayList;
import java.util.List;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentBuilderType;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ait.util.CommonUtil;
import ext.sap.Config;
import wt.util.WTException;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

@ComponentBuilder(value = { "masterData.result.table" }, type = ComponentBuilderType.CONFIG_AND_DATA)
public class ResultTableBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		List<Result> result = new ArrayList<>();
		JcaComponentParams jca_params = (JcaComponentParams) params;
		NmCommandBean cb = jca_params.getHelperBean().getNmCommandBean();
		NmOid primaryOid2 = cb.getPrimaryOid();
		if (primaryOid2 != null) {
			Object content = primaryOid2.getRef();
			if (content instanceof WorkItem) {
				WorkItem wi = (WorkItem) content;
				WfAssignedActivity wfAssignedActivity = (WfAssignedActivity) wi.getSource().getObject();
				String value = (String) wfAssignedActivity.getContext().getValue(Config.getJsonVar());
				result = CommonUtil.getEntitiesFromJson(value.toString(), Result.class, "");
			}
		}

//		result.add(new Result("A10000001", "测试物料01", "ERROR", "SAP未给出错误信息", new Date().toString()));
//		result.add(new Result("A10000002", "测试物料02", "ERROR", "SAP未给出错误信息", new Date().toString()));
//		result.add(new Result("A10000003", "测试物料03", "ERROR", "SAP未给出错误信息", new Date().toString()));
//		result.add(new Result("A10000004", "测试物料04", "SUCCESS", "物料创建或修改成功", new Date().toString()));
		return result;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		ComponentConfigFactory configFactory = getComponentConfigFactory();
		TableConfig tableConfig = configFactory.newTableConfig();
		tableConfig.setSelectable(true);// 显示表格前面的选择框
		tableConfig.setLabel("SAP返回的结果信息表格");
		tableConfig.setConfigurable(true);

		ColumnConfig sourceConfig = configFactory.newColumnConfig("number", true);
		sourceConfig.setLabel("物料编码");
		tableConfig.addComponent(sourceConfig);

		ColumnConfig endItemConfig = configFactory.newColumnConfig("name", true);
		endItemConfig.setLabel("物料描述");
		tableConfig.addComponent(endItemConfig);

		ColumnConfig phantomConfig = configFactory.newColumnConfig("result", true);
		phantomConfig.setLabel("物料集成结果");
		tableConfig.addComponent(phantomConfig);

		ColumnConfig sapMaterialGroupConfig = configFactory.newColumnConfig("msg", true);
		sapMaterialGroupConfig.setLabel("物料集成返回信息");
		tableConfig.addComponent(sapMaterialGroupConfig);

		ColumnConfig SourceChildPartConfig = configFactory.newColumnConfig("time", true);
		SourceChildPartConfig.setLabel("集成时间");
		tableConfig.addComponent(SourceChildPartConfig);

		return tableConfig;
	}
}
