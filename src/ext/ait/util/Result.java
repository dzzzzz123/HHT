package ext.ait.util;

import java.util.HashMap;

import com.google.gson.Gson;

public class Result extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	/** 状态码 */
	public static final String CODE_TAG = "code";

	/** 返回内容 */
	public static final String MSG_TAG = "msg";

	/** 数据对象 */
	public static final String DATA_TAG = "data";

	private static final int SUCCESS_CODE = 200;

	private static final int WARN_CODE = 600;

	private static final int ERROR_CODE = 500;

	/**
	 * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
	 */
	public Result() {
	}

	/**
	 * 初始化一个新创建的 AjaxResult 对象
	 * 
	 * @param code 状态码
	 * @param msg  返回内容
	 */
	public Result(int code, String msg) {
		super.put(CODE_TAG, code);
		super.put(MSG_TAG, msg);
	}

	/**
	 * 初始化一个新创建的 AjaxResult 对象
	 * 
	 * @param code 状态码
	 * @param msg  返回内容
	 * @param data 数据对象
	 */
	public Result(int code, String msg, Object data) {
		super.put(CODE_TAG, code);
		super.put(MSG_TAG, msg);
		if (data != null) {
			super.put(DATA_TAG, data);
		}
	}

	/**
	 * 返回成功消息
	 * 
	 * @return 成功消息
	 */
	public static Result success() {
		return Result.success("操作成功");
	}

	/**
	 * 返回成功数据
	 * 
	 * @return 成功消息
	 */
	public static Result success(Object data) {
		return Result.success("操作成功", data);
	}

	/**
	 * 返回成功消息
	 * 
	 * @param msg 返回内容
	 * @return 成功消息
	 */
	public static Result success(String msg) {
		return Result.success(msg, null);
	}

	/**
	 * 返回成功消息
	 * 
	 * @param msg  返回内容
	 * @param data 数据对象
	 * @return 成功消息
	 */
	public static Result success(String msg, Object data) {
		return new Result(SUCCESS_CODE, msg, data);
	}

	/**
	 * 返回警告消息
	 *
	 * @param msg 返回内容
	 * @return 警告消息
	 */
	public static Result warn(String msg) {
		return Result.warn(msg, null);
	}

	/**
	 * 返回警告消息
	 *
	 * @param msg  返回内容
	 * @param data 数据对象
	 * @return 警告消息
	 */
	public static Result warn(String msg, Object data) {
		return new Result(WARN_CODE, msg, data);
	}

	/**
	 * 返回错误消息
	 * 
	 * @return 错误消息
	 */
	public static Result error() {
		return Result.error("操作失败");
	}

	/**
	 * 返回错误消息
	 * 
	 * @param msg 返回内容
	 * @return 错误消息
	 */
	public static Result error(String msg) {
		return Result.error(msg, null);
	}

	/**
	 * 返回错误消息
	 * 
	 * @param msg  返回内容
	 * @param data 数据对象
	 * @return 错误消息
	 */
	public static Result error(String msg, Object data) {
		return new Result(ERROR_CODE, msg, data);
	}

	/**
	 * 返回错误消息
	 * 
	 * @param code 状态码
	 * @param msg  返回内容
	 * @return 错误消息
	 */
	public static Result error(int code, String msg) {
		return new Result(code, msg, null);
	}

	/**
	 * 方便链式调用
	 *
	 * @param key   键
	 * @param value 值
	 * @return 数据对象
	 */
	@Override
	public Result put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
