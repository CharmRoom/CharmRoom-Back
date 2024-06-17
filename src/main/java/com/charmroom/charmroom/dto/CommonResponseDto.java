package com.charmroom.charmroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@RequiredArgsConstructor(staticName = "of")
public class CommonResponseDto<D> {
	Code code;
	String message;
	D data;
	
	@Getter
	public enum Code{
		BAD_REQUEST("BAD_REQUEST"), OKAY("OKAY"), NOT_FOUND("NOT_FOUND"), CREATED("CREATED"), INVALID("NOT_VALID");
		Code(String code) {
			this.code = code;
		}
		private String code;
	}
	
	public static <D> CommonResponseDto<D> okay(){
		return new CommonResponseDto<>(Code.OKAY, null, null); 
	}
	public static <D> CommonResponseDto<D> okay(D data){ 
		return new CommonResponseDto<>(Code.OKAY, null, data); 
	}
	public static <D> CommonResponseDto<D> okay(String message){
		return new CommonResponseDto<>(Code.OKAY, message, null);
	}
	public static <D> CommonResponseDto<D> okay(String message, D data){
		return new CommonResponseDto<>(Code.OKAY, message, data);
	}
	public static <D> CommonResponseDto<D> badRequest(){
		return new CommonResponseDto<>(Code.BAD_REQUEST, null, null); 
	}
	public static <D> CommonResponseDto<D> badRequest(D data){ 
		return new CommonResponseDto<>(Code.BAD_REQUEST, null, data); 
	}
	public static <D> CommonResponseDto<D> badRequest(String message){
		return new CommonResponseDto<>(Code.BAD_REQUEST, message, null);
	}
	public static <D> CommonResponseDto<D> badRequest(String message, D data){
		return new CommonResponseDto<>(Code.BAD_REQUEST, message, data);
	}
	public static <D> CommonResponseDto<D> notFound(){
		return new CommonResponseDto<>(Code.NOT_FOUND, null, null); 
	}
	public static <D> CommonResponseDto<D> notFound(D data){ 
		return new CommonResponseDto<>(Code.NOT_FOUND, null, data); 
	}
	public static <D> CommonResponseDto<D> notFound(String message){
		return new CommonResponseDto<>(Code.NOT_FOUND, message, null);
	}
	public static <D> CommonResponseDto<D> notFound(String message, D data){
		return new CommonResponseDto<>(Code.NOT_FOUND, message, data);
	}
	public static <D> CommonResponseDto<D> created(){
		return new CommonResponseDto<>(Code.CREATED, null, null); 
	}
	public static <D> CommonResponseDto<D> created(D data){ 
		return new CommonResponseDto<>(Code.CREATED, null, data); 
	}
	public static <D> CommonResponseDto<D> created(String message){
		return new CommonResponseDto<>(Code.CREATED, message, null);
	}
	public static <D> CommonResponseDto<D> created(String message, D data){
		return new CommonResponseDto<>(Code.CREATED, message, data);
	}
	public static <D> CommonResponseDto<D> invalid(){
		return new CommonResponseDto<>(Code.INVALID, null, null); 
	}
	public static <D> CommonResponseDto<D> invalid(D data){ 
		return new CommonResponseDto<>(Code.INVALID, null, data); 
	}
	public static <D> CommonResponseDto<D> invalid(String message){
		return new CommonResponseDto<>(Code.INVALID, message, null);
	}
	public static <D> CommonResponseDto<D> invalid(String message, D data){
		return new CommonResponseDto<>(Code.INVALID, message, data);
	}
}
