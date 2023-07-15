package com.yuqian.itax.util.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * JSON
 * 
 * @author huliang
 * @version $Id: JSONUtils.java, v 0.1 2011-12-12 下午5:12:44 huliang Exp $
 */
@Slf4j
public class JSONUtils<T> {

    /**
     * 
     * @param response
     * @param jsonStr
     */
    public static void write(HttpServletResponse response, String jsonStr) {
        try {
            log.debug("JSON Str：" + jsonStr);
            if(StringUtils.isNotBlank(jsonStr)){
                response.setCharacterEncoding("UTF-8");
                PrintWriter writer = response.getWriter();
                writer.print(jsonStr);
                writer.close();
            }
        } catch (IOException e) {
            log.error("发生异常,详细信息==>>",e);
        }
    }
    

}

