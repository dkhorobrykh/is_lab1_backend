package ru.itmo.is.lab1.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.is.lab1.exception.CustomException;
import ru.itmo.is.lab1.exception.ExceptionEnum;
import ru.itmo.is.lab1.model.User;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

import static java.time.temporal.ChronoUnit.MINUTES;

@Slf4j
@ApplicationScoped
public class JwtProvider {
    private final PrivateKey accessPrivate;
    private final RSAPublicKey accessPublic;

    private final PrivateKey refreshPrivate;
    private final RSAPublicKey refreshPublic;

    public JwtProvider() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String accessPr = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCi+X9AMJzDL4XtBbAMhqDlm5gQXQfDM+n9UyjDSUYio7rSmAEX/SlRQKk//8xC7prN9sVtcVxfNODpelpr9/PrgtOcZ6RHv9UWQUhlRIKTqvEvk95GodC6EOzyi9AeAk0ucc1zRZOOfFZ3WwB9oEqTm/26o+IIkSs4IKAW2qjaECAkaZnmzBrsTDwKJ5Yjrpwdz2/qVMhhjKy9cRJtYEzKox2VRdsoO26TLGgsZSsHjjv4LHenHFDkzFGif6HxUEOTIuQ+VXjrBKRzxgt2U1VNaTX0X1pMIYiwCQGFG8jtKh9MiFpdSRoSvKsSA1/27MZX9gsam5l9sHfzbWjRWqNDAgMBAAECggEANG7q9t7tAIsgnnJPAqipyVV25gTYQ4BHTt5bZgEEFI7yw2BuMaacjWytR07oC7yyh4NWX3CyVFsx5UwdHVGH44SSymY532+jgeZ9AT+6dwHvWOxM/hJ/ke1yHlLHIXCCHmg0vlprL9NnCj13+fMBK15rkom5ZiI2cndkqhWtb4JswvRxyZX5ACsrQf2M8F7mXDM2Q59U0KTgsdd9kGIXMV0LHEVOCPJwIUsfAfkn/A+3EfsW7xvL+FPO56ABf3K+tyoxVgmdlTGbeJHsax3vY+3UeMI+ys6bADnlyTw+gSzp3DZ7rm4Ib+bjN4or+rvmzf1IRJ720eBF2OAyU9KZSQKBgQD3r/epqH9+cv+aG10KU0unpLYH+d7lJbbquKMyEMy1VezFBQBl+4MFLQ5+WJJhqpONWxnF/U67qqYZnKqyHSIAycG3W0TWNqySwO092dZKXPtrrugXizUBQGji54fwJr3dELnKtUswVyZTKu5OGoJVqc2DX8gxJ2uIM/mc00kzLQKBgQCocbX1E0LayAvEtCBpFzuxc6DyOyis8zvKHtU9xDT903wS7eA+bSYPJLuTu2rVPzZa6jeUlRfc1dDmHLHrc9L6+MSxF1WBfPP0rb4kFNQ8UdLvSHr4JPzsE9zNRFm6uF7LJl+N4nfNLVj95M6hsDkH0SLuDuCfxrifW7kPiNH2LwKBgCS9NDY1KRwfgKxiLK3QZyNpuhp6xMYmjvhIBOTzOvoW0bTzpfh6kxecdE7nYmccPyCzh6InjZsml9aqt2MOETIeux4waoh/uZ9XAJmxCykim6SAYCrF2kniPOyTPUWw6W62AWkA9TMpaiTh9FEfe3+rXTGEbpRj7X5Zf0f0yyHNAoGALykvJb/PzwuFZ/x6upG7r9JM9KUlnU64pb1+PL3rjEmjCnW5fCD5L/I75n/pyGzbXhcHmr0nojfnNhZApkiWgJgYEfETip6eexoOC3Idfgn2wfIMI9/PRJXHE12YtdObDsveow4ONs8EhImEZPa+8PZYDkjyESoFVPDMXpITHxcCgYBTls4ICkD3Mz2lkAWXE962aDRQvogImUXf7NwEhfKXQZoNKYCMv6vB21a/s8BARpzIONrUn0TK7cI7rZ9lMGV/VesjCSC3v5HJy6sJvVGkYQkMSHcZHsgG4AcC2f7CEYvzPwTSby/tmj5VeyrqENUxVHG8z4iEaMJgYfKgUZTW6Q==";
        String accessPub = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAovl/QDCcwy+F7QWwDIag5ZuYEF0HwzPp/VMow0lGIqO60pgBF/0pUUCpP//MQu6azfbFbXFcXzTg6Xpaa/fz64LTnGekR7/VFkFIZUSCk6rxL5PeRqHQuhDs8ovQHgJNLnHNc0WTjnxWd1sAfaBKk5v9uqPiCJErOCCgFtqo2hAgJGmZ5swa7Ew8CieWI66cHc9v6lTIYYysvXESbWBMyqMdlUXbKDtukyxoLGUrB447+Cx3pxxQ5MxRon+h8VBDkyLkPlV46wSkc8YLdlNVTWk19F9aTCGIsAkBhRvI7SofTIhaXUkaEryrEgNf9uzGV/YLGpuZfbB3821o0VqjQwIDAQAB";
        String refreshPr = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCi+X9AMJzDL4XtBbAMhqDlm5gQXQfDM+n9UyjDSUYio7rSmAEX/SlRQKk//8xC7prN9sVtcVxfNODpelpr9/PrgtOcZ6RHv9UWQUhlRIKTqvEvk95GodC6EOzyi9AeAk0ucc1zRZOOfFZ3WwB9oEqTm/26o+IIkSs4IKAW2qjaECAkaZnmzBrsTDwKJ5Yjrpwdz2/qVMhhjKy9cRJtYEzKox2VRdsoO26TLGgsZSsHjjv4LHenHFDkzFGif6HxUEOTIuQ+VXjrBKRzxgt2U1VNaTX0X1pMIYiwCQGFG8jtKh9MiFpdSRoSvKsSA1/27MZX9gsam5l9sHfzbWjRWqNDAgMBAAECggEANG7q9t7tAIsgnnJPAqipyVV25gTYQ4BHTt5bZgEEFI7yw2BuMaacjWytR07oC7yyh4NWX3CyVFsx5UwdHVGH44SSymY532+jgeZ9AT+6dwHvWOxM/hJ/ke1yHlLHIXCCHmg0vlprL9NnCj13+fMBK15rkom5ZiI2cndkqhWtb4JswvRxyZX5ACsrQf2M8F7mXDM2Q59U0KTgsdd9kGIXMV0LHEVOCPJwIUsfAfkn/A+3EfsW7xvL+FPO56ABf3K+tyoxVgmdlTGbeJHsax3vY+3UeMI+ys6bADnlyTw+gSzp3DZ7rm4Ib+bjN4or+rvmzf1IRJ720eBF2OAyU9KZSQKBgQD3r/epqH9+cv+aG10KU0unpLYH+d7lJbbquKMyEMy1VezFBQBl+4MFLQ5+WJJhqpONWxnF/U67qqYZnKqyHSIAycG3W0TWNqySwO092dZKXPtrrugXizUBQGji54fwJr3dELnKtUswVyZTKu5OGoJVqc2DX8gxJ2uIM/mc00kzLQKBgQCocbX1E0LayAvEtCBpFzuxc6DyOyis8zvKHtU9xDT903wS7eA+bSYPJLuTu2rVPzZa6jeUlRfc1dDmHLHrc9L6+MSxF1WBfPP0rb4kFNQ8UdLvSHr4JPzsE9zNRFm6uF7LJl+N4nfNLVj95M6hsDkH0SLuDuCfxrifW7kPiNH2LwKBgCS9NDY1KRwfgKxiLK3QZyNpuhp6xMYmjvhIBOTzOvoW0bTzpfh6kxecdE7nYmccPyCzh6InjZsml9aqt2MOETIeux4waoh/uZ9XAJmxCykim6SAYCrF2kniPOyTPUWw6W62AWkA9TMpaiTh9FEfe3+rXTGEbpRj7X5Zf0f0yyHNAoGALykvJb/PzwuFZ/x6upG7r9JM9KUlnU64pb1+PL3rjEmjCnW5fCD5L/I75n/pyGzbXhcHmr0nojfnNhZApkiWgJgYEfETip6eexoOC3Idfgn2wfIMI9/PRJXHE12YtdObDsveow4ONs8EhImEZPa+8PZYDkjyESoFVPDMXpITHxcCgYBTls4ICkD3Mz2lkAWXE962aDRQvogImUXf7NwEhfKXQZoNKYCMv6vB21a/s8BARpzIONrUn0TK7cI7rZ9lMGV/VesjCSC3v5HJy6sJvVGkYQkMSHcZHsgG4AcC2f7CEYvzPwTSby/tmj5VeyrqENUxVHG8z4iEaMJgYfKgUZTW6Q==";
        String refreshPub = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAovl/QDCcwy+F7QWwDIag5ZuYEF0HwzPp/VMow0lGIqO60pgBF/0pUUCpP//MQu6azfbFbXFcXzTg6Xpaa/fz64LTnGekR7/VFkFIZUSCk6rxL5PeRqHQuhDs8ovQHgJNLnHNc0WTjnxWd1sAfaBKk5v9uqPiCJErOCCgFtqo2hAgJGmZ5swa7Ew8CieWI66cHc9v6lTIYYysvXESbWBMyqMdlUXbKDtukyxoLGUrB447+Cx3pxxQ5MxRon+h8VBDkyLkPlV46wSkc8YLdlNVTWk19F9aTCGIsAkBhRvI7SofTIhaXUkaEryrEgNf9uzGV/YLGpuZfbB3821o0VqjQwIDAQAB";

        KeyFactory kf = KeyFactory.getInstance("RSA");

        PKCS8EncodedKeySpec keySpecAPr = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(accessPr.getBytes()));
        PrivateKey accessPrivateKey = kf.generatePrivate(keySpecAPr);
        X509EncodedKeySpec keySpecAPub = new X509EncodedKeySpec(Base64.getDecoder().decode(accessPub.getBytes()));
        RSAPublicKey accessPublicKey = (RSAPublicKey) kf.generatePublic(keySpecAPub);

        PKCS8EncodedKeySpec keySpecRPr = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(refreshPr.getBytes()));
        PrivateKey refreshPrivateKey = kf.generatePrivate(keySpecRPr);
        X509EncodedKeySpec keySpecRPub = new X509EncodedKeySpec(Base64.getDecoder().decode(refreshPub.getBytes()));
        RSAPublicKey refreshPublicKey = (RSAPublicKey) kf.generatePublic(keySpecRPub);

        this.accessPrivate = accessPrivateKey;
        this.accessPublic = accessPublicKey;
        this.refreshPrivate = refreshPrivateKey;
        this.refreshPublic = refreshPublicKey;
    }

    public String generateAccessToken(User user) {
        final Instant accessExpirationInstant = Instant.now().plus(1000000, MINUTES);
        final Date accessExpiration = Date.from(accessExpirationInstant);

        return Jwts.builder()
                .setExpiration(accessExpiration)
                .signWith(accessPrivate, SignatureAlgorithm.RS256)
                .claim("userId", user.getId())
                .compact();
    }

    public String generateRefreshToken(User user) {
        final Instant refreshExpirationInstant = Instant.now().plus(60 * 24, ChronoUnit.MINUTES);
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setExpiration(refreshExpiration)
                .signWith(refreshPrivate, SignatureAlgorithm.RS256)
                .claim("userId", user.getId())
                .compact();
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, accessPublic);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, refreshPublic);
    }

    private boolean validateToken(String token, Key secret) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            log.info("Token \"%s\" expired".formatted(token));
            throw new CustomException(ExceptionEnum.TOKEN_CHECKING_ERROR);
        }
    }

    public Claims getAccessClaims(String token) {
        return getClaims(token, accessPublic);
    }

    public Claims getRefreshClaims(String token) {
        return getClaims(token, refreshPublic);
    }

    private Claims getClaims(String token, Key secret) {
        return Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
