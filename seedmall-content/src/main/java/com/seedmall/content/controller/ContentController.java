/*
 * 内容接口控制器，提供发布和 Feed 查询入口。
 */
package com.seedmall.content.controller;

import com.seedmall.common.response.ApiResponse;
import com.seedmall.content.entity.SeedContent;
import com.seedmall.content.service.ContentPublishService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 内容接口。
 */
@RestController
@RequestMapping("/contents")
public class ContentController {

    private final ContentPublishService publishService;

    /**
     * 注入内容发布服务。
     */
    public ContentController(ContentPublishService publishService) {
        this.publishService = publishService;
    }

    /**
     * 发布种草内容。
     */
    @PostMapping
    public ApiResponse<SeedContent> publish(@RequestBody SeedContent content) {
        return ApiResponse.ok(publishService.publish(content));
    }

    /**
     * 查询首页 Feed 示例数据。
     */
    @GetMapping("/feed")
    public ApiResponse<List<String>> feed() {
        return ApiResponse.ok(List.of("爆款内容缓存位", "关注流内容位", "推荐流内容位"));
    }
}
