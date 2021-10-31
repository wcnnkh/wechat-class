function showTempMsg(msg) {
	var div = document.createElement("div");
	div.className = "tempMsg";
	div.innerHTML = msg;
	document.body.appendChild(div);
	$(div).css("margin-left", -$(div).width() / 2);
	setTimeout(function() {
		$(div).fadeOut("slow", function() {
			document.body.removeChild(div);
		});
	}, 1000);
}

function setJsonValue(json) {
	if(json) {
		for(key in json) {
			$("#" + key).val(json[key]);
		}
	}
}

/**
 * 上传图片
 * @param {Object} ev 事件
 * @param {Object} call 回调函数
 */
function changeSubmitImage(ev, call){
	submitImage(ev.target.files[0], call);
}

/**
 * 上传图片
 * @param {Object} file 文件
 * @param {Object} call 回调函数
 */
function submitImage(file, call) {
	if(file == undefined) {
		return;
	}

	var file_size = Math.round(file.size / 1024 * 100) / 100; //kb
	if(!/image\/\w+/.test(file.type)) {
		showTempMsg("请选择文件类型为图片的");
		return;
	}

	if(file_size > 5000) {
		showTempMsg("上传文件的大小必须小于5MB!");
		return;
	}

	var reader = new FileReader();
	showLoading();
	reader.onload = function(e) {
		var result = e.target.result;
		var base64 = result.split(",")[1];
		var hz = file.name.split(".")[1];
		$.ajax({
			url: "http://192.168.0.223/dfs/upload/base64ImageResize?tag=smt-wx-shop",
			type: "POST",
			dataType: "json",
			data: { "files": base64, "suffixs": hz },
			success: function(data) {
				if(data.error == 0) {
					var d = data.data[0];
					if(d.error == 0) {
						showTempMsg("上传成功");
						call(d.data);
					} else {
						showTempMsg(d.message);
					}
				} else {
					showTempMsg(data.message);
				}
				hideLoading();
			},
			error: function() {
				showTempMsg("服务器错误");
				hideLoading();
			}
		});
	}
	reader.readAsDataURL(file);
}

function showLoading() {
	var load = $("#loading");
	if(load.length == 0) {
		var div = document.createElement("div");
		div.setAttribute("id", "loading");
		document.body.appendChild(div);
		div.innerHTML = '<div id="loading-center">' +
			'<div id="loading-center-absolute">' +
			'<div class="object" id="object_one"></div>' +
			'<div class="object" id="object_two" style="left:20px;"></div>' +
			'<div class="object" id="object_three" style="left:40px;"></div>' +
			'<div class="object" id="object_four" style="left:60px;"></div>' +
			'<div class="object" id="object_five" style="left:80px;"></div>' +
			'</div>' +
			'</div>';
	}
	$("#loading").show();
}

function hideLoading() {
	$("#loading").hide();
}

function Table(tableId, isSelect, cellConfig) {
	this.isSelect = isSelect;
	this.cellConfig = cellConfig;
	this.table = document.getElementById(tableId);
	this.trs = new Array();
	this.data = new Array();
	this.count = 0;

	this.init = function() {
		var tr = document.createElement("tr");
		if(this.isSelect) {
			var th = document.createElement("th");
			var input = document.createElement("input");
			input.setAttribute("type", "checkbox");
			input.setAttribute("title", "全选/反选");
			th.appendChild(input);
			tr.appendChild(th);

			$(input).click(function() {
				var tr = $(this).parent().parent();
				var trs = tr.parent().find("tr");
				if($(this).prop("checked")) {
					for(var i = 0; i < trs.length; i++) {
						var tr = trs[i];
						$(tr).find("td:first-child input[type='checkbox']").prop("checked", true);
						$(tr).addClass("selectActive");
					}
				} else {
					for(var i = 0; i < trs.length; i++) {
						var tr = trs[i];
						$(tr).find("td:first-child input[type='checkbox']").prop("checked", false);
						$(tr).removeClass("selectActive");
					}
				}
			})
		}
		for(var i = 0; i < this.cellConfig.length; i++) {
			var th = document.createElement("th");
			th.innerHTML = this.cellConfig[i].name;
			tr.appendChild(th);
		}
		this.table.appendChild(tr);
	}

	/**
	 * 获取被选中的行
	 */
	this.getSelectTrs = function() {
		return $(this.table).find("tr.selectActive");
	}

	/**
	 * 清除所有数据
	 */
	this.clearData = function() {
		for(var i = 0; i < this.trs.length; i++) {
			$(this.trs[i]).remove();
		}
		this.trs = new Array();
		this.data = new Array();
	}

	/**
	 * 删除指定数据
	 */
	this.deleteData = function(index) {
		var newData = new Array();
		for(var i = 0; i < this.data.lenght; i++) {
			if(i != index) {
				newData.push(this.data[i]);
			}
		}
		this.data = newData;

		var newTrs = new Array();
		for(var i = 0; i < this.trs.length; i++) {
			var tr = this.trs[i];
			if(i == index) {
				$(tr).remove();
			} else {
				$(tr).attr("index", i - 1);
				newTrs.push(tr);
			}
		}
	}

	this.updateData = function(index, newCellData) {
		var tr = this.trs[index];
		tr.innerHTML = "";
		if(this.isSelect) {
			var td = document.createElement("td");
			var input = document.createElement("input");
			input.setAttribute("type", "checkbox");
			td.appendChild(input);
			tr.appendChild(td);

			$(input).click(function() {
				if($(this).prop("checked")) {
					$(this).parent().parent().addClass("selectActive");
				} else {
					$(this).parent().parent().removeClass("selectActive");
				}
			})
		}

		for(var i = 0; i < this.cellConfig.length; i++) {
			var td = document.createElement("td");
			td.setAttribute("index", i);
			var render = this.cellConfig[i].render;
			if(render) {
				td.innerHTML = render(this.count, newCellData);
			} else {
				var key = this.cellConfig[i].key;
				if(key) {
					td.innerHTML = newCellData[key];
				}
			}
			tr.appendChild(td);
		}

		this.trs[index] = tr;
		this.data[index] = newCellData;
	}

	/**
	 * 添加数据
	 */
	this.appendData = function(cellData) {
		var tr = document.createElement("tr");
		tr.setAttribute("index", this.count);
		if(this.isSelect) {
			var td = document.createElement("td");
			var input = document.createElement("input");
			input.setAttribute("type", "checkbox");
			td.appendChild(input);
			tr.appendChild(td);

			$(input).click(function() {
				if($(this).prop("checked")) {
					$(this).parent().parent().addClass("selectActive");
				} else {
					$(this).parent().parent().removeClass("selectActive");
				}
			})
		}

		for(var i = 0; i < this.cellConfig.length; i++) {
			var td = document.createElement("td");
			td.setAttribute("index", i);
			var render = this.cellConfig[i].render;
			if(render) {
				td.innerHTML = render(this.count, cellData);
			} else {
				var key = this.cellConfig[i].key;
				if(key) {
					td.innerHTML = cellData[key];
				}
			}
			tr.appendChild(td);
			$(tr).hover(function() {
				$(this).addClass("active");
			}, function() {
				$(this).removeClass("active");
			})
		}
		this.table.appendChild(tr);
		this.trs.push(tr);
		this.data.push(cellData);
		this.count += 1;
	}
	this.init();
	return this;
}

// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
// 例子： 
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
Date.prototype.format = function(fmt) { //author: meizz 
	var o = {
		"M+": this.getMonth() + 1, //月份 
		"d+": this.getDate(), //日 
		"H+": this.getHours(), //小时 
		"m+": this.getMinutes(), //分 
		"s+": this.getSeconds(), //秒 
		"q+": Math.floor((this.getMonth() + 3) / 3), //季度 
		"S": this.getMilliseconds() //毫秒 
	};
	if(/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	for(var k in o)
		if(new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}