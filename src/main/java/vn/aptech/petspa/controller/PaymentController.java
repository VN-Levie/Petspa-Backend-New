package vn.aptech.petspa.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.management.relation.Role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import vn.aptech.petspa.dto.LoginDTO;
import vn.aptech.petspa.dto.PaymentDTO;
import vn.aptech.petspa.dto.RegisterDTO;
import vn.aptech.petspa.dto.UserDTO;
import vn.aptech.petspa.dto.VerifyDTO;
import vn.aptech.petspa.entity.Order;
import vn.aptech.petspa.entity.User;
import vn.aptech.petspa.repository.UserRepository;
import vn.aptech.petspa.service.EmailService;
import vn.aptech.petspa.service.OrderService;
import vn.aptech.petspa.service.PaymentService;
import vn.aptech.petspa.util.ApiResponse;
import vn.aptech.petspa.util.JwtUtil;
import vn.aptech.petspa.util.OrderStatusType;
import vn.aptech.petspa.util.ZDebug;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    String vnp_Version = "2.1.0";
    String vnp_Command = "pay";
    String orderType = "other";
    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    // vnpay.vnp_TmnCode=CIII2H79
    // vnpay.vnp_HashSecret=8NP378YROF833PY8KXBSZKVP7W18SZ74

    @Value("${vnpay.vnp_TmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.vnp_HashSecret}")
    private String vnp_HashSecret;

    @Autowired
    private Environment environment;
    private String port;

    @Autowired
    PaymentService paymentService;

    @Autowired
    OrderService orderService;

    @PostConstruct
    public void init() {
        this.port = environment.getProperty("server.port");
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    @PostMapping("/create-payment")
    public ResponseEntity<ApiResponse> createPay(@RequestParam int orderId, @RequestParam String ip) {
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.ok(new ApiResponse("error"));
            }
            long amount = (long) (order.getTotalPrice() * 100) * 23_000;
            String callBack = "http://localhost:" + 3000 + "/payment/vnpay_ipn";
            String payUrl = paymentService.createPaymentUrl(amount, orderId, callBack, ip, callBack);
            return ResponseEntity.ok(new ApiResponse(payUrl));
        } catch (Exception e) {
            return ApiResponse.internalServerError(e.getMessage());
        }
    }

    public String hashAllFields(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(fieldValue);
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        return hmacSHA512(vnp_HashSecret, sb.toString());
    }

    // vnpay_ipn
    @GetMapping("/vnpay_ipn")
    public ResponseEntity<ApiResponse> vnpay_ipn(@RequestParam Map<String, String> allParams) {
        try {
            // Example response from vnpay
            // {
            // "vnp_Amount": "1000000",
            // "vnp_BankCode": "NCB",
            // "vnp_BankTranNo": "VNP14788608",
            // "vnp_CardType": "ATM",
            // "vnp_OrderInfo": "Thanh toan don hang10391115",
            // "vnp_PayDate": "20250111032710",
            // "vnp_ResponseCode": "00",
            // "vnp_TmnCode": "CIII2H79",
            // "vnp_TransactionNo": "14788608",
            // "vnp_TransactionStatus": "00",
            // "vnp_TxnRef": "10391115",
            // "vnp_SecureHash":
            // "f68f584feb09bf55d0c818e5672d131a939e823223af73c9647655f346d706d43b8275dcc31dfec7b7b990f49e533a26bf1b6826abd85709683b3043b6995ee7"
            // }
            if (!verifyTransactionHash(allParams)) {
                return ApiResponse.badRequest("Invalid hash");
            }

            long orderId = Long.parseLong(allParams.get("vnp_TxnRef"));
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return ApiResponse.badRequest("Error while processing order id");
            }

            if (order.getStatus() != OrderStatusType.PENDING) {
                // return ApiResponse.badRequest("This order has been processed");
                return ResponseEntity.ok(new ApiResponse("This order has been processed successfully"));
            }

            // lấy mã phản hồi vnp_ResponseCode
            String vnp_ResponseCode = allParams.get("vnp_ResponseCode");

            // lấy mã phản hồi vnp_TransactionStatus
            String vnpPaymentStatus = allParams.get("vnp_TransactionStatus");

            // cập nhật trạng thái đơn hàng
            if (vnp_ResponseCode.equals("00") && vnpPaymentStatus.equals("00")) {
                order.setStatus(OrderStatusType.CONFIRMED);

                orderService.saveOrder(order);
                return ResponseEntity.ok(new ApiResponse("Payment success"));
            } else {
                order.setStatus(OrderStatusType.CANCELLED);
                return ApiResponse.badRequest("Payment failed");
            }

            // checkOrderStatus: 00: Thanh toán thành công, 01: Thanh toán thất bại, 02: Đã
            // hủy, 03: Đang chờ xử lý

        } catch (Exception e) {
            return ApiResponse.internalServerError(e.getMessage());
        }
    }

    private boolean verifyTransactionHash(Map<String, String> allParams) throws UnsupportedEncodingException {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = Collections.enumeration(allParams.keySet()); params.hasMoreElements();) {
            String fieldName = URLEncoder.encode((String) params.nextElement(),
                    StandardCharsets.US_ASCII.toString());
            String fieldValue = URLEncoder.encode(allParams.get(fieldName), StandardCharsets.US_ASCII.toString());
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = allParams.get("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }

        String signValue = hashAllFields(fields);
        // if (signValue.equals(vnp_SecureHash)) {
        // ZDebug.gI().ZigDebug("Valid hash");
        // } else {
        // ZDebug.gI().ZigDebug("Invalid hash: " + signValue + " - " + vnp_SecureHash);
        // }
        return signValue.equals(vnp_SecureHash);
    }

    // check and update order status
    @GetMapping("/check-order-status")
    public ResponseEntity<ApiResponse> checkOrderStatus(@RequestParam int orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.ok(new ApiResponse("error"));
            }
            if (order.getStatus() == OrderStatusType.CONFIRMED) {
                return ResponseEntity.ok(new ApiResponse("success"));
            }
            return ResponseEntity.ok(new ApiResponse("error"));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse("error"));
        }
    }
}
