package ext.sap.masterData.mvc;

import com.ptc.core.components.descriptor.DescriptorConstants;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentBuilderType;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;

import wt.util.WTException;

@ComponentBuilder(value = { "sapResult.error.list" }, type = ComponentBuilderType.CONFIG_AND_DATA)
public class SAPResultTableBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		ComponentConfigFactory configFactory = getComponentConfigFactory();
		TableConfig tableConfig = configFactory.newTableConfig();
		tableConfig.setActionModel("sapResult error table actions");// 设置模型
		tableConfig.setSelectable(true);// 显示表格前面的选择框
		tableConfig.setLabel("SPA集成结果");
		tableConfig.setConfigurable(true);
		// 图标
		ColumnConfig iconConfig = configFactory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.ICON, true);
		tableConfig.addComponent(iconConfig);

		// 图标
		ColumnConfig latestStatusConfig = configFactory.newColumnConfig("latestStatus", true);
		tableConfig.addComponent(latestStatusConfig);

		ColumnConfig sourceConfig = configFactory.newColumnConfig("partNumer", true);
		sourceConfig.setLabel("物料编码");
		tableConfig.addComponent(sourceConfig);

		ColumnConfig endItemConfig = configFactory.newColumnConfig("partDescribe", true);
		endItemConfig.setLabel("物料描述");
		tableConfig.addComponent(endItemConfig);

		ColumnConfig phantomConfig = configFactory.newColumnConfig("partIntegrationResult", true);
		phantomConfig.setLabel("物料集成结果");
		tableConfig.addComponent(phantomConfig);

		ColumnConfig sapMaterialGroupConfig = configFactory.newColumnConfig("partIntegrationInfo", true);
		sapMaterialGroupConfig.setLabel("物料集成返回信息");
		tableConfig.addComponent(sapMaterialGroupConfig);

		ColumnConfig SourceChildPartConfig = configFactory.newColumnConfig("integrationTime", true);
		SourceChildPartConfig.setLabel("集成时间");
		tableConfig.addComponent(SourceChildPartConfig);

		return null;
	}

}
