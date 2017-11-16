package com.sinosoft.siava.xml;

import java.lang.reflect.Field;
import java.util.Date;

import org.dom4j.Element;

/**
 * XML数据转换工具
 * @author ZhangXin
 *
 */
public class XmlUtil
{
	/**
	 * json 数据转换对象
	 * 
	 * @param Element 要转换的Element数据
	 * @param pojo 要转换的目标对象类型
	 * @return 转换的目标对象
	 * @throws Exception 转换失败
	 */
	@SuppressWarnings("rawtypes")
	public static Object fromXmlToBean(Element rootElt, Class pojo) throws Exception
	{
		// 首先得到pojo所定义的字段
		Field[] fields = pojo.getDeclaredFields();
		// 根据传入的Class动态生成pojo对象
		Object obj = pojo.newInstance();
		for (Field field : fields)
		{
			// 设置字段可访问（必须，否则报错）
			field.setAccessible(true);
			// 得到字段的属性名
			String name = field.getName();
			// 这一段的作用是如果字段在Element中不存在会抛出异常，如果出异常，则跳过。
			try
			{
				rootElt.elementTextTrim(name);
			}
			catch (Exception ex)
			{
				continue;
			}
			if (rootElt.elementTextTrim(name) != null && !"".equals(rootElt.elementTextTrim(name)))
			{
				// 根据字段的类型将值转化为相应的类型，并设置到生成的对象中。
				if (field.getType().equals(Long.class) || field.getType().equals(long.class))
				{
					field.set(obj, Long.parseLong(rootElt.elementTextTrim(name)));
				}
				else if (field.getType().equals(String.class))
				{
					field.set(obj, rootElt.elementTextTrim(name));
				}
				else if (field.getType().equals(Double.class) || field.getType().equals(double.class))
				{
					field.set(obj, Double.parseDouble(rootElt.elementTextTrim(name)));
				}
				else if (field.getType().equals(Integer.class) || field.getType().equals(int.class))
				{
					field.set(obj, Integer.parseInt(rootElt.elementTextTrim(name)));
				}
				else if (field.getType().equals(Date.class))
				{
					field.set(obj, Date.parse(rootElt.elementTextTrim(name)));
				}
				else
				{
					continue;
				}
			}
		}
		return obj;
	}
}