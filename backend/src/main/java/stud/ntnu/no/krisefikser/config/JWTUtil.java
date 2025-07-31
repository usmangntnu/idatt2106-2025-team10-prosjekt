package stud.ntnu.no.krisefikser.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility service for handling JWT token creation, parsing, and validation.
 * <p>
 * This class provides methods to:
 * <ul>
 *   <li>Generate JWT tokens for authenticated users.</li>
 *   <li>Extract claims such as username from tokens.</li>
 *   <li>Validate tokens to ensure their authenticity and expiry status.</li>
 * </ul>
 * It is primarily used in the authentication and authorization flow of the application.
 */
@Service
public class JWTUtil {
  //TODO add secret key and token expiration in application.properties

  @Value("${security.jwt.secret-key}")
  private String secretKey;
  private static final Logger logger = LogManager.getLogger(JWTUtil.class);

  /**
   * Generates a JWT token for the specified user.
   *
   * @param userDetails the user details containing the username
   * @return a signed JWT token
   */
  public String generateToken(UserDetails userDetails) {
    return generateJwtToken(userDetails);
  }

  /**
   * Generates a JWT token for the specified user with a custom expiration time.
   *
   * @param userDetails    the user details containing the username
   * @return a signed JWT token
   */
  private String generateJwtToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList());
    System.out.println("Generated claims: " + claims);
    logger.info("Generating JWT token for user: " + userDetails.getUsername());
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * Duration.ofDays(365).toSeconds()))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Extracts the username (subject) from the provided JWT token.
   *
   * @param token the JWT token
   * @return the username contained in the token
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Retrieves the cryptographic signing key used for token generation and validation.
   *
   * @return the signing key
   */
  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  /**
   * Extracts a specific claim from the provided JWT token.
   *
   * @param token          the JWT token
   * @param claimsResolver a function to extract the desired claim from the token's claims
   * @param <T>            the type of the claim
   * @return the extracted claim
   */
  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    logger.info("Extracting claims from token: " + token);
    final Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    T ts = claimsResolver.apply(claims);
    logger.info("Extracted claims: " + ts);
    return claimsResolver.apply(claims);
  }

  /**
   * Validates the provided JWT token.
   * <p>
   * A token is considered valid if it is correctly signed.
   *
   * @param token the JWT token
   * @return {@code true} if the token is valid, {@code false} otherwise
   */
  public boolean isTokenValid(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token)
          .getBody();

      return true;
    } catch (JwtException e) {
      return false;
    }
  }
}
