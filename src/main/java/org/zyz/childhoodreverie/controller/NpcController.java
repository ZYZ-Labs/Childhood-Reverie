package org.zyz.childhoodreverie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zyz.childhoodreverie.entity.NpcMemoryEntity;
import org.zyz.childhoodreverie.service.StorageService;

import java.util.Map;

/**
 * NPC 记忆管理接口
 */
@RestController
@RequestMapping("/npc")
public class NpcController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/{npcId}/memory")
    public void setMemory(
            @PathVariable String npcId,
            @RequestBody Map<String, String> req
    ) {
        NpcMemoryEntity mem = new NpcMemoryEntity();
        mem.setNpcId(npcId);
        mem.setMemoryKey(req.get("memoryKey"));
        mem.setMemoryValue(req.get("memoryValue"));
        storageService.saveNpcMemory(mem);
    }

    @GetMapping("/{npcId}/memory/{memoryKey}")
    public NpcMemoryEntity getMemory(
            @PathVariable String npcId,
            @PathVariable String memoryKey
    ) {
        return storageService.getNpcMemory(npcId, memoryKey);
    }
}