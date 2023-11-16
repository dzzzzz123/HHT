package ext.HHT.disposition;

import java.util.List;

import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.OverrideComponentBuilder;
import com.ptc.windchill.enterprise.change2.mvc.builders.tables.AffectedItemsTableBuilder;

import wt.util.WTException;

@ComponentBuilder("changeTask.affectedItemsTable")
@OverrideComponentBuilder
public class MyAffectedItemsTableBuilder extends AffectedItemsTableBuilder {
	/**
	 * Returns the list of supported disposition types.
	 */
	@Override
	public List<String> getDispositionComponentIds() {
		List<String> ids = super.getDispositionComponentIds();
		ids.add(MyDispositionHandler.MY_DISPOSITION_COMPID);
		return ids;
	}

	/**
	 * Returns the configurable table.
	 */
	@Override
	public ConfigurableTable buildConfigurableTable(String id) throws WTException {
		return new MyChangeTaskAffectedItemsTableViews();
	}
}