package com.github.dhaval2404.material_icon_generator;

import com.github.dhaval2404.material_icon_generator.widget.MaterialDesignIconGenerateDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;

import javax.annotation.Nonnull;

/**
 * Provide Initial Trigger for the Menu Item Click
 * <p>
 * Ref: https://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/creating_an_action.html
 * <p>
 * Created by Dhaval Patel on 24 January 2019.
 */
public class MaterialIconGeneratorAction extends AnAction {

    /**
     * actionPerformed method is called each time you select a menu item or click a toolbar button
     */
    public void actionPerformed(@Nonnull AnActionEvent event) {
        DataContext dataContext = event.getDataContext();

        Module module = LangDataKeys.MODULE.getData(dataContext);
        Project project = event.getProject();

        String projectPath = null;
        String modulePath = null;

        if (project != null) {
            projectPath = project.getBasePath();
            System.out.println("projectPath:" + projectPath);
        }

        if (module != null) {
            modulePath = module.getModuleFile().getParent().getPath();
            System.out.println("modulePath:" + modulePath);
        }

        Module[] modules = ModuleManager.getInstance(project).getModules();
        for(Module module1: modules){
            String modulePath1 = module1.getModuleFile().getParent().getPath();
            System.out.println("modulePath1:" + modulePath1);
        }

        String defaultResourcePath;
        if (projectPath != null && projectPath.equals(modulePath)) {
            //Project Path
            defaultResourcePath = projectPath + "/app/src/main/res";
        } else if(modulePath!=null){
            //Module Path
            defaultResourcePath = modulePath + "/src/main/res";
        } else {
            //Module Path
            defaultResourcePath = projectPath + "app/src/main/res";
        }


        MaterialDesignIconGenerateDialog dialog = new MaterialDesignIconGenerateDialog(event.getProject(), defaultResourcePath);
        dialog.show();
    }

}