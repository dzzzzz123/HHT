package ext.sap.CostCenter;

import java.util.ArrayList;
import java.util.List;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;

import wt.util.WTException;

public class CostCenterUtility extends DefaultDataUtility {

	@Override
	public Object getDataValue(String componentId, Object datum, ModelContext modelContext) throws WTException {
		GUIComponentArray array = new GUIComponentArray();
		ComboBox comboBox = new ComboBox();

		List<CostCenterEntity> entityList = CostCenterServlet.getAllCostCenter();
		ArrayList<String> keys = new ArrayList<>();
		ArrayList<String> values = new ArrayList<>();
		for (CostCenterEntity entity : entityList) {
			keys.add(entity.getInternalName());
			values.add(entity.getDisplayName());
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
