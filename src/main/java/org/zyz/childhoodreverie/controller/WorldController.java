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

    /**
     * 获取世界历 (自定义时间)
     */
    @GetMapping("/calendar")
    public Map<String, String> getWorldCalendar() {
        String calendar = worldTimeService.getWorldCalendar();
        double multiplier = worldTimeService.getMultiplier();
        return Map.of(
                "calendar", calendar,
                "multiplier", String.valueOf(multiplier)
        );
    }


    @PostMapping("/speed")
    public void setSpeed(@RequestParam double multiplier) {
        worldTimeService.setMultiplier(multiplier);
    }
}