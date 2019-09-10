package com.dragonforest.plugin.archetype.config;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private static Configuration instance = new Configuration();
    private List<String> projectArchetypeList = new ArrayList<>();
    private List<String> moduleArchetypeList = new ArrayList<>();


    private Configuration() {
        // TODO: 2019/6/14  这里可能是从网络加载，现在先设置一个
//        archetypeList.add("https://github.com/dragonforestgroup/Library_DragonForestAop.git");
//        archetypeList.add("https://github.com/dragonforestgroup/Plugin_DragonForestPlugin.git");
//        archetypeList.add("https://github.com/hanlonglinandroidstudys/MaterialDesignStudy.git");
        projectArchetypeList.add("https://github.com/TestHanlonglin/TemplateAndroidApplication.git");
        moduleArchetypeList.add("https://github.com/TestHanlonglin/TemplateAndroidModule.git");
    }

    public static Configuration getInstance() {
        return instance;
    }

    public void addArchetype(String archetypeName) {

    }

    public void removeArchetype(String archetypeName) {

    }

    public List<String> getModuleArchetypeList() {
        return moduleArchetypeList;
    }

    public List<String> getProjectArchetypeList() {
        return projectArchetypeList;
    }
}
