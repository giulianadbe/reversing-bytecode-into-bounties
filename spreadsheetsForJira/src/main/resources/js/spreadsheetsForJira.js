(function(){
	function contextPath(){
		try { return AJS && AJS.contextPath ? AJS.contextPath() : '/jira'; } catch(e){ return '/jira'; }
	}
	function readFileAsText(file){
		return new Promise(function(resolve, reject){
			var reader = new FileReader();
			reader.onload = function(){ resolve(reader.result); };
			reader.onerror = reject;
			reader.readAsText(file);
		});
	}
	function postXml(path, xml){
		return fetch(contextPath() + path, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/xml',
				'X-Atlassian-Token': 'no-check'
			},
			credentials: 'same-origin',
			body: xml
		}).then(function(r){ return r.text(); });
	}
	function initUploadPage(){
		var form = document.getElementById('xml-upload-form');
		if (!form) return;
		var fileInput = document.getElementById('upload-file');
		var clientErr = document.getElementById('client-error');
		var clientRes = document.getElementById('client-result');
		function show(el, text){ if (!el) return; el.textContent = text; el.style.display = ''; }
		function hide(el){ if (el) el.style.display = 'none'; }
		function handleSubmit(isValidate){
			hide(clientErr); hide(clientRes);
			if (!fileInput || !fileInput.files || !fileInput.files[0]) { show(clientErr, 'Error: Please select a file to upload'); return; }
			var restPath = isValidate ? '/rest/spreadsheet/1.0/validate' : '/rest/spreadsheet/1.0/process-xml';
			readFileAsText(fileInput.files[0])
				.then(function(xml){ return postXml(restPath, xml); })
				.then(function(text){ show(clientRes, text); })
				.catch(function(err){ show(clientErr, (err && err.message) ? err.message : String(err)); });
		}
		form.addEventListener('submit', function(e){ e.preventDefault(); handleSubmit(false); });
		var processBtn = document.getElementById('process-file-btn');
		var validateBtn = document.getElementById('validate-only-btn');
		if (processBtn) processBtn.addEventListener('click', function(){ handleSubmit(false); });
		if (validateBtn) validateBtn.addEventListener('click', function(){ handleSubmit(true); });
	}
	if (document.readyState === 'loading') {
		document.addEventListener('DOMContentLoaded', initUploadPage);
	} else {
		initUploadPage();
	}
})();


