package org.zyz.childhoodreverie.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

@Component
@Order(0)  // 确保最先执行
public class LanOnlyFilter implements Filter {
    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        // 如果是 /world/** 下的接口
        if (matcher.match("/world/speed", uri)||
                matcher.match("/world/reset", uri)) {
            String ip = request.getRemoteAddr();
            // 允许 127.0.0.1 和 192.168.x.x、10.x.x.x、172.16–31.x.x
            if (!(ip.equals("127.0.0.1")
                    || ip.startsWith("192.168.")
                    || ip.startsWith("10.")
                    || ip.matches("^172\\.(1[6-9]|2\\d|3[0-1])\\..*"))) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "仅限局域网访问");
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
