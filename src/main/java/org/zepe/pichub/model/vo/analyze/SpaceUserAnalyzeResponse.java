package org.zepe.pichub.model.vo.analyze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zzpus
 * @datetime 2025/5/16 21:25
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceUserAnalyzeResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 时间区间
     */
    private String period;
    /**
     * 上传数量
     */
    private Long count;
}

