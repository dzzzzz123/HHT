package ext.sap.masterData.mvc;

public class Result {
	private String number;
	private String name;
	private String result;
	private String msg;
	private String time;
	private String sapMark;

	public Result() {
		super();
	}

	public Result(String number, String name, String result, String msg, String time, String sapMark) {
		super();
		this.number = number;
		this.name = name;
		this.result = result;
		this.msg = msg;
		this.time = time;
		this.sapMark = sapMark;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSapMark() {
		return sapMark;
	}

	public void setSapMark(String sapMark) {
		this.sapMark = sapMark;
	}

	@Override
	public String toString() {
		return "Result [number=" + number + ", name=" + name + ", result=" + result + ", msg=" + msg + ", time=" + time
				+ ", sapMark=" + sapMark + "]";
	}

}