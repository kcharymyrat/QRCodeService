package qrcodeapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppConroller {

    @GetMapping("/api/health")
    @ResponseStatus(HttpStatus.OK)
    public void getHealth() {
    }

    @GetMapping("/api/qrcode")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void getQrCode() {
    }
}
