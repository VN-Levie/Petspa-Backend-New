package vn.aptech.petspa.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import vn.aptech.petspa.util.ZDebug;

@Service
public class AzureMapService {

    @Value("${azure.maps.api.key}")
    private String azureMapsApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> getCoordinatesFromAddress(String address) {
        try {
            String baseUrl = "https://atlas.microsoft.com/search/address/json";
            String url = UriComponentsBuilder.fromUriString(baseUrl)
                    .queryParam("subscription-key", azureMapsApiKey)
                    .queryParam("api-version", "1.0")
                    .toUriString() + "&query=" + address;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("API key is invalid or expired.");
            } else if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API key rate limit exceeded.");
            }
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error occurred.");
        }
    }

    public ResponseEntity<String> searchPlace(String query) {
        try {

            String baseUrl = "https://atlas.microsoft.com/search/fuzzy/json";

            String url = UriComponentsBuilder.fromUriString(baseUrl)
                    .queryParam("subscription-key", azureMapsApiKey)
                    .queryParam("api-version", "1.0")
                    .queryParam("typeahead", true)
                    // .queryParam("entityType", "Municipality")
                    .queryParam("limit", 5)
                    .toUriString() + "&query=" + query;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("API key is invalid or expired.");
            } else if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API key rate limit exceeded.");
            }
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error occurred.");
        }
    }

    public ResponseEntity<String> getAddressFromCoordinates(double lat, double lon) {
        try {
            String baseUrl = "https://atlas.microsoft.com/search/address/reverse/json";
            String url = UriComponentsBuilder.fromUriString(baseUrl)
                    .queryParam("subscription-key", azureMapsApiKey)
                    .queryParam("api-version", "1.0")
                    .toUriString() + "&query=" + lat + "," + lon;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("API key is invalid or expired.");
            } else if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API key rate limit exceeded.");
            }
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error occurred.");
        }
    }

    // Kiểm tra xem tọa độ có nằm trong Việt Nam không
    public boolean isInVietnam(double lat, double lon) {
        try {
            String baseUrl = "https://atlas.microsoft.com/search/address/reverse/json";
            String url = UriComponentsBuilder.fromUriString(baseUrl)
                    .queryParam("subscription-key", azureMapsApiKey)
                    .queryParam("api-version", "1.0")
                    .queryParam("query", lat + "," + lon)
                    .toUriString();

            // Gửi request
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // Kiểm tra trạng thái HTTP
            if (!response.getStatusCode().is2xxSuccessful()) {
                return false; // Trả về false nếu lỗi HTTP
            }

            // Xử lý JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonResponse = objectMapper.readValue(response.getBody(), new TypeReference<>() {
            });

            List<Map<String, Object>> addresses = objectMapper.convertValue(jsonResponse.get("addresses"),
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            // Kiểm tra dữ liệu
            if (addresses == null || addresses.isEmpty()) {
                return false; // Trả về false nếu không có địa chỉ nào
            }

            // Trích xuất thông tin quốc gia
            Map<String, Object> addressDetails = addresses.get(0);
            objectMapper = new ObjectMapper();
            Map<String, Object> addressInfo = objectMapper.convertValue(addressDetails.get("address"),
                    new TypeReference<Map<String, Object>>() {
                    });
            String country = (String) addressInfo.get("country");
            ZDebug.gI().ZigDebug("country: " + country);
            String[] vietNamEx = { "Việt Nam", "Vietnam" };
            boolean isVietNam = false;
            for (String s : vietNamEx) {
                if (country.equalsIgnoreCase(s)) {
                    isVietNam = true;
                    break;
                }
            }
            // So sánh với "vietNamEx"
            return isVietNam;
        } catch (Exception e) {
            // Ghi log lỗi chi tiết và trả về false
            e.printStackTrace();
            return false;
        }
    }

    public ResponseEntity<String> getRouteFromShopToUser(double shopLat, double shopLon, double userLat,
            double userLon) {
        try {
            String baseUrl = "https://atlas.microsoft.com/route/directions/json";
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("subscription-key", azureMapsApiKey)
                    .queryParam("api-version", "1.0")
                    .queryParam("query", shopLat + "," + shopLon + ":" + userLat + "," + userLon)
                    .queryParam("travelMode", "car")
                    .queryParam("traffic", "true") // Tính toán thời gian di chuyển dựa trên tình trạng giao thông hiện
                                                   // tại
                    .toUriString();

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("API key is invalid or expired.");
            } else if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API key rate limit exceeded.");
            }

            // Lấy dữ liệu từ response và xử lý chỉ trả về các thông tin cần thiết
            Map<String, Object> jsonResponse = new ObjectMapper().readValue(response.getBody(),
                    new TypeReference<Map<String, Object>>() {
                    });

            // Lấy thông tin từ summary của route
            List<Map<String, Object>> routes = new ObjectMapper().convertValue(jsonResponse.get("routes"),
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            Map<String, Object> routeSummary = new ObjectMapper().convertValue(routes.get(0).get("summary"),
                    new TypeReference<Map<String, Object>>() {
                    });

            // Trích xuất thông tin cần thiết
            int distanceInMeters = (int) routeSummary.get("lengthInMeters");
            int travelTimeInSeconds = (int) routeSummary.get("travelTimeInSeconds");
            String arrivalTime = (String) routeSummary.get("arrivalTime");

            // Tạo đối tượng để trả về
            Map<String, Object> result = new HashMap<>();
            result.put("distanceInMeters", distanceInMeters);
            result.put("travelTimeInSeconds", travelTimeInSeconds);
            result.put("arrivalTime", arrivalTime);
            // result.put("routes", routes);

            // Trả về kết quả đã xử lý
            return ResponseEntity.ok(new ObjectMapper().writeValueAsString(result));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error occurred.");
        }
    }

    public ResponseEntity<String> getRouteFromShopToUserDetail(double shopLat, double shopLon, double userLat,
            double userLon) {
        try {
            String baseUrl = "https://atlas.microsoft.com/route/directions/json";
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("subscription-key", azureMapsApiKey)
                    .queryParam("api-version", "1.0")
                    .queryParam("query", shopLat + "," + shopLon + ":" + userLat + "," + userLon)
                    .queryParam("travelMode", "car")
                    .queryParam("traffic", "true") // Tính toán thời gian di chuyển dựa trên tình trạng giao thông hiện
                                                   // tại
                    .toUriString();

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("API key is invalid or expired.");
            } else if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API key rate limit exceeded.");
            }

            // Lấy dữ liệu từ response và xử lý chỉ trả về các thông tin cần thiết
            Map<String, Object> jsonResponse = new ObjectMapper().readValue(response.getBody(),
                    new TypeReference<Map<String, Object>>() {
                    });

            // Lấy thông tin từ summary của route
            List<Map<String, Object>> routes = new ObjectMapper().convertValue(jsonResponse.get("routes"),
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            Map<String, Object> routeSummary = new ObjectMapper().convertValue(routes.get(0).get("summary"),
                    new TypeReference<Map<String, Object>>() {
                    });

            // Trích xuất thông tin cần thiết
            int distanceInMeters = (int) routeSummary.get("lengthInMeters");
            int travelTimeInSeconds = (int) routeSummary.get("travelTimeInSeconds");
            String arrivalTime = (String) routeSummary.get("arrivalTime");

            // Tạo đối tượng để trả về
            Map<String, Object> result = new HashMap<>();
            result.put("distanceInMeters", distanceInMeters);
            result.put("travelTimeInSeconds", travelTimeInSeconds);
            result.put("arrivalTime", arrivalTime);
            result.put("routes", routes);

            // Trả về kết quả đã xử lý
            return ResponseEntity.ok(new ObjectMapper().writeValueAsString(result));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error occurred.");
        }
    }

}