package stud.ntnu.no.krisefikser.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Service class responsible for handling reCAPTCHA verification.
 * <p>
 * This service verifies the reCAPTCHA token received from the client
 * by sending a request to the Google reCAPTCHA API.
 * </p>
 */
@Service
public class CaptchaService {
  private static final Logger logger = Logger.getLogger(CaptchaService.class.getName());
  private static final String SECRET_KEY = System.getenv("RECAPTCHA_SECRET_KEY");
  private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

  /**
   * Verifies the reCAPTCHA token by sending a request to the Google reCAPTCHA API.
   *
   * @param token the reCAPTCHA token to verify
   * @return true if the token is valid, false otherwise
   */
  public boolean verifyToken(String token) {
    logger.info("Verifying reCAPTCHA token: " + token);
    logger.info("Using secret key: " + SECRET_KEY);
    RestTemplate restTemplate = new RestTemplate();
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("secret", SECRET_KEY);
    params.add("response", token);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
    ResponseEntity<Map> response = restTemplate.postForEntity(VERIFY_URL, request, Map.class);

    Map body = response.getBody();
    return body != null && (Boolean) body.get("success");
  }
}
