package ext.sap.purchase;

import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.CHANGE_STATUS_FAMILY;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.CONTAINER_NAME;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.FORMAT_ICON;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.GENERAL_STATUS_FAMILY;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.ICON;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.INFO_ACTION;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.LAST_MODIFIED;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NAME;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NM_ACTIONS;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NUMBER;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.ORG_ID;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.SHARE_STATUS_FAMILY;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.STATE;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.VERSION;

import com.ptc.core.htmlcomp.components.ConfigurableTableBuilder;
import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentConfigBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentDataBuilder;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ait.util.WorkflowUtil;
import wt.maturity.PromotionNotice;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

@ComponentBuilder("purchase.alter.table")
public class PurchaseAlterTable extends AbstractComponentConfigBuilder
		implements ConfigurableTableBuilder, ComponentDataBuilder {

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();

		table.setLabel("修改价格属性表格");
		table.setSelectable(true);
		table.setType("wt.part.WTPart");
		table.setActionModel("mvc_purchasAlter_tables_toolbar");

		table.setShowCount(true);
		table.setShowCustomViewLink(true);

		table.addComponent(factory.newColumnConfig(ICON, true));
		ColumnConfig col = factory.newColumnConfig(NAME, true);

		table.addComponent(col);
		table.addComponent(factory.newColumnConfig(FORMAT_ICON, false));
		table.addComponent(factory.newColumnConfig(NUMBER, false));
		table.addComponent(factory.newColumnConfig(ORG_ID, false));
		table.addComponent(factory.newColumnConfig(INFO_ACTION, false));
		ColumnConfig nmActionsCol = factory.newColumnConfig(NM_ACTIONS, false);
		((JcaColumnConfig) nmActionsCol).setActionModel("CustEx_table_row_actions");
		table.addComponent(nmActionsCol);
		table.addComponent(factory.newColumnConfig(SHARE_STATUS_FAMILY, false));
		table.addComponent(factory.newColumnConfig(GENERAL_STATUS_FAMILY, false));
		table.addComponent(factory.newColumnConfig(CHANGE_STATUS_FAMILY, false));
		table.addComponent(factory.newColumnConfig(VERSION, true));
		table.addComponent(factory.newColumnConfig(LAST_MODIFIED, true));
		table.addComponent(factory.newColumnConfig(CONTAINER_NAME, false));
		ColumnConfig stateColumn = factory.newColumnConfig(STATE, true);
		stateColumn.setDataStoreOnly(true);
		table.addComponent(stateColumn);

		return table;
	}

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws WTException {
		JcaComponentParams jca_params = (JcaComponentParams) params;
		NmCommandBean cb = jca_params.getHelperBean().getNmCommandBean();
		NmOid primaryOid2 = cb.getPrimaryOid();
		if (primaryOid2 == null)
			return null;
		Object content = primaryOid2.getRef();
		if (content instanceof WorkItem) {
			WorkItem wi = (WorkItem) content;
			PromotionNotice pn = (PromotionNotice) wi.getPrimaryBusinessObject().getObject();
			return WorkflowUtil.getTargerObjectByPromotionNotices(pn);
		}
		return null;
	}

	@Override
	public ConfigurableTable buildConfigurableTable(String arg0) throws WTException {
		// TODO Auto-generated method stub
		return null;
	}

}
