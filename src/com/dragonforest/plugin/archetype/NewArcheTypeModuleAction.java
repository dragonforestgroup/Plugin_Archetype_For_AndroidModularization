package com.dragonforest.plugin.archetype;

import com.dragonforest.plugin.archetype.config.Configuration;
import com.dragonforest.plugin.archetype.dialog.ShowArchetypesDialog;
import com.dragonforest.plugin.archetype.dialog.ConfigProjectDialog;
import com.dragonforest.plugin.archetype.dialog.LoadingDialog;
import com.dragonforest.plugin.archetype.listener.OnChooseArchetypeListener;
import com.dragonforest.plugin.archetype.listener.OnConfigProjectListener;
import com.dragonforest.plugin.archetype.model.AppModel;
import com.dragonforest.plugin.archetype.model.Result;
import com.dragonforest.plugin.archetype.utils.GitUtil;
import com.dragonforest.plugin.archetype.utils.MessageUtil;
import com.dragonforest.plugin.archetype.utils.ModifyManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.io.File;

public class NewArcheTypeModuleAction extends AnAction {

    private Project project;

    private String mArcheTypeName;

    private AppModel mAppModel;
    // 对话框
    private ShowArchetypesDialog showArchetypesDialog;
    private ConfigProjectDialog configProjectDialog;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        // TODO: insert action logic here
        project = anActionEvent.getData(PlatformDataKeys.PROJECT);

        showChooseArchetypeDialog();

    }

    /**
     * 选择archetype
     */
    private void showChooseArchetypeDialog() {
        showArchetypesDialog = new ShowArchetypesDialog();
        showArchetypesDialog.setOnChooseArchetypeListener(new OnChooseArchetypeListener() {
            @Override
            public void onChoose(String archetypeName) {
                NewArcheTypeModuleAction.this.mArcheTypeName = archetypeName;

                String basePath = project.getBasePath();
                // 显示选择项目路径对话框
                MessageUtil.showMessage("","即将克隆到目录："+basePath,null);
                showConfigMouleDialog();
            }

            @Override
            public void onPrevious() {

            }
        });

        //设置数据
        showArchetypesDialog.setListData(Configuration.getInstance().getModuleArchetypeList());
        showArchetypesDialog.setVisible(true);

    }

    private void showConfigMouleDialog() {
        configProjectDialog = new ConfigProjectDialog();
        configProjectDialog.setOnConfigProjectListener(new OnConfigProjectListener() {
            @Override
            public void onFinish(AppModel appModel) {
                NewArcheTypeModuleAction.this.mAppModel = appModel;

                MessageUtil.debugMessage("", "配置的项为：" + appModel.getAppName() + "," + appModel.getAppName() + "," + appModel.getApplicationId(), Messages.getInformationIcon());

                // 1.从git上克隆
                // TODO: 2019/6/12 这里加一个进度框
                // FIXME: 2019/6/12 异步克隆的问题（线程切换）

                // 异步加载
                LoadingDialog.loading("cloning from " + mArcheTypeName);
                String localPath=project.getBasePath()+File.separator+mAppModel.getAppName();
                File localModuleDir=new File(localPath);
                if(!localModuleDir.exists()){
                    localModuleDir.mkdir();
                }
                GitUtil.asynCloneToLocalPath(project, NewArcheTypeModuleAction.this.mArcheTypeName, localPath, new GitUtil.OnCloneListener() {
                    @Override
                    public void onCloneSuccess() {
                        LoadingDialog.loading("modifying...");
                        // 2.修改包名，applicationid,appName 等
                        Result modifyResult = ModifyManager.modifyModuleInfo(project.getBasePath(),appModel);
                        if (!modifyResult.isOk()) {
                            LoadingDialog.cancel();
                            MessageUtil.showMessage("警告", modifyResult.getMsg(), Messages.getErrorIcon());
                            return;
                        }
                        LoadingDialog.cancel();
                        // 3.打开项目
                        MessageUtil.showMessage("创建完成", "创建成功：",Messages.getInformationIcon());
//                        try {
//                            ProjectManager.getInstance().loadAndOpenProject(localProjectPath);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (JDOMException e) {
//                            e.printStackTrace();
//                        }
                    }

                    @Override
                    public void onCloneError(String msg) {
                        LoadingDialog.cancel();
                        MessageUtil.showMessage("错误", "克隆出错！！！" + msg, Messages.getErrorIcon());
                    }
                });
            }

            @Override
            public void onPrevious() {
//                configAboutInfoDialog.setVisible(true);
            }
        });
        configProjectDialog.setVisible(true);
    }

    private Result modifyAppInfo() {
        // 1.修改包名

        // 2.修改applicationId

        /* 3.修改build.gradle中的配置
         *      1).sdk和依赖库版本统一
         *      2).使用变量控制 应用类型，manifest,aplication
         */

        // 4.添加libmanifest和下面的manifest.xml

        // 5.settings.gradle中添加本模块

        // 6.gradle.properties 或config.gradle 中添加变量


        return null;
    }
}
