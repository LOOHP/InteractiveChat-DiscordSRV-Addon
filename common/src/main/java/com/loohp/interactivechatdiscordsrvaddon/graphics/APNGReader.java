/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
 *
 * Copyright (C) 2020 - 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2020 - 2025. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.interactivechatdiscordsrvaddon.graphics;

import java.util.Collections;
import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.zip.CRC32;

public class APNGReader {

    private static final byte[] PNG_SIGNATURE = new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte DISPOSE_OP_NONE = 0;
    private static final byte DISPOSE_OP_BACKGROUND = 1;
    private static final byte DISPOSE_OP_PREVIOUS = 2;
    private static final byte BLEND_OP_SOURCE = 0;

    public static Future<List<ImageFrame>> readAPNG(InputStream stream, ExecutorService service, BiConsumer<List<ImageFrame>, Throwable> completeAction) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[2048];
        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        stream.close();
        byte[] targetArray = buffer.toByteArray();
        return service.submit(() -> {
            try {
                List<ImageFrame> frames = readAPNG(new ByteArrayInputStream(targetArray));
                completeAction.accept(frames, null);
                return frames;
            } catch (Throwable e) {
                completeAction.accept(null, e);
                return null;
            }
        });
    }

    public static List<ImageFrame> readAPNG(InputStream input) throws IOException {
        DataInputStream in = new DataInputStream(input);
        byte[] signature = new byte[PNG_SIGNATURE.length];
        in.readFully(signature);
        if (!Arrays.equals(signature, PNG_SIGNATURE)) {
            throw new IOException("Invalid PNG signature");
        }

        byte[] ihdr = null;
        int canvasWidth = -1;
        int canvasHeight = -1;
        boolean animated = false;
        List<PngChunk> sharedChunks = new ArrayList<>();
        List<APNGFrame> rawFrames = new ArrayList<>();
        APNGFrame currentFrame = null;

        while (true) {
            int length;
            try {
                length = in.readInt();
            } catch (IOException e) {
                break;
            }
            String type = readChunkType(in);
            byte[] data = new byte[length];
            in.readFully(data);
            in.readInt();

            if (type.equals("IHDR")) {
                ihdr = data;
                canvasWidth = readInt(data, 0);
                canvasHeight = readInt(data, 4);
            } else if (type.equals("acTL")) {
                animated = true;
            } else if (type.equals("fcTL")) {
                if (currentFrame != null && currentFrame.hasImageData()) {
                    rawFrames.add(currentFrame);
                }
                currentFrame = APNGFrame.fromControl(data, ihdr);
            } else if (type.equals("IDAT")) {
                if (currentFrame == null) {
                    currentFrame = APNGFrame.defaultFrame(ihdr);
                }
                currentFrame.addImageData(data);
            } else if (type.equals("fdAT")) {
                if (currentFrame == null) {
                    throw new IOException("fdAT chunk found before fcTL chunk");
                }
                currentFrame.addImageData(Arrays.copyOfRange(data, 4, data.length));
            } else if (type.equals("IEND")) {
                if (currentFrame != null && currentFrame.hasImageData()) {
                    rawFrames.add(currentFrame);
                }
                break;
            } else if (!isAnimationChunk(type)) {
                sharedChunks.add(new PngChunk(type, data));
            }
        }

        if (!animated || rawFrames.isEmpty()) {
            BufferedImage image = ImageIO.read(input);
            if (image == null) {
                image = ImageIO.read(new ByteArrayInputStream(writeStaticPng(ihdr, sharedChunks, rawFrames)));
            }
            return Collections.singletonList(new ImageFrame(image));
        }

        return composeFrames(canvasWidth, canvasHeight, sharedChunks, rawFrames);
    }

    private static List<ImageFrame> composeFrames(int canvasWidth, int canvasHeight, List<PngChunk> sharedChunks, List<APNGFrame> rawFrames) throws IOException {
        List<ImageFrame> frames = new ArrayList<>(rawFrames.size());
        BufferedImage canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = canvas.createGraphics();
        BufferedImage previousCanvas = null;

        for (APNGFrame rawFrame : rawFrames) {
            BufferedImage frameImage = ImageIO.read(new ByteArrayInputStream(rawFrame.toPng(sharedChunks)));
            if (frameImage == null) {
                throw new IOException("Unable to decode APNG frame");
            }

            if (rawFrame.disposeOp == DISPOSE_OP_PREVIOUS) {
                previousCanvas = copyImage(canvas);
            }

            if (rawFrame.blendOp == BLEND_OP_SOURCE) {
                graphics.setComposite(AlphaComposite.Clear);
                graphics.fillRect(rawFrame.xOffset, rawFrame.yOffset, rawFrame.width, rawFrame.height);
                graphics.setComposite(AlphaComposite.SrcOver);
            }
            graphics.drawImage(frameImage, rawFrame.xOffset, rawFrame.yOffset, null);

            frames.add(new ImageFrame(copyImage(canvas), rawFrame.getDelay(), ""));

            if (rawFrame.disposeOp == DISPOSE_OP_BACKGROUND) {
                graphics.setComposite(AlphaComposite.Clear);
                graphics.fillRect(rawFrame.xOffset, rawFrame.yOffset, rawFrame.width, rawFrame.height);
                graphics.setComposite(AlphaComposite.SrcOver);
            } else if (rawFrame.disposeOp == DISPOSE_OP_PREVIOUS && previousCanvas != null) {
                graphics.dispose();
                canvas = previousCanvas;
                graphics = canvas.createGraphics();
                previousCanvas = null;
            }
        }

        graphics.dispose();
        return frames;
    }

    private static BufferedImage copyImage(BufferedImage image) {
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = copy.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return copy;
    }

    private static byte[] writeStaticPng(byte[] ihdr, List<PngChunk> sharedChunks, List<APNGFrame> rawFrames) throws IOException {
        if (rawFrames.isEmpty()) {
            throw new IOException("No PNG image data found");
        }
        return rawFrames.get(0).toPng(sharedChunks);
    }

    private static boolean isAnimationChunk(String type) {
        return type.equals("acTL") || type.equals("fcTL") || type.equals("fdAT");
    }

    private static String readChunkType(DataInputStream in) throws IOException {
        byte[] bytes = new byte[4];
        in.readFully(bytes);
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    private static int readInt(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 24) | ((data[offset + 1] & 0xFF) << 16) | ((data[offset + 2] & 0xFF) << 8) | (data[offset + 3] & 0xFF);
    }

    private static int readUnsignedShort(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }

    private static byte[] intToBytes(int value) {
        return new byte[] {(byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value};
    }

    private static void writeChunk(DataOutputStream out, String type, byte[] data) throws IOException {
        byte[] typeBytes = type.getBytes(StandardCharsets.US_ASCII);
        out.writeInt(data.length);
        out.write(typeBytes);
        out.write(data);

        CRC32 crc = new CRC32();
        crc.update(typeBytes, 0, typeBytes.length);
        crc.update(data, 0, data.length);
        out.writeInt((int) crc.getValue());
    }

    private static class APNGFrame {
        private final byte[] ihdr;
        private final List<byte[]> imageData;
        private final int width;
        private final int height;
        private final int xOffset;
        private final int yOffset;
        private final int delayNumerator;
        private final int delayDenominator;
        private final byte disposeOp;
        private final byte blendOp;

        private APNGFrame(byte[] ihdr, int width, int height, int xOffset, int yOffset, int delayNumerator, int delayDenominator, byte disposeOp, byte blendOp) {
            this.ihdr = ihdr;
            this.imageData = new ArrayList<>();
            this.width = width;
            this.height = height;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.delayNumerator = delayNumerator;
            this.delayDenominator = delayDenominator;
            this.disposeOp = disposeOp;
            this.blendOp = blendOp;
        }

        private static APNGFrame fromControl(byte[] data, byte[] ihdr) {
            return new APNGFrame(ihdr, readInt(data, 4), readInt(data, 8), readInt(data, 12), readInt(data, 16), readUnsignedShort(data, 20), readUnsignedShort(data, 22), data[24], data[25]);
        }

        private static APNGFrame defaultFrame(byte[] ihdr) {
            return new APNGFrame(ihdr, readInt(ihdr, 0), readInt(ihdr, 4), 0, 0, 1, 10, DISPOSE_OP_NONE, BLEND_OP_SOURCE);
        }

        private void addImageData(byte[] data) {
            imageData.add(data);
        }

        private boolean hasImageData() {
            return !imageData.isEmpty();
        }

        private int getDelay() {
            if (delayNumerator == 0) {
                return 100;
            }
            int denominator = delayDenominator == 0 ? 100 : delayDenominator;
            return Math.max(10, (int) Math.round(delayNumerator * 1000.0 / denominator));
        }

        private byte[] toPng(List<PngChunk> sharedChunks) throws IOException {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteArrayOutputStream);
            out.write(PNG_SIGNATURE);

            byte[] frameIhdr = Arrays.copyOf(ihdr, ihdr.length);
            System.arraycopy(intToBytes(width), 0, frameIhdr, 0, 4);
            System.arraycopy(intToBytes(height), 0, frameIhdr, 4, 4);
            writeChunk(out, "IHDR", frameIhdr);
            for (PngChunk chunk : sharedChunks) {
                writeChunk(out, chunk.type, chunk.data);
            }
            for (byte[] data : imageData) {
                writeChunk(out, "IDAT", data);
            }
            writeChunk(out, "IEND", new byte[0]);
            return byteArrayOutputStream.toByteArray();
        }
    }

    private static class PngChunk {
        private final String type;
        private final byte[] data;

        private PngChunk(String type, byte[] data) {
            this.type = type;
            this.data = data;
        }
    }
}
