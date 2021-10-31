var websocket;

function connectionWebSocket() {
	if(window.WebSocket) {
		//websocket = new WebSocket("ws://service.simingtang.com/smt-wx-class/websocket/wechat");
		websocket = new WebSocket("ws://"+window.location.host + "/class/" + $("#openid").val());
		//websocket = new WebSocket("ws://192.168.0.222/smt-wx-class/websocket/wechat");
		websocket.onclose = function(ev) {
			$("#restLogin").modal("show");
		}

		window.onopen = function(ev){
			
		}

		websocket.onerror = function(ev) {
			myAlert("连接异常");
		}

		websocket.onmessage = function(ev) {
			var json = JSON.parse(ev.data);
			addHtmlMessage(json);
		}
	} else {
		alert("微信版本过低，请升级微信");
		window.location.href = "index.html";
	}
}

function myAlert(msg) {
	$("#alert div.modal-body").html(msg);
	$("#alert").modal("show");
}

$(function() {
	connectionWebSocket();
	
	var winH = $(window).height();
	$("#message-list").css("height", winH - 34);

	var topInfoIsHide = false;
	$("div.top-info").on("click", function() {
		var div = $(this).find("div.text-center");
		if(topInfoIsHide) {
			div.next().hide();
			div.show();
		} else {
			div.hide();
			div.next().show();
		}
		topInfoIsHide = !topInfoIsHide;
	})

	initWeiXinJs();

	wx.ready(function() {
		// config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。
		$("img.audio").on("click", function() {
			var status = $(this).attr("status");
			if(status == 0) {
				wx.startRecord();
				$(this).attr("status", 1); //录音中
				$(this).attr("src", "static/img/stopRecord.png");
			} else {
				$(this).attr("status", 0); //停止
				$(this).attr("src", "static/img/audio.png");
				wx.stopRecord({
					success: function(res) {
						var localId = res.localId;
						uploadVoice(localId);
					}
				});
			}
		})

		wx.onVoicePlayEnd({
			success: function(res) {
				var localId = res.localId; // 返回音频的本地ID
				console.log(localId);
				var img = $("img[localId='" + localId + "']");
				img.attr("src", "static/img/audio.png");
				img.attr("status", 0);
			}
		});

		wx.onVoiceRecordEnd({
			// 录音时间超过一分钟没有停止的时候会执行 complete 回调
			complete: function(res) {
				$("img.audio").attr("status", 0); //停止
				$("img.audio").attr("src", "static/img/audio.png");
				var localId = res.localId;
				uploadVoice(localId);
			}
		});

		var loadImgs = $("img.picture");

		$("img.picture").on("click", function() {
			wx.chooseImage({
				count: 1, // 默认9
				sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
				sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
				success: function(res) {
					var localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
					wx.uploadImage({
						localId: localIds.toString().split(",")[0], // 需要上传的图片的本地ID，由chooseImage接口获得
						isShowProgressTips: 1, // 默认为1，显示进度提示
						success: function(res) {
							var serverId = res.serverId; // 返回图片的服务器端ID
							sendData(3, serverId);
						}
					});
				}
			});
		})

		$("img.msg-audio").on("click", function() {
			var localId = $(this).attr("localId");
			var myImg = $(this);
			if(!localId || localId.length == 0) {
				wx.downloadVoice({
					serverId: $(this).attr("serverId"), // 需要下载的音频的服务器端ID，由uploadVoice接口获得
					isShowProgressTips: 1, // 默认为1，显示进度提示
					success: function(res) {
						var localId = res.localId; // 返回音频的本地ID
						myImg.attr("localId", localId);
						myImg.attr("status", 1);
						myImg.attr("src", "static/img/stopAudio.png");
						wx.playVoice({
							localId: localId // 需要播放的音频的本地ID，由stopRecord接口获得
						});
					}
				});
			} else {
				var status = $(this).attr("status");
				if(status == 0) {
					myImg.attr("status", 1);
					myImg.attr("src", "static/img/stopAudio.png");
					wx.playVoice({
						localId: localId // 需要播放的音频的本地ID，由stopRecord接口获得
					});
				} else {
					myImg.attr("status", 0);
					myImg.attr("src", "static/img/audio.png");
					wx.stopVoice({
						localId: localId //  需要停止的音频的本地ID，由stopRecord接口获得
					});
				}
			}
		})

		$("img.img-msg").on("click", function() {
			var imgList = new Array();
			$("img.img-msg").each(function() {
				imgList.push($(this).attr("localId"));
			})

			var src = $(this).attr("localId");
			wx.previewImage({
				current: src, // 当前显示图片的http链接
				urls: imgList // 需要预览的图片http链接列表
			});
		})
		
		loadImg();
	});

	wx.error(function(res) {
		// config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。
		if(confirm("微信验证失败,您可能无法上传语音和图片,是否重新验证?")) {
			window.location.reload(true);
		}
	});

	/**
	 * 举手
	 */
	$("img.raise").on("click", function() {
		sendData(5, "");
		var li = document.createElement("li");
		li.className = "time";
		var span = document.createElement("span");
		span.innerHTML = "您已请求发言";
		li.appendChild(span);
		msgList.appendChild(li);
		msgList.scrollTop = msgList.scrollHeight;
	})

	//禁言
	$("img.material").on("click", function() {
		var status = $(this).attr("status");
		status = parseInt(status);
		if(status == 0) {
			sendData(4, "1");
		} else {
			sendData(4, "0");
		}
	})

	msgList.scrollTop = msgList.scrollHeight;
})

var i = 0;
var loadImgs = $("img.img-msg");

function loadImg() {
	if(i >= loadImgs.length) {
		return;
	}

	var img = loadImgs.eq(i);
	i++;
	wx.downloadImage({
		serverId: img.attr("serverId"), // 需要下载的图片的服务器端ID，由uploadImage接口获得
		isShowProgressTips: 1, // 默认为1，显示进度提示
		success: function(res) {
			var localId = res.localId; // 返回图片下载后的本地ID
			img.attr("localId", localId);
			img.on("load", function() {
				msgList.scrollTop = msgList.scrollHeight;
			})
			img.attr("src", localId);
			loadImg();
		}
	});
}

function appendTempMsg(msg) {
	var li = document.createElement("li");
	li.className = "time";
	var span = document.createElement("span");
	span.innerHTML = msg;
	li.appendChild(span);
	msgList.appendChild(li);
}

function uploadVoice(localId) {
	if(confirm("录音已经结束，是否发送？")) {
		wx.uploadVoice({
			localId: localId, // 需要上传的音频的本地ID，由stopRecord接口获得
			isShowProgressTips: 1, // 默认为1，显示进度提示
			success: function(res) {
				var serverId = res.serverId; // 返回音频的服务器端ID
				sendData(2, serverId);
			}
		});
	}
}

function initWeiXinJs() {
	$.get("http://qrcode.yzan.net/weixin/signature", {
		"url": window.location.href.split("#")[0],
		"appid": "wxdb193ef7bee93c26",
	}, function(data) {
		wx.config({
			"debug": false,
			"appId": data.data.appid,
			"timestamp": data.data.timestamp,
			"nonceStr": data.data.nonceStr,
			"signature": data.data.signature,
			"jsApiList": [
				"startRecord",
				"stopRecord",
				"onVoiceRecordEnd",
				"playVoice",
				"pauseVoice",
				"stopVoice",
				"onVoicePlayEnd",
				"uploadVoice",
				"downloadVoice",
				"chooseImage",
				"previewImage",
				"uploadImage",
				"downloadImage",
				"translateVoice",
				"getLocalImgData"
			]
		})
	})
}

function sendText() {
	var text = $("#input").val();
	if(text.length == 0) {
		myAlert("请输入内容");
		return false;
	}

	sendData(1, text);
	$("#input").val("");
	return false;
}

/**
 * 1是文本2是语音3是图片
 * @param {Object} type
 * @param {Object} data
 */
function sendData(type, data) {
	var json = {
		"type": type,
		"data": data
	};
	websocket.send(JSON.stringify(json));
}

var myOpenid = document.getElementById("openid").value;
var msgList = document.getElementById("message-list");

var onlineCount = document.getElementById("onlineCount");

function addHtmlMessage(data) {
	var isBottom = msgList.scrollTop >= (msgList.scrollHeight - $("#message-list").height());
	switch(data.type) {
		case 10://弹出链接
			
			break;
		case 9://本节课程已经结束
			
			break;
		case 4: //禁言
			var status = parseInt(data.data);
			$("img.material").attr("status", status);
			if(status == 1) {
				//禁言中
				var li = document.createElement("li");
				li.className = "time";
				var span = document.createElement("span");
				span.innerHTML = "禁言中......";
				li.appendChild(span);
				msgList.appendChild(li);

				if(isBottom) {
					msgList.scrollTop = msgList.scrollHeight;
				}
			} else {
				//取消禁言
				var li = document.createElement("li");
				li.className = "time";
				var span = document.createElement("span");
				span.innerHTML = "已取消禁言";
				li.appendChild(span);
				msgList.appendChild(li);

				if(isBottom) {
					msgList.scrollTop = msgList.scrollHeight;
				}
			}
			break;
		case 5: //举手
			var li = document.createElement("li");
			li.className = "time";
			var span = document.createElement("span");
			span.innerHTML = "<span class='nickName'>" + data.user.nickName + "</span>请求发言";
			li.appendChild(span);
			msgList.appendChild(li);

			if(isBottom) {
				msgList.scrollTop = msgList.scrollHeight;
			}
			break;
		case 6: //在线统计
			onlineCount.innerHTML = data.data;
			break;
		case 7:
			var li = document.createElement("li");
			li.className = "time";
			var span = document.createElement("span");
			span.innerHTML = "<span class='nickName'>" + data.user.nickName + "</span>进入";
			li.appendChild(span);
			msgList.appendChild(li);

			if(isBottom) {
				msgList.scrollTop = msgList.scrollHeight;
			}
			break;
		case 8:
			var li = document.createElement("li");
			li.className = "time";
			var span = document.createElement("span");
			span.innerHTML = "<span class='nickName'>" + data.user.nickName + "</span>离开";
			li.appendChild(span);
			msgList.appendChild(li);

			if(isBottom) {
				msgList.scrollTop = msgList.scrollHeight;
			}
			break;
		default:
			var li = getLi(data.user.openid);
			var img = getImg(data.user.titleImg);
			li.appendChild(img);
			var div = getDiv();
			li.appendChild(div);
			var titleDiv = getDiv();
			titleDiv.className = "title";
			titleDiv.innerHTML = data.user.nickName;
			div.appendChild(titleDiv);
			var contentDiv = getDiv();
			contentDiv.className = "content";
			div.appendChild(contentDiv);
			switch(data.type) {
				case 1:
					$(contentDiv).text(data.data.data);
					//contentDiv.innerHTML = msgContent;
					msgList.appendChild(li);

					if(isBottom) {
						msgList.scrollTop = msgList.scrollHeight;
					}
					break;
				case 2:
					var img = document.createElement("img");
					img.setAttribute("src", "static/img/audio.png");
					img.setAttribute("serverId", data.data);
					img.setAttribute("status", 0);
					img.className = "msg-audio";
					contentDiv.appendChild(img);
					msgList.appendChild(li);

					if(isBottom) {
						msgList.scrollTop = msgList.scrollHeight;
					}

					$(img).on("click", function() {
						var localId = $(this).attr("localId");
						var myImg = $(this);
						if(!localId || localId.length == 0) {
							wx.downloadVoice({
								serverId: $(this).attr("serverId"), // 需要下载的音频的服务器端ID，由uploadVoice接口获得
								isShowProgressTips: 1, // 默认为1，显示进度提示
								success: function(res) {
									var localId = res.localId; // 返回音频的本地ID
									myImg.attr("localId", localId);
									myImg.attr("status", 1);
									myImg.attr("src", "static/img/stopAudio.png");
									wx.playVoice({
										localId: localId // 需要播放的音频的本地ID，由stopRecord接口获得
									});
								}
							});
						} else {
							var status = $(this).attr("status");
							if(status == 0) {
								myImg.attr("status", 1);
								myImg.attr("src", "static/img/stopAudio.png");
								wx.playVoice({
									localId: localId // 需要播放的音频的本地ID，由stopRecord接口获得
								});
							} else {
								myImg.attr("status", 0);
								myImg.attr("src", "static/img/audio.png");
								wx.stopVoice({
									localId: localId //  需要停止的音频的本地ID，由stopRecord接口获得
								});
							}
						}
					})
					break;
				case 3:
					wx.downloadImage({
						serverId: data.data, // 需要下载的图片的服务器端ID，由uploadImage接口获得
						isShowProgressTips: 1, // 默认为1，显示进度提示
						success: function(res) {
							var localId = res.localId; // 返回图片下载后的本地ID

							var img = document.createElement("img");
							img.className = "img-msg";
							img.setAttribute("localId", localId);
							$(img).on("load", function() {
								if(isBottom) {
									msgList.scrollTop = msgList.scrollHeight;
								}
							})
							img.setAttribute("src", localId);
							contentDiv.appendChild(img);
							msgList.appendChild(li);

							$(img).on("click", function() {
								var imgList = new Array();
								$("img.img-msg").each(function() {
									imgList.push($(this).attr("localId"));
								})

								var src = $(this).attr("localId");
								wx.previewImage({
									current: src, // 当前显示图片的http链接
									urls: imgList // 需要预览的图片http链接列表
								});
							})
						}
					});
					break;
				default:
					break;
			}
			break;
	}
}

function getLi(openid) {
	var li = document.createElement("li");
	if(openid == myOpenid) {
		li.className = "msg clearfix right";
	} else {
		li.className = "msg clearfix left";
	}
	return li;
}

function getImg(src) {
	var img = document.createElement("img");
	img.setAttribute("src", src);
	return img;
}

function getDiv() {
	var div = document.createElement("div");
	div.className = "clearfix";
	return div;
}
