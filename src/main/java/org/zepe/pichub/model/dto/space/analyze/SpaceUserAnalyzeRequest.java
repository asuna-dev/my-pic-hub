package org.zepe.pichub.model.dto.space.analyze;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zzpus
 * @datetime 2025/5/16 21:25
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceUserAnalyzeRequest extends SpaceAnalyzeRequest {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 时间维度：day / week / month
     */
    private String timeDimension;
}


