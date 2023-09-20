package ext.srm.pdf;

import com.google.gson.Gson;

/**
 * @author Administrator
 *
 */
@SuppressWarnings("unused")
public class Result {

	private Boolean success;
	private Integer code;
	private String msg;
	private Object data;

	private Result(Boolean success, Integer code, String msg, Object data) {
		super();
		this.success = success;
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public static Result result(Boolean success, Integer code, String msg, Object data) {
		return new Result(success, code, msg, data);
	}

	public static Result sucess() {
		return sucess("");
	}

	public static Result sucess(String msg, Object data) {
		return result(true, 200, msg, data);
	}

	public static Result sucess(String msg) {
		return sucess(msg, null);
	}

	public static Result error(Integer code, String msg) {
		return result(false, code, msg, null);
	}

	public static Result error(String msg) {
		return error(300, msg);
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);

	}

}
