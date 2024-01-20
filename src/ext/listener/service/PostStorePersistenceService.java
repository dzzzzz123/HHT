package ext.listener.service;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.apache.commons.lang3.StringUtils;

import com.ptc.core.meta.server.TypeIdentifierUtility;

import ext.listener.Config;
import ext.listener.Listener.PostStorePersistenceListener;
import wt.events.KeyedEvent;
import wt.events.KeyedEventListener;
import wt.fc.PersistenceManagerEvent;
import wt.fc.PersistenceServerHelper;
import wt.part.WTPart;
import wt.services.ManagerException;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class PostStorePersistenceService extends StandardManager implements PostStorePersistenceListener, Serializable {

	private static final long serialVersionUID = 1L;
	private static final String CLASSNAME = PostStorePersistenceService.class.getName();
	private KeyedEventListener listener;

	public String getConceptualClassname() {
		return CLASSNAME;
	}

	public PostStorePersistenceService() {
	}

	public static PostStorePersistenceService newPostStorePersistenceService() throws WTException {
		PostStorePersistenceService instance = new PostStorePersistenceService();
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
			Object target = (Object) ((KeyedEvent) event).getEventTarget();
			if (target instanceof WTPart) {
				System.out.println("changePartType Checking WTPart: " + (WTPart) target);
				changePartType((WTPart) target);
			}
		}
	}

	public static void changePartType(WTPart part) {
		try {
			String oldType = TypeIdentifierUtility.getTypeIdentifier(part).getTypeInternalName();
			String classification = Config.getHHT_Classification(part);
			System.out.println("classification: " + classification);
			String newType = Config.getValueByKey(String.valueOf(classification.charAt(0)));
			System.out.println("oldType: " + oldType);
			System.out.println("newType: " + newType);
			if (StringUtils.isNotBlank(newType) && !StringUtils.equals(oldType, newType)) {
				TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(newType);
				part.setTypeDefinitionReference(tdr);
				PersistenceServerHelper.manager.update(part);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
	}

}
