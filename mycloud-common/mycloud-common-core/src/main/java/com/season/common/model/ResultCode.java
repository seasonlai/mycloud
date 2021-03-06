package com.season.common.model;

/**
 * 基础返回码，具体业务返回码可继承ResultCode
 * 
 */
public class ResultCode {

	public final static int SUCCESS = 0;// 成功

	// 通用错误以9开头
	public final static int ERROR = 9999;// 未知错误
	public final static int APPLICATION_ERROR = 9000;// 应用级错误
	public final static int VALIDATE_ERROR = 9001;// 参数验证错误
	public final static int SERVICE_ERROR = 9002;// 业务逻辑验证错误
	public final static int CACHE_ERROR = 9003;// 缓存访问错误
	public final static int DAO_ERROR = 9004;// 数据访问错误
	public final static int IO_ERROR = 9005;// IO错误

	// 具体错误
	public final static int CAPTCHA_ERROR = 1000;// 验证码错误
}
