package ext.ait.properties;

import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.CHANGE_STATUS_FAMILY;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.CONTAINER_NAME;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.FORMAT_ICON;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.GENERAL_STATUS_FAMILY;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.ICON;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.INFO_ACTION;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.LAST_MODIFIED;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NAME;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NM_ACTIONS;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NON_SELECTABLE_COLUMN;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NUMBER;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.ORG_ID;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.PRE_SELECTABLE_COLUMN;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.SHARE_STATUS_FAMILY;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.STATE;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.VERSION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import com.ptc.core.components.util.RequestHelper;
import com.ptc.core.htmlcomp.components.ConfigurableTableBuilder;
import com.ptc.core.htmlcomp.components.JCAConfigurableTable;
import com.ptc.core.htmlcomp.components.TableViewUtils;
import com.ptc.core.htmlcomp.createtableview.Attribute;
import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.core.htmlcomp.tableview.SortColumnDescriptor;
import com.ptc.core.htmlcomp.tableview.TableColumnDefinition;
import com.ptc.core.htmlcomp.tableview.TableViewDescriptor;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentConfigBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.util.ClientMessageSource;
import com.ptc.netmarkets.util.beans.NmHelperBean;

import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public abstract class AbstractCarambolaTableConfigBuilder extends AbstractComponentConfigBuilder
		implements ConfigurableTableBuilder {

	private static final String RESOURCE = "com.ptc.carambola.carambolaResource";

	public final ClientMessageSource messageSource = getMessageSource(RESOURCE);

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		/**
		 * Set properties that used to be on <describeTable>
		 */
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();

		table.setLabel(messageSource.getMessage("PART_TABLE_LABEL"));
		table.setSelectable(true);
		table.setType("wt.part.WTPart");
		table.setActionModel("mvc_tables_toolbar");

		/**
		 * These are properties that used to be set on <renderTable>
		 */
		table.setShowCount(true);
		table.setShowCustomViewLink(true);

		table.addComponent(factory.newColumnConfig(ICON, true));
		ColumnConfig col = factory.newColumnConfig(NAME, true);
		// This is how to set a column to have variable height
		// ((JcaColumnConfig)col).setVariableHeight(true);

		// specifying a custom javascript function for the column
		col.setCompareJsFunction("PTC.carambola.customCompareFunction");

		table.addComponent(col);
		table.addComponent(factory.newColumnConfig(FORMAT_ICON, false));
		table.addComponent(factory.newColumnConfig(NUMBER, false));
		table.addComponent(factory.newColumnConfig(ORG_ID, false));
		table.addComponent(factory.newColumnConfig(INFO_ACTION, false));
		ColumnConfig nmActionsCol = factory.newColumnConfig(NM_ACTIONS, false);
		((JcaColumnConfig) nmActionsCol).setActionModel("CustEx_table_row_actions");
		table.addComponent(nmActionsCol);

		table.addComponent(factory.newColumnConfig(SHARE_STATUS_FAMILY, false));
		table.addComponent(factory.newColumnConfig(GENERAL_STATUS_FAMILY, false));
		table.addComponent(factory.newColumnConfig(CHANGE_STATUS_FAMILY, false));
		table.addComponent(factory.newColumnConfig(VERSION, true));
		table.addComponent(factory.newColumnConfig(LAST_MODIFIED, true));
		table.addComponent(factory.newColumnConfig(CONTAINER_NAME, false));
		// State column is a DataStore-only column
		ColumnConfig stateColumn = factory.newColumnConfig(STATE, true);
		stateColumn.setDataStoreOnly(true);
		table.addComponent(stateColumn);

		table.setView("/carambola/carambolaMVCExampleTable.jsp");

		return table;
	}

	/**
	 * @param tableId
	 * @param factory
	 * @param table
	 * @throws WTException
	 */
	protected void setColumnsOnView(String tableId, ComponentConfigFactory factory, TableConfig table,
			ComponentParams params) throws WTException {
		NmHelperBean helperBean = ((JcaComponentParams) params).getHelperBean();
		ReferenceFactory referenceFactory = new ReferenceFactory();
		String currentView = null;
		String requested_view = RequestHelper.getRequestedTableViewId(tableId, helperBean.getRequest());
		if (requested_view != null) {
			TableViewDescriptor obj = (TableViewDescriptor) referenceFactory.getReference(requested_view).getObject();
			// This method sets the localized values of name and description
			obj.localizeFields(helperBean.getNmCommandBean().getLocale());
			currentView = obj.getName();
		} else {
			currentView = TableViewUtils.getCurrentView(tableId);
		}
		if ((messageSource.getMessage("NON_SELECTABLE_VIEW_NAME")).equals(currentView)) {
			ColumnConfig col2 = factory.newColumnConfig(NON_SELECTABLE_COLUMN, false);
			col2.setDataStoreOnly(true);
			col2.setNeed("endItem");
			table.addComponent(col2);
			table.setNonSelectableColumn(col2);
		} else if ((messageSource.getMessage("PRE_SELECTABLE_VIEW_NAME")).equals(currentView)) {
			ColumnConfig preSelectableColumn = factory.newColumnConfig(PRE_SELECTABLE_COLUMN, false);
			preSelectableColumn.setDataStoreOnly(true);
			preSelectableColumn.setNeed("endItem");
			table.addComponent(preSelectableColumn);
			table.setPreSelectableColumn(preSelectableColumn);
		} else if ((messageSource.getMessage("STRIKE_THROUGH_VIEW_NAME")).equals(currentView)) {
			ColumnConfig strikeThroughColumn = factory.newColumnConfig("strikeThroughRow", false);
			strikeThroughColumn.setDataStoreOnly(true);
			strikeThroughColumn.setNeed("endItem");
			table.addComponent(strikeThroughColumn);
			table.setStrikeThroughColumn(strikeThroughColumn);
		}
	}

	@Override
	public ConfigurableTable buildConfigurableTable(String tableId) throws WTException {
		return new MvcConfigurableTable();
	}

	private static class MvcConfigurableTable extends JCAConfigurableTable {

		@Override
		public Class<?>[] getClassTypes() {
			return new Class[] { WTPart.class };
		}

		@Override
		public String getDefaultSortColumn() {
			return LAST_MODIFIED;
		}

		@Override
		public String getLabel(Locale locale) {
			return getMessageSource(RESOURCE).getMessage("PART_TABLE_LABEL", null, locale);
		}

		@Override
		public String getOOTBActiveViewName() {
			return getViewResourceEntryKey(RESOURCE, "ALL_VIEW_NAME");
		}

		@Override
		public List<?> getOOTBTableViews(String tableId, Locale locale) throws WTException {

			Vector<TableColumnDefinition> columns = getCommonColumnList();
			ArrayList<SortColumnDescriptor> sortColumns = new ArrayList<SortColumnDescriptor>();
			SortColumnDescriptor sortColumn = new SortColumnDescriptor();
			sortColumn = new SortColumnDescriptor();
			sortColumn.setColumnId(LAST_MODIFIED); // Primary Sort
			sortColumn.setOrder(SortColumnDescriptor.DESCENDING);
			sortColumns.add(sortColumn);

			try {
				List<TableViewDescriptor> result1 = new ArrayList<TableViewDescriptor>();

				String viewName = getViewResourceEntryKey(RESOURCE, "ALL_VIEW_NAME");
				String viewDesc = getViewResourceEntryKey(RESOURCE, "ALL_VIEW_NAME_DESC");
				TableViewDescriptor tvd = TableViewDescriptor.newTableViewDescriptor(viewName, tableId, true, true,
						columns, null, true, viewDesc);
				tvd.setColumnSortOrder(sortColumns);
				result1.add(tvd);

				String nonSelectableViewName = getViewResourceEntryKey(RESOURCE, "NON_SELECTABLE_VIEW_NAME");
				String nonSelectableViewDesc = getViewResourceEntryKey(RESOURCE, "NON_SELECTABLE_VIEW_NAME_DESC");
				Vector<TableColumnDefinition> nonSelectableColumns = new Vector<TableColumnDefinition>();
				nonSelectableColumns.addAll(columns);

				TableColumnDefinition nonSelectableColumn = TableColumnDefinition
						.newTableColumnDefinition(PRE_SELECTABLE_COLUMN, /* lockable */ false);
				nonSelectableColumn.setHidden(true);
				nonSelectableColumns.add(nonSelectableColumn);
				TableViewDescriptor nonSelectableTvd = TableViewDescriptor.newTableViewDescriptor(nonSelectableViewName,
						tableId, true, true, nonSelectableColumns, null, true, nonSelectableViewDesc);
				result1.add(nonSelectableTvd);
				String preSelectableViewName = getViewResourceEntryKey(RESOURCE, "PRE_SELECTABLE_VIEW_NAME");
				String preSelectableViewDesc = getViewResourceEntryKey(RESOURCE, "PRE_SELECTABLE_VIEW_NAME_DESC");
				Vector<TableColumnDefinition> preSelectableColumns = new Vector<TableColumnDefinition>();
				preSelectableColumns.addAll(columns);
				TableColumnDefinition preSelectableColumn = TableColumnDefinition
						.newTableColumnDefinition(PRE_SELECTABLE_COLUMN, /* lockable */ false);
				preSelectableColumn.setHidden(true);
				preSelectableColumns.add(preSelectableColumn);
				TableViewDescriptor preSelectableTvd = TableViewDescriptor.newTableViewDescriptor(preSelectableViewName,
						tableId, true, true, preSelectableColumns, null, true, preSelectableViewDesc);
				preSelectableTvd.setColumnSortOrder(sortColumns);
				result1.add(preSelectableTvd);

				String strikeThroughViewName = getViewResourceEntryKey(RESOURCE, "STRIKE_THROUGH_VIEW_NAME");
				String strikeThroughViewDesc = getViewResourceEntryKey(RESOURCE, "STRIKE_THROUGH_VIEW_NAME_DESC");
				Vector<TableColumnDefinition> strikeThroughColumns = new Vector<TableColumnDefinition>();
				strikeThroughColumns.addAll(columns);
				TableColumnDefinition strikeThroughCol = TableColumnDefinition
						.newTableColumnDefinition("strikeThroughRow", /* lockable */ false);
				strikeThroughCol.setHidden(true);
				strikeThroughColumns.add(strikeThroughCol);
				TableViewDescriptor strikeThroughTvd = TableViewDescriptor.newTableViewDescriptor(strikeThroughViewName,
						tableId, true, true, strikeThroughColumns, null, true, strikeThroughViewDesc);
				strikeThroughTvd.setColumnSortOrder(sortColumns);
				result1.add(strikeThroughTvd);
				return result1;
			} catch (WTPropertyVetoException wtpve) {
				throw new WTException(wtpve, "Unable to create table views");
			}
		}

		/**
		 * This method will return the list of column that will be present in all views
		 * 
		 * @return
		 * @throws WTException
		 */
		private Vector<TableColumnDefinition> getCommonColumnList() throws WTException {
			Vector<TableColumnDefinition> columns = new Vector<TableColumnDefinition>();
			columns.add(TableColumnDefinition.newTableColumnDefinition(ICON, /* lockable */false));
			columns.add(TableColumnDefinition.newTableColumnDefinition(NAME, /* lockable */false));
			columns.add(TableColumnDefinition.newTableColumnDefinition(FORMAT_ICON, /* lockable */false));
			columns.add(TableColumnDefinition.newTableColumnDefinition(NUMBER, /* lockable */false));
			columns.add(TableColumnDefinition.newTableColumnDefinition(ORG_ID, /* lockable */false));
			columns.add(TableColumnDefinition.newTableColumnDefinition(INFO_ACTION, /* lockable */false));
			columns.add(TableColumnDefinition.newTableColumnDefinition(NM_ACTIONS, /* lockable */false));
			columns.add(TableColumnDefinition.newTableColumnDefinition(SHARE_STATUS_FAMILY, /* lockable */false));
			columns.add(TableColumnDefinition.newTableColumnDefinition(GENERAL_STATUS_FAMILY, /* lockable */false));
			columns.add(TableColumnDefinition.newTableColumnDefinition(CHANGE_STATUS_FAMILY, /* lockable */false));
			columns.add(TableColumnDefinition.newTableColumnDefinition(VERSION, /* lockable */false));
			columns.add(TableColumnDefinition.newTableColumnDefinition(LAST_MODIFIED, /* lockable */false));
			columns.add(TableColumnDefinition.newTableColumnDefinition(CONTAINER_NAME, /* lockable */false));
			return columns;
		}

		@Override
		public boolean isAttributeValidForColumnStep(String columnId) {
			if (STATE.equals(columnId)) {
				return false;
			}

			return super.isAttributeValidForColumnStep(columnId);
		}

		@Override
		public List<?> getSpecialTableColumnsAttrDefinition(Locale locale) {
			return Collections.singletonList(
					new Attribute.TextAttribute(NM_ACTIONS, getMessageSource("com.ptc.core.ui.componentRB")
							.getMessage(com.ptc.core.ui.componentRB.ACTIONS, null, locale), locale));
		}
	}
}
