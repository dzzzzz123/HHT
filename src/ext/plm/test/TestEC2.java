package ext.plm.test;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Date;

import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeRequest2;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;

public class TestEC2 implements RemoteAccess {

	/**
	 * @param args
	 * @throws WTException 
	 */
	public static void main( String[] args ) throws WTException {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		if(args==null||args.length<2 || args[0]==null||"".equals(args[0].trim()) || args[1]==null||"".equals(args[1].trim())){
			System.out.println("---error:name and passwd is null.");
			return;
		}
		rms.setUserName(args[0]);
		rms.setPassword(args[1]);
		
		System.out.println("start to test----2--------"+(new Date()).toLocaleString());
		System.out.println("----ecr---arg:"+args[2]);
		createECR(args[2]);
		
		System.out.println("end test---2-------"+(new Date()).toLocaleString());
	}
	
	public static void createECA(String id){
		if (!RemoteMethodServer.ServerFlag) {	
			String CLASSNAME = (TestEC2.class).getName();
			Class argTypes[] = new Class[]{String.class};
			Object svrArgs[] = new Object[]{id};
			try {
				RemoteMethodServer.getDefault().invoke("createECA", CLASSNAME, null, argTypes, svrArgs);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}else{
			try {
				ReferenceFactory ref = new ReferenceFactory();
				WTReference wf = ref.getReference(id);
				WTChangeActivity2 eca = (WTChangeActivity2)wf.getObject();
				System.out.println("------eca:"+eca.getNumber());
				ext.plm.change.ChangeUtil.createECAFile(eca);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static void createECR(String id){
		if (!RemoteMethodServer.ServerFlag) {	
			String CLASSNAME = (TestEC2.class).getName();
			Class argTypes[] = new Class[]{String.class};
			Object svrArgs[] = new Object[]{id};
			try {
				RemoteMethodServer.getDefault().invoke("createECR", CLASSNAME, null, argTypes, svrArgs);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}else{
			try {
				ReferenceFactory ref = new ReferenceFactory();
				WTReference wf = ref.getReference(id);
				WTChangeRequest2 ecr = (WTChangeRequest2)wf.getObject();
				System.out.println("----22--ecr:"+ecr.getNumber());
				ext.plm.change.ChangeUtil.createECRFile(ecr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
