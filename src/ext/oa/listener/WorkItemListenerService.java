//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ext.oa.listener;

import java.io.Serializable;

import ext.oa.service.OAWaitingProcessingService;
import ext.oa.service.ProcessStatus;
import wt.events.KeyedEvent;
import wt.events.KeyedEventListener;
import wt.fc.PersistenceManagerEvent;
import wt.lifecycle.LifeCycleServiceEvent;
import wt.ownership.OwnershipServiceEvent;
import wt.services.ManagerException;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

public class WorkItemListenerService extends StandardManager implements ListenerService, Serializable {

	private static final long serialVersionUID = 1L;
	private static final String CLASSNAME = WorkItemListenerService.class.getName();
	private KeyedEventListener listener;

	public WorkItemListenerService() {
	}

	public String getConceptualClassname() {
		return CLASSNAME;
	}

	public static WorkItemListenerService newWorkItemListenerService() throws WTException {
		WorkItemListenerService instance = new WorkItemListenerService();
		instance.initialize();
		return instance;
	}

	protected void performStartupProcess() throws ManagerException {
		this.listener = new WCListenerEventListener(this.getConceptualClassname());
		this.getManagerService().addEventListener(this.listener,
				PersistenceManagerEvent.generateEventKey(LifeCycleServiceEvent.class, "STATE_CHANGE"));
		this.getManagerService().addEventListener(this.listener, PersistenceManagerEvent.generateEventKey("UPDATE"));
		this.getManagerService().addEventListener(this.listener,
				PersistenceManagerEvent.generateEventKey("PRE_REMOVE"));
		this.getManagerService().addEventListener(this.listener, "POST_STORE");
		this.getManagerService().addEventListener(this.listener,
				OwnershipServiceEvent.generateEventKey("PRE_CHANGEOWNER"));
	}

	class WCListenerEventListener extends ServiceEventListenerAdapter {
		public WCListenerEventListener(String manager_name) {
			super(manager_name);
		}

		public void notifyVetoableEvent(Object eve) throws WTException {
			if (eve instanceof KeyedEvent) {
				KeyedEvent event = (KeyedEvent) eve;
				String eventType = event.getEventType();
				Object target = event.getEventTarget();
				if (target instanceof WorkItem) {
					WorkItem item = (WorkItem) target;
					if (eventType.equalsIgnoreCase(ProcessStatus.UPDATE.toString())) {

						if (item.getStatus().toString().equals(ProcessStatus.COMPLETED.toString())) {
							System.out.println("当前进入" + ProcessStatus.COMPLETED);
							OAWaitingProcessingService.sendTaskToOA(item, ProcessStatus.COMPLETED);
						} else {
							System.out.println("当前进入" + ProcessStatus.UPDATE);
							OAWaitingProcessingService.sendTaskToOA(item, ProcessStatus.UPDATE);
						}

					} else if (eventType.equalsIgnoreCase(ProcessStatus.PRE_REMOVE.toString())) {
						System.out.println("进入PRE_REMOVE");
						OAWaitingProcessingService.sendDeleteTaskToOA(item);

					} else if (eventType.equalsIgnoreCase(ProcessStatus.PRE_CHANGEOWNER.toString())) {
						System.out.println("进入PRE_CHANGEOWNER");
						OAWaitingProcessingService.sendTurnToTaskToOA(item);

					}
				}

			}
		}
	}
}
