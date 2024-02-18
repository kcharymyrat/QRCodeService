package qrcodeapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<?> getImage(
            @RequestParam("size") int size,
            @RequestParam("type") String type
    ) throws IOException {

        System.out.printf("\n\n\nsize = %d, type = %s\n\n\n", size, type);

        if (size < 150 || size > 350) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CustomError("Image size must be between 150 and 350 pixels"));
        }

        MediaType mediaType;
        if (type.trim().equalsIgnoreCase("png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else if (type.trim().equalsIgnoreCase("jpeg")){
            mediaType = MediaType.IMAGE_JPEG;
        } else if (type.trim().equalsIgnoreCase("gif")) {
            mediaType = MediaType.IMAGE_GIF;
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CustomError("Only png, jpeg and gif image types are supported"));
        }

        BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = bufferedImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size, size);

        try (var baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, type, baos); // writing the image in the PNG format
            byte[] bytes = baos.toByteArray();
            return ResponseEntity
                    .ok()
                    .contentType(mediaType)
                    .body(bytes);
        } catch (IOException e) {
            // handle the IOEexception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
