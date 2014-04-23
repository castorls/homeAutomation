package smadja.homeAutomation.model;

import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Position implements Serializable {

	private static final long serialVersionUID = -4505870717922280616L;

	private static Logger logger = Logger.getLogger(Position.class);

	private int level;
	private int x = -1;
	private int y = -1;

	public Position() {
	}

	public Position(String positionStr) {
		super();
		try {
			Position pos = new ObjectMapper().readValue(positionStr, Position.class);
			if (pos != null) {
				this.level = pos.getLevel();
				this.x = pos.getX();
				this.y = pos.getY();
			}
		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
		}
	}

	public Position(int level, int x, int y) {
		super();
		this.level = level;
		this.x = x;
		this.y = y;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + level;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (level != other.level)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			logger.warn(e.getMessage(), e);
			return null;
		}
	}

}
