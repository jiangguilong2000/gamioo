package io.gamioo.core.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * xml 和 json的转换器
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class JsonXmlUtil {
    private static final Logger logger = LogManager.getLogger(JsonXmlUtil.class);
    private static DocumentBuilderFactory documentBuilderFactory;

    static {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        //XML外部实体注入漏洞 https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=23_5
        try {
            // This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all
            // XML entity attacks are prevented
            // Xerces 2 only -
            // http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
            String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            documentBuilderFactory.setFeature(FEATURE, true);

            // If you can't completely disable DTDs, then at least do the following:
            // Xerces 1 -
            // http://xerces.apache.org/xerces-j/features.html#external-general-entities
            // Xerces 2 -
            // http://xerces.apache.org/xerces2-j/features.html#external-general-entities
            // JDK7+ - http://xml.org/sax/features/external-general-entities
            FEATURE = "http://xml.org/sax/features/external-general-entities";
            documentBuilderFactory.setFeature(FEATURE, false);

            // Xerces 1 -
            // http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
            // Xerces 2 -
            // http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
            // JDK7+ - http://xml.org/sax/features/external-parameter-entities
            FEATURE = "http://xml.org/sax/features/external-parameter-entities";
            documentBuilderFactory.setFeature(FEATURE, false);

            // Disable external DTDs as well
            FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            documentBuilderFactory.setFeature(FEATURE, false);

            // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and
            // Entity Attacks"
            documentBuilderFactory.setXIncludeAware(false);
            documentBuilderFactory.setExpandEntityReferences(false);

            // And, per Timothy Morgan: "If for some reason support for inline DOCTYPEs are
            // a requirement, then
            // ensure the entity settings are disabled (as shown above) and beware that SSRF
            // attacks
            // (http://cwe.mitre.org/data/definitions/918.html) and denial
            // of service attacks (such as billion laughs or decompression bombs via "jar:")
            // are a risk."
            // remaining parser logic
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * xml转json
     *
     * @param element
     * @param json
     */
    public static void xml2Json(Element element, JSONObject json) {
        //如果是属性


        for (Attribute e : element.attributes()) {
            json.put(e.getName(), e.getValue());

        }
        List<Element> list = element.elements();

        for (Element e : list) {
            JSONObject object = new JSONObject();
            xml2Json(e, object);
            Object obj = json.get(e.getName());
            if (obj == null) {
                json.put(e.getName(), object);
            } else {
                JSONArray array = null;
                if (obj instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) obj;
                    json.remove(e.getName());
                    array = new JSONArray();
                    array.add(jsonObject);
                    array.add(object);

                } else if (obj instanceof JSONArray) {
                    array = (JSONArray) obj;
                    array.add(object);
                }
                json.put(e.getName(), array);
            }
        }


    }

    public static boolean isEmpty(String str) {

        if (str == null || str.trim().isEmpty() || "null".equals(str)) {
            return true;
        }
        return false;
    }


    /**
     * 将Map转换为XML格式的字符串
     *
     * @param store Map类型数据
     * @return XML格式的字符串
     * @throws Exception
     */
    public static String mapToXml(Map<String, Object> store) {
        try {

            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            org.w3c.dom.Document document = documentBuilder.newDocument();
            org.w3c.dom.Element root = document.createElement("xml");
            document.appendChild(root);
            for (String key : store.keySet()) {
                Object obj = store.get(key);
                String value = "";
                if (obj != null) {
                    value = String.valueOf(obj).trim();
                }
                org.w3c.dom.Element filed = document.createElement(key);
                filed.appendChild(document.createTextNode(value));
                root.appendChild(filed);
            }
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(document);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            String output = writer.getBuffer().toString(); // .replaceAll("\n|\r",
            writer.close();
            return output;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * XML格式字符串转换为Map
     *
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String strXML) {
        try {
            Map<String, String> data = new HashMap<>();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            stream.close();
            return data;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
}
