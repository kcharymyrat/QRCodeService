package qrcodeapi;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
public class AppConroller {

    QRCodeWriter writer = new QRCodeWriter();

    @GetMapping("/api/health")
    @ResponseStatus(HttpStatus.OK)
    public void getHealth() {
    }

    @GetMapping("/api/qrcode")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getImage(
            @RequestParam("contents") String contents,
            @RequestParam("size") int size,
            @RequestParam("type") String type
    ) throws IOException {

        System.out.printf("\n\n\nsize = %d, type = %s, contents = %s, %s\n\n\n", size, type, contents, contents.isEmpty());

        if (contents.trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CustomError("Contents cannot be null or blank"));
        }

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

        try (var baos = new ByteArrayOutputStream()) {
            BitMatrix bitMatrix = writer.encode(contents.trim(), BarcodeFormat.QR_CODE, size, size);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ImageIO.write(bufferedImage, type, baos); // writing the image in the PNG format
            byte[] bytes = baos.toByteArray();
            return ResponseEntity
                    .ok()
                    .contentType(mediaType)
                    .body(bytes);
        } catch (IOException e) {
            // handle the IOEexception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }
}
