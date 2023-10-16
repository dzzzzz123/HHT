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

@ComponentBuilder("alpha.budget.ProjectBudget.SetAttributesPanel")
@TypeBased("ext.alpha.budget.ProjectBudget")
public class ProjectBudgetSetAttributesPanelBuilder extends AbstractAttributesComponentBuilder {

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
				group.setId("setAttributes");
				group.setLabel("新建项目预算");
				group.setIsGridLayout(true);
				group.addComponent(getAttribute(ProjectBudget.BUGET_NUMBER, factory));
				group.addComponent(getAttribute(ProjectBudget.NAME, factory));
				group.addComponent(getAttribute(ProjectBudget.BUGET_AMOUNT, factory));
				group.addComponent(getAttribute(ProjectBudget.BUGET_CATEGORY, factory));
				group.addComponent(getAttribute(ProjectBudget.FOLDER_PATH, factory));
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
