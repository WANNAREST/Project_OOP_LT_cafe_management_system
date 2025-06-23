package Controller;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class PaymentAPI {
    private static String CASSO_API_URL;
    private static String CASSO_API_KEY;
    private static String API_URL;
    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static String ACCOUNT_NO;
    private static String ACCOUNT_NAME;
    private static String BANK_NAME;

    static {
        try (InputStream input = PaymentAPI.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            API_URL = prop.getProperty("api.url");
            CASSO_API_URL = prop.getProperty("casso.api.url");
            CASSO_API_KEY = prop.getProperty("casso.api.key");
            CLIENT_ID = prop.getProperty("client.id");
            CLIENT_SECRET = prop.getProperty("client.secret");
            ACCOUNT_NO = prop.getProperty("account.no");
            ACCOUNT_NAME = prop.getProperty("account.name");
            BANK_NAME = prop.getProperty("bank.name");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String generateQRCode(int amount, String orderId, String bankCode) throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("accountNo", ACCOUNT_NO);
        requestBody.put("accountName", ACCOUNT_NAME);
        requestBody.put("acqId", bankCode);
        requestBody.put("amount", amount);
        requestBody.put("addInfo", "Thanh toan don hang #" + orderId);
        requestBody.put("format", "compact2"); // This returns a proper QR image URL

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(API_URL);

        // Add headers
        post.setHeader("Content-Type", "application/json");
        post.setHeader("x-client-id", CLIENT_ID);
        post.setHeader("x-api-key", CLIENT_SECRET);

        try {
            post.setEntity(new StringEntity(requestBody.toString(), "UTF-8"));
            String response = client.execute(post, new BasicResponseHandler());
            JSONObject jsonResponse = new JSONObject(response);

            // Log for debugging
            System.out.println("API Response: " + jsonResponse.toString());

            // Check if request was successful
            if (jsonResponse.has("code")) {
                String code = jsonResponse.getString("code");
                if (!"00".equals(code)) {
                    String desc = jsonResponse.optString("desc", "Unknown error");
                    throw new Exception("API Error: " + code + " - " + desc);
                }
            }

            // Try to get QR code from response
            if (jsonResponse.has("data")) {
                JSONObject data = jsonResponse.getJSONObject("data");

                // Check for qrDataURL first (image URL)
                if (data.has("qrDataURL") && !data.isNull("qrDataURL")) {
                    return data.getString("qrDataURL");
                }

                // If qrDataURL is null, check for qrCode (string data)
                if (data.has("qrCode") && !data.isNull("qrCode")) {
                    return data.getString("qrCode");
                }
            }

            // Fallback: check top level
            if (jsonResponse.has("qrDataURL") && !jsonResponse.isNull("qrDataURL")) {
                return jsonResponse.getString("qrDataURL");
            }

            // If no QR found, throw descriptive error
            throw new Exception("QR code not found in API response: " + jsonResponse.toString());

        } finally {
            client.close();
        }
    }

    public static boolean verifyTransaction(int expectedAmount, String orderId) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = CASSO_API_URL + "/transactions?sort=-created_at&limit=10&page=1";
        HttpGet get = new HttpGet(url);


        System.out.println("Request URL: " + get.getURI());
        System.out.println("Request Headers: " + Arrays.toString(get.getAllHeaders()));

        get.setHeader("Authorization", "Apikey " + CASSO_API_KEY);
        get.setHeader("Content-Type", "application/json");
        get.setHeader("Accept", "application/json");


        try {
            String response = client.execute(get, new BasicResponseHandler());
            System.out.println("API Response: " + response); // Log response để debug

            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.has("error") && jsonResponse.getInt("error") != 0) {
                String message = jsonResponse.optString("message", "Unknown error");
                throw new Exception("Casso API Error: " + jsonResponse.getInt("error") + " - " + message);
            }

            if (jsonResponse.has("data")) {
                JSONObject data = jsonResponse.getJSONObject("data");

                // Kiểm tra xem có records không
                if (!data.has("records")) {
                    System.out.println("No records field in response");
                    return false;
                }

                JSONArray transactions = data.getJSONArray("records");
                System.out.println("Found " + transactions.length() + " transactions");

                // Nếu không có giao dịch nào
                if (transactions.length() == 0) {
                    System.out.println("No transactions found");
                    return false;
                }

                for (int i = 0; i < transactions.length(); i++) {
                    JSONObject transaction = transactions.getJSONObject(i);
                    int amount = transaction.getInt("amount");
                    String description = transaction.getString("description").toLowerCase();
                    String reference = transaction.optString("reference", "").toLowerCase();

                    System.out.println("Checking transaction: Amount=" + amount +
                            ", Description=" + description +
                            ", Reference=" + reference);



                    boolean amountMatch = (amount == expectedAmount);
                    boolean contentMatch = false;

                    // Tạo các pattern để tìm kiếm
                    String orderIdLower = orderId.toLowerCase();
                    String[] searchPatterns = {
                            orderIdLower,
                            "don hang " + orderIdLower,
                            "donhang" + orderIdLower,
                            orderIdLower.replace("order", ""),
                            orderIdLower.replace("_", ""),
                    };

                    // Kiểm tra trong description
                    for (String pattern : searchPatterns) {
                        if (description.contains(pattern)) {
                            contentMatch = true;
                            System.out.println("  ✓ Found pattern '" + pattern + "' in description");
                            break;
                        }
                    }

                    // Kiểm tra trong reference nếu chưa tìm thấy
                    if (!contentMatch) {
                        for (String pattern : searchPatterns) {
                            if (reference.contains(pattern)) {
                                contentMatch = true;
                                System.out.println("  ✓ Found pattern '" + pattern + "' in reference");
                                break;
                            }
                        }
                    }

                    System.out.println("  Amount match: " + amountMatch);
                    System.out.println("  Content match: " + contentMatch);

                    if (amountMatch && contentMatch) {
                        System.out.println(" Transaction matched!");
                        return true;
                    }

                }
            }
            return false;
        }catch (Exception e) {
            System.out.println("Casso API Error: " + e.getMessage());
            throw e;
        }

        finally {
            client.close();

        }
    }




    public static void main(String[] args) {
        try {
           // String qrCodeUrl = generateQRCode(50000, "TEST123", "970422");
            //System.out.println("Generated QR Code URL: " + qrCodeUrl);

          //  boolean isValid = verifyTransaction(50000, "TEST123");
           // System.out.println("Transaction verification result: " + isValid);
            String orderId = "TEST" + System.currentTimeMillis(); // Tạo orderId unique
            String qrCodeUrl = generateQRCode(10000, orderId, "970422");
            System.out.println("=== BƯỚC 1: TẠO QR CODE ===");
            System.out.println("Order ID: " + orderId);
            System.out.println("Generated QR Code URL: " + qrCodeUrl);
            System.out.println("Amount: 50,000 VND");
            System.out.println("Content: Thanh toan don hang #" + orderId);

            // Bước 2: Hướng dẫn user
            System.out.println("\n=== BƯỚC 2: THỰC HIỆN THANH TOÁN ===");
            System.out.println("Hãy:");
            System.out.println("1. Mở app ngân hàng");
            System.out.println("2. Quét QR code hoặc truy cập URL: " + qrCodeUrl);
            System.out.println("3. Chuyển khoản 10,000 VND với nội dung: Thanh toan don hang " + orderId);

            // Bước 3: Chờ user thanh toán
            System.out.println("\n=== BƯỚC 3: CHỜ THANH TOÁN ===");
            System.out.println("Nhấn Enter sau khi đã chuyển khoản...");
            System.in.read(); // Chờ user nhấn Enter

            // Bước 4: Verify transaction
            System.out.println("\n=== BƯỚC 4: KIỂM TRA GIAO DỊCH ===");
            boolean isValid = verifyTransaction(10000, orderId);
            System.out.println("Transaction verification result: " + isValid);

            if (isValid) {
                System.out.println(" Thanh toán thành công!");
            } else {
                System.out.println(" Không tìm thấy giao dịch. Có thể:");
                System.out.println("- Chưa chuyển khoản");
                System.out.println("- Sai số tiền");
                System.out.println("- Sai nội dung chuyển khoản");
                System.out.println("- Cần đợi thêm vài phút để hệ thống cập nhật");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}