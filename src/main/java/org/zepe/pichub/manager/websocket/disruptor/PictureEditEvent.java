package org.zepe.pichub.manager.websocket.disruptor;

import lombok.Data;
import org.springframework.web.socket.WebSocketSession;
import org.zepe.pichub.manager.websocket.model.PictureEditRequestMessage;
import org.zepe.pichub.model.entity.User;

/**
 * @author zzpus
 * @datetime 2025/5/18 13:27
 * @description
 */
@Data
public class PictureEditEvent {

    /**
     * 消息
     */
    private PictureEditRequestMessage pictureEditRequestMessage;

    /**
     * 当前用户的 session
     */
    private WebSocketSession session;

    /**
     * 当前用户
     */
    private User user;

    /**
     * 图片 id
     */
    private Long pictureId;

}

