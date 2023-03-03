package io.gamioo.common.util;

import com.alibaba.fastjson.JSONObject;
import io.gamioo.common.exception.ServiceException;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class JSONUtils {

    public static JSONObject loadFromXMLFile(String fileName) throws ServiceException {
        JSONObject ret = new JSONObject();
        Document document = XMLUtil.loadFromFile(fileName);
        Element root = document.getRootElement();
        JsonXmlUtil.xml2Json(root, ret);
        return ret;
    }
}
