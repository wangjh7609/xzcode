package com.yinhai.xzcode.utils;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * velocity模板工具类
 *
 * @version  2018/5/14 0014 上午 11:24
 * @author wangjh
 */
public class VelocityUtil {
    private final static VelocityEngine velocityEngine;

    static {
        velocityEngine = new VelocityEngine();
        // Disable separate Velocity logging.
        velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                NullLogChute.class.getName());
        velocityEngine.init();
        velocityEngine.setProperty("input.encoding","utf-8");
    }

    public static String process(String template, Map<String, Object> map,String realPath) {
        try {
            String content;
            if(StringUtil.isEmpty(realPath)) {
                content = FileUtil.loadTextAndClose(VelocityUtil.class.getResourceAsStream("/template/" + template));
            }else{
                content = FileUtil.loadTextAndClose(new FileInputStream(realPath + template));
            }
            VelocityContext context = new VelocityContext();
            map.forEach(context::put);
            StringWriter writer = new StringWriter();
            velocityEngine.evaluate(context, writer, "", content);
            return writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
