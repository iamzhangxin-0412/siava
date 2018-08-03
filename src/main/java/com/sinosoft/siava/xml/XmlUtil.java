package com.sinosoft.siava.xml;

import com.sinosoft.siava.annotation.XmlNodeName;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * XML数据转换工具
 *
 * @author ZhangXin
 */
public class XmlUtil {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void toElement(Object object, Element root) {
        if (object != null) {
            if (object instanceof Number || object instanceof Boolean || object instanceof String) {
                root.setText(object.toString());
            } else if (object instanceof Map) {
                mapToElement((Map) object, root);
            } else if (object instanceof Collection) {
                collToElement((Collection) object, root);
            } else {
                pojoToElement(object, root);
            }
        } else {
            root.setText("");
        }
    }

    /**
     * list中存放的数据类型有基本类型、用户自定对象 map、list
     *
     * @param coll
     * @param root
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void collToElement(Collection<?> coll, Element root) {
        for (Object value : coll) {
            if (coll == value) {
                continue;
            }
            if (value instanceof Number || value instanceof Boolean || value instanceof String) {
                Class<?> classes = value.getClass();
                String objName = classes.getName();
                String elementName = objName.substring(objName.lastIndexOf(".") + 1, objName.length());
                Element elementOfObject = root.addElement(elementName);
                elementOfObject.setText(value.toString());
            } else if (value instanceof Map) {
                Class<?> classes = value.getClass();
                String objName = classes.getName();
                String elementName = objName.substring(objName.lastIndexOf(".") + 1, objName.length());
                Element elementOfObject = root.addElement(elementName);
                mapToElement((Map) value, elementOfObject);
            } else if (value instanceof Collection) {
                Class<?> classes = value.getClass();
                String objName = classes.getName();
                String elementName = objName.substring(objName.lastIndexOf(".") + 1, objName.length());
                Element elementOfObject = root.addElement(elementName);
                collToElement((Collection) value, elementOfObject);
            } else {
                toElement(value, root);
            }

        }
    };

    /**
     * map中存放的数据类型有基本类型、用户自定对象 map、list
     *
     * @param map
     * @param root
     */

    @SuppressWarnings("rawtypes")
    private static void mapToElement(Map<String, Object> map, Element root) {
        for (Iterator<?> it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            if (null == name)
                continue;
            Object value = entry.getValue();
            if (value == map) {

            }
            Element elementValue = root.addElement(name);
            toElement(value, elementValue);
        }
    }

    /**
     *
     * @param obj
     * @param root
     */
    private static void pojoToElement(Object obj, Element root) {
        Class<?> classes = obj.getClass();
//        String objName = classes.getName();
//        String elementName = objName.substring(objName.lastIndexOf(".") + 1, objName.length());
//        /** 该类为一个节点 */
//        Element elementOfObject = null;
        //        if(!root.getName().equals(elementName)){
//            elementOfObject = root.addElement(elementName);
//        }else{
//            elementOfObject = root;
//        }
        Field[] fields = classes.getDeclaredFields();
        for (Field f : fields) {
            if (Modifier.isStatic(f.getModifiers()))
                continue;
            String name = f.getName();
            if (f.isAnnotationPresent(XmlNodeName.class)) {
                XmlNodeName xmlNameNode = f.getAnnotation(XmlNodeName.class);
                name = xmlNameNode.name();
            }
            f.setAccessible(true);
            Object value = null;
            try {
                value = f.get(obj);
            } catch (Exception ignored) {
            }
            Element elementValue = root.addElement(name);
            toElement(value, elementValue);
        }
    }

    public static String createXmlDocument(Object obj) {
        return createXmlDocument(obj, "UTF-8");
    }

    public static String createXmlDocument(Object obj, String encode) {
        Class<?> classes = obj.getClass();
        String objName = classes.getName();
        String elementName = objName.substring(objName.lastIndexOf(".") + 1, objName.length());
        return createXmlDocument(obj,elementName, encode);
    }

    private static String createXmlDocument(Object obj, String xmlRootName, String encode) {
        Document xmlDoc = DocumentHelper.createDocument();
        Element root = xmlDoc.addElement(xmlRootName);
        toElement(obj, root);
        // 设置XML文档格式
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();
        // 设置XML编码方式,即是用指定的编码方式保存XML文档到字符串(String),这里也可以指定为GBK或是ISO8859-1
        outputFormat.setEncoding(encode);
        //outputFormat.setSuppressDeclaration(true); //是否生产xml头
        outputFormat.setIndent(true); //设置是否缩进
        outputFormat.setIndent("    "); //以四个空格方式实现缩进
        outputFormat.setNewlines(true); //设置是否换行

        try {
            // stringWriter字符串是用来保存XML文档的
            StringWriter stringWriter = new StringWriter();
            // xmlWriter是用来把XML文档写入字符串的(工具)
            XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);

            // 把创建好的XML文档写入字符串
            xmlWriter.write(xmlDoc);

            xmlWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xmlDoc.asXML();
    }

}