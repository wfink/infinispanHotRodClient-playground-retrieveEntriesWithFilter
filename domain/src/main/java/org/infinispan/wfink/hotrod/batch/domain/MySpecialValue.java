package org.infinispan.wfink.hotrod.batch.domain;

import java.io.Serializable;

public class MySpecialValue implements Serializable {
	private String text;
	private Boolean deleted;
	private int counter;
	
	public MySpecialValue() {
		this(null,0,false);
	}
	public MySpecialValue(String text) {
		this(text,0,false);
	}
	
    public MySpecialValue(String text, int counter, boolean deleted) {
		this.text = text;
		this.counter = counter;
		this.deleted = deleted;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	@Override
	public String toString() {
		return "MySpecialValue [text=" + text + ", deleted=" + deleted + ", counter=" + counter + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + counter;
		result = prime * result + ((deleted == null) ? 0 : deleted.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		MySpecialValue other = (MySpecialValue) obj;
		if (counter != other.counter)
			return false;
		if (deleted == null) {
			if (other.deleted != null)
				return false;
		} else if (!deleted.equals(other.deleted))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}
}
