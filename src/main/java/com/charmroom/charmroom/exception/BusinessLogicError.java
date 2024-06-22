package com.charmroom.charmroom.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessLogicError {
	
	// Unknown
	UNKNOWN(HttpStatus.BAD_REQUEST, "UNKNOWN", "Unknown"),
	// end Unknown
	
	// Article
	NOTFOUND_ARTICLE(HttpStatus.NOT_FOUND, "01100", "Article not found"),
	// end Article
	
	// Attachment
	NOTFOUND_ATTACHMENT(HttpStatus.NOT_FOUND, "03100", "Attachment not found"),
	// end Attachment
	
	// Club
	NOTFOUND_CLUB(HttpStatus.NOT_FOUND, "05100", "Club not found"),
	// end Club
	
	// Comment
	NOTFOUND_COMMENT(HttpStatus.NOT_FOUND, "06100", "Comment not found"),
	UNAUTHORIZED_COMMENT(HttpStatus.UNAUTHORIZED, "06200", "Don't have permission on comment"),
	// end Comment
	
	// Image
	FILE_NOT_IMAGE(HttpStatus.BAD_REQUEST, "08000", "Content type of the file is not image"),
	NOTFOUND_IMAGE(HttpStatus.NOT_FOUND, "08100", "Image not found"),
	// end Image
	
	// User
	DUPLICATED_USERNAME(HttpStatus.BAD_REQUEST, "12000", "Duplicated username"),
	DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "12001", "Duplicated email"),
	DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "12002", "Duplicated nickname"),
	
	NOTFOUND_USER(HttpStatus.NOT_FOUND, "12100", "User not found"), 
	// end User
	
	// ETC
	MKDIR_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "MKDIR_FAIL", "mkdir failed"),
	CHMOD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "CHMOD_FAIL", "chmod failed"), 
	DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "DELETE_FAIL", "file delete failed"),
	// end ETC
	;
	private final HttpStatus status;
	private final String code;
	private final String describe;
}
