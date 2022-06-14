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

package com.loohp.interactivechatdiscordsrvaddon.utils;

import club.minnced.discord.webhook.IOUtil;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.receive.ReadonlyAttachment;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import okhttp3.RequestBody;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class WebhookMessageUtils {

    private static Object[] requestTypes;
    private static Method webhookClientExecuteMethod;

    static {
        try {
            Class<?> requestTypeClass = Class.forName(WebhookClient.class.getName() + "$RequestType");
            requestTypes = requestTypeClass.getEnumConstants();
            webhookClientExecuteMethod = WebhookClient.class.getDeclaredMethod("execute", RequestBody.class, String.class, requestTypeClass);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static CompletableFuture<ReadonlyMessage> retainAttachments(WebhookClient client, long messageId, Collection<ReadonlyAttachment> attachments) {
        return retainAttachments(client, String.valueOf(messageId), attachments);
    }

    public static CompletableFuture<ReadonlyMessage> retainAttachments(WebhookClient client, String messageId, Collection<ReadonlyAttachment> attachments) {
        JSONObject root = new JSONObject();
        JSONArray attachmentsArray = new JSONArray();
        for (ReadonlyAttachment attachment : attachments) {
            JSONObject attachmentData = new JSONObject();
            attachmentData.put("id", attachment.getId());
            attachmentsArray.add(attachmentData);
        }
        root.put("attachments", attachmentsArray);
        try {
            RequestBody requestBody = RequestBody.create(IOUtil.JSON, root.toString());
            webhookClientExecuteMethod.setAccessible(true);
            return (CompletableFuture<ReadonlyMessage>) webhookClientExecuteMethod.invoke(client, requestBody, messageId, requestTypes[1]);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
