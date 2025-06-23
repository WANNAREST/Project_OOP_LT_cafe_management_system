package Controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import obj.GeminiChatBot;
import obj.Store;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatBotController implements Initializable {

    @FXML private VBox chatContainer;
    @FXML private ScrollPane chatScrollPane;
    @FXML private TextField messageField;
    @FXML private Button sendBtn;
    @FXML private Button menuBtn;
    @FXML private Button priceBtn;
    @FXML private Button pointBtn;
    @FXML private Button timeBtn;

    private GeminiChatBot chatBot;
    private Store store;

    public ChatBotController(Store store) {
        this.store = store;
        this.chatBot = new GeminiChatBot(store);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Welcome message
        addBotMessage("Xin chào! Tôi là chatbot của OOP Coffee ☕\n" +
                "Tôi có thể giúp bạn về menu, giá cả, tích điểm và nhiều thông tin khác!\n" +
                "Hãy hỏi tôi bất cứ điều gì bạn muốn biết!");

        // Auto scroll to bottom
        chatContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
        });
    }

    @FXML
    void sendMessage(ActionEvent event) {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            addUserMessage(message);
            messageField.clear();

            // Show typing indicator
            Label typingLabel = addTypingIndicator();

            // Send to AI in background thread
            Task<String> task = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    return chatBot.getChatResponse(message);
                }
            };

            task.setOnSucceeded(e -> {
                Platform.runLater(() -> {
                    chatContainer.getChildren().remove(typingLabel);
                    addBotMessage(task.getValue());
                });
            });

            task.setOnFailed(e -> {
                Platform.runLater(() -> {
                    chatContainer.getChildren().remove(typingLabel);
                    addBotMessage("Xin lỗi, có lỗi xảy ra. Vui lòng thử lại!");
                });
            });

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @FXML void askAboutMenu(ActionEvent event) {
        messageField.setText("Menu có những đồ uống gì?");
        sendMessage(event);
    }

    @FXML void askAboutPrice(ActionEvent event) {
        messageField.setText("Giá cả các đồ uống như thế nào?");
        sendMessage(event);
    }

    @FXML void askAboutPoints(ActionEvent event) {
        messageField.setText("Làm sao để tích điểm?");
        sendMessage(event);
    }

    @FXML void askAboutTime(ActionEvent event) {
        messageField.setText("Cửa hàng mở cửa lúc mấy giờ?");
        sendMessage(event);
    }

    private void addUserMessage(String message) {
        Label label = new Label(message);
        label.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; " +
                "-fx-padding: 10px; -fx-background-radius: 15px; " +
                "-fx-font-size: 14px;");
        label.setWrapText(true);
        label.setMaxWidth(600);

        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_RIGHT);
        container.getChildren().add(label);
        container.setPadding(new Insets(5, 10, 5, 10));

        chatContainer.getChildren().add(container);
    }

    private void addBotMessage(String message) {
        Label label = new Label("🤖 " + message);
        label.setStyle("-fx-background-color: #e9ecef; -fx-text-fill: #333; " +
                "-fx-padding: 10px; -fx-background-radius: 15px; " +
                "-fx-font-size: 14px;");
        label.setWrapText(true);
        label.setMaxWidth(600);

        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);
        container.getChildren().add(label);
        container.setPadding(new Insets(5, 10, 5, 10));

        chatContainer.getChildren().add(container);
    }

    private Label addTypingIndicator() {
        Label label = new Label("🤖 Đang trả lời...");
        label.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #666; " +
                "-fx-padding: 10px; -fx-background-radius: 15px; " +
                "-fx-font-size: 14px; -fx-font-style: italic;");

        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);
        container.getChildren().add(label);
        container.setPadding(new Insets(5, 10, 5, 10));

        chatContainer.getChildren().add(container);
        return label;
    }
}