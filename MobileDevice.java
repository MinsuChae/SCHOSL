
public class MobileDevice extends EdgeDevice{
	
	private int power;
	private int allowPower;
	
	public MobileDevice(Resource _resource, int _power, int _allowPower) {
		super(_resource);
		this.power=_power;
		this.allowPower=_allowPower;
	}

	public void setAllowPower(int _allowPower){
		this.allowPower=_allowPower;
	}
	
	public void setPower(int _power){
		this.power=_power;
	}
	
	@Override
	public boolean isCanExecuted(Job job){
		if(this.power>=this.allowPower && allowResource.isGreateOrEquals(job.required)){
			return true;
		}else{
			return false;
		}
	}
}
