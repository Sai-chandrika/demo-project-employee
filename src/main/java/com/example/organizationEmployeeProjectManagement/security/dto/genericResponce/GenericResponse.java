package com.example.organizationEmployeeProjectManagement.security.dto.genericResponce;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class GenericResponse {

	private int code;

	private String status;

	private String message;

	private Object payLoad;

	public GenericResponse(int code, String message, String status) {
		this.status = status;
		this.message = message;
		this.code = code;
	}
	public GenericResponse(int code, Object payLoad) {

		this.code = code;
		this.payLoad = payLoad;
	}
	public GenericResponse(int code, String message, Object payLoad) {
		this.payLoad = payLoad;
		this.message = message;
		this.code = code;
	}
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getPayLoad() {
		return payLoad;
	}

	public void setPayLoad(Object payLoad) {
		this.payLoad = payLoad;
	}

	public GenericResponse() {
		super();

	}

}
