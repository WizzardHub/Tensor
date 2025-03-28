package dev.wizzardr.tensor.util.error;

import java.util.HashMap;
import java.util.Map;

public enum Error {
	CLICK_LIMIT(1101, "Exceeded the click limit to often"),
	INVALID_PACKET(1201, "Invalid packet received");

	private static Map<Integer, Error> ID_TO_ERROR;
	private final int id;

	Error(int id, String translation) {
		this.id = id;
		this.register(this);
	}

	private void register(Error error) {
		if (ID_TO_ERROR == null) {
			ID_TO_ERROR = new HashMap<>();
		}

		ID_TO_ERROR.put(id, error);
	}

	public String getMessage() {
			return "Error Code " + this.id + '\n'
				+ "Contact moderation with this error code if it continues to happen!";
		}

	public static Error byId(int id) {
		return ID_TO_ERROR.get(id);
	}
}