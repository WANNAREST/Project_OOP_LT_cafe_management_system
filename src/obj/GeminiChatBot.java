package obj;

import Controller.PaymentAPI;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class GeminiChatBot {
    private static String API_KEY;
    private static String API_URL="https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";


    static {
        try (InputStream input = PaymentAPI.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            API_KEY = prop.getProperty("gemini.api.key");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final Store store;

    public GeminiChatBot(Store store) {
        this.store = store;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public String getChatResponse(String userMessage) {
        try {
            String prompt = buildCoffeeShopPrompt(userMessage);
            String requestBody = buildRequestBody(prompt);

            Request request = new Request.Builder()
                    .url(API_URL + "?key=" + API_KEY)
                    .post(RequestBody.create(MediaType.parse("application/json"), requestBody))
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return parseResponse(response.body().string());
                } else {
                    return "Xin lỗi, tôi không thể trả lời lúc này. Vui lòng thử lại sau.";
                }
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
        context.append("- Hệ thống tích điểm: 1000đ = 1 điểm\n");
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
        return """
            {
                "contents": [
                    {
                        "parts": [
                            {
                                "text": "%s"
                            }
                        ]
                    }
                ]
            }
            """.formatted(prompt.replace("\"", "\\\"").replace("\n", "\\n"));
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