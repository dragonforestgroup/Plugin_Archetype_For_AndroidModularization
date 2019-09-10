package com.dragonforest.plugin.archetype.utils;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.*;

public class GradleUtil {

    String configPath;

    public GradleUtil(String configPath) {
        this.configPath = configPath;
    }

    /**
     * 修改config.gradle
     * 修改内容：
     * 1.applicationId
     * 2.appname
     * 3.appId
     *
     * @param applicationId
     * @param appName
     * @param appId
     * @return
     */
    // TODO: 2019/6/12  待实现 需要按照config.gradle的格式来搞... 
    public boolean modifiedGradleConfig(String applicationId, String appName, String appId) {

        String configContent = readConfigGradle(configPath);
        if (configContent == null || configContent.equals("")) {
            return false;
        }
        // 修改applicationId
        String applicationIdOld = findPropertyValue(configContent, "applicationId");
        if (applicationIdOld != null) {
            configContent = configContent.replace(applicationIdOld, applicationId);
        }
        // 修改appname
        String appNameOld = findPropertyValue(configContent, "appName");
        if (applicationIdOld != null) {
            configContent = configContent.replace(appNameOld, appName);
        }
        // 修改appId
        String appIdOld = findPropertyValue(configContent, "appId");
        if (appIdOld != null) {
            configContent = configContent.replace(appIdOld, appId);
        }
        return saveToFile(configContent);
    }

    /**
     * 读取文件内容
     *
     * @param configPath
     * @return
     */
    private String readConfigGradle(String configPath) {
        File file = new File(configPath);
        if (!file.exists()) {
            System.out.println("文件不存在！");
            return "";
        }
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                sb.append(strLine);
                sb.append("\n");
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 保存到文件
     * 覆盖写入
     *
     * @param content
     * @return
     */
    private boolean saveToFile(String content) {
        Boolean isSaved = false;
        File file = new File(configPath);
        if (!file.exists()) {
            System.out.println("文件不存在！");
            return false;
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            bw.write(content);
            bw.flush();
            isSaved = true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSaved;
    }

    /**
     * 通过属性名查找到属性值
     *
     * @param content
     * @param propertyName
     * @return
     */
    private String findPropertyValue(String content, String propertyName) {
        String value = "";
        int beginIndex = content.indexOf(propertyName);
        if (beginIndex == -1) {
            //没找到
            return null;
        }
        String tempStr = content.substring(beginIndex);
        String tempStr2 = tempStr.substring(tempStr.indexOf("'") + 1);
        value = tempStr2.substring(0, tempStr2.indexOf("'"));
        return value;
    }


    //========================================================

    static String tempelateBuildGradleStr = "if(rootProject.ext.moduleSetting.%s.toBoolean()){\n" +
            "    apply plugin: 'com.android.application'\n" +
            "}else{\n" +
            "    apply plugin: 'com.android.library'\n" +
            "}\n" +
            "\n" +
            "android {\n" +
            "    compileSdkVersion  rootProject.ext.android.compileSdkVersion.toInteger()\n" +
            "\n" +
            "    defaultConfig {\n" +
            "        if(rootProject.ext.moduleSetting.%s.toBoolean()){\n" +
            "            applicationId \"%s\"\n" +
            "        }\n" +
            "        minSdkVersion rootProject.ext.android.minSdkVersion.toInteger()\n" +
            "        targetSdkVersion rootProject.ext.android.targetSdkVersion.toInteger()\n" +
            "        versionCode rootProject.ext.android.versionCode.toInteger()\n" +
            "        versionName rootProject.ext.android.versionName\n" +
            "        testInstrumentationRunner \"android.support.test.runner.AndroidJUnitRunner\"\n" +
            "\n" +
            "    }\n" +
            "\n" +
            "    buildTypes {\n" +
            "        release {\n" +
            "            minifyEnabled false\n" +
            "            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    // Manifest配置\n" +
            "    sourceSets {\n" +
            "        main {\n" +
            "            if (rootProject.ext.moduleSetting.%s.toBoolean()) {\n" +
            "                manifest.srcFile 'src/main/AndroidManifest.xml'\n" +
            "            } else {\n" +
            "                manifest.srcFile 'src/main/libmanifest/AndroidManifest.xml'\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "dependencies {\n" +
            "    implementation fileTree(include: ['*.jar'], dir: 'libs')\n" +
            "    implementation rootProject.ext.dependencies.appcompatV7\n" +
            "    implementation rootProject.ext.dependencies.design\n" +
            "    implementation rootProject.ext.dependencies.constraint\n" +
            "    testImplementation 'junit:junit:4.12'\n" +
            "    androidTestImplementation 'com.android.support.test:runner:1.0.2'\n" +
            "    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'\n" +
            "\n" +
            "}\n";

    /**
     * 创建自带组件化的build.gradle
     *
     * @param applicationId
     * @param variable
     */
    public static boolean createArchetypeBuildGradle(String applicationId, String variable, String buildGradlePath) {
        String buildGradleStr = String.format(tempelateBuildGradleStr, variable, variable, applicationId, variable);
        return FileUtil.writeFile(buildGradleStr, buildGradlePath, false);
    }


    static String tempelateConfigGradleStr = "ext {\n" +
            "    //默认属性\n" +
            "    android = [\n" +
            "            compileSdkVersion: 28,\n" +
            "            minSdkVersion    : 17,\n" +
            "            targetSdkVersion : 28,\n" +
            "            versionCode      : 1,\n" +
            "            versionName      : 'v1.1'\n" +
            "    ]\n" +
            "\n" +
            "    defaultConfig = [\n" +
            "            versionCode: 1,\n" +
            "            versionName: '1.1',\n" +
            "    ]\n" +
            "\n" +
            "    dependencies = [\n" +
            "            appcompatV7: 'com.android.support:appcompat-v7:28.0.0',\n" +
            "            design     : 'com.android.support:design:28.0.0',\n" +
            "            constraint : 'com.android.support.constraint:constraint-layout:1.1.3'\n" +
            "    ]\n" +
            "\n" +
            "    signingConfigs = [\n" +
            "            keyAlias     : 'xxxxx',\n" +
            "            keyPassword  : 'xxxxx',\n" +
            "            storeFile    : '../xxxx.jks',\n" +
            "            storePassword: 'xxxxx'\n" +
            "    ]\n" +
            "\n" +
            "    outApkInfo = [\n" +
            "            appName: '%s'\n" +
            "    ]\n" +
            "\n" +
            "    //BuildConfig属性\n" +
            "    //公共\n" +
            "    commonInfo = [\n" +
            "            appId         : '\"xxxxx\"',\n" +
            "            cacheDir      : '\"/mnt/sdcard/crash/%s/\"',\n" +
            "            apkDownloadDir: '\"/mnt/sdcard/DYYP/ISERVERAS/PREINST/\"'\n" +
            "    ]\n" +
            "\n" +
            "    // 组件化设置\n" +
            "    moduleSetting = [\n" +
            "    ]\n" +
            "    //个性化\n" +
            "    personalInfo = [\n" +
            "\n" +
            "    ]\n" +
            "}";

    /**
     * 创建项目配置文件config.gradle
     *
     * @param appName
     */
    public static boolean createArchetypeConfigGradle(String appName,String configGradlePath) {
        String configGradleStr = String.format(tempelateConfigGradleStr, appName,appName);
        return FileUtil.writeFile(configGradleStr, configGradlePath, false);
    }

}
