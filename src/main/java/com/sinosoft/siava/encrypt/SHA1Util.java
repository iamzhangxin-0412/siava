package com.sinosoft.siava.encrypt;

import com.sinosoft.siava.exception.SignatureCannotBeNullException;

import java.util.Arrays;

/**
 * SHA1加密工具
 * @author ZhangXin
 *
 */
public class SHA1Util {
	
	/**
	 * SHA1加密，会将传入的参数，按照传入的值决定是否按照字典顺序排序，然后采用SHA1的加密算法进行加密
	 * @param str 要加密的内容
	 * @param flag 是否按照字典顺序进行排序
	 * @return 加密后的字符串
	 */
	public static String encode(String[] str,boolean flag)
    {
		if (flag) {
			// 字典序排序
			Arrays.sort(str);
		}
        String bigStr = "";
        for (String message:str) {
			bigStr += message;
		}
        // SHA1加密
        return SignUtil.encode("SHA1", bigStr).toLowerCase(); 
    }
	
	/**
	 * SHA1加密，会将传入的参数，按照传入的值决定是否按照字典顺序排序，然后采用SHA1的加密算法进行加密。然后与signature做比较
	 * @param str 要加密的内容
	 * @param flag 是否按照字典顺序进行排序
	 * @param signature 要比对的内容
	 * @return 比对结果
	 */
	public static boolean encode(String[] str,boolean flag,String signature)
    {
		if (signature==null||"".equals(signature))
		{
			throw new SignatureCannotBeNullException("要比对的内容不得为空");
		}
        return encode(str,flag).equals(signature);
    }
}
