package ext.alpha.budget.mvc.builders;

import com.ptc.jca.mvc.builders.DefaultInfoComponentBuilder;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentId;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.InfoConfig;
import com.ptc.mvc.components.TypeBased;

import wt.util.WTException;

@ComponentBuilder(ComponentId.INFOPAGE_ID)
@TypeBased("ext.alpha.budget.ProjectBudget")
public class ProjectBudgetInfoBuilder extends DefaultInfoComponentBuilder {
	@Override
	protected InfoConfig buildInfoConfig(final ComponentParams params) throws WTException {
		final InfoConfig info = getComponentConfigFactory().newInfoConfig();
		info.setTabSet("ProjectBudgetDetails");
		return info;
	}
}
