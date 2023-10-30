package ext.sap.SupplierMasterData;

import java.util.ArrayList;
import java.util.Map;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;

import ext.ait.util.PropertiesUtil;
import wt.util.WTException;

public class SupplierMasterDataUtility extends DefaultDataUtility {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("customEnum.properties");

	@Override
	public Object getDataValue(String componentId, Object datum, ModelContext modelContext) throws WTException {
		GUIComponentArray array = new GUIComponentArray();
		ComboBox comboBox = new ComboBox();

		Map<String, String> map = pUtil.getAll();
		ArrayList<String> keys = new ArrayList<>(map.keySet());
		ArrayList<String> values = new ArrayList<>();
		for (String key : keys) {
			values.add(map.get(key).split("/")[0]);
		}

		String columnName = AttributeDataUtilityHelper.getColumnName(componentId, datum, modelContext);
		comboBox.setId(componentId);
		comboBox.setColumnName(columnName);
		comboBox.setInternalValues(keys);
		comboBox.setValues(values);
		comboBox.setRequired(true);
		comboBox.setEnabled(true);
		array.addGUIComponent(comboBox);
		return array;
	}
}
