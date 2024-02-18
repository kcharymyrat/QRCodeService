package qrcodeapi;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.boot.context.properties.bind.DefaultValue;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AppConroller {

    private final QRCodeWriter writer = new QRCodeWriter();
    private final Map<String, ErrorCorrectionLevel> errorCorrectionMap = new HashMap<>() {{
        put("L", ErrorCorrectionLevel.L);
        put("M", ErrorCorrectionLevel.M);
        put("Q", ErrorCorrectionLevel.Q);
        put("H", ErrorCorrectionLevel.H);
    }};


    @GetMapping("/api/health")
    @ResponseStatus(HttpStatus.OK)
    public void getHealth() {
    }

    @GetMapping("/api/qrcode")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getImage(
            @RequestParam("contents") String contents,
            @RequestParam(value = "size", defaultValue = "250") int size,
            @RequestParam(value = "correction", defaultValue = "L") String correction,
            @RequestParam(value = "type", defaultValue = "png") String type
    ) throws IOException {

        System.out.printf("\n\n\nsize = %d, type = %s, contents = %s, correction = %s\n\n\n", size, type, contents, correction);

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

        correction = correction.trim().toUpperCase();
        System.out.printf("correction = %s, %d\n", correction, correction.length());
        if (!errorCorrectionMap.containsKey(correction)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CustomError("Permitted error correction levels are L, M, Q, H"));
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

        Map<EncodeHintType, ?> hints = Map.of(EncodeHintType.ERROR_CORRECTION, errorCorrectionMap.get(correction));
        try (var baos = new ByteArrayOutputStream()) {
            BitMatrix bitMatrix = writer.encode(contents.trim(), BarcodeFormat.QR_CODE, size, size, hints);
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
