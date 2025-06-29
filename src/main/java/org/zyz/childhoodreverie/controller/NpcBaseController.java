package org.zyz.childhoodreverie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zyz.childhoodreverie.entity.NpcEntity;
import org.zyz.childhoodreverie.service.StorageService;

import java.util.Map;

/**
 * NPC 基本信息管理接口
 */
@RestController
@RequestMapping("/npc/base")
public class NpcBaseController {

    @Autowired
    private StorageService storageService;

    /**
     * 创建 NPC
     */
    @PostMapping
    public NpcEntity createNpc(@RequestBody Map<String, String> req) {
        NpcEntity npc = new NpcEntity();
        npc.setNpcId(req.get("npcId"));
        npc.setName(req.get("name"));
        npc.setLevel(Integer.parseInt(req.getOrDefault("level", "1")));
        npc.setLocation(req.getOrDefault("location", ""));
        storageService.saveNpc(npc);
        return npc;
    }

    /**
     * 查询 NPC 信息
     */
    @GetMapping("/{npcId}")
    public NpcEntity getNpc(@PathVariable String npcId) {
        return storageService.getNpc(npcId);
    }

    /**
     * 更新 NPC 信息
     */
    @PutMapping("/{npcId}")
    public NpcEntity updateNpc(@PathVariable String npcId,
                               @RequestBody Map<String, String> req) {
        NpcEntity npc = storageService.getNpc(npcId);
        if (npc != null) {
            if (req.containsKey("name")) {
                npc.setName(req.get("name"));
            }
            if (req.containsKey("level")) {
                npc.setLevel(Integer.parseInt(req.get("level")));
            }
            if (req.containsKey("location")) {
                npc.setLocation(req.get("location"));
            }
            storageService.updateNpc(npc);
        }
        return npc;
    }
}
