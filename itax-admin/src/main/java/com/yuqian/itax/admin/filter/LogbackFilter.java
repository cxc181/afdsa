package com.yuqian.itax.admin.filter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.UUID;

/**
 * @ClassName LogbackFilter
 * @Description 增加输出日志traceRootId
 * @Author jiangni
 * @Date 2019/7/18
 * @Version 1.0
 */
@Component
@WebFilter(urlPatterns = "/*")
@Slf4j
public class LogbackFilter implements Filter {

    private static final String UNIQUE_ID = "traceRootId";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("LogbackFilter init...");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        boolean bInsertMDC = insertMDC();
        try {
            chain.doFilter(request, response);
        } finally {
            if (bInsertMDC) {
                MDC.remove(UNIQUE_ID);
            }
        }
    }

    private boolean insertMDC() {
        UUID uuid = UUID.randomUUID();
        String uniqueId = UNIQUE_ID + "-" + uuid.toString().replace("-", "");
        MDC.put(UNIQUE_ID, uniqueId);
        return true;
    }

    @Override
    public void destroy() {
        log.info("LogbackFilter destroy...");
    }
}
