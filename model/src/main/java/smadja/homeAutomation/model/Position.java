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
	private Integer x = null;
	private Integer y = null;

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

	public Position(int level, Integer x, Integer y) {
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

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + level;
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
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
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
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
