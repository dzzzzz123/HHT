package ext.HHT.disposition;

import java.util.ArrayList;

import com.ptc.core.lwc.server.LWCNormalizedObject;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.windchill.enterprise.change2.ChangeLinkAttributeHelper;
import com.ptc.windchill.enterprise.change2.beans.ChangeLinkAttributeBean;
import com.ptc.windchill.enterprise.change2.handler.DefaultDispositionHandler;

import wt.change2.InventoryDisposition;
import wt.fc.BinaryLink;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class MyDispositionHandler extends DefaultDispositionHandler {
	public static final String MY_DISPOSITION_COMPID = "myDisposition";

	public static final String MY_DISPOSITION_ATTR = "myDispositionAttr";

	/**
	 * Get disposition value for a given disposition type.
	 */
	@Override
	public InventoryDisposition getDispositionValue(String component_id, BinaryLink link) {
		InventoryDisposition dispo = null;
		if (link != null && MY_DISPOSITION_COMPID.equals(component_id)) {
			try {
				LWCNormalizedObject lwc = new LWCNormalizedObject(link, null, null, null);
				lwc.load(MY_DISPOSITION_ATTR);
				dispo = InventoryDisposition.toInventoryDisposition((String) lwc.get(MY_DISPOSITION_ATTR));
			} catch (WTException e) {
				e.printStackTrace();
			}
		} else {
			dispo = super.getDispositionValue(component_id, link);
		}
		return dispo;
	}

	/**
	 * Set the disposition value for a given disposition type.
	 */
	@Override
	public boolean setDispositionValue(String component_id, BinaryLink link, InventoryDisposition newdisposition)
			throws WTException {
		boolean updated = false;
		if (link != null && MY_DISPOSITION_COMPID.equals(component_id)) {
			try {
				LWCNormalizedObject lwc = new LWCNormalizedObject(link, null, null, new UpdateOperationIdentifier());
				lwc.load(MY_DISPOSITION_ATTR);
				String curValue = (String) lwc.get(MY_DISPOSITION_ATTR);
				String newValue = newdisposition.toString();
				if (curValue != null && curValue.equals(newValue)) {
					return false;
				} else {
					lwc.set(MY_DISPOSITION_ATTR, newValue);
					lwc.apply();
					updated = true;
					try {
						link.getPersistInfo().setVerified(true);
					} catch (WTPropertyVetoException e) {
						e.printStackTrace();
					}

				}
			} catch (WTException e) {
				e.printStackTrace();
			}
		} else {
			updated = super.setDispositionValue(component_id, link, newdisposition);
		}
		return updated;
	}

	/**
	 * Get the disposition value set of a given disposition type
	 */
	@Override
	public ArrayList<InventoryDisposition> getInventoryDispositionSet(ChangeLinkAttributeBean linkBean,
			String component_id) {
		ArrayList<InventoryDisposition> validValues = null;
		if (MY_DISPOSITION_COMPID.equals(component_id)) {
			validValues = ChangeLinkAttributeHelper.getValidDispositionValues(linkBean, MY_DISPOSITION_ATTR);
		} else {
			validValues = super.getInventoryDispositionSet(linkBean, component_id);
		}
		return validValues;
	}
}