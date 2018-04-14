package hr.fer.zemris.thread;

public class MyInteger extends Number {

	private int value;
	public MyInteger(int value) {
		this.value = value;
	}
	@Override
	public double doubleValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float floatValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int intValue() {
		return value;
	}
	public MyInteger setValue(int value) {
		this.value = value;
		return this;
	}
	@Override
	public long longValue() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
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
		MyInteger other = (MyInteger) obj;
		if (value != other.value)
			return false;
		return true;
	}
	

}
