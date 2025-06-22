package org.zyz.childhoodreverie.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Ollama 本地 REST API 封装，提供事件生成方法
 */
@Service
public class OllamaAgentService {

    @Value("${ollama.host:localhost}")
    private String ollamaHost;

    @Value("${ollama.port:11434}")
    private int ollamaPort;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OllamaAgentService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 调用 Ollama 生成事件
     * @param playerState 传入玩家状态等上下文
     * @return 包含标题、描述和选项的 Map
     */
    public Map<String, Object> generateEvent(Map<String, Object> playerState) {
        String url = String.format("http://%s:%d/api/chat", ollamaHost, ollamaPort);

        // 构造 messages
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "你是一个文字冒险游戏的事件生成器。"));
        messages.add(Map.of("role", "user", "content", buildUserContent(playerState)));

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "dream-ai14b");
        payload.put("messages", messages);
        payload.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        String body = resp.getBody();

        // 解析返回内容
        Map<String, Object> result = new HashMap<>();
        try {
            JsonNode root = objectMapper.readTree(body);
            String content = root.path("message").path("content").asText();
            // 简单拆分：标题|描述|选项
            String[] parts = content.split("\\n", 2);
            result.put("eventTitle", parts[0].trim());
            String descAndOptions = parts.length > 1 ? parts[1].trim() : "";
            result.put("eventDescription", descAndOptions);
            // 这里可根据格式进一步拆分 options，后续扩展
            return result;
        } catch (Exception e) {
            throw new RuntimeException("解析 Ollama 返回失败", e);
        }
    }

    private String buildUserContent(Map<String, Object> state) {
        // 将 playerState 序列化为文本 prompt
        StringBuilder sb = new StringBuilder();
        sb.append("玩家状态：").append(state.get("player")).append("\n");
        sb.append("位置：").append(state.get("location")).append("\n");
        sb.append("历史记录：").append(state.get("history")).append("\n");
        sb.append("请生成事件标题、描述和选项。");
        return sb.toString();
    }
}