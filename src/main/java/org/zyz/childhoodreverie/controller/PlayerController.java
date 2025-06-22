package org.zyz.childhoodreverie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zyz.childhoodreverie.entity.PlayerEntity;
import org.zyz.childhoodreverie.service.StorageService;

import java.util.Map;

/**
 * 玩家注册与信息查询接口
 */
@RestController
@RequestMapping("/player")
public class PlayerController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/register")
    public PlayerEntity register(@RequestBody Map<String, String> req) {
        PlayerEntity p = new PlayerEntity();
        p.setPlayerId(req.get("playerId"));
        p.setName(req.get("name"));
        p.setLevel(1);
        p.setLocation("home_village");
        storageService.savePlayer(p);
        return p;
    }

    @GetMapping("/{playerId}")
    public PlayerEntity getPlayer(@PathVariable String playerId) {
        return storageService.getPlayer(playerId);
    }
}