package io.gamioo.common.util;

import io.gamioo.common.exception.ServiceException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.rmi.ServerException;


/**
 * @author Guilong Jiang
 */
public class XMLUtil {
    public static boolean saveXML(Element ele, String sFilePathName) throws ServerException {
        return saveXML(ele, sFilePathName, "UTF-8");
    }

    /**
     * save xml to file with special encode
     *
     * @param ele           元素
     * @param sFilePathName 文件路径
     * @param encode        XMLUtil.ENCODE_UTF_8 or XMLUtil.ENCODE_GBK, default is
     *                      former
     * @return 保存成功与否
     */
    public static boolean saveXML(Element ele, String sFilePathName, String encode) throws ServiceException {

        Document dom = ele.getDocument();
        if (dom == null) {
            dom = DocumentHelper.createDocument(ele);
        }
        return saveXML(dom, sFilePathName, encode);
    }

    /**
     * save xml to file with special encode
     *
     * @param dom           节点
     * @param sFilePathName 文件路径
     * @param encode        XMLUtil.ENCODE_UTF_8 or XMLUtil.ENCODE_GBK, default is
     *                      former
     * @return 保存成功与否
     */
    public static boolean saveXML(Document dom, String sFilePathName, String encode) throws ServiceException {

        File file = new File(sFilePathName);
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            FileOutputStream out = new FileOutputStream(file);
            // if(!encode.equals(ENCODE_UTF_8)){
            format.setEncoding(encode);
            XMLWriter xmlWriter = new XMLWriter(out, format);
            xmlWriter.write(dom);
            xmlWriter.flush();
            xmlWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("XUT-PSX10003", "write element to file error:{} {}", sFilePathName, e.getMessage());
        }
        return true;
    }

    public static Document loadFromFile(String filePathName) throws ServiceException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePathName);
        return loadDocument(is);
    }

    public static Document loadDocument(InputStream is) throws ServiceException {
        SAXReader rd = new SAXReader();
        Document document = null;
        try {
            document = rd.read(is);
        } catch (Exception e) {
            throw new ServiceException("parsing document failed:" + e.getMessage());
        }
        return document;
    }

    public static int attributeValueInt(Element element, String attr) throws ServiceException {
        if (element == null) {
            throw new ServiceException("element == null");
        }
        String value = element.attributeValue(attr);
        if (value == null) {
            throw new ServiceException("缺少属性{}: {}", attr, element.asXML());
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ServiceException("属性{}不是数值类型: {}", attr, element.asXML());
        }
    }

    public static float attributeValueFloat(Element element, String attr) throws ServiceException {
        if (element == null) {
            throw new ServiceException("element == null");
        }
        String value = element.attributeValue(attr);
        if (value == null) {
            throw new ServiceException("缺少属性{}: {}", attr, element.asXML());
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new ServiceException("属性{}不是数值类型: {}", attr, element.asXML());
        }
    }

    public static int attributeValueInt(Element element, String attr, int defaultValue) throws ServiceException {
        if (element == null) {
            throw new ServiceException("element == null");
        }
        String value = element.attributeValue(attr);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ServiceException("属性{}不是数值类型: {}", attr, element.asXML());
        }
    }

    public static String attributeValueString(Element element, String attr) throws ServiceException {
        if (element == null) {
            throw new ServiceException("element == null");
        }
        String value = element.attributeValue(attr);
        if (value == null) {
            throw new ServiceException("缺少属性{}: element={}", attr, element.asXML());
        }
        return value;
    }

    public static String attributeValueString(Element element, String attr, String defaultValue) throws ServiceException {
        if (element == null) {
            throw new ServiceException("element == null");
        }
        String value = element.attributeValue(attr);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static boolean attributeValueBoolean(Element element, String attr, boolean defaultValue) throws ServiceException {
        if (element == null) {
            throw new ServiceException("element == null");
        }
        String value = element.attributeValue(attr);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(value);
        } catch (NumberFormatException e) {
            throw new ServiceException("属性{}不是布尔类型: {}", attr, element.asXML());
        }
    }

    public static boolean attributeValueBoolean(Element element, String attr) throws ServiceException {
        if (element == null) {
            throw new ServiceException("element == null");
        }
        String value = element.attributeValue(attr);
        if (value == null) {
            throw new ServiceException("缺少属性{}: {}", attr, element.asXML());
        }
        try {
            return Boolean.parseBoolean(value);
        } catch (NumberFormatException e) {
            throw new ServiceException("属性{}不是布尔类型: {}", attr, element.asXML());
        }
    }

    public static Element subElement(Element parent, String name) throws ServiceException {
        if (parent == null) {
            throw new ServiceException("parent == null");
        }
        Element result = parent.element(name);
        if (result == null) {
            throw new ServiceException("找不到{}节点的子节点{}", parent.getName(), name);
        }
        return result;
    }

    public static Element parseText(String content) {
        Element ret = null;
        try {
            Document doc = DocumentHelper.parseText(content);
            ret = doc.getRootElement(); // 获取根节点
        } catch (DocumentException e) {
            throw new ServiceException("parsing content failed:{}", e.getMessage());
        }
        return ret;
    }
}
