package ext.signature;

import java.io.Serializable;

import wt.content.ApplicationData;
import wt.content.HolderToContent;
import wt.events.KeyedEvent;
import wt.events.KeyedEventListener;
import wt.fc.Persistable;
import wt.fc.PersistenceManagerEvent;
import wt.services.ManagerException;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.util.FileUtil;
import wt.util.WTException;

/**
 * 监听新建表示法成功的POST_STORE事件，获取最近一次签署流程后执行签名
 * @author samuel
 *
 */

public class NewPDFListener extends StandardManager implements
		NewPDFListenerIntf, Serializable {
	private static final long serialVersionUID = 7674613111164596422L;
	private static final String CLASSNAME = NewPDFListener.class
			.getName();
	private KeyedEventListener listener;

	public String getConceptualClassname() {
		return CLASSNAME;
	}

	public static NewPDFListener newNewPDFListener()
			throws WTException {
		NewPDFListener instance = new NewPDFListener();
		instance.initialize();
		return instance;
	}

	protected void performStartupProcess() throws ManagerException {
		this.listener = new WCListenerEventListener(getConceptualClassname());
		getManagerService().addEventListener(this.listener, PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.POST_STORE));
	}

	class WCListenerEventListener extends ServiceEventListenerAdapter {

		public WCListenerEventListener(String manager_name) {
			super(manager_name);
		}

		@SuppressWarnings("deprecation")
		public void notifyVetoableEvent(Object eve) throws WTException {
			if (!(eve instanceof KeyedEvent)) {
				return;
			}
			KeyedEvent event = (KeyedEvent) eve;
			Object target = event.getEventTarget();
			//表示法
			if(target instanceof HolderToContent) {
				HolderToContent di = (HolderToContent)target;
				Persistable roleAObject = di.getRoleAObject();
				Persistable roleBObject = di.getRoleBObject();
				if(roleBObject instanceof ApplicationData) {
					ApplicationData pdf = (ApplicationData)roleBObject;
					String fileName = pdf.getFileName();
					String extension = FileUtil.getExtension(fileName);
					if("pdf".equalsIgnoreCase(extension)) {
						System.out.println("roleAObject="+roleAObject);
						System.out.println("roleBObject="+roleBObject);
					}
				}
			}else {
				
			}
		}
	}

}