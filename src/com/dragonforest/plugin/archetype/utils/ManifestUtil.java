package com.dragonforest.plugin.archetype.utils;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class ManifestUtil {

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
     * 从AndroidManifest.xml中读取包名
     *
     * @return
     */
    public static String readPackageNameFromManifest(String path) {
        Document document = loadDocument(path);
        if (document == null)
            return null;
        Element manifestElement = document.getDocumentElement();
        String packageName = manifestElement.getAttribute("package");
        return packageName;
    }

    /**
     * 修改AndroidManifest.xml文件
     * 修改内容：
     * 1.包名
     *
     * @param packageName
     * @return
     */
    public static boolean modifyManifest(String packageName, String path) {
        Document document = loadDocument(path);
        if (document == null)
            return false;
        // 修改packageName
        Element manifestElement = document.getDocumentElement();
        manifestElement.setAttribute("package", packageName);
        //...
        return saveToXmlFile(document, path);
    }

    /**
     * 转化为组件化使用的AndroidManifest
     *
     * @param oldManifestPath 原来的Manifest文件path
     * @return
     */
    public static boolean covertToLibManifest(String oldManifestPath, String libManifestPath) {
        Document document = loadDocument(oldManifestPath);
        if (document == null)
            return false;
        NodeList applicationNodeList = document.getElementsByTagName("application");
        if (applicationNodeList.getLength() != 1) {
            return false;
        }
        Element applicationItem =  (Element)applicationNodeList.item(0);

        applicationItem.removeAttribute(("android:allowBackup"));
        applicationItem.removeAttribute("android:allowBackup");
        applicationItem.removeAttribute("android:icon");
        applicationItem.removeAttribute("android:label");
        applicationItem.removeAttribute("android:roundIcon");
        applicationItem.removeAttribute("android:supportsRtl");
        applicationItem.removeAttribute("android:theme");

        return saveToXmlFile(document, libManifestPath);
    }


    /**
     * 保存当前的document到xml文件中
     *
     * @return
     */
    private static boolean saveToXmlFile(Document document, String savePath) {
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
