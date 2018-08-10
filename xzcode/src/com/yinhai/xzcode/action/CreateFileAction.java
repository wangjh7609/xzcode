package com.yinhai.xzcode.action;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.xml.actions.xmlbeans.FileUtils;

import java.io.ByteArrayInputStream;
import java.nio.charset.UnsupportedCharsetException;

/**
 * 代码文件创建类，必需使用多线程方式
 * @author wangjh
 * @version 1.0 2018-5-17
 */
public class CreateFileAction implements Runnable {

    private static final Logger LOGGER = Logger.getInstance(CreateFileAction.class);
    /** 输出文件的完整路径 */
    private String outputFile;
    /** 输出到文件的内容 */
    private String content;
    /** 输出文件的字符集编码 */
    private String fileEncoding;
    /** 当前操作的项目 */
    private Project project;

    public CreateFileAction(String outputFile, String content, String fileEncoding, Project project) {
        this.outputFile = outputFile;
        this.content = content;
        this.fileEncoding = fileEncoding;
        this.project = project;
    }

    @Override
    public void run() {
        try {
            VirtualFileManager manager = VirtualFileManager.getInstance();
            VirtualFile virtualFile = manager.refreshAndFindFileByUrl(VfsUtil.pathToUrl(outputFile));
            if (virtualFile != null && virtualFile.exists()) {
                LOGGER.error("文件已经存在："+outputFile);
                return;
            } else {
                ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes(fileEncoding));
                FileUtils.saveStreamContentAsFile(outputFile, bis);
                virtualFile = manager.refreshAndFindFileByUrl(VfsUtil.pathToUrl(outputFile));
            }
            VirtualFile finalVirtualFile = virtualFile;
            if (finalVirtualFile == null || project == null) {
                LOGGER.error(this);
                return;
            }
            ApplicationManager.getApplication().invokeLater(() -> FileEditorManager.getInstance(project).openFile(finalVirtualFile, true,true));

        } catch (UnsupportedCharsetException ex) {
            ApplicationManager.getApplication().invokeLater(() -> Messages.showMessageDialog("未知的字符集: " + fileEncoding , "Generate Failed", null));
        } catch (Exception e) {
            LOGGER.error("文件创建失败", e);
        }

    }
}
