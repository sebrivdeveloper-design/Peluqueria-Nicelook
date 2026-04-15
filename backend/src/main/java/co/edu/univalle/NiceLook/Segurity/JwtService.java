package co.edu.univalle.NiceLook.Segurity;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

import co.edu.univalle.NiceLook.model.Usuario;

@Service
public class JwtService {

    private final String SECRET = "clave_secreta_super_larga_123456789";

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(Usuario user) {

        return Jwts.builder()
                .setSubject(user.getCorreo())
                .claim("rol", user.getRol().getNombreRol()) // 🔥 IMPORTANTE
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 día
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractCorreo(String token) {
        return getClaims(token).getSubject();
    }

    public String extractRol(String token) {
        return getClaims(token).get("rol", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}