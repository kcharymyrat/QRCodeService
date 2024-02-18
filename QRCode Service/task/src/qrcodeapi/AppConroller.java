package qrcodeapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
public class AppConroller {

    @GetMapping("/api/health")
    @ResponseStatus(HttpStatus.OK)
    public void getHealth() {
    }

    @GetMapping("/api/qrcode")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getImage() throws IOException {
        BufferedImage bufferedImage = new BufferedImage(250, 250, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = bufferedImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 250, 250);

        try (var baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", baos); // writing the image in the PNG format
            byte[] bytes = baos.toByteArray();
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(bytes);
        } catch (IOException e) {
            // handle the IOEexception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
