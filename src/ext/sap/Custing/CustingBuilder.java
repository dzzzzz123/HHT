package ext.sap.Custing;

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

@TypeBased(value = "wt.part.WTPart")
@ComponentBuilder(value = ComponentId.INFOPAGE_ID)
public class CustingBuilder extends AbstractInfoConfigBuilder implements ComponentDataBuilder{

	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
		return arg1.getContextObject();
	}

	@Override
	protected InfoConfig buildInfoConfig(ComponentParams arg0) throws WTException {
		InfoComponentConfigFactory factory = getComponentConfigFactory();
		InfoConfig result = factory.newInfoConfig();
		result.setNavBarName("wtpart_custing_menu");
		result.setTabSet("CustingInfoPageTabSet");
		return result;
	}
	
}