package ext.HHT.project.workHours.project;

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
import com.ptc.projectmanagement.plan.Plan;

import wt.util.WTException;

@ComponentBuilder(value = { "project.workHours.table" }, type = ComponentBuilderType.CONFIG_AND_DATA)
public class CustomTableBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		List<Result> result = new ArrayList<>();
		JcaComponentParams jca_params = (JcaComponentParams) params;
		NmCommandBean cb = jca_params.getHelperBean().getNmCommandBean();
		NmOid primaryOid2 = cb.getPrimaryOid();
		if (primaryOid2 != null) {
			Object content = primaryOid2.getRef();
			if (content instanceof Plan) {
				Plan plan = (Plan) content;
				result = Service.getResults(plan);
			}
		}
		return result;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		ComponentConfigFactory configFactory = getComponentConfigFactory();
		TableConfig tableConfig = configFactory.newTableConfig();
		tableConfig.setSelectable(true);// 显示表格前面的选择框
		tableConfig.setLabel("鸿合项目工时统计报表");
		tableConfig.setConfigurable(true);
		tableConfig.setActionModel("CustEx_exportlisttofile_submenu");

		ColumnConfig sourceConfig = configFactory.newColumnConfig("HHT_ActivityName", true);
		sourceConfig.setLabel("任务名称");
		tableConfig.addComponent(sourceConfig);

		ColumnConfig endItemConfig = configFactory.newColumnConfig("HHT_UserName", true);
		endItemConfig.setLabel("成员名称");
		tableConfig.addComponent(endItemConfig);

		ColumnConfig phantomConfig = configFactory.newColumnConfig("HHT_StandardHours", true);
		phantomConfig.setLabel("标准工时");
		tableConfig.addComponent(phantomConfig);

		ColumnConfig sapMaterialGroupConfig = configFactory.newColumnConfig("HHT_ActualHours", true);
		sapMaterialGroupConfig.setLabel("实际工时");
		tableConfig.addComponent(sapMaterialGroupConfig);

		ColumnConfig SourceChildPartConfig = configFactory.newColumnConfig("HHT_PercentWorkComplete", true);
		SourceChildPartConfig.setLabel("完工率");
		tableConfig.addComponent(SourceChildPartConfig);

		return tableConfig;
	}
}
