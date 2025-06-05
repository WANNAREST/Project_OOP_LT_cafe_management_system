package Controller;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

public class PaymentAPI {
    private static final String API_URL = "https://api.vietqr.io/v2/generate";
    private static final String CLIENT_ID = "2d3f23d0-22d8-47ad-91f9-e4f2c37f1d36";
    private static final String CLIENT_SECRET = "4e149f82-3610-4e4f-a00a-57f7ed639064";

    // Centralized account information
    public static final String ACCOUNT_NO = "0862853702";
    public static final String ACCOUNT_NAME = "NGUYEN MINH QUAN";
    public static final String BANK_NAME = "MB Bank";

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

    public static void main(String[] args) {
        try {
            String qrCodeUrl = generateQRCode(50000, "TEST123", "970422");
            System.out.println("Generated QR Code URL: " + qrCodeUrl);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}