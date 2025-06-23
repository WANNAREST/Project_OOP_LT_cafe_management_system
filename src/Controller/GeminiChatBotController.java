package Controller;

import obj.Coffee;
import obj.GeminiChatBot;
import obj.Product;
import obj.Store;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class GeminiChatBotController {
    private GeminiChatBot chatBot;
    private Store store;
    private Scanner scanner;
    private boolean isRunning;

    public GeminiChatBotController() {
        this.scanner = new Scanner(System.in);
        this.isRunning = false;
        initializeStore();
        this.chatBot = new GeminiChatBot(store);
    }

    /**
     * Initialize the store with sample products for testing
     */
    private void initializeStore() {
        this.store = new Store();

        // Add sample products for testing
        store.addProduct(new Coffee("Cà phê đen", "Beverage", 25000));
        store.addProduct(new Coffee("Cà phê sữa", "Beverage", 30000));
        store.addProduct(new Coffee("Cappuccino", "Beverage", 45000));
        store.addProduct(new Coffee("Espresso", "Beverage", 35000));
        store.addProduct(new Coffee("Americano", "Beverage", 40000));
        store.addProduct(new Coffee("Latte", "Beverage", 50000));

        // Set quantities and descriptions after creation
        for (Product product : store.getItemsInStore()) {
            product.setQuantity(50);
            switch (product.getName()) {
                case "Cà phê đen":
                    product.setDescription("Cà phê đen truyền thống, đậm đà");
                    break;
                case "Cà phê sữa":
                    product.setDescription("Cà phê sữa ngọt ngào");
                    break;
                case "Cappuccino":
                    product.setDescription("Cappuccino Ý với bọt sữa mịn");
                    break;
                case "Espresso":
                    product.setDescription("Espresso đậm đà chuẩn Ý");
                    break;
                case "Americano":
                    product.setDescription("Americano nhẹ nhàng");
                    break;
                case "Latte":
                    product.setDescription("Latte với nghệ thuật latte art");
                    break;
            }
        }

        System.out.println(" Store initialized with " + store.getItemsInStore().size() + " products");
    }

    /**
     * Start interactive chat session
     */
    public void startInteractiveChat() {
        isRunning = true;

        System.out.println("🤖=======================================");
        System.out.println("    GEMINI CHATBOT TEST CONTROLLER");
        System.out.println("=======================================");
        System.out.println("💬 Bắt đầu chat với OOP Coffee Bot!");
        System.out.println(" Gõ 'exit' để thoát");
        System.out.println(" Gõ 'test' để chạy các test tự động");
        System.out.println(" Gõ 'menu' để xem menu");
        System.out.println("=======================================\n");

        while (isRunning) {
            System.out.print("👤 Bạn: ");
            String userInput = scanner.nextLine().trim();

            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("👋 Cảm ơn bạn đã sử dụng OOP Coffee Bot!");
                isRunning = false;
                break;
            }

            if (userInput.equalsIgnoreCase("test")) {
                runAutomatedTests();
                continue;
            }

            if (userInput.equalsIgnoreCase("menu")) {
                displayMenu();
                continue;
            }

            if (userInput.isEmpty()) {
                System.out.println(" Vui lòng nhập câu hỏi của bạn.");
                continue;
            }

            // Get response from chatbot
            getChatResponse(userInput);
        }
    }

    /**
     * Get response from chatbot with loading indicator
     */
    private void getChatResponse(String userMessage) {
        System.out.print("🤖 Bot đang suy nghĩ");

        // Show loading animation
        CompletableFuture<String> responseTask = CompletableFuture.supplyAsync(() -> {
            return chatBot.getChatResponse(userMessage);
        });

        // Loading animation
        CompletableFuture<Void> loadingTask = CompletableFuture.runAsync(() -> {
            try {
                for (int i = 0; i < 10 && !responseTask.isDone(); i++) {
                    System.out.print(".");
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        try {
            String response = responseTask.get(30, TimeUnit.SECONDS);
            loadingTask.cancel(true);
            System.out.println("\n🤖 Bot: " + response + "\n");

        } catch (Exception e) {
            loadingTask.cancel(true);
            System.out.println("\n Lỗi: " + e.getMessage());
            System.out.println("🔧 Kiểm tra kết nối mạng và API key.\n");
        }
    }

    /**
     * Display current menu
     */
    private void displayMenu() {
        System.out.println("\n☕ MENU OOP COFFEE:");
        System.out.println("========================");
        for (int i = 0; i < store.getItemsInStore().size(); i++) {
            Product product = store.getItemsInStore().get(i);
            System.out.printf("%d. %s - %,dđ\n",
                    i + 1, product.getName(), (int)product.getPrice());
        }
        System.out.println("========================\n");
    }

    /**
     * Run automated tests with predefined questions
     */
    public void runAutomatedTests() {
        System.out.println("\n🧪 CHẠY CÁC TEST TỰ ĐỘNG...\n");

        String[] testQuestions = {
                "Xin chào, bạn có thể giới thiệu về cửa hàng không?",
                "Menu của cửa hàng có những gì?",
                "Tôi muốn uống cà phê đậm đà, bạn gợi ý gì?",
                "Giá của cappuccino là bao nhiêu?",
                "Cửa hàng mở cửa lúc mấy giờ?",
                "Tôi có thể tích điểm không?",
                "Cà phê nào ngọt nhất?",
                "Bạn có thể gợi ý đồ uống cho người mới?",
                "Espresso và Americano khác nhau như thế nào?",
                "Cảm ơn bạn!"
        };

        for (int i = 0; i < testQuestions.length; i++) {
            System.out.println(" Test " + (i + 1) + "/10:");
            System.out.println("👤 " + testQuestions[i]);

            String response = chatBot.getChatResponse(testQuestions[i]);
            System.out.println("🤖 " + response);
            System.out.println("------------------------------------------------------------");

            // Small delay between tests
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println(" Hoàn thành tất cả các test!");
        System.out.println("💡 Quay lại chat tương tác bằng cách nhập câu hỏi mới.\n");
    }

    /**
     * Test single question (for programmatic testing)
     */
    public String testSingleQuestion(String question) {
        System.out.println("🧪 Testing: " + question);
        String response = chatBot.getChatResponse(question);
        System.out.println("🤖 Response: " + response);
        return response;
    }

    /**
     * Validate API configuration
     */
    public boolean validateConfiguration() {
        System.out.println(" Kiểm tra cấu hình...");

        try {
            // Test with a simple question
            String testResponse = chatBot.getChatResponse("Xin chào");

            if (testResponse.contains("Có lỗi xảy ra") ||
                    testResponse.contains("không thể trả lời")) {
                System.out.println(" Cấu hình không đúng hoặc API key không hợp lệ");
                return false;
            }

            System.out.println(" Cấu hình hợp lệ!");
            return true;

        } catch (Exception e) {
            System.out.println(" Lỗi cấu hình: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get store instance for external access
     */
    public Store getStore() {
        return store;
    }

    /**
     * Get chatbot instance for external access
     */
    public GeminiChatBot getChatBot() {
        return chatBot;
    }

    /**
     * Add custom product for testing
     */
    public void addTestProduct(String name, int price, int quantity, String description) {
        Coffee newCoffee = new Coffee(name, "Beverage", price);
        newCoffee.setQuantity(quantity);
        newCoffee.setDescription(description);
        store.addProduct(newCoffee);
        System.out.println(" Added test product: " + name);
    }

    /**
     * Main method for standalone testing
     */
    public static void main(String[] args) {
        GeminiChatBotController controller = new GeminiChatBotController();

        // Validate configuration first
        if (controller.validateConfiguration()) {
            controller.startInteractiveChat();
        } else {
            System.out.println(" Vui lòng kiểm tra cấu hình API key trong config.properties");
            System.out.println(" Thêm dòng: gemini.api.key=YOUR_API_KEY");
        }
    }
}