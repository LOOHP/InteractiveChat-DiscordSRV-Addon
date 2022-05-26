/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
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

package com.loohp.interactivechatdiscordsrvaddon.resources;

import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelRenderer.RenderResult;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class CacheObject<T> {

    protected static CacheObject<?> deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(data))) {
            long timeCreated = inputStream.readLong();
            byte type = inputStream.readByte();
            if (type == 0) {
                byte[] dataArray = new byte[data.length - 9];
                inputStream.readFully(dataArray);
                return new CacheObject<>(timeCreated, new String(dataArray, StandardCharsets.UTF_8));
            } else if (type == 1) {
                byte[] dataArray = new byte[data.length - 9];
                inputStream.readFully(dataArray);
                return new CacheObject<>(timeCreated, ImageUtils.fromArray(dataArray));
            } else if (type == 2) {
                byte[] dataArray = new byte[data.length - 10];
                inputStream.readFully(dataArray);
                if (inputStream.readBoolean()) {
                    return new CacheObject<>(timeCreated, new RenderResult(ImageUtils.fromArray(dataArray)));
                } else {
                    return new CacheObject<>(timeCreated, new RenderResult(new String(dataArray, StandardCharsets.UTF_8)));
                }
            } else if (type == 3) {
                byte[] dataArray = new byte[data.length - 9];
                inputStream.readFully(dataArray);
                try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(dataArray))) {
                    return new CacheObject<>(timeCreated, objectInputStream.readObject());
                }
            }
            throw new IllegalArgumentException("Illegal class type " + type);
        }
    }

    private long timeCreated;
    private T object;

    protected CacheObject(long timeCreated, T object) {
        this.timeCreated = timeCreated;
        this.object = object;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public T getObject() {
        return object;
    }

    protected byte[] serialize() throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            dataOutputStream.writeLong(timeCreated);
            if (object instanceof String) {
                dataOutputStream.writeByte(0);
                dataOutputStream.write(((String) object).getBytes(StandardCharsets.UTF_8));
            } else if (object instanceof BufferedImage) {
                dataOutputStream.writeByte(1);
                dataOutputStream.write(ImageUtils.toArray((BufferedImage) object));
            } else if (object instanceof RenderResult) {
                dataOutputStream.writeByte(2);
                RenderResult renderResult = (RenderResult) object;
                if (renderResult.isSuccessful()) {
                    dataOutputStream.writeBoolean(true);
                    dataOutputStream.write(ImageUtils.toArray(renderResult.getImage()));
                } else {
                    dataOutputStream.writeBoolean(false);
                    dataOutputStream.write(renderResult.getRejectedReason().getBytes(StandardCharsets.UTF_8));
                }
            } else if (object instanceof Serializable) {
                dataOutputStream.writeByte(3);
                try (ObjectOutputStream outputStream = new ObjectOutputStream(dataOutputStream)) {
                    outputStream.writeObject(object);
                    outputStream.flush();
                }
            } else {
                throw new IllegalArgumentException("Illegal object class: " + object.getClass());
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

}