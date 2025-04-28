package org.zepe.pichub.common;

import lombok.Data;

/**
 * @author zzpus
 * @datetime 2025/4/27 22:07
 * @description
 */
@Data
public class PageRequest {
    private int current = 1;
    private int pageSize = 10;
    private String sortField;
    private String sortOrder = "ascend";
}
