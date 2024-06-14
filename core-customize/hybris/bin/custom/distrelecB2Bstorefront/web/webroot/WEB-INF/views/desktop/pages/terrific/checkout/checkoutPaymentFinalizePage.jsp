<script type="text/javascript">
	<!--
		var continueUrl = '${continueUrl}';
		var code = '${orderCode}';
		var param = '${allParameters}';
		
		if(!code) {
			code = '${errorCode}';
		}
		
		if(code) {
			top.location = window.location.protocol + '//' + window.location.host + continueUrl + '/' + code + '?' + param;
		} else {
			top.location = window.location.protocol + '//' + window.location.host + continueUrl + '?' + param;
		}
	//-->
</script>

Please wait...