package org.zepe.pichub.manager.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zepe.pichub.model.vo.UserVO;

import java.io.Serializable;

/**
 * @author zzpus
 * @datetime 2025/5/17 23:09
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureEditResponseMessage implements Serializable {

    /**
     * 消息类型，例如 "INFO", "ERROR", "ENTER_EDIT", "EXIT_EDIT", "EDIT_ACTION"
     */
    private String type;

    /**
     * 信息
     */
    private String message;

    /**
     * 执行的编辑动作
     */
    private String editAction;

    /**
     * 用户信息
     */
    private UserVO user;
}


