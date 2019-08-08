package com.ljw.gateway.business.param;

/**
 * @ClassName: ParamAttribute
 * @Description: ParamAttribute
 * @Author: ljw
 * @Date: 2019/7/30 13:50
 **/
public class ParamAttribute {

	private String paramKey;
	private int length;
	private String type;

	public String getParamKey() {
		return paramKey;
	}

	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}
	public void setLength(int length) {
		this.length = length;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "ParamAttribute [paramKey=" + paramKey + ", length=" + length
				+ ", type=" + type + "]";
	}

}
