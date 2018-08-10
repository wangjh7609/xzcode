package com.yinhai.xzcode.component;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.JavaProjectRootsUtil;
import com.intellij.openapi.ui.FrameWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.yinhai.xzcode.action.CreateFileAction;
import com.yinhai.xzcode.database.DatabaseHandle;
import com.yinhai.xzcode.ui.CodeCreatorForm;
import com.yinhai.xzcode.utils.VelocityUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.util.*;

/**
 * 代码创建器组件
 * @version 1.0 2018/5/14 0014 下午 11:29
 * @author wangjh
 */
public class CodeCreatorComponent implements ApplicationComponent {
    private CodeCreatorForm codeCreatorForm;
    private FrameWrapper fw;
    private DatabaseHandle handle ;
    private Project project;

    @Override
    public void initComponent() {
        handle = new DatabaseHandle();
        codeCreatorForm = new CodeCreatorForm();
    }

    @Override
    public void disposeComponent() {
        codeCreatorForm = null;
        handle = null;
        project = null;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "codeCreator";
    }

    /**
     * 获取包所在的资源路径
     * @param packagePath 包路径
     * @return 资源路径
     */
    public VirtualFile makeRootPath(String packagePath){
        java.util.List<VirtualFile> suitableRoots = JavaProjectRootsUtil.getSuitableDestinationSourceRoots(project);
        VirtualFile path = null;
        if (suitableRoots.size() > 1) {
            File file ;
            String temp ;
            for (VirtualFile vf : suitableRoots){
                temp = vf.getPath()+"/"+ packagePath;
                file = new File(temp);
                if (file.exists()&&file.isDirectory()){
                    path = vf;
                    break;
                }
            }
        } else if (suitableRoots.size() == 1) {
            path = suitableRoots.get(0);
        }
        return path;
    }

    /**
     * 生成代码文件
     * @param templateName 模板名称
     * @param data 模板数据
     * @param targetPath 目标路径
     */
    public void createCode(String templateName, Map<String,Object> data, String targetPath){
        File file = new File(project.getBasePath()+"/template/"+templateName);
        String content;
        if (file.isFile()&&file.exists()){
            content = VelocityUtil.process(templateName,data,file.getPath());
        }else{
            content = VelocityUtil.process(templateName,data,null);
        }
        ApplicationManager.getApplication().runWriteAction(new CreateFileAction(targetPath, content, "utf-8", project));
    }

    /**
     * 显示代码生成界面
     * @param e 行为观察者
     */
    public void showPanel(AnActionEvent e){
        if (e==null||e.getProject()==null){
            Messages.showMessageDialog("请至少打开一个项目","兴政代码生成工具",Messages.getInformationIcon());
            return;
        }
        if (fw == null||fw.isDisposed()){
            fw = new FrameWrapper(e.getProject());
        }
        codeCreatorForm.setHandle(handle);
        project = e.getProject();
        fw.setProject(e.getProject());
        fw.setComponent(codeCreatorForm.getCreaterPanel());
        fw.setTitle("兴政代码生成器");
        fw.setSize(new Dimension(1000,600));
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        fw.setLocation(new Point((dimension.width-1000)/2,(dimension.height-600)/2));
        fw.show();
    }

    /**
     * 获取当前项目的路径
     * @return 路径
     */
    public String getProjectBasePath(){
        return project.getBasePath();
    }
}
