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

import java.awt.image.BufferedImage

/**
 * @author Gianluca Sartori
 */

@CompileStatic
class BarcodeUtils {

    static BufferedImage generateEAN13(String code, Integer width, Integer height) {
        encode(BarcodeFormat.EAN_13, code, width, height)
    }

    static BufferedImage generateUPCA(String code, Integer width, Integer height) {
        encode(BarcodeFormat.UPC_A, code, width, height)
    }

    static BufferedImage generateCode128(String code, Integer width, Integer height) {
        encode(BarcodeFormat.CODE_128, code, width, height)
    }

    static BufferedImage generateITF(String code, Integer width, Integer height) {
        encode(BarcodeFormat.ITF, code, width, height)
    }

    static BufferedImage generateDataMatrix(String code, Integer width, Integer height) {
        encode(BarcodeFormat.DATA_MATRIX, code, width, height)
    }

    static BufferedImage generateQRCode(String code, Integer width, Integer height) {
        encode(BarcodeFormat.QR_CODE, code, width, height)
    }

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
