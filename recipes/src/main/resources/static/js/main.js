"use strict";

/**
 * Delete a recipe.
 */
function deleteById(name, id) {
	if (confirm("Are you sure you want to delete '" + name + "'?")) {
		$.ajax({
			url: window.location.protocol + "//" + window.location.hostname + ":" + window.location.port + "/delete/" + id,
			type: 'DELETE',
			success: function(result) {
				document.write(decodeURI(result));
			},
			error: function(request, msg, error, status) {
				alert(msg);
				console.log("request=" + request.url + ", msg=" + msg, "error=" + error, " status=" + status);
			}
		});
	}
}
