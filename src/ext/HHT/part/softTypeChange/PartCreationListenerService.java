package ext.HHT.part.softTypeChange;

import java.io.Serializable;

import ext.ait.util.IBAUtil;
import ext.ait.util.PartUtil;
import ext.ait.util.PersistenceUtil;
import wt.epm.EPMDocument;
import wt.epm.workspaces.EPMWorkspaceManagerEvent;
import wt.events.KeyedEvent;
import wt.events.KeyedEventListener;
import wt.fc.Persistable;
import wt.part.WTPart;
import wt.services.ManagerException;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.util.WTException;

public class PartCreationListenerService extends StandardManager implements PartCreationListener, Serializable {

	private static final long serialVersionUID = 1L;
	private static final String CLASSNAME = PartCreationListenerService.class.getName();
	private KeyedEventListener listener;

	public String getConceptualClassname() {
		return CLASSNAME;
	}

	public PartCreationListenerService() {
		// TODO Auto-generated constructor stub
	}

	public static PartCreationListenerService newPartCreationListenerService() throws WTException {
		PartCreationListenerService instance = new PartCreationListenerService();
		instance.initialize();
		return instance;
	}

	@Override
	protected void performStartupProcess() throws ManagerException {
		this.listener = new WCListenerEventListener(getConceptualClassname());
		getManagerService().addEventListener(listener,
				EPMWorkspaceManagerEvent.generateEventKey(EPMWorkspaceManagerEvent.POST_WORKSPACE_CHECKIN));
		getManagerService().addEventListener(listener,
				EPMWorkspaceManagerEvent.generateEventKey(EPMWorkspaceManagerEvent.NEW_TO_WORKSPACE));
	}

	class WCListenerEventListener extends ServiceEventListenerAdapter {
		public WCListenerEventListener(String manager_name) {
			super(manager_name);
		}

		@Override
		public void notifyVetoableEvent(Object event) throws Exception {
			if (!(event instanceof KeyedEvent)) {
				return;
			}

			System.out.println("event: " + event);
			WTPart part = null;
			Persistable target = (Persistable) ((KeyedEvent) event).getEventTarget();
			if (target instanceof WTPart) {
				System.out.println("-----------sout-------------Listener WTPart");
				part = (WTPart) target;
			} else if (target instanceof EPMDocument) {
				System.out.println("-----------sout-------------Listener EPMDocument");
				EPMDocument epm = (EPMDocument) target;
				IBAUtil ibaUtil = new IBAUtil(epm);
				String partNumber = ibaUtil.getIBAValue("HHT_PART_NUMBER");
				part = PartUtil.getWTPartByNumber(partNumber);
			}
			if (part != null) {
				System.out.println("part: " + part.getNumber());
				System.out.println("part: " + part.getName());
				System.out.println("part: " + PersistenceUtil.getSubTypeInternal(part));
			}
		}
	}
}
