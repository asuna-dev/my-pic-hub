package org.zepe.pichub.model.dto.space.analyze;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author zzpus
 * @datetime 2025/5/16 21:25
 * @description
 */
@Data
public class SpaceRankAnalyzeRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 排名前 N 的空间
     */
    private Integer topN = 10;
}

