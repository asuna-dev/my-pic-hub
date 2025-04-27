package org.zepe.pichub.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zepe.pichub.common.Response;

/**
 * @author zzpus
 * @datetime 2025/4/27 22:14
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/")
public class HealthCheckController {

    @GetMapping("health")
    public Response healthCheck() {
        return Response.success("ok");
    }
}
