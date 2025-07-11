package org.zyz.childhoodreverie.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     *
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
        payload.put("model", playerState.get("model"));
        payload.put("messages", messages);
        payload.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        String body = resp.getBody();

        Map<String, Object> result = new HashMap<>();
        try {
            JsonNode root = objectMapper.readTree(body);
            String content = root.path("message").path("content").asText();
            result.put("eventMarkdown", content);  // 保留原始 markdown

            // 提取 <think> 标签内容
            String thinkContent = "";
            Pattern thinkPattern = Pattern.compile("<think>(.*?)</think>", Pattern.DOTALL);
            Matcher matcher = thinkPattern.matcher(content);
            if (matcher.find()) {
                thinkContent = matcher.group(1).trim();
                content = content.replace(matcher.group(0), "").trim(); // 移除 <think> 部分
            }
            result.put("think", thinkContent);

            // 粗略解析 Markdown 内容
            String[] lines = content.split("\\n");
            String title = "";
            StringBuilder descBuilder = new StringBuilder();
            List<String> options = new ArrayList<>();

            for (String line : lines) {
                if (line.startsWith("#")) {
                    title = line.replaceAll("^#+", "").trim(); // #、## 都可以
                } else if (line.startsWith("- ")) {
                    options.add(line.substring(2).trim());
                } else {
                    descBuilder.append(line).append("\n");
                }
            }

            result.put("eventTitle", title);
            result.put("eventDescription", descBuilder.toString().trim());
            result.put("eventOptions", options);

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