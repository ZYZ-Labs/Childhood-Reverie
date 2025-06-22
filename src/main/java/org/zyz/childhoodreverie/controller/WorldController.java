package org.zyz.childhoodreverie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zyz.childhoodreverie.service.WorldTimeService;

import java.util.Map;

/**
 * 世界时间与状态控制接口
 */
@RestController
@RequestMapping("/world")
public class WorldController {

    @Autowired
    private WorldTimeService worldTimeService;

    @GetMapping("/time")
    public Map<String, Object> getWorldTime() {
        long worldTime = worldTimeService.getWorldTime();
        double multiplier = worldTimeService.getMultiplier();
        return Map.of(
                "worldTime", worldTime,
                "multiplier", multiplier
        );
    }

    @PostMapping("/speed")
    public void setSpeed(@RequestParam double multiplier) {
        worldTimeService.setMultiplier(multiplier);
    }
}