package store.auth;


import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import store.account.AccountOut;

@FeignClient(
    name="auth",
    url="http://auth:8080"
)
public interface AuthController {

    public static String AUTH_COOKIE_TOKEN = "__store_jwt_token";

    @PostMapping("/auth/login")
    public ResponseEntity<Void> login(
        @RequestBody LoginIn in
    );

    @PostMapping("/auth/register")
    public ResponseEntity<Void> register(
        @RequestBody RegisterIn in
    );

    @GetMapping("/auth/whoiam")
    public ResponseEntity<AccountOut> whoIAm();

    @GetMapping("/auth/health-check")
    public ResponseEntity<Void> healthCheck();

    @PostMapping("/auth/solve")
    public ResponseEntity<Map<String, String>> solveToken(
        TokenOut map
    );

}
