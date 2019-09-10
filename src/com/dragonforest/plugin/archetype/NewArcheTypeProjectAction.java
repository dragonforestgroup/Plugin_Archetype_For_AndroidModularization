package com.dragonforest.plugin.archetype;

import com.dragonforest.plugin.archetype.config.Configuration;
import com.dragonforest.plugin.archetype.dialog.*;
import com.dragonforest.plugin.archetype.listener.OnChooseArchetypeListener;
import com.dragonforest.plugin.archetype.listener.OnConfigAboutInfoListener;
import com.dragonforest.plugin.archetype.listener.OnConfigProjectListener;
import com.dragonforest.plugin.archetype.listener.OnFinishProjectPathListener;
import com.dragonforest.plugin.archetype.model.AboutModel;
import com.dragonforest.plugin.archetype.model.AppModel;
import com.dragonforest.plugin.archetype.model.Result;
import com.dragonforest.plugin.archetype.utils.GitUtil;
import com.dragonforest.plugin.archetype.utils.MessageUtil;
import com.dragonforest.plugin.archetype.utils.ModifyManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import org.jdom.JDOMException;

import java.io.File;
import java.io.IOException;

public class NewArcheTypeProjectAction extends AnAction {

    private Project project;

    /**
     * 选择的archeType, 当前是github项目地址
     */
    String archeTypeName = null;
    /**
     * 项目主目录
     */
    String localProjectPath = null;
    /**
     * app信息
     */
    AppModel appModel = null;

    /**
     * about信息
     */
    AboutModel aboutModel=null;

    // 对话框
    private ShowArchetypesDialog showArchetypesDialog;
    private ChooseProjectPathDialog chooseProjectPathDialog;
    private ConfigProjectDialog configProjectDialog;
    private ConfigAboutInfoDialog configAboutInfoDialog;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        // TODO: insert action logic here

        project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        MessageUtil.debugMessage("DragonForest", "创建一个archetype project:basePath:" + project.getBasePath() + ",getProjectFilePath:" + project.getProjectFilePath(), Messages.getInformationIcon());
        // 第1步，显示选择Archetype对话框
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
                NewArcheTypeProjectAction.this.archeTypeName = archetypeName;

                // 第2步，显示选择项目路径对话框
                showChooseProjectPathDialog();

            }

            @Override
            public void onPrevious() {

            }
        });

        //设置数据
        showArchetypesDialog.setListData(Configuration.getInstance().getProjectArchetypeList());
        showArchetypesDialog.setVisible(true);

    }

    /**
     * 选择项目路径
     */
    private void showChooseProjectPathDialog() {
        chooseProjectPathDialog = new ChooseProjectPathDialog(project);
        chooseProjectPathDialog.setOnFinishProjectPathListener(new OnFinishProjectPathListener() {
            @Override
            public void onFinish(String chooseDir) {
                NewArcheTypeProjectAction.this.localProjectPath = chooseDir;
                MessageUtil.debugMessage("", "选择最终的path：" + chooseDir, Messages.getInformationIcon());

                // 第3步，显示配置about的属性dialog
                showConfigAboutInfoDialog();
            }

            @Override
            public void onPrevious() {
                showArchetypesDialog.setVisible(true);
            }
        });
        chooseProjectPathDialog.setVisible(true);
    }

    /**
     * 配置about信息
     */
    private void showConfigAboutInfoDialog(){
        configAboutInfoDialog = new ConfigAboutInfoDialog();
        configAboutInfoDialog.setOnConfigAboutInfoListener(new OnConfigAboutInfoListener() {
            @Override
            public void onFinish(AboutModel aboutModel) {
                NewArcheTypeProjectAction.this.aboutModel=aboutModel;
                // 第4步，显示app配置信息
                showConfigProjectDialog();
            }

            @Override
            public void onPrevious() {
                chooseProjectPathDialog.setVisible(true);
            }
        });
        configAboutInfoDialog.setVisible(true);
    }

    /**
     * 配置app信息
     */
    private void showConfigProjectDialog() {
        configProjectDialog = new ConfigProjectDialog();
        configProjectDialog.setOnConfigProjectListener(new OnConfigProjectListener() {
            @Override
            public void onFinish(AppModel appModel) {
                NewArcheTypeProjectAction.this.appModel = appModel;

                MessageUtil.debugMessage("", "配置的项为：" + appModel.getAppName() + "," + appModel.getAppName() + "," + appModel.getApplicationId(), Messages.getInformationIcon());

                // 第5步，克隆并修改参数
                // 1.从git上克隆
                // TODO: 2019/6/12 这里加一个进度框
                // FIXME: 2019/6/12 异步克隆的问题（线程切换）

                // 异步加载
                LoadingDialog.loading("cloning from " + archeTypeName);
                GitUtil.asynCloneToLocalPath(project, NewArcheTypeProjectAction.this.archeTypeName, NewArcheTypeProjectAction.this.localProjectPath, new GitUtil.OnCloneListener() {
                    @Override
                    public void onCloneSuccess() {
                        LoadingDialog.loading("modifying...");
                        // 2.修改包名，applicationid,appName 等
                        Result modifyResult = ModifyManager.modifyApplicationInfo(NewArcheTypeProjectAction.this.localProjectPath,appModel,aboutModel);
                        if (!modifyResult.isOk()) {
                            LoadingDialog.cancel();
                            MessageUtil.showMessage("警告", modifyResult.getMsg(), Messages.getErrorIcon());
                            return;
                        }
                        LoadingDialog.cancel();
                        // 3.打开项目
                        MessageUtil.showMessage("创建完成", "即将打开项目：" + new File(localProjectPath).getAbsolutePath(), Messages.getInformationIcon());
                        try {
                            ProjectManager.getInstance().loadAndOpenProject(localProjectPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JDOMException e) {
                            e.printStackTrace();
                        }
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
                configAboutInfoDialog.setVisible(true);
            }
        });
        configProjectDialog.setVisible(true);
    }
}
