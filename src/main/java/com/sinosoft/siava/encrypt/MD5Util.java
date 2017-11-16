package com.sinosoft.siava.encrypt;

import com.sinosoft.siava.exception.SignatureCannotBeNullException;

/**
 * MD5加密工具类
 * 
 * @author ZhangXin
 *
 */
public class MD5Util {
	/**
	 * MD5加密方法
	 * 
	 * @param message 要加密的字符串
	 * @return 加密后的参数
	 */
	public static String encode(String message) {

		return SignUtil.encode("MD5", message);
	}

	/**
	 * MD5加密，将传入的字符串采用MD5的加密算法进行加密。然后与signature做比较
	 * 
	 * @param str 要加密的内容
	 * @param signature 要比对的内容
	 * @return 比对结果
	 */
	public static boolean encode(String str, String signature) {
		if (signature==null||"".equals(signature))
		{
			throw new SignatureCannotBeNullException("要比对的内容不得为空");
		}
		return encode(str).equals(signature);
	}
}
