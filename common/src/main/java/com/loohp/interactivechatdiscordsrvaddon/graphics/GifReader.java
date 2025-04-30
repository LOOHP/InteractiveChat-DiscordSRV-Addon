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

import com.loohp.interactivechatdiscordsrvaddon.utils.ThrowingSupplier;
import com.madgag.gif.fmsware.GifDecoder;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

public class GifReader {

    public static Future<List<ImageFrame>> readGif(InputStream stream, ExecutorService service, BiConsumer<List<ImageFrame>, Throwable> completeAction) throws IOException {
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
                List<ImageFrame> result;
                result = returnOrNull(() -> readGifMethod0(new ByteArrayInputStream(targetArray)));
                if (result != null) {
                    completeAction.accept(result, null);
                    return result;
                }
                result = returnOrNull(() -> readGifMethod1(new ByteArrayInputStream(targetArray)));
                if (result != null) {
                    completeAction.accept(result, null);
                    return result;
                }
                List<ImageFrame> frames = returnOrNull(() -> readGifFallbackMethod(new ByteArrayInputStream(targetArray)));
                completeAction.accept(frames, null);
                return frames;
            } catch (Throwable e) {
                completeAction.accept(null, e);
                return null;
            }
        });
    }

    private static List<ImageFrame> returnOrNull(ThrowingSupplier<List<ImageFrame>> supplier) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            return null;
        }
    }

    private static List<ImageFrame> readGifMethod0(InputStream stream) throws IOException {
        GifDecoder reader = new GifDecoder();
        if (reader.read(stream) == 0) {
            List<ImageFrame> frames = new ArrayList<>(reader.getFrameCount());
            for (int i = 0; i < reader.getFrameCount(); i++) {
                BufferedImage image = reader.getFrame(i);
                int delay = reader.getDelay(i);
                frames.add(new ImageFrame(image, delay, ""));
            }
            return frames;
        } else {
            throw new IOException("Unable to read Gif");
        }
    }

    private static List<ImageFrame> readGifMethod1(InputStream input) throws IOException {
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream stream = ImageIO.createImageInputStream(input);
        reader.setInput(stream);

        List<ImageFrame> frames = new LinkedList<>();

        int width = -1;
        int height = -1;

        IIOMetadata metadata = reader.getStreamMetadata();
        if (metadata != null) {
            IIOMetadataNode globalRoot = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());

            NodeList globalScreenDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");

            if (globalScreenDescriptor != null && globalScreenDescriptor.getLength() > 0) {
                IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreenDescriptor.item(0);

                if (screenDescriptor != null) {
                    width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
                    height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
                }
            }
        }

        BufferedImage master = null;
        Graphics2D masterGraphics = null;

        for (int frameIndex = 0; ; frameIndex++) {
            BufferedImage image;
            try {
                image = reader.read(frameIndex);
            } catch (IndexOutOfBoundsException io) {
                break;
            }

            if (width == -1 || height == -1) {
                width = image.getWidth();
                height = image.getHeight();
            }

            IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
            IIOMetadataNode gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
            int delay = Integer.parseInt(gce.getAttribute("delayTime")) * 10;
            String disposal = gce.getAttribute("disposalMethod");

            int x = 0;
            int y = 0;

            if (master == null) {
                master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                masterGraphics = master.createGraphics();
                masterGraphics.setBackground(new Color(0, 0, 0, 0));
            } else {
                NodeList children = root.getChildNodes();
                for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++) {
                    Node nodeItem = children.item(nodeIndex);
                    if (nodeItem.getNodeName().equals("ImageDescriptor")) {
                        NamedNodeMap map = nodeItem.getAttributes();
                        x = Integer.parseInt(map.getNamedItem("imageLeftPosition").getNodeValue());
                        y = Integer.parseInt(map.getNamedItem("imageTopPosition").getNodeValue());
                    }
                }
            }
            masterGraphics.drawImage(image, x, y, null);

            BufferedImage copy = new BufferedImage(master.getColorModel(), master.copyData(null), master.isAlphaPremultiplied(), null);
            frames.add(new ImageFrame(copy, delay, disposal));

            if (disposal.equals("restoreToPrevious")) {
                BufferedImage from = null;
                for (int i = frameIndex - 1; i >= 0; i--) {
                    if (!frames.get(i).getDisposal().equals("restoreToPrevious") || frameIndex == 0) {
                        from = frames.get(i).getImage();
                        break;
                    }
                }

                master = new BufferedImage(from.getColorModel(), from.copyData(null), from.isAlphaPremultiplied(), null);
                masterGraphics = master.createGraphics();
                masterGraphics.setBackground(new Color(0, 0, 0, 0));
            } else if (disposal.equals("restoreToBackgroundColor")) {
                masterGraphics.clearRect(x, y, image.getWidth(), image.getHeight());
            }
        }
        reader.dispose();

        return frames;
    }

    private static List<ImageFrame> readGifFallbackMethod(InputStream input) throws IOException {
        return Collections.singletonList(new ImageFrame(ImageIO.read(input)));
    }

}
