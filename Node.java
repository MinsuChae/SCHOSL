import java.util.ArrayList;
import java.util.HashSet;

public class Node extends Device{
	
	public Node(){
		super(null);
	}
	
	protected Node(Resource _resource) {
		super(_resource);
		// TODO Auto-generated constructor stub
	}
	protected ArrayList<Device> nearDevice=new ArrayList<Device>();
	protected ArrayList<MECServer> nearMECServer=new ArrayList<MECServer>();
	protected ArrayList<Node> nearNode=new ArrayList<Node>();
	protected ArrayList<CoreCloud> nearCoreCloud=new ArrayList<CoreCloud>();
	protected ArrayList<EdgeDevice> nearEdgeDevice=new ArrayList<EdgeDevice>();
	
	public void addNearDevice(Device device){
		device.setConnectNode(this);
		nearDevice.add(device);
		if(device instanceof MECServer){
			nearMECServer.add((MECServer) device);
			device.setConnectNode(this);
		}else if(device instanceof CoreCloud){
			nearCoreCloud.add((CoreCloud) device);
			device.setConnectNode(this);
		}else if(device instanceof EdgeDevice){
			nearEdgeDevice.add((EdgeDevice) device);
			device.setConnectNode(this);
		}else if(device instanceof Node){
			nearNode.add((Node) device);
		}
	}
	public void removeNearDevice(Device device){
		nearDevice.remove(device);
		if(device instanceof MECServer){
			nearMECServer.remove((MECServer) device);
			device.setConnectNode(null);
		}else if(device instanceof CoreCloud){
			nearCoreCloud.remove((CoreCloud) device);
			device.setConnectNode(null);
		}else if(device instanceof EdgeDevice){
			nearEdgeDevice.remove((EdgeDevice) device);
			device.setConnectNode(null);
		}else if(device instanceof Node){
			nearNode.remove((Node) device);
		}
	}
	
	public HashSet<Device> getAllocateDevices(Job job, Type type, int needCount){
		final int REQUEST_VM = needCount;
		HashSet<Device> result=new HashSet<Device>();
		HashSet<Device> candidate=new HashSet<Device>();
		HashSet<Device> ignore = new HashSet<Device>();
		ignore.add(this);
		
		if(maxResource!=null){
			if(this.isCanExecuted(job)){
				candidate.add(this);
			}
		}
		if(candidate.size()==needCount){
			return candidate;
		}else{
			result.addAll(candidate);
			needCount -= candidate.size();
			candidate.clear();
		}
		for(Device device : nearEdgeDevice){
			if(device.isCanExecuted(job)){
				candidate.add(device);
			}
		}
		if(candidate.size()<=needCount){
			result.addAll(candidate);
			needCount -= candidate.size();
		}else if(candidate.size()>needCount){
			while(needCount>=1){
				Device select=null;
				for(Device tmp : candidate){
					if(select==null){
						select = tmp;
					}else{
						select = select.compareResource(tmp, job, type);
					}
				}
				result.add(select);
				candidate.remove(select);
				needCount -= 1;
			}
		}
		candidate.clear();
		if(needCount>=1){
			// nearnode ÀÇ ¿§Áö
			for(Node node : nearNode){
				ArrayList<EdgeDevice> nearNodeEdge = node.nearEdgeDevice;
				for(Device device : nearNodeEdge){
					if(device.isCanExecuted(job)){
						candidate.add(device);
					}
				}
			}
			if(candidate.size()<=needCount){
				result.addAll(candidate);
				needCount -= candidate.size();
			}else if(candidate.size()>needCount){
				while(needCount>=1){
					Device select=null;
					for(Device tmp : candidate){
						if(select==null){
							select = tmp;
						}else{
							select = select.compareResource(tmp, job, type);
						}
					}
					result.add(select);
					candidate.remove(select);
					needCount -= 1;
				}
			}
			candidate.clear();
		}
		
		if(needCount>=1){
			// mec
			for(Device device : nearMECServer){
				if(device.isCanExecuted(job)){
					candidate.add(device);
				}
				ignore.add(device);
				
			}
			int limit=1;
			if(REQUEST_VM>1 && needCount==REQUEST_VM){
				limit=2;
			}
		
			while(needCount>=limit){
				Device select=null;
				for(Device tmp : candidate){
					if(select==null){
						select = tmp;
					}else{
						select = select.compareResource(tmp, job, type);
					}
				}
				if(select==null)
					break;
				result.add(select);
				candidate.remove(select);
				needCount -= 1;
			}
			candidate.clear();
		}
		
		if(needCount>=1){
			// core
			for(Device device : nearCoreCloud){
				if(device.isCanExecuted(job)){
					candidate.add(device);
				}
				ignore.add(device);
			}
			int limit=1;
			if(REQUEST_VM>1 && needCount==REQUEST_VM){
				limit=2;
			}
			
			while(needCount>=limit){
				Device select=null;
				for(Device tmp : candidate){
					if(select==null){
						select = tmp;
					}else{
						select = select.compareResource(tmp, job, type);
					}
				}
				if(select==null)
					break;
				result.add(select);
				candidate.remove(select);
				needCount -= 1;
			}
			candidate.clear();
		}
		if(needCount>=1){
			// nearnode ÀÇ MEC
			for(Node node : nearNode){
				ArrayList<MECServer> nearOfNearMECServer = node.nearMECServer;
				for(Device device : nearOfNearMECServer){
					if(device.isCanExecuted(job)){
						candidate.add(device);
					}
					ignore.add(device);
				}
				int limit=1;
				if(REQUEST_VM>1 && needCount==REQUEST_VM){
					limit=2;
				}
				
				while(needCount>=limit){
					Device select=null;
					for(Device tmp : candidate){
						if(select==null){
							select = tmp;
						}else{
							select = select.compareResource(tmp, job, type);
						}
					}
					if(select==null)
						break;
					result.add(select);
					candidate.remove(select);
					needCount -= 1;
				}
				candidate.clear();
			}
		}
		
		if(needCount>=1){
			// nearnode ÀÇ Core 
			for(Node node : nearNode){
				ArrayList<CoreCloud> nearOfNearCoreCloud = node.nearCoreCloud;
				for(Device device : nearOfNearCoreCloud){
					if(device.isCanExecuted(job)){
						candidate.add(device);
					}
					ignore.add(device);
				}
				int limit=1;
				if(REQUEST_VM>1 && needCount==REQUEST_VM){
					limit=2;
				}
				
				while(needCount>=limit){
					Device select=null;
					for(Device tmp : candidate){
						if(select==null){
							select = tmp;
						}else{
							select = select.compareResource(tmp, job, type);
						}
					}
					if(select==null)
						break;
					result.add(select);
					candidate.remove(select);
					needCount -= 1;
				}
				candidate.clear();
			}
		}
		
		if(needCount>=1){
			// nearnode ·Î Àç±Í
			
			for(Node node : nearNode){
				if(!ignore.contains(node)){
					candidate.addAll(node.getAllocateDevices(job, type, needCount, ignore));
					ignore.add(node);
					
					if(candidate.size()<=needCount){
						result.addAll(candidate);
						needCount -= candidate.size();
					}else if(candidate.size()>needCount){
						
						while(needCount>=1){
							Device select=null;
							for(Device tmp : candidate){
								if(select==null){
									select = tmp;
								}else{
									select = select.compareResource(tmp, job, type);
								}
							}
							if(select==null)
								break;
							result.add(select);
							candidate.remove(select);
							needCount -= 1;
						}
					}
					candidate.clear();
				}
				if(needCount==0){
					break;
				}
			}
			
		}
		
		
		return result;
	}
	
	private HashSet<Device> getAllocateDevices(Job job, Type type, int needCount, HashSet<Device> ignore){
		final int REQUEST_VM = needCount;
		HashSet<Device> result=new HashSet<Device>();
		if(needCount<=0)
			return result;
		HashSet<Device> candidate=new HashSet<Device>();
		ignore.add(this);

		if(maxResource!=null){
			if(this.isCanExecuted(job)){
				candidate.add(this);
			}
		}
		if(candidate.size()==needCount){
			return candidate;
		}else{
			result.addAll(candidate);
			needCount -= candidate.size();
			candidate.clear();
		}
		for(Device device : nearEdgeDevice){
			if(device.isCanExecuted(job)){
				candidate.add(device);
			}
		}
		if(candidate.size()<=needCount){
			result.addAll(candidate);
			needCount -= candidate.size();
		}else if(candidate.size()>needCount){
			while(needCount>=1){
				Device select=null;
				for(Device tmp : candidate){
					if(select==null){
						select = tmp;
					}else{
						select = select.compareResource(tmp, job, type);
					}
				}
				result.add(select);
				candidate.remove(select);
				needCount -= 1;
			}
		}
		candidate.clear();
		if(needCount>=1){
			// nearnode ÀÇ ¿§Áö
			for(Node node : nearNode){
				ArrayList<EdgeDevice> nearNodeEdge = node.nearEdgeDevice;
				for(Device device : nearNodeEdge){
					if(device.isCanExecuted(job)){
						candidate.add(device);
					}
				}
			}
			if(candidate.size()<=needCount){
				result.addAll(candidate);
				needCount -= candidate.size();
			}else if(candidate.size()>needCount){
				while(needCount>=1){
					Device select=null;
					for(Device tmp : candidate){
						if(select==null){
							select = tmp;
						}else{
							select = select.compareResource(tmp, job, type);
						}
					}
					result.add(select);
					candidate.remove(select);
					needCount -= 1;
				}
			}
			candidate.clear();
		}
		
		if(needCount>=1){
			// mec
			for(Device device : nearMECServer){
				if(ignore.contains(device))
					continue;
				if(device.isCanExecuted(job)){
					candidate.add(device);
				}
				ignore.add(device);
			}
			int limit=1;
			if(REQUEST_VM>1 && needCount==REQUEST_VM){
				limit=2;
			}
			
			while(needCount>=limit){
				Device select=null;
				for(Device tmp : candidate){
					if(select==null){
						select = tmp;
					}else{
						select = select.compareResource(tmp, job, type);
					}
				}
				if(select==null)
					break;
				result.add(select);
				candidate.remove(select);
				needCount -= 1;
			}
			candidate.clear();
		}
		
		if(needCount>=1){
			// core
			for(Device device : nearCoreCloud){
				if(ignore.contains(device))
					continue;
				if(device.isCanExecuted(job)){
					candidate.add(device);
				}
				ignore.add(device);
			}
			int limit=1;
			if(REQUEST_VM>1 && needCount==REQUEST_VM){
				limit=2;
			}
			
			while(needCount>=limit){
				Device select=null;
				for(Device tmp : candidate){
					if(select==null){
						select = tmp;
					}else{
						select = select.compareResource(tmp, job, type);
					}
				}
				if(select==null)
					break;
				result.add(select);
				candidate.remove(select);
				needCount -= 1;
			}
			candidate.clear();
		}
		
		if(needCount>=1){
			// nearnode ·Î Àç±Í
			for(Node node : nearNode){
				candidate.addAll(node.getAllocateDevices(job, type, needCount, ignore));
				ignore.add(node);
				
				if(candidate.size()<=needCount){
					result.addAll(candidate);
					needCount -= candidate.size();
				}else if(candidate.size()>needCount){
					while(needCount>=1){
						Device select=null;
						for(Device tmp : candidate){
							if(select==null){
								select = tmp;
							}else{
								select = select.compareResource(tmp, job, type);
							}
						}
						if(select==null)
							break;
						result.add(select);
						candidate.remove(select);
						needCount -= 1;
					}
				}
				candidate.clear();
			}
			
		}
		return result;
	}
}
