package ext.listener.Listener;

import java.io.Serializable;

import ext.listener.service.WTDocumentService;
import ext.listener.service.WTPartService;
import wt.doc.WTDocument;
import wt.events.KeyedEvent;
import wt.events.KeyedEventListener;
import wt.fc.PersistenceManagerEvent;
import wt.part.WTPart;
import wt.services.ManagerException;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.util.WTException;

public class ListenManager extends StandardManager implements ListenService, Serializable {
	private static final long serialVersionUID = 1L;
	private static final String CLASSNAME = ListenManager.class.getName();
	private KeyedEventListener listener;

	public String getConceptualClassname() {
		return CLASSNAME;
	}

	public ListenManager() {
	}

	public static ListenManager newListenManager() throws WTException {
		ListenManager instance = new ListenManager();
		instance.initialize();
		return instance;
	}

	@Override
	protected void performStartupProcess() throws ManagerException {
		this.listener = new WCListenerEventListener(getConceptualClassname());
		getManagerService().addEventListener(listener,
				PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.POST_STORE));
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
			Object eventTarget = (Object) ((KeyedEvent) event).getEventTarget();
			if (eventTarget instanceof WTDocument) {
				WTDocument document = (WTDocument) eventTarget;
				WTDocumentService.process_POST_STORE(document);
			} else if (eventTarget instanceof WTPart) {
				WTPart part = (WTPart) eventTarget;
				WTPartService.process_POST_STORE(part);
			}
		}
	}
}
