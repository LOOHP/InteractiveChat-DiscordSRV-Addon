/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
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

public class VideoReader {
/*
    public static Future<List<ImageFrame>> readVideo(InputStream stream, double maxFrameRate, ExecutorService service, BiConsumer<List<ImageFrame>, Throwable> completeAction) throws IOException {
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
                double minInterval = 1.0 / maxFrameRate;
                FrameGrab grab = FrameGrab.createFrameGrab(new ByteBufferSeekableByteChannel(ByteBuffer.wrap(targetArray), targetArray.length));
                PictureWithMetadata picture;
                List<ImageFrame> frames = new LinkedList<>();
                double accumulatedDuration = 0.0;
                while ((picture = grab.getNativeFrameWithMetadata()) != null) {
                    double duration = picture.getDuration();
                    accumulatedDuration += duration;
                    if (accumulatedDuration > minInterval) {
                        BufferedImage image = AWTUtil.toBufferedImage(picture.getPicture());
                        if (image == null) {
                            continue;
                        }
                        switch (picture.getOrientation()) {
                            case D_90:
                                image = ImageUtils.rotateImageByDegrees(image, 90);
                                break;
                            case D_180:
                                image = ImageUtils.rotateImageByDegrees(image, 180);
                                break;
                            case D_270:
                                image = ImageUtils.rotateImageByDegrees(image, 270);
                                break;
                        }
                        frames.add(new ImageFrame(image, (int) Math.round(accumulatedDuration * 1000)));
                        accumulatedDuration = accumulatedDuration % minInterval;
                    }
                }
                completeAction.accept(frames, null);
                return frames;
            } catch (Throwable e) {
                completeAction.accept(null, e);
                return null;
            }
        });
    }
*/
}
