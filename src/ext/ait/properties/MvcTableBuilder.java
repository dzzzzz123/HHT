package ext.ait.properties;

import org.apache.logging.log4j.Logger;

import com.ptc.core.components.descriptor.DescriptorConstants;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.builders.carambola.table.AbstractCarambolaTableConfigBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentDataBuilder;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.ds.DataSourceMode;
import com.ptc.mvc.ds.server.jmx.PerformanceConfig;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.pds.PartialResultException;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.util.WTException;

@ComponentBuilder("carambola.mvc.table")
public class MvcTableBuilder extends AbstractCarambolaTableConfigBuilder implements ComponentDataBuilder {
	private static final Logger log = LogR.getLoggerInternal(MvcTableBuilder.class.getName());

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		JcaTableConfig config = (JcaTableConfig) super.buildComponentConfig(params);
		setColumnsOnView("carambola.mvc.table", getComponentConfigFactory(), config, params);
		config.setLabel(config.getLabel() + " (Datasource disabled)");
		config.setDataSourceMode(DataSourceMode.DISABLED);

		ComponentConfigFactory factory = getComponentConfigFactory();
		ColumnConfig actionsMenu = factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.NM_ACTIONS, true);

		((JcaColumnConfig) actionsMenu).setDescriptorProperty(DescriptorConstants.ActionProperties.ACTION_MODEL,
				"model_name_here");
		config.addComponent(actionsMenu);

		return config;
	}

	@Override
	public QueryResult buildComponentData(ComponentConfig config, ComponentParams params) throws WTException {
		QuerySpec qs = new QuerySpec(WTPart.class);
		PerformanceConfig pConfig = PerformanceConfig.getPerformanceConfig();
		int queryLimit = pConfig.getQueryLimit();
		qs.setQueryLimit(queryLimit > 3000 ? 3000 : queryLimit);
		QueryResult qr = null;
		try {
			qr = PersistenceHelper.manager.find((StatementSpec) qs);
		} catch (PartialResultException pre) {
			qr = pre.getQueryResult();
			if (log.isDebugEnabled()) {
				log.debug("Performance config query  Limit reached " + pre.getMessage(), pre);
			}
		}

		return qr;
	}
}