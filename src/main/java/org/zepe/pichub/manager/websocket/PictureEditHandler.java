package org.zepe.pichub.manager.websocket;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.zepe.pichub.manager.websocket.disruptor.PictureEditEventProducer;
import org.zepe.pichub.manager.websocket.model.PictureEditActionEnum;
import org.zepe.pichub.manager.websocket.model.PictureEditMessageTypeEnum;
import org.zepe.pichub.manager.websocket.model.PictureEditRequestMessage;
import org.zepe.pichub.manager.websocket.model.PictureEditResponseMessage;
import org.zepe.pichub.model.entity.User;
import org.zepe.pichub.model.vo.UserVO;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zzpus
 * @datetime 2025/5/17 23:31
 * @description
 */
@Slf4j
@Component
public class PictureEditHandler extends TextWebSocketHandler {
    // 每张图片的编辑状态，key: pictureId, value: 当前正在编辑的用户 ID
    private final Map<Long, Long> pictureEditingUsers = new ConcurrentHashMap<>();
    // 保存所有连接的会话，key: pictureId, value: 用户会话集合
    private final Map<Long, Set<WebSocketSession>> pictureSessions = new ConcurrentHashMap<>();
    @Resource
    private PictureEditEventProducer pictureEditEventProducer;

    private void broadcastToPicture(Long pictureId, PictureEditResponseMessage pictureEditResponseMessage,
                                    WebSocketSession excludeSession) throws Exception {
        Set<WebSocketSession> sessionSet = pictureSessions.get(pictureId);
        if (CollUtil.isNotEmpty(sessionSet)) {
            // 创建 ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            // 配置序列化：将 Long 类型转为 String，解决丢失精度问题
            SimpleModule module = new SimpleModule();
            module.addSerializer(Long.class, ToStringSerializer.instance);
            module.addSerializer(Long.TYPE, ToStringSerializer.instance); // 支持 long 基本类型
            objectMapper.registerModule(module);
            // 序列化为 JSON 字符串
            String message = objectMapper.writeValueAsString(pictureEditResponseMessage);
            TextMessage textMessage = new TextMessage(message);
            for (WebSocketSession session : sessionSet) {
                // 排除掉的 session 不发送
                if (Objects.equals(session, excludeSession)) {
                    continue;
                }
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            }
        }
    }

    // 全部广播
    private void broadcastToPicture(Long pictureId, PictureEditResponseMessage pictureEditResponseMessage)
        throws Exception {
        broadcastToPicture(pictureId, pictureEditResponseMessage, null);
    }

    public void handleEnterEditMessage(PictureEditRequestMessage pictureEditRequestMessage, WebSocketSession session,
                                       User user, Long pictureId) throws Exception {
        // 没有用户正在编辑该图片，才能进入编辑
        // 设置当前用户为编辑用户
        if (pictureEditingUsers.putIfAbsent(pictureId, user.getId()) == null) {
            PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
            pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.ENTER_EDIT.getValue());
            String message = String.format("%s开始编辑图片", user.getUserName());
            pictureEditResponseMessage.setMessage(message);
            pictureEditResponseMessage.setUser(UserVO.objToVo(user));
            broadcastToPicture(pictureId, pictureEditResponseMessage);
        }
    }

    public void handleEditActionMessage(PictureEditRequestMessage pictureEditRequestMessage, WebSocketSession session,
                                        User user, Long pictureId) throws Exception {
        Long editingUserId = pictureEditingUsers.get(pictureId);
        String editAction = pictureEditRequestMessage.getEditAction();
        PictureEditActionEnum actionEnum = PictureEditActionEnum.getEnumByValue(editAction);
        if (actionEnum == null) {
            return;
        }
        // 确认是当前编辑者
        if (Objects.equals(user.getId(), editingUserId)) {
            PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
            pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.EDIT_ACTION.getValue());
            String message = String.format("%s执行%s", user.getUserName(), actionEnum.getText());
            pictureEditResponseMessage.setMessage(message);
            pictureEditResponseMessage.setEditAction(editAction);
            pictureEditResponseMessage.setUser(UserVO.objToVo(user));
            // 广播给除了当前客户端之外的其他用户，否则会造成重复编辑
            broadcastToPicture(pictureId, pictureEditResponseMessage, session);
        }
    }

    public void handleExitEditMessage(PictureEditRequestMessage pictureEditRequestMessage, WebSocketSession session,
                                      User user, Long pictureId) throws Exception {
        Long editingUserId = pictureEditingUsers.get(pictureId);
        if (Objects.equals(user.getId(), editingUserId)) {
            // 移除当前用户的编辑状态
            pictureEditingUsers.remove(pictureId);
            // 构造响应，发送退出编辑的消息通知
            PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
            pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.EXIT_EDIT.getValue());
            String message = String.format("%s退出编辑图片", user.getUserName());
            pictureEditResponseMessage.setMessage(message);
            pictureEditResponseMessage.setUser(UserVO.objToVo(user));
            broadcastToPicture(pictureId, pictureEditResponseMessage);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 将消息解析为 PictureEditMessage
        PictureEditRequestMessage pictureEditRequestMessage =
            JSONUtil.toBean(message.getPayload(), PictureEditRequestMessage.class);
        String type = pictureEditRequestMessage.getType();
        PictureEditMessageTypeEnum pictureEditMessageTypeEnum = PictureEditMessageTypeEnum.valueOf(type);

        // 从 Session 属性中获取公共参数
        Map<String, Object> attributes = session.getAttributes();
        User user = (User)attributes.get("user");
        Long pictureId = (Long)attributes.get("pictureId");

        log.info("Recv WsMsg: {}", pictureEditRequestMessage);

        pictureEditEventProducer.publishEvent(pictureEditRequestMessage, session, user, pictureId);
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        log.info("WsConnected: {}", session);
        // 保存会话到集合中
        User user = (User)session.getAttributes().get("user");
        Long pictureId = (Long)session.getAttributes().get("pictureId");
        pictureSessions.putIfAbsent(pictureId, ConcurrentHashMap.newKeySet());
        pictureSessions.get(pictureId).add(session);

        // 构造响应
        PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
        pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.INFO.getValue());
        String message = String.format("%s加入编辑", user.getUserName());
        pictureEditResponseMessage.setMessage(message);
        pictureEditResponseMessage.setUser(UserVO.objToVo(user));
        // 广播给同一张图片的用户
        broadcastToPicture(pictureId, pictureEditResponseMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        log.info("WsClosed: {}", session);
        Map<String, Object> attributes = session.getAttributes();
        Long pictureId = (Long)attributes.get("pictureId");
        User user = (User)attributes.get("user");
        // 移除当前用户的编辑状态
        handleExitEditMessage(null, session, user, pictureId);

        // 删除会话
        Set<WebSocketSession> sessionSet = pictureSessions.get(pictureId);
        if (sessionSet != null) {
            sessionSet.remove(session);
            if (sessionSet.isEmpty()) {
                pictureSessions.remove(pictureId);
            }
        }

        // 响应
        PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
        pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.INFO.getValue());
        String message = String.format("%s离开编辑", user.getUserName());
        pictureEditResponseMessage.setMessage(message);
        pictureEditResponseMessage.setUser(UserVO.objToVo(user));
        broadcastToPicture(pictureId, pictureEditResponseMessage);
    }

}
