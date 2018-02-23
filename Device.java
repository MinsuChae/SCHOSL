import java.util.ArrayList;

abstract public class Device {
	protected Resource maxResource;
	protected Resource allowResource;
	protected Node connect;
	protected ArrayList<Job> workingjob=new ArrayList<Job>();
	
	public Node getConnectNode(){
		return connect;
	}
	
	public void setConnectNode(Node node){
		this.connect=node;
	}
	
	
	public Device(Resource _resource){
		this.maxResource=_resource;
		this.allowResource=_resource;
	}
		
	public boolean isCanExecuted(Job job){
		if(allowResource!=null && allowResource.isGreateOrEquals(job.required)){
			return true;
		}else{
			return false;
		}
	}
	
	public void allocateResource(Job job){
		Resource required=job.required;
		if(allowResource!=null && allowResource.isGreateOrEquals(required)){
			workingjob.add(job);
			allowResource=new Resource(allowResource.cpu-required.cpu, allowResource.memory-required.memory, allowResource.io-required.io);
		}
	}
	
	public void deallocateResource(Job job){
		Resource resource=job.required;
		if(allowResource!=null && workingjob.contains(job)){
			workingjob.remove(job);
			allowResource=new Resource(allowResource.cpu+resource.cpu, allowResource.memory+resource.memory, allowResource.io+resource.io);
		}
	}
	
	public Device compareResource(Device other, Job job ,Type type){
		if(other==null){
			return this;
		}
		if(type == Type.NORMAL){
			double this_cpuPercent=(allowResource.cpu-job.required.cpu)/(double)maxResource.cpu;
			double this_memoryPercent=(allowResource.memory-job.required.memory)/(double)maxResource.memory;
			double this_ioPercent=(allowResource.io-job.required.io)/(double)maxResource.io;
			double other_cpuPercent=(other.allowResource.cpu-job.required.cpu)/(double)other.maxResource.cpu;
			double other_memoryPercent=(other.allowResource.memory-job.required.memory)/(double)other.maxResource.memory;
			double other_ioPercent=(other.allowResource.io-job.required.io)/(double)other.maxResource.io;
			
			if(this_cpuPercent==other_cpuPercent){
				if(this_memoryPercent==other_memoryPercent){
					if(this_ioPercent>=other_ioPercent){
						return this;
					}else{
						return other;
					}
				}else if(this_memoryPercent>=other_memoryPercent){
					return this;
				}else{
					return other;
				}
			}else if(this_cpuPercent>=other_cpuPercent){
				return this;
			}else{
				return other;
			}
		}else if(type == Type.CPU){
			if(allowResource.cpu>=other.allowResource.cpu){
				return this;
			}else{
				return other;
			}
		}else if(type == Type.MEMORY){
			if(allowResource.memory>=other.allowResource.memory){
				return this;
			}else{
				return other;
			}
		}else if(type == Type.IO){
			if(allowResource.io>=other.allowResource.io){
				return this;
			}else{
				return other;
			}
		}else{
			double this_cpuPercent=(allowResource.cpu-job.required.cpu)/(double)maxResource.cpu;
			double this_memoryPercent=(allowResource.memory-job.required.memory)/(double)maxResource.memory;
			double this_ioPercent=(allowResource.io-job.required.io)/(double)maxResource.io;
			double other_cpuPercent=(other.allowResource.cpu-job.required.cpu)/(double)other.maxResource.cpu;
			double other_memoryPercent=(other.allowResource.memory-job.required.memory)/(double)other.maxResource.memory;
			double other_ioPercent=(other.allowResource.io-job.required.io)/(double)other.maxResource.io;
			
			if(this_cpuPercent==other_cpuPercent){
				if(this_memoryPercent==other_memoryPercent){
					if(this_ioPercent>=other_ioPercent){
						return this;
					}else{
						return other;
					}
				}else if(this_memoryPercent>=other_memoryPercent){
					return this;
				}else{
					return other;
				}
			}else if(this_cpuPercent>=other_cpuPercent){
				return this;
			}else{
				return other;
			}
		}
	}
}
