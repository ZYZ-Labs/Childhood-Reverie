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

    /**
     * 世界时间
     * @return
     */
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

    /**
     * 世界速度调整
     * @param multiplier
     */
    @PostMapping("/speed")
    public void setSpeed(@RequestParam double multiplier) {
        worldTimeService.setMultiplier(multiplier);
    }

    /**
     * 重置世界状态，包括时间等
     */
    @PostMapping("/reset")
    public void resetWorld() {
        worldTimeService.resetWorld();
    }
}