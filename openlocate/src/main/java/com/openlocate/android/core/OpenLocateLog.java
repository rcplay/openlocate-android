/*
 * Copyright (c) 2017 OpenLocate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.openlocate.android.core;

import org.json.JSONException;
import org.json.JSONObject;

final class OpenLocateLog implements LogType {

    private static final String MESSAGE_KEY = "message";
    private static final String LOG_LEVEL_KEY = "log_level";

    private String message;
    private LogLevel logLevel;

    OpenLocateLog(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            message = jsonObject.getString(MESSAGE_KEY);
            logLevel = LogLevel.values()[jsonObject.getInt(LOG_LEVEL_KEY)];

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public LogLevel getLogLevel() {
        return logLevel;
    }

    @Override
    public String toString() {
        return logLevel.toString() + " | " + message;
    }

    private OpenLocateLog(String message, LogLevel logLevel) {
        this.message = message;
        this.logLevel = logLevel;
    }

    static class Builder {
        private String message;
        private LogLevel logLevel;

        Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        Builder setLogLevel(LogLevel logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        OpenLocateLog build() {
            return new OpenLocateLog(message, logLevel);
        }
    }

    @Override
    public String getDatabaseJsonString() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(MESSAGE_KEY, message);
            jsonObject.put(LOG_LEVEL_KEY, logLevel);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
}
