package org.zyz.childhoodreverie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zyz.childhoodreverie.entity.InventoryEntity;
import org.zyz.childhoodreverie.service.StorageService;

import java.util.List;
import java.util.Map;

/**
 * 玩家背包增删改查接口
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private StorageService storageService;

    /**
     * 查询玩家背包
     */
    @GetMapping("/{playerId}")
    public List<InventoryEntity> getInventory(@PathVariable String playerId) {
        return storageService.getInventory(playerId);
    }

    /**
     * 新增背包物品
     */
    @PostMapping("/{playerId}")
    public InventoryEntity addItem(@PathVariable String playerId,
                                   @RequestBody Map<String, String> req) {
        InventoryEntity item = new InventoryEntity();
        item.setPlayerId(playerId);
        item.setItemId(req.get("itemId"));
        item.setQuantity(Integer.parseInt(req.getOrDefault("quantity", "1")));
        storageService.addInventoryItem(item);
        return item;
    }

    /**
     * 更新背包物品
     */
    @PutMapping("/{playerId}/{id}")
    public InventoryEntity updateItem(@PathVariable String playerId,
                                      @PathVariable Long id,
                                      @RequestBody Map<String, String> req) {
        InventoryEntity item = storageService.getInventoryItem(id);
        if (item != null && item.getPlayerId().equals(playerId)) {
            if (req.containsKey("itemId")) {
                item.setItemId(req.get("itemId"));
            }
            if (req.containsKey("quantity")) {
                item.setQuantity(Integer.parseInt(req.get("quantity")));
            }
            storageService.updateInventoryItem(item);
        }
        return item;
    }

    /**
     * 删除背包物品
     */
    @DeleteMapping("/{playerId}/{id}")
    public void deleteItem(@PathVariable String playerId, @PathVariable Long id) {
        InventoryEntity item = storageService.getInventoryItem(id);
        if (item != null && item.getPlayerId().equals(playerId)) {
            storageService.deleteInventoryItem(id);
        }
    }
}
