/*
 * Copyright 2021 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dueuno.commons.utils

import com.google.zxing.BarcodeFormat
import com.google.zxing.Writer
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.datamatrix.DataMatrixWriter
import com.google.zxing.oned.Code128Writer
import com.google.zxing.oned.EAN13Writer
import com.google.zxing.oned.ITFWriter
import com.google.zxing.oned.UPCAWriter
import com.google.zxing.qrcode.QRCodeWriter
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.awt.image.BufferedImage

/**
 * Utility class for generating various types of barcodes and QR codes as {@link BufferedImage} instances.
 * <p>
 * Supported barcode formats include EAN-13, UPC-A, Code 128, ITF, Data Matrix, and QR Code.
 * <p>
 * This class uses the ZXing library to encode the barcode data into images.
 *
 * @author Gianluca Sartori
 */
@Slf4j
@CompileStatic
class BarcodeUtils {

    /**
     * Generates an EAN-13 barcode image from the given code.
     *
     * @param code the numeric code to encode (must comply with EAN-13 format)
     * @param width the desired image width in pixels
     * @param height the desired image height in pixels
     * @return a {@link BufferedImage} representing the EAN-13 barcode
     * @throws IllegalArgumentException if the code is invalid
     */
    static BufferedImage generateEAN13(String code, Integer width, Integer height) {
        return encode(BarcodeFormat.EAN_13, code, width, height)
    }

    /**
     * Generates a UPC-A barcode image from the given code.
     *
     * @param code the numeric code to encode (must comply with UPC-A format)
     * @param width the desired image width in pixels
     * @param height the desired image height in pixels
     * @return a {@link BufferedImage} representing the UPC-A barcode
     * @throws IllegalArgumentException if the code is invalid
     */
    static BufferedImage generateUPCA(String code, Integer width, Integer height) {
        return encode(BarcodeFormat.UPC_A, code, width, height)
    }

    /**
     * Generates a Code 128 barcode image from the given code.
     *
     * @param code the text or numeric code to encode
     * @param width the desired image width in pixels
     * @param height the desired image height in pixels
     * @return a {@link BufferedImage} representing the Code 128 barcode
     */
    static BufferedImage generateCode128(String code, Integer width, Integer height) {
        return encode(BarcodeFormat.CODE_128, code, width, height)
    }

    /**
     * Generates an ITF (Interleaved 2 of 5) barcode image from the given code.
     *
     * @param code the numeric code to encode (must be an even number of digits)
     * @param width the desired image width in pixels
     * @param height the desired image height in pixels
     * @return a {@link BufferedImage} representing the ITF barcode
     */
    static BufferedImage generateITF(String code, Integer width, Integer height) {
        return encode(BarcodeFormat.ITF, code, width, height)
    }

    /**
     * Generates a Data Matrix barcode image from the given code.
     *
     * @param code the text or numeric code to encode
     * @param width the desired image width in pixels
     * @param height the desired image height in pixels
     * @return a {@link BufferedImage} representing the Data Matrix barcode
     */
    static BufferedImage generateDataMatrix(String code, Integer width, Integer height) {
        return encode(BarcodeFormat.DATA_MATRIX, code, width, height)
    }

    /**
     * Generates a QR Code image from the given code.
     *
     * @param code the text or numeric code to encode
     * @param width the desired image width in pixels
     * @param height the desired image height in pixels
     * @return a {@link BufferedImage} representing the QR Code
     */
    static BufferedImage generateQRCode(String code, Integer width, Integer height) {
        return encode(BarcodeFormat.QR_CODE, code, width, height)
    }

    /**
     * Encodes a given code into a barcode or QR Code image using the specified format.
     *
     * @param format the {@link BarcodeFormat} to use for encoding
     * @param code the text or numeric code to encode
     * @param width the desired image width in pixels
     * @param height the desired image height in pixels
     * @param hints optional encoding hints (may be empty)
     * @return a {@link BufferedImage} representing the encoded barcode
     * @throws IllegalArgumentException if the specified format is not supported
     */
    static BufferedImage encode(BarcodeFormat format, String code, Integer width, Integer height, Map hints = [:]) {
        Writer writer

        switch (format) {
            case BarcodeFormat.EAN_13:
                writer = new EAN13Writer()
                break

            case BarcodeFormat.UPC_A:
                writer = new UPCAWriter()
                break

            case BarcodeFormat.CODE_128:
                writer = new Code128Writer()
                break

            case BarcodeFormat.ITF:
                writer = new ITFWriter()
                break

            case BarcodeFormat.DATA_MATRIX:
                writer = new DataMatrixWriter()
                break

            case BarcodeFormat.QR_CODE:
                writer = new QRCodeWriter()
                break

            default:
                throw new IllegalArgumentException("No encoder available for '${format}'")
        }

        BitMatrix bitImage = writer.encode(code, format, width, height, hints)
        return MatrixToImageWriter.toBufferedImage(bitImage)
    }
}
