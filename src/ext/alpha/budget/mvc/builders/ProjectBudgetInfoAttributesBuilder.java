package ext.alpha.budget.mvc.builders;

import com.ptc.core.ui.resources.ComponentType;
import com.ptc.jca.mvc.components.AbstractAttributesComponentBuilder;
import com.ptc.jca.mvc.components.JcaAttributeConfig;
import com.ptc.jca.mvc.components.JcaGroupConfig;
import com.ptc.mvc.components.AttributePanelConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentId;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TypeBased;

import ext.alpha.budget.ProjectBudget;
import wt.util.WTException;

@ComponentBuilder("primaryAttributes")
@TypeBased("ext.alpha.budget.ProjectBudget")
public class ProjectBudgetInfoAttributesBuilder extends AbstractAttributesComponentBuilder {

	@Override
	protected AttributePanelConfig buildAttributesComponentConfig(ComponentParams arg0) throws WTException {
		final ComponentConfigFactory factory = getComponentConfigFactory();
		final AttributePanelConfig panel;
		{
			panel = factory.newAttributePanelConfig(ComponentId.ATTRIBUTE_PANEL_ID);
			panel.setComponentType(ComponentType.WIZARD_ATTRIBUTES_TABLE);
			final JcaGroupConfig group;
			{
				group = (JcaGroupConfig) factory.newGroupConfig();
				group.setId("attributes");
				group.setLabel("Attributes");
				group.setIsGridLayout(true);
				group.addComponent(getAttribute(ProjectBudget.BUGET_NUMBER, factory));
				group.addComponent(getAttribute(ProjectBudget.NAME, factory));
				group.addComponent(getAttribute(ProjectBudget.BUGET_AMOUNT, factory));
				group.addComponent(getAttribute(ProjectBudget.BUGET_CATEGORY, factory));
				group.addComponent(getAttribute(ProjectBudget.BUGET_SUBCATEGORY, factory));
				group.addComponent(getAttribute(ProjectBudget.BUGET_RESPONSIBLE, factory));
				group.addComponent(getAttribute(ProjectBudget.FOLDER_PATH, factory));
				group.addComponent(getAttribute(ProjectBudget.ESTIMATED_USAGE_TIME, factory));
				group.addComponent(getAttribute(ProjectBudget.ACTUAL_USAGE_TIME, factory));
			}
			panel.addComponent(group);
		}
		return panel;
	}

	JcaAttributeConfig getAttribute(final String id, final ComponentConfigFactory factory) {
		final JcaAttributeConfig attribute = (JcaAttributeConfig) factory.newAttributeConfig();
		attribute.setId(id);
		return attribute;
	}

}
