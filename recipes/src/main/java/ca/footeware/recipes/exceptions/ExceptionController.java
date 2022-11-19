/**
 *
 */
package ca.footeware.recipes.exceptions;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * @author craig
 *
 */
@ControllerAdvice
public class ExceptionController {

	public static final String ERROR = "error";

	@ExceptionHandler(value = org.springframework.web.multipart.MaxUploadSizeExceededException.class)
	public String constraintViolationExceptionHandler(HttpServletRequest req, MaxUploadSizeExceededException t,
			Model model) {
		model.addAttribute("timestamp", LocalDateTime.now());
		model.addAttribute("path", req.getRequestURL());
		model.addAttribute("status", HttpStatus.BAD_REQUEST);
		model.addAttribute(ERROR,
				"Cannot delete. Object is being used elsewhere." + System.lineSeparator() + t.getCause().getMessage());
		model.addAttribute("message", "Unattach the object and try again.");
		return ERROR;
	}

	@ExceptionHandler(value = Throwable.class)
	public String defaultErrorHandler(HttpServletRequest req, Throwable t, Model model) {
		model.addAttribute("timestamp", LocalDateTime.now());
		model.addAttribute("path", req.getRequestURL());
		model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR);
		model.addAttribute(ERROR, t.getCause());
		model.addAttribute("message", t.getMessage());
		model.addAttribute("trace", extractTraces(t.getStackTrace()));
		return ERROR;
	}

	private String extractTraces(StackTraceElement[] stackTrace) {
		StringBuilder buffer = new StringBuilder();
		for (StackTraceElement stackTraceElement : stackTrace) {
			buffer.append(stackTraceElement.toString());
			buffer.append("\n");
		}
		return buffer.toString();
	}
}
