package org.zepe.pichub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.zepe.pichub.aop.WsHandshakeInterceptor;
import org.zepe.pichub.manager.websocket.PictureEditHandler;

import javax.annotation.Resource;

/**
 * @author zzpus
 * @datetime 2025/5/18 13:11
 * @description
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Resource
    private PictureEditHandler pictureEditHandler;
    @Resource
    private WsHandshakeInterceptor wsHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(pictureEditHandler, "/ws/picture/edit")
            .addInterceptors(wsHandshakeInterceptor)
            .setAllowedOrigins("*");
    }
}
