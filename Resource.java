
public class Resource {
	public final int cpu;
	public final int memory;
	public final int io;
	
	public Resource(int _cpu, int _memory, int _io){
		this.cpu=_cpu;
		this.memory=_memory;
		this.io=_io;
	}
	public boolean isGreateOrEquals(Resource resource){
		if(cpu>=resource.cpu && memory>=resource.memory && io>=resource.io){
			return true;
		}else{
			return false;
		}
	}
}
