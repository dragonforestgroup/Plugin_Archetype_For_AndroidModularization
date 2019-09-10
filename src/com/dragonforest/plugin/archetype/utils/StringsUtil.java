package com.dragonforest.plugin.archetype.utils;

import com.dragonforest.plugin.archetype.model.AboutModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class StringsUtil {

    private static Document loadDocument(String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File(path));
            return document;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 修改Strings.xml 文件
     * 修改内容：
     * 1.app名字
     * 2.关于信息
     *
     * @param appName
     * @param aboutModel
     * @return
     */
    public static boolean modifyStrings(String appName, AboutModel aboutModel,String stringsPath) {
        Document document = loadDocument(stringsPath);
        if (document == null)
            return false;
        // 修改app名字
        Element resourceElement = document.getDocumentElement();
        NodeList nodeList = resourceElement.getElementsByTagName("string");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element item = (Element) nodeList.item(i);
            if (item != null) {
                if (item.hasAttribute("name")) {
                    String attrName = item.getAttribute("name");
                    if ("app_name".equals(attrName)) {
                        String appName1 = item.getFirstChild().getNodeValue();
                        item.getFirstChild().setNodeValue(appName);
                        break;
                    }
                }
            }
        }
        // 添加about信息
        Element companyNameElement = document.createElement("string");
        companyNameElement.appendChild(document.createTextNode(aboutModel.getCompanyName()));
        companyNameElement.setAttribute("name", "app_build_company_name");
        resourceElement.appendChild(companyNameElement);
        Element companyPhoneElement = document.createElement("string");
        companyPhoneElement.appendChild(document.createTextNode(aboutModel.getCompanyPhone()));
        companyPhoneElement.setAttribute("name", "app_build_company_phone");
        resourceElement.appendChild(companyPhoneElement);
        Element companyUrlElement = document.createElement("string");
        companyUrlElement.appendChild(document.createTextNode(aboutModel.getCompanyUrl()));
        companyUrlElement.setAttribute("name", "app_build_company_url_name");
        resourceElement.appendChild(companyUrlElement);
        Element supportElement = document.createElement("string");
        supportElement.appendChild(document.createTextNode(aboutModel.getSupport()));
        supportElement.setAttribute("name", "app_support_company_name");
        resourceElement.appendChild(supportElement);
        Element supportPhoneElement = document.createElement("string");
        supportPhoneElement.appendChild(document.createTextNode(aboutModel.getSupportPhone()));
        supportPhoneElement.setAttribute("name", "app_support_company_phone");
        resourceElement.appendChild(supportPhoneElement);

        // 保存信息
        return saveToXmlFile(document,stringsPath);
    }

    /**
     * 保存当前的document到xml文件中
     *
     * @return
     */
    private static boolean saveToXmlFile(Document document,String savePath) {
        /*输出文件到XML中*/
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            /*转换器*/
            Transformer tf = transformerFactory.newTransformer();

            /**设置输出性质  Provides string constants that can be used to set
             * output properties for a Transformer, or to retrieve output
             *  properties from a Transformer or Templates object.
             *  提供字符串常数被用去设置输出属性从转换器中，或者去恢复输出属性从转换器或模版对象中。
             *
             *  */
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            /*输出文件到XML中*/
            tf.transform(new DOMSource(document),
                    new StreamResult(new File(savePath)));
            return true;
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return false;
    }
}
