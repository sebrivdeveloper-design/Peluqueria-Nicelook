package co.edu.univalle.NiceLook.Segurity;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import co.edu.univalle.NiceLook.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final String SECRET = "clave_secreta_super_larga_123456789";

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(Usuario user, Integer idCliente, Integer idEmpleado) {
    return Jwts.builder()
            .setSubject(user.getCorreo())
            .claim("rol", user.getRol().getNombreRol())
            .claim("nombreCompleto", user.getNombreCompleto())
            .claim("idCliente", idCliente)
            .claim("idEmpleado", idEmpleado)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000))
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