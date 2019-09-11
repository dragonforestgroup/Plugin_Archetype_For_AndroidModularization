package com.dragonforest.plugin.archetype.utils;

import com.dragonforest.plugin.archetype.model.AboutModel;
import com.dragonforest.plugin.archetype.model.AppModel;
import com.dragonforest.plugin.archetype.model.Result;
import com.intellij.openapi.ui.Messages;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

public class ModifyManager {

    /**
     * 修改模块配置
     * // 1.修改包名
     * // 2.修改applicationId
     * // 3.修改build.gradle中的配置
     * 1).sdk和依赖库版本统一
     * 2).使用变量控制 应用类型，manifest,aplication
     * // 4.添加libmanifest和下面的manifest.xml
     * // 5.settings.gradle中添加本模块
     * // 6.gradle.properties 或config.gradle 中添加变量
     *
     * @param localProjectPath
     * @param appModel
     * @return
     */
    public static Result modifyModuleInfo(String localProjectPath, AppModel appModel) {
        Result result = new Result();
        // 第1步，修改包名
        // 从AndroidManifest文件中读取原有包名
        String manifestPath = localProjectPath
                + File.separator
                + appModel.getAppName()
                + File.separator
                + "src"
                + File.separator
                + "main"
                + File.separator
                + "AndroidManifest.xml";
        String packageNameOld = ManifestUtil.readPackageNameFromManifest(manifestPath);
        if (packageNameOld == null) {
            result = Result.errorResult("获取的包名为空!,请检查项目中Manifest文件是否存在和格式是否正确！！！！");
            return result;
        }

        // 创建新的包名临时目录（必须是临时目录，避免包名路径可能和原有包名路径重合），并将原包名下的文件拷贝过去
        String mainPackageDir = localProjectPath
                + File.separator
                + appModel.getAppName()
                + File.separator
                + "src"
                + File.separator
                + "main"
                + File.separator
                + "java"; //包名基本目录
        String tempPackageDir = localProjectPath
                + File.separator
                + "temp"; // 临时目录，包含所有原有包名下的内容
        String[] splitOldPackageName = packageNameOld.split("\\.");
        String innerPackageDirOld = "";
        for (int i = 0; i < splitOldPackageName.length; i++) {
            innerPackageDirOld += splitOldPackageName[i] + File.separator;
        }
        String packagePathOld = mainPackageDir + File.separator + innerPackageDirOld;
        MessageUtil.debugMessage("获取包名路径完成", "old包名路径为：" + packagePathOld, Messages.getInformationIcon());
        boolean isPackageDirOldCopyed = FileUtil.copyDir(packagePathOld, tempPackageDir);
        if (!isPackageDirOldCopyed) {
            result = Result.errorResult("拷贝包目录失败");
            return result;
        }
        MessageUtil.debugMessage("拷贝成功：", "拷贝成功", Messages.getInformationIcon());

        // 遍历新包名下的所有文件，查找替换包名字符串
        Collection<File> javaFiles = FileUtil.listFiles(tempPackageDir, new String[]{"java"}, true);
        Iterator<File> iterator = javaFiles.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            FileUtil.readAndReplace(file.getAbsolutePath(), packageNameOld, appModel.getPackageName());
        }

        // 删除main/java原有包名目录，并将临时目录中的文件拷贝过去,最后删除临时目录
        boolean isMainPackageDirCleaned = FileUtil.cleanDir(mainPackageDir);
        if (!isMainPackageDirCleaned) {
            result = Result.errorResult("main/java 目录旧包名结构清理失败");
            return result;
        }
        String[] splitNewPackageName = appModel.getPackageName().split("\\.");
        String innerPackageDirNew = "";
        for (int i = 0; i < splitNewPackageName.length; i++) {
            innerPackageDirNew += splitNewPackageName[i] + File.separator;
        }
        String packagePathNew = mainPackageDir + File.separator + innerPackageDirNew;
        boolean isTempPackageDirNewCopyed = FileUtil.copyDir(tempPackageDir + File.separator, packagePathNew);
        if (!isTempPackageDirNewCopyed) {
            result = Result.errorResult("临时目录拷贝失败");
            return result;
        }
        FileUtil.deleteDir(tempPackageDir);

        // 删除test/java 下原有目录，并创建新的包名目录
        String testPackageDir = localProjectPath
                + File.separator
                + appModel.getAppName()
                + File.separator
                + "src"
                + File.separator
                + "test"
                + File.separator
                + "java"; //包名基本目录
        boolean isTestPackageDirCleaned = FileUtil.cleanDir(testPackageDir);
        if (!isTestPackageDirCleaned) {
            result = Result.errorResult("test/java 目录旧包名结构清理失败！");
            return result;
        }
        String testPackagePathNew = testPackageDir + File.separator + innerPackageDirNew;
        boolean isMkTestPackage = FileUtil.mkDir(testPackagePathNew);
        if (!isMkTestPackage) {
            result = Result.errorResult("test/java 目录新包名结构创建失败！");
            return result;
        }

        // 删除androidTest/java 下原有目录，并创建新的包名目录
        String androidTestPackageDir = localProjectPath
                + File.separator
                + appModel.getAppName()
                + File.separator
                + "src"
                + File.separator
                + "androidTest"
                + File.separator
                + "java"; //包名基本目录
        boolean isAndroidTestPackageDirCleaned = FileUtil.cleanDir(androidTestPackageDir);
        if (!isAndroidTestPackageDirCleaned) {
            result = Result.errorResult("test/java 目录旧包名结构清理失败！");
            return result;
        }
        String androidTestPackagePathNew = androidTestPackageDir + File.separator + innerPackageDirNew;
        boolean isMkAndroidTestPackagePath = FileUtil.mkDir(androidTestPackagePathNew);
        if (!isMkAndroidTestPackagePath) {
            result = Result.errorResult("androidTest/java 目录新包名结构创建失败！");
            return result;
        }
        // 替换AndroidManifest.xml中的包名
        boolean isManifestModified = ManifestUtil.modifyManifest(appModel.getPackageName(), manifestPath);
        if (!isManifestModified) {
            result = Result.errorResult("Manifest.xml修改出错！Manifest.xml是否存在！");
            return result;
        }

        // 第2步，修改appName
        // 修改strings.xml
        String StringsXmlPath = localProjectPath
                + File.separator
                + appModel.getAppName()
                + File.separator
                + "src"
                + File.separator
                + "main"
                + File.separator
                + "res"
                + File.separator
                + "values"
                + File.separator
                + "strings.xml";
        AboutModel aboutModel = new AboutModel("", "", "", "", "");
        boolean isStringsModified = StringsUtil.modifyStrings(appModel.getAppName(), aboutModel, StringsXmlPath);
        if (!isStringsModified) {
            result = Result.errorResult("Strings.xml文件修改失败！请检查strings.xml是否配置正常！");
            return result;
        }

        // 第3步，修改build.gradle
        String variable = appModel.getAppName() + "RunAlone"; //组件化配置变量
        boolean isBuildGradleModified = GradleUtil.createArchetypeBuildGradle(appModel.getApplicationId(), variable, localProjectPath + File.separator + appModel.getAppName() + File.separator + "build.gradle");
        if (!isBuildGradleModified) {
            MessageUtil.showMessage("失败", "创建buide.gradle失败", Messages.getErrorIcon());
            result = Result.errorResult("创建buide.gradle失败");
            return result;
        }
        // 在gradle.properties中添加组件化控制变量
        boolean isAddVariableToFile = FileUtil.writeFile(variable + " = true", localProjectPath + File.separator + "gradle.properties", true);
        if(!isAddVariableToFile){
            result = Result.errorResult("添加组件化变量进gradle.properties失败！");
            return result;
        }
        // 第4步，添加libmanifest和下面的manifest.xml
        // 创建libmanifest文件夹
        String oldManifestPath = localProjectPath + File.separator + appModel.getAppName() + File.separator + "src/main";
        String libManifestPath = localProjectPath + File.separator + appModel.getAppName() + File.separator + "src/main/libmanifest";
        boolean isMkLibManifestDir = FileUtil.mkDir(libManifestPath);
        if (!isMkLibManifestDir) {
            result = Result.errorResult("创建libmanifest文件夹失败");
            return result;
        }
        // 创建AndroidManifest.xml
        boolean isCovertToLibManifest = ManifestUtil.covertToLibManifest(oldManifestPath + File.separator + "AndroidManifest.xml", libManifestPath + File.separator + "AndroidManifest.xml");
        if (!isCovertToLibManifest) {
            result = Result.errorResult("libManifest转化失败！");
            return result;
        }

        // 第5步，settings.gradle中添加本模块
        String settingsFilePath = localProjectPath + File.separator + "settings.gradle";
        boolean isAddToSettings = FileUtil.appendFile(",':" + appModel.getAppName() + "'", settingsFilePath);
        if (!isAddToSettings) {
            result = Result.errorResult("添加setttings。gradle失败！");
            return result;
        }
        result = Result.successResult("修改完成ok");
        return result;
    }


    /**
     * 修改工程配置
     * // 第1步，修改包名
     * // 第2步，修改appName和about信息
     * // 第3步，修改配置文件config.gradle
     */
    public static Result modifyApplicationInfo(String localProjectPath, AppModel appModel, AboutModel aboutModel) {

        Result result = new Result();
        // 第1步，修改包名
        // 从AndroidManifest文件中读取原有包名
        String manifestPath = localProjectPath
                + File.separator
                + "app"
                + File.separator
                + "src"
                + File.separator
                + "main"
                + File.separator
                + "AndroidManifest.xml";
        String packageNameOld = ManifestUtil.readPackageNameFromManifest(manifestPath);
        if (packageNameOld == null) {
            result = Result.errorResult("获取的包名为空!,请检查项目中Manifest文件是否存在和格式是否正确！！！！");
            return result;
        }

        // 创建新的包名临时目录（必须是临时目录，避免包名路径可能和原有包名路径重合），并将原包名下的文件拷贝过去
        String mainPackageDir = localProjectPath
                + File.separator
                + "app"
                + File.separator
                + "src"
                + File.separator
                + "main"
                + File.separator
                + "java"; //包名基本目录
        String tempPackageDir = localProjectPath
                + File.separator
                + "temp"; // 临时目录，包含所有原有包名下的内容
        String[] splitOldPackageName = packageNameOld.split("\\.");
        String innerPackageDirOld = "";
        for (int i = 0; i < splitOldPackageName.length; i++) {
            innerPackageDirOld += splitOldPackageName[i] + File.separator;
        }
        String packagePathOld = mainPackageDir + File.separator + innerPackageDirOld;
        MessageUtil.debugMessage("获取包名路径完成", "old包名路径为：" + packagePathOld, Messages.getInformationIcon());
        boolean isPackageDirOldCopyed = FileUtil.copyDir(packagePathOld, tempPackageDir);
        if (!isPackageDirOldCopyed) {
            result = Result.errorResult("拷贝包目录失败!");
            return result;
        }

        // 遍历新包名下的所有文件，查找替换包名字符串
        Collection<File> javaFiles = FileUtil.listFiles(tempPackageDir, new String[]{"java"}, true);
        Iterator<File> iterator = javaFiles.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            FileUtil.readAndReplace(file.getAbsolutePath(), packageNameOld, appModel.getPackageName());
        }
        // 删除main/java原有包名目录，并将临时目录中的文件拷贝过去,最后删除临时目录
        boolean isMainPackageDirCleaned = FileUtil.cleanDir(mainPackageDir);
        if (!isMainPackageDirCleaned) {
            result = Result.errorResult("main/java 目录旧包名结构清理失败！");
            return result;
        }
        String[] splitNewPackageName = appModel.getPackageName().split("\\.");
        String innerPackageDirNew = "";
        for (int i = 0; i < splitNewPackageName.length; i++) {
            innerPackageDirNew += splitNewPackageName[i] + File.separator;
        }
        String packagePathNew = mainPackageDir + File.separator + innerPackageDirNew;
        boolean isTempPackageDirNewCopyed = FileUtil.copyDir(tempPackageDir + File.separator, packagePathNew);
        if (!isTempPackageDirNewCopyed) {
            result = Result.errorResult("临时目录拷贝失败！");
            return result;
        }
        FileUtil.deleteDir(tempPackageDir);

        // 删除test/java 下原有目录，并创建新的包名目录
        String testPackageDir = localProjectPath
                + File.separator
                + "app"
                + File.separator
                + "src"
                + File.separator
                + "test"
                + File.separator
                + "java"; //包名基本目录
        boolean isTestPackageDirCleaned = FileUtil.cleanDir(testPackageDir);
        if (!isTestPackageDirCleaned) {
            result = Result.errorResult("test/java 目录旧包名结构清理失败！！");
            return result;
        }
        String testPackagePathNew = testPackageDir + File.separator + innerPackageDirNew;
        boolean isMkTestPackage = FileUtil.mkDir(testPackagePathNew);
        if (!isMkTestPackage) {
            result = Result.errorResult("test/java 目录新包名结构创建失败！");
            return result;
        }

        // 删除androidTest/java 下原有目录，并创建新的包名目录
        String androidTestPackageDir = localProjectPath
                + File.separator
                + "app"
                + File.separator
                + "src"
                + File.separator
                + "androidTest"
                + File.separator
                + "java"; //包名基本目录
        boolean isAndroidTestPackageDirCleaned = FileUtil.cleanDir(androidTestPackageDir);
        if (!isAndroidTestPackageDirCleaned) {
            result = Result.errorResult("androidTest/java 目录旧包名结构清理失败！");
            return result;
        }
        String androidTestPackagePathNew = androidTestPackageDir + File.separator + innerPackageDirNew;
        boolean isMkAndroidTestPackagePath = FileUtil.mkDir(androidTestPackagePathNew);
        if (!isMkAndroidTestPackagePath) {
            result = Result.errorResult("androidTest/java 目录新包名结构创建失败！");
            return result;
        }

        // 修改AndroidManifest.xml
        boolean isManifestModified = ManifestUtil.modifyManifest(appModel.getPackageName(), manifestPath);
        if (!isManifestModified) {
            result = Result.errorResult("Manifest.xml修改出错！Manifest.xml是否存在！!");
            return result;
        }

        // 第2步，修改appName和about信息
        // 修改strings.xml
        String StringsXmlPath = localProjectPath
                + File.separator
                + "app"
                + File.separator
                + "src"
                + File.separator
                + "main"
                + File.separator
                + "res"
                + File.separator
                + "values"
                + File.separator
                + "strings.xml";

        boolean isStringsModified = StringsUtil.modifyStrings(appModel.getAppName(), aboutModel, StringsXmlPath);
        if (!isStringsModified) {
            result = Result.errorResult("Strings.xml文件修改失败！请检查strings.xml是否配置正常！！!");
            return result;
        }

        // 第3步，修改配置文件config.gradle
        // 修改config.gradle
        String configGradlePath = localProjectPath
                + File.separator
                + "config.gradle";
        boolean isConfigGradleModified = GradleUtil.createArchetypeConfigGradle(appModel.getAppName(), configGradlePath);
        if (!isConfigGradleModified) {
            result = Result.errorResult("gradle配置修改出错！请检查项目根目录下config.gradle是否存在！!");
            return result;
        }

        result = result.successResult("项目信息修改配置成功！");
        return result;
    }
}
