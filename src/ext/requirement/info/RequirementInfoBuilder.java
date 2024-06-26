package ext.requirement.info;

import com.ptc.mvc.components.AbstractInfoConfigBuilder;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentDataBuilder;
import com.ptc.mvc.components.ComponentId;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.InfoComponentConfigFactory;
import com.ptc.mvc.components.InfoConfig;
import com.ptc.mvc.components.TypeBased;

import wt.util.WTException;

@TypeBased(value = "wt.part.WTPart|com.honghe_tech.HHRRequirement")
@ComponentBuilder(value = ComponentId.INFOPAGE_ID)
public class RequirementInfoBuilder extends AbstractInfoConfigBuilder implements ComponentDataBuilder {

	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
		return arg1.getContextObject();
	}

	@Override
	protected InfoConfig buildInfoConfig(ComponentParams arg0) throws WTException {
		InfoComponentConfigFactory factory = getComponentConfigFactory();
		InfoConfig result = factory.newInfoConfig();
		result.setNavBarName("requirement_customize_menu");
		result.setTabSet("requirementCustomPartInfoPageTabSet");
		return result;
	}

}
