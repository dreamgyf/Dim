package com.dreamgyf.dim.base.mqtt;

import java.nio.charset.Charset;

public class MqttMessageHandler {

	public static class Type {
		public final static byte CHAT = 0b00;
		public final static byte REQUEST = 0b01;
	}

	public static class Builder {
		private byte type;
		/**
		 * type == REQUEST
		 */
		private String verifyText;
		private String remarkText;

		public Builder setType(byte type) {
			if ((type & 0b11111000) != 0) {
				throw new IllegalArgumentException("The type can not over 3 bits");
			}
			this.type = type;
			return this;
		}

		public Builder setVerifyText(String text) {
			this.verifyText = text;
			return this;
		}

		public Builder setRemarkText(String text) {
			this.remarkText = text;
			return this;
		}

		public String build() {
			String message = "";
			switch (type) {
				case Type.REQUEST: {
					byte header = 0;
					header |= (type << 5);

					byte[] verifyBytes = verifyText.getBytes();
					byte[] remarkBytes = remarkText.getBytes();

					byte[] messageBytes = new byte[3 + verifyBytes.length + remarkBytes.length];
					messageBytes[0] = header;
					messageBytes[1] = (byte) verifyBytes.length;
					System.arraycopy(verifyBytes, 0, messageBytes, 2, verifyBytes.length);
					messageBytes[2 + verifyBytes.length] = (byte) remarkBytes.length;
					System.arraycopy(remarkBytes, 0, messageBytes, 3 + verifyBytes.length, remarkBytes.length);
					message = new String(messageBytes);
				}
				break;
			}
			return message;
		}

	}

	public static class Parser {
		private Parser() {}

		private byte type;
		/**
		 * type == REQUEST
		 */
		private String verifyText;
		private String remarkText;

		public static Parser parse(String message) {
			byte[] messageBytes = message.getBytes();
			Parser parser = new Parser();
			parser.type = (byte) ((messageBytes[0] & 0xff) >>> 5);
			switch (parser.type) {
				case Type.REQUEST: {
					int verifyBytesLength = messageBytes[1] & 0xff;
					parser.verifyText = new String(messageBytes,2,verifyBytesLength, Charset.forName("UTF-8"));
					int remarkBytesLength = messageBytes[2 + verifyBytesLength] & 0xff;
					parser.remarkText = new String(messageBytes,3 + verifyBytesLength,remarkBytesLength, Charset.forName("UTF-8"));
				}
				break;
			}
			return parser;
		}

		public byte getType() {
			return type;
		}

		public String getVerifyText() {
			return verifyText;
		}

		public String getRemarkText() {
			return remarkText;
		}
	}
}
