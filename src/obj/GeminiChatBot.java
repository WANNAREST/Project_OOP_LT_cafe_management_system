package obj;

import Controller.PaymentAPI;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

public class GeminiChatBot {
    private static String API_KEY;
    private static String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    static {
        try (InputStream input = PaymentAPI.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            API_KEY = prop.getProperty("gemini.api.key");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Store store;

    public GeminiChatBot(Store store) {
        this.store = store;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public String getChatResponse(String userMessage) {
        try {
            String prompt = buildCoffeeShopPrompt(userMessage);
            String requestBody = buildRequestBody(prompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "?key=" + API_KEY))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseResponse(response.body());
            } else {
                System.err.println("HTTP Error: " + response.statusCode() + " - " + response.body());
                return "Xin lỗi, tôi không thể trả lời lúc này. Vui lòng thử lại sau.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Có lỗi xảy ra khi xử lý câu hỏi của bạn.";
        }
    }

    private String buildCoffeeShopPrompt(String userMessage) {
        StringBuilder context = new StringBuilder();
        context.append("Bạn là chatbot thông minh của cửa hàng cà phê OOP Coffee. ");
        context.append("Hãy trả lời thân thiện, chuyên nghiệp và hữu ích.\n\n");

        // Thông tin menu
        context.append("MENU CÀ PHÊ:\n");
        if (store != null && store.getItemsInStore() != null) {
            for (Product product : store.getItemsInStore()) {
                context.append("- ").append(product.getName())
                        .append(": ").append(String.format("%,d", (int)product.getPrice()))
                        .append("đ\n");
            }
        }

        context.append("\nTHÔNG TIN CỬA HÀNG:\n");
        context.append("- Tên: OOP Coffee\n");
        context.append("- Giờ mở cửa: 6:00 - 22:00 hàng ngày\n");
        context.append("- Hệ thống tích điểm: 20 điểm = 200 đồng\n");
        context.append("- Chính sách: Thân thiện, chất lượng cao\n\n");

        context.append("HƯỚNG DẪN:\n");
        context.append("- Trả lời bằng tiếng Việt\n");
        context.append("- Nếu không biết thông tin, hãy thành thật nói không biết\n");
        context.append("- Gợi ý sản phẩm phù hợp khi khách hàng hỏi\n");
        context.append("- Luôn lịch sự và nhiệt tình\n\n");

        context.append("Câu hỏi từ khách hàng: ").append(userMessage);

        return context.toString();
    }

    private String buildRequestBody(String prompt) {
        String jsonTemplate = "{\n" +
                "    \"contents\": [\n" +
                "        {\n" +
                "            \"parts\": [\n" +
                "                {\n" +
                "                    \"text\": \"%s\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        return String.format(jsonTemplate, prompt.replace("\"", "\\\"").replace("\n", "\\n"));
    }

    private String parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidates = root.get("candidates");
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).get("content");
                if (content != null) {
                    JsonNode parts = content.get("parts");
                    if (parts != null && parts.isArray() && parts.size() > 0) {
                        JsonNode text = parts.get(0).get("text");
                        if (text != null) {
                            return text.asText();
                        }
                    }
                }
            }
            return "Xin lỗi, tôi không hiểu câu hỏi của bạn.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Có lỗi khi xử lý phản hồi.";
        }
    }
}