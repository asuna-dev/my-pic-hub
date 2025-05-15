package org.zepe.pichub.model.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zzpus
 * @datetime 2025/5/14 22:34
 * @description
 */
@Data
@AllArgsConstructor
public class SpaceLevel {

    private int value;

    private String text;

    private long maxCount;

    private long maxSize;
}
