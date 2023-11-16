package ext.HHT.disposition;

import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.OverrideComponentBuilder;
import com.ptc.windchill.enterprise.change2.mvc.builders.tables.ChangeSummaryTableBuilder;

import wt.util.WTException;

@ComponentBuilder("changeNotice.changeSummary")
@OverrideComponentBuilder
public class MyChangeSummaryTableBuilder extends ChangeSummaryTableBuilder {
	/**
	 * Returns the configurable table.
	 */
	@Override
	public ConfigurableTable buildConfigurableTable(String id) throws WTException {
		return new MyChangeSummaryTableViews();
	}
}