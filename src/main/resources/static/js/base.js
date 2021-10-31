var urlParams = parseUrlParams();

function parseUrlParams() {
	var params = {};
	var url = window.location.search;
	if(url == "undefined" || url == null) {
		return params;
	}

	if(url.length == 1) {
		return params;
	}

	url = url.substring(1);
	url = url.split("#")[0];
	url = url.split("&");
	for(var i = 0; i < url.length; i++) {
		var paramsWith = url[i];
		var index = paramsWith.indexOf("=");
		if(index > -1) {
			params[paramsWith.substring(0, index)] = paramsWith.substring(index + 1);
		}
	}
	return params;
}

function fixedCountInterval(callback, count, delay) {
	var c = 0;
	var v = setInterval(function() {
		c++;
		callback();

		if(c >= count) {
			clearTimeout(v);
		}
	}, delay);
}