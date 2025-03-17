package com.milklabs.exemplo.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountryCurrencyVO {
	
	private String sISOCode;
	private String sName;
	
	@Override
	public String toString() {
		return "CountryCurrencyVO [sISOCode=" + sISOCode + ", sName=" + sName + "]";
	}
}
