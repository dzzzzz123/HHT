package ext.alpha.budget.mvc.builders;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;

import ext.alpha.budget.ProjectBudget;
import ext.alpha.budget.jca.alphaManagerResource;
import wt.fc.PersistenceHelper;
import wt.query.QuerySpec;
import wt.util.WTException;
import wt.util.WTMessage;

@ComponentBuilder("alpha.budget.ProjectBudget.table")
public class ProjectBudgetTableBuilder extends AbstractComponentBuilder {

	static final String RESOURCE = alphaManagerResource.class.getName();

	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
		return PersistenceHelper.manager.find(new QuerySpec(ProjectBudget.class));
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();

		table.setType(ProjectBudget.class.getName());
		table.setLabel(WTMessage.getLocalizedMessage(RESOURCE, alphaManagerResource.PROJECT_BUDGET_TABLE_LABEL, null));
		table.setSelectable(true);
		table.setShowCount(true);
		table.setShowCustomViewLink(false);
		final ColumnConfig name;
		{
			name = factory.newColumnConfig(ProjectBudget.NAME, true);
			name.setInfoPageLink(true);
			name.setSortable(true);
		}
		table.addComponent(getColumn(ColumnIdentifiers.INFO_ACTION, factory));
		table.addComponent(getColumn(ProjectBudget.BUGET_NUMBER, factory));
		table.addComponent(name);
		table.addComponent(getColumn(ColumnIdentifiers.NM_ACTIONS, factory));
		table.addComponent(getColumn(ProjectBudget.STATE, factory));
		table.addComponent(getColumn(ProjectBudget.BUGET_AMOUNT, factory));
		table.addComponent(getColumn(ProjectBudget.BUGET_CATEGORY, factory));
		table.addComponent(getColumn(ProjectBudget.BUGET_SUBCATEGORY, factory));
		table.addComponent(getColumn(ProjectBudget.STAGE_OF_PROJECT, factory));
		table.addComponent(getColumn(ColumnIdentifiers.LAST_MODIFIED, factory));
		table.addComponent(getColumn(ProjectBudget.ESTIMATED_USAGE_TIME, factory));
		table.addComponent(getColumn(ProjectBudget.ACTUAL_USAGE_TIME, factory));

		return table;
	}

	private ComponentConfig getColumn(String fixed, ComponentConfigFactory factory) {
		ComponentConfig column = getComponentConfigFactory().newColumnConfig(fixed, true);
		return column;
	}
}
