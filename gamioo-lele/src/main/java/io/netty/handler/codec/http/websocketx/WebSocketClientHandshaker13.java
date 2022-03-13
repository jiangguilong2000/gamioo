/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.net.URI;

/**
 * <p>
 * Performs client side opening and closing handshakes for web socket
 * specification version
 * <a href="http://tools.ietf.org/html/draft-ietf-hybi-thewebsocketprotocol-17"
 * >draft-ietf-hybi-thewebsocketprotocol- 17</a>
 * </p>
 */
public class WebSocketClientHandshaker13 extends WebSocketClientHandshaker {

	private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketClientHandshaker13.class);

	public static final String MAGIC_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

	private String expectedChallengeResponseString;

	private final boolean allowExtensions;
	private final boolean performMasking;
	private final boolean allowMaskMismatch;

	/**
	 * Creates a new instance.
	 *
	 * @param webSocketURL          URL for web socket communications. e.g
	 *                              "ws://myhost.com/mypath". Subsequent web socket
	 *                              frames will be sent to this URL.
	 * @param version               Version of web socket specification to use to
	 *                              connect to the server
	 * @param subprotocol           Sub protocol request sent to the server.
	 * @param allowExtensions       Allow extensions to be used in the reserved bits
	 *                              of the web socket frame
	 * @param customHeaders         Map of custom headers to add to the client
	 *                              request
	 * @param maxFramePayloadLength Maximum length of a frame's payload
	 */
	public WebSocketClientHandshaker13(URI webSocketURL, WebSocketVersion version, String subprotocol,
                                       boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength) {
		this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, true, false);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param webSocketURL          URL for web socket communications. e.g
	 *                              "ws://myhost.com/mypath". Subsequent web socket
	 *                              frames will be sent to this URL.
	 * @param version               Version of web socket specification to use to
	 *                              connect to the server
	 * @param subprotocol           Sub protocol request sent to the server.
	 * @param allowExtensions       Allow extensions to be used in the reserved bits
	 *                              of the web socket frame
	 * @param customHeaders         Map of custom headers to add to the client
	 *                              request
	 * @param maxFramePayloadLength Maximum length of a frame's payload
	 * @param performMasking        Whether to mask all written websocket frames.
	 *                              This must be set to true in order to be fully
	 *                              compatible with the websocket specifications.
	 *                              Client applications that communicate with a
	 *                              non-standard server which doesn't require
	 *                              masking might set this to false to achieve a
	 *                              higher performance.
	 * @param allowMaskMismatch     When set to true, frames which are not masked
	 *                              properly according to the standard will still be
	 *                              accepted.
	 */
	public WebSocketClientHandshaker13(URI webSocketURL, WebSocketVersion version, String subprotocol,
                                       boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking,
                                       boolean allowMaskMismatch) {
		this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking,
				allowMaskMismatch, DEFAULT_FORCE_CLOSE_TIMEOUT_MILLIS);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param webSocketURL            URL for web socket communications. e.g
	 *                                "ws://myhost.com/mypath". Subsequent web
	 *                                socket frames will be sent to this URL.
	 * @param version                 Version of web socket specification to use to
	 *                                connect to the server
	 * @param subprotocol             Sub protocol request sent to the server.
	 * @param allowExtensions         Allow extensions to be used in the reserved
	 *                                bits of the web socket frame
	 * @param customHeaders           Map of custom headers to add to the client
	 *                                request
	 * @param maxFramePayloadLength   Maximum length of a frame's payload
	 * @param performMasking          Whether to mask all written websocket frames.
	 *                                This must be set to true in order to be fully
	 *                                compatible with the websocket specifications.
	 *                                Client applications that communicate with a
	 *                                non-standard server which doesn't require
	 *                                masking might set this to false to achieve a
	 *                                higher performance.
	 * @param allowMaskMismatch       When set to true, frames which are not masked
	 *                                properly according to the standard will still
	 *                                be accepted
	 * @param forceCloseTimeoutMillis Close the connection if it was not closed by
	 *                                the server after timeout specified.
	 */
	public WebSocketClientHandshaker13(URI webSocketURL, WebSocketVersion version, String subprotocol,
                                       boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking,
                                       boolean allowMaskMismatch, long forceCloseTimeoutMillis) {
		this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking,
				allowMaskMismatch, forceCloseTimeoutMillis, false);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param webSocketURL            URL for web socket communications. e.g
	 *                                "ws://myhost.com/mypath". Subsequent web
	 *                                socket frames will be sent to this URL.
	 * @param version                 Version of web socket specification to use to
	 *                                connect to the server
	 * @param subprotocol             Sub protocol request sent to the server.
	 * @param allowExtensions         Allow extensions to be used in the reserved
	 *                                bits of the web socket frame
	 * @param customHeaders           Map of custom headers to add to the client
	 *                                request
	 * @param maxFramePayloadLength   Maximum length of a frame's payload
	 * @param performMasking          Whether to mask all written websocket frames.
	 *                                This must be set to true in order to be fully
	 *                                compatible with the websocket specifications.
	 *                                Client applications that communicate with a
	 *                                non-standard server which doesn't require
	 *                                masking might set this to false to achieve a
	 *                                higher performance.
	 * @param allowMaskMismatch       When set to true, frames which are not masked
	 *                                properly according to the standard will still
	 *                                be accepted
	 * @param forceCloseTimeoutMillis Close the connection if it was not closed by
	 *                                the server after timeout specified.
	 * @param absoluteUpgradeUrl      Use an absolute url for the Upgrade request,
	 *                                typically when connecting through an HTTP
	 *                                proxy over clear HTTP
	 */
	WebSocketClientHandshaker13(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions,
                                HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch,
                                long forceCloseTimeoutMillis, boolean absoluteUpgradeUrl) {
		super(webSocketURL, version, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis,
				absoluteUpgradeUrl);
		this.allowExtensions = allowExtensions;
		this.performMasking = performMasking;
		this.allowMaskMismatch = allowMaskMismatch;
	}

	/**
	 * /**
	 * <p>
	 * Sends the opening request to the server:
	 * </p>
	 *
	 * <pre>
	 * GET /chat HTTP/1.1
	 * Host: server.example.com
	 * Upgrade: websocket
	 * Connection: Upgrade
	 * Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==
	 * Origin: http://example.com
	 * Sec-WebSocket-Protocol: chat, superchat
	 * Sec-WebSocket-Version: 13
	 * </pre>
	 *
	 */
//    @Override
//    protected FullHttpRequest newHandshakeRequest() {
//        URI wsURL = uri();
//
//        // Get 16 bit nonce and base 64 encode it
//        byte[] nonce = WebSocketUtil.randomBytes(16);
//        String key = WebSocketUtil.base64(nonce);
//
//        String acceptSeed = key + MAGIC_GUID;
//        byte[] sha1 = WebSocketUtil.sha1(acceptSeed.getBytes(CharsetUtil.US_ASCII));
//        expectedChallengeResponseString = WebSocketUtil.base64(sha1);
//
//        if (logger.isDebugEnabled()) {
//            logger.debug(
//                    "WebSocket version 13 client handshake key: {}, expected response: {}",
//                    key, expectedChallengeResponseString);
//        }
//
//        // Format request
//        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, upgradeUrl(wsURL),
//                Unpooled.EMPTY_BUFFER);
//        HttpHeaders headers = request.headers();
//
//        if (customHeaders != null) {
//            headers.add(customHeaders);
//            if (!headers.contains(HttpHeaderNames.HOST)) {
//                // Only add HOST header if customHeaders did not contain it.
//                //
//                // See https://github.com/netty/netty/issues/10101
//                headers.set(HttpHeaderNames.HOST, websocketHostValue(wsURL));
//            }
//        } else {
//            headers.set(HttpHeaderNames.HOST, websocketHostValue(wsURL));
//        }
//
//        headers.set(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET)
//                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE)
//                .set(HttpHeaderNames.SEC_WEBSOCKET_KEY, key);
//
//        if (!headers.contains(HttpHeaderNames.ORIGIN)) {
//            headers.set(HttpHeaderNames.ORIGIN, websocketOriginValue(wsURL));
//        }
//
//        String expectedSubprotocol = expectedSubprotocol();
//        if (expectedSubprotocol != null && !expectedSubprotocol.isEmpty()) {
//            headers.set(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, expectedSubprotocol);
//        }
//
//        headers.set(HttpHeaderNames.SEC_WEBSOCKET_VERSION, version().toAsciiString());
//        return request;
//    }

	@Override
	protected FullHttpRequest newHandshakeRequest() {
		// Get path
		URI wsURL = uri();
		String path = wsURL.getPath();
		if (wsURL.getQuery() != null && !wsURL.getQuery().isEmpty()) {
			path = wsURL.getPath() + '?' + wsURL.getQuery();
		}

		if (path == null || path.isEmpty()) {
			path = "/";
		}

		// Get 16 bit nonce and base 64 encode it
		byte[] nonce = WebSocketUtil.randomBytes(16);
		String key = WebSocketUtil.base64(nonce);

		String acceptSeed = key + MAGIC_GUID;
		byte[] sha1 = WebSocketUtil.sha1(acceptSeed.getBytes(CharsetUtil.US_ASCII));
		expectedChallengeResponseString = WebSocketUtil.base64(sha1);

		if (logger.isDebugEnabled()) {
			logger.debug("WebSocket version 13 client handshake key: {}, expected response: {}", key,
					expectedChallengeResponseString);
		}

		// Format request
		int wsPort = wsURL.getPort();
		// check if the URI contained a port if not set the correct one depending on the
		// schema.
		// See https://github.com/netty/netty/pull/1558
		if (wsPort == -1) {
			if ("wss".equals(wsURL.getScheme())) {
				wsPort = 443;
			} else {
				wsPort = 80;
			}
		}

		FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
		HttpHeaders headers = request.headers();

		headers.add(Names.UPGRADE, Values.WEBSOCKET.toLowerCase()).add(Names.CONNECTION, Values.UPGRADE)
				.add(Names.SEC_WEBSOCKET_KEY, key).add(Names.HOST, wsURL.getHost() + ':' + wsPort);

		String originValue = "http://" + wsURL.getHost();
		if (wsPort != 80 && wsPort != 443) {
			// if the port is not standard (80/443) its needed to add the port to the
			// header.
			// See http://tools.ietf.org/html/rfc6454#section-6.2
			originValue = originValue + ':' + wsPort;
		}
		headers.add(Names.SEC_WEBSOCKET_ORIGIN, originValue);
	//	headers.add(Names.COOKIE, "https_waf_cookie=34a42064-6cd4-47d12959d46f6b8b3e522e1b41fc184dc2aa");
		headers.add(Names.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 NetType/WIFI MicroMessenger/7.0.20.1781(0x6700143B) WindowsWechat(0x6305002e)");




		String expectedSubprotocol = expectedSubprotocol();
		if (expectedSubprotocol != null && !expectedSubprotocol.isEmpty()) {
			headers.add(Names.SEC_WEBSOCKET_PROTOCOL, expectedSubprotocol);
		}

		headers.add(Names.SEC_WEBSOCKET_VERSION, "13");

		if (customHeaders != null) {
			headers.add(customHeaders);
		}
		return request;
	}

	/**
	 * <p>
	 * Process server response:
	 * </p>
	 *
	 * <pre>
	 * HTTP/1.1 101 Switching Protocols
	 * Upgrade: websocket
	 * Connection: Upgrade
	 * Sec-WebSocket-Accept: s3pPLMBiTxaQ9kYGzzhZRbK+xOo=
	 * Sec-WebSocket-Protocol: chat
	 * </pre>
	 *
	 * @param response HTTP response returned from the server for the request sent
	 *                 by beginOpeningHandshake00().
	 * @throws WebSocketHandshakeException if handshake response is invalid.
	 */
	@Override
	protected void verify(FullHttpResponse response) {
		final HttpResponseStatus status = HttpResponseStatus.SWITCHING_PROTOCOLS;
		final HttpHeaders headers = response.headers();

		if (!response.status().equals(status)) {
			throw new WebSocketHandshakeException("Invalid handshake response getStatus: " + response.status());
		}

		CharSequence upgrade = headers.get(HttpHeaderNames.UPGRADE);
		if (!HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(upgrade)) {
			throw new WebSocketHandshakeException("Invalid handshake response upgrade: " + upgrade);
		}

		if (!headers.containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true)) {
			throw new WebSocketHandshakeException(
					"Invalid handshake response connection: " + headers.get(HttpHeaderNames.CONNECTION));
		}

		CharSequence accept = headers.get(HttpHeaderNames.SEC_WEBSOCKET_ACCEPT);
		if (accept == null || !accept.equals(expectedChallengeResponseString)) {
			throw new WebSocketHandshakeException(String.format("Invalid challenge. Actual: %s. Expected: %s", accept,
					expectedChallengeResponseString));
		}
	}

	@Override
	protected WebSocketFrameDecoder newWebsocketDecoder() {
		return new WebSocket13FrameDecoder(false, allowExtensions, maxFramePayloadLength(), allowMaskMismatch);
	}

	@Override
	protected WebSocketFrameEncoder newWebSocketEncoder() {
		return new WebSocket13FrameEncoder(performMasking);
	}

	@Override
	public WebSocketClientHandshaker13 setForceCloseTimeoutMillis(long forceCloseTimeoutMillis) {
		super.setForceCloseTimeoutMillis(forceCloseTimeoutMillis);
		return this;
	}

}
