
public class Job {
	public final Resource required;
	public final int workload;
	public final Type type;
	
	public Job(Resource _required,int _workload){
		this(_required,_workload,Type.NORMAL);
	}
	public Job(Resource _required,int _workload, Type _type){
		this.required=_required;
		this.workload=_workload;
		this.type=_type;
	}
}
