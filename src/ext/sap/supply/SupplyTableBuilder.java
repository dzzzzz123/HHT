package ext.sap.supply;

import com.ptc.core.htmlcomp.components.AbstractConfigurableTableBuilder;
import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.ds.server.jmx.PerformanceConfig;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.pds.PartialResultException;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.util.WTException;

@ComponentBuilder("supply.mvc.table")
public class SupplyTableBuilder extends AbstractConfigurableTableBuilder {

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {

		return null;
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
		}
		return qr;
	}

	@Override
	public ConfigurableTable buildConfigurableTable(String arg0) throws WTException {
		// TODO Auto-generated method stub
		return null;
	}
}
