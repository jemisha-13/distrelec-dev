<?xml version='1.0' encoding="windows-1252"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="html" encoding="UTF-8"/>

	<xsl:template match="/">
		<html>
			<head>
				<title>PaySSL</title>
			
			
			<script id="clientEventHandlersJS" language="JavaScript">
			
					var index = 0;
					
					function SSLForm_onsubmit() 
					{
						var checkCC = /\D.+/;
						//JavaScriptCheck
						document.SSLForm.JS.value = "true";

						document.SSLForm.CCBrand.style.borderColor = "";
						document.SSLForm.CCnr.style.borderColor = "";
						document.SSLForm.KKMonth.style.borderColor = "";
						document.SSLForm.KKYear.style.borderColor = "";
						document.SSLForm.cccvc.style.borderColor = "";
						
						if (document.SSLForm.CCBrand.selectedIndex == 0) 
						{
							alert("<xsl:value-of select="/paygate/language/JSError/ChooseBrand"/>");
							document.SSLForm.CCBrand.focus();
							document.SSLForm.CCBrand.style.borderColor = "red";
							return false;
						}
						if (document.SSLForm.CCnr.value == "") 
						{
							alert("<xsl:value-of select="/paygate/language/JSError/ValidCC"/>");
							document.SSLForm.CCnr.focus();
							document.SSLForm.CCnr.style.borderColor = "red";
							return false;
						}
						if (document.SSLForm.CCnr.value.length &lt; 13) 
						{
							alert("<xsl:value-of select="/paygate/language/JSError/ValidCC"/>");
							document.SSLForm.CCnr.focus();
							document.SSLForm.CCnr.style.borderColor = "red";
							return false;
						}
						if (document.SSLForm.CCnr.value.length &gt; 19) 
						{
							alert("<xsl:value-of select="/paygate/language/JSError/ValidCC"/>");
							document.SSLForm.CCnr.focus();
							document.SSLForm.CCnr.style.borderColor = "red";
							return false;
						}						
						if (document.SSLForm.KKMonth.selectedIndex == 0)
						{
							alert("<xsl:value-of select="/paygate/language/JSError/ChooseMonth"/>");
							document.SSLForm.KKMonth.focus();
							document.SSLForm.KKMonth.style.borderColor = "red";
							return false;
						}
						if (document.SSLForm.KKYear.selectedIndex == 0)
						{
							alert("<xsl:value-of select="/paygate/language/JSError/ChooseYear"/>");
							document.SSLForm.KKYear.focus();
							document.SSLForm.KKYear.style.borderColor = "red";
							return false;
						}						
						if (document.SSLForm.cccvc.value.length &lt; 3)
						{
							alert("<xsl:value-of select="/paygate/language/JSError/FillCVV"/>");
							document.SSLForm.cccvc.focus();
							document.SSLForm.cccvc.style.borderColor = "red";
							return false;
						}	
						if (index == 0) 
						{
							index += 1;
							hiddensubmit1();
							showpayStatus();							
							return true;
						}
						else
						{
							return false;
						}
					}
					function showpayStatus()
					{
						if(document.getElementById)
						document.getElementById("payStatus").style.visibility = "visible";
					}	
											
					function firstField()
					{
						document.SSLForm.creditcardholder.focus();
						//JavaScriptCheck
						document.SSLForm.JS.value = "true";
					}

					function openWin()
					{
						window.open("", "kpn", "width=500,height=470,resizable,screenX=0,screenY=0");
					}					
					
										
					function openCT()
					{
						window.open("",
							"Impressum",
							"width=390,height=450,resizable,screenX=0,screenY=0");
					}
					
					function hiddensubmit1()
					{
						if(document.getElementById)
						document.getElementById("submit1").style.visibility = "hidden";						
					}
					function releaseclr()
					{
						document.SSLForm.creditcardholder.value = "";
						document.getElementById("creditcardholder_label").style.display = "none";
						document.getElementById("creditcardholder_field").style.display = "";
						document.SSLForm.CCBrand.selectedIndex = 0;
						document.getElementById("CCBrand_label").style.display = "none";
						document.getElementById("CCBrand_field").style.display = "";
						document.SSLForm.CCnr.value = "";
						document.getElementById("CCnr_label").style.display = "none";
						document.getElementById("CCnr_field").style.display = "";
						document.SSLForm.KKMonth.selectedIndex = 0;
						document.SSLForm.KKYear.selectedIndex = 0;
						document.getElementById("KKMonthYear_label").style.display = "none";
						document.getElementById("KKMonthYear_field").style.display = "";
						document.SSLForm.cccvc.value = "";
						document.SSLForm.cccvc.style.borderColor = "";
					}
			</script>
			</head>
			<style type="text/css">
				body
					{
						font-family : Verdana,Arial,Helvetica,sans-serif;
						color : #000000;
						font-size : 11px;
					}
				td
					{
						font-family : Verdana,Arial,Helvetica,sans-serif;
						color : #000000;
						font-size : 11px;
					}
				input
					{
						font-family : Verdana,Arial,Helvetica,sans-serif;
						color : #000000;
						font-size : 11px;
					}
			</style>
			<body bgcolor="#FFFFFF" text="#000000" link="#003366" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">			
				<form name="SSLForm" id="SSLForm" action="https://www.netkauf.de/paygate/payinterim.aspx" method="POST" language="JavaScript" onsubmit="return SSLForm_onsubmit();" autocomplete="off">
				    <input type="hidden" name="MerchantID"><xsl:attribute name="value"><xsl:value-of select="/paygate/merchantID"/></xsl:attribute></input>
			        <input type="hidden" name="Len"><xsl:attribute name="value"><xsl:value-of select="/paygate/len"/></xsl:attribute></input>
			        <input type="hidden" name="Data"><xsl:attribute name="value"><xsl:value-of select="/paygate/data"/></xsl:attribute></input>
			        <input type="hidden" name="Counter"><xsl:attribute name="value"><xsl:value-of select="/paygate/counter" /></xsl:attribute></input> 
				    <input type="hidden" name="Template" value="deucsdemo"/>
				    <input type="hidden" name="language"><xsl:attribute name="value"><xsl:value-of select="/paygate/language/@name" /></xsl:attribute></input>
				    <input type="hidden" name="Sprache"><xsl:attribute name="value"><xsl:value-of select="/paygate/language/@name" /></xsl:attribute></input>
				    <input type="hidden" name="Background"><xsl:attribute name="value"><xsl:value-of select="/paygate/Background"/></xsl:attribute></input>
					<input type="hidden" name="BGImage"><xsl:attribute name="value"><xsl:value-of select="/paygate/BGImage"/></xsl:attribute></input>
				    <input type="hidden" name="BGColor"><xsl:attribute name="value"><xsl:value-of select="/paygate/BGColor"/></xsl:attribute></input>
				    <input type="hidden" name="FFace"><xsl:attribute name="value"><xsl:value-of select="/paygate/FFace"/></xsl:attribute></input>
				    <input type="hidden" name="FSize"><xsl:attribute name="value"><xsl:value-of select="/paygate/FSize"/></xsl:attribute></input>
				    <input type="hidden" name="FColor"><xsl:attribute name="value"><xsl:value-of select="/paygate/FColor"/></xsl:attribute></input>
				    <input type="hidden" name="twidth"><xsl:attribute name="value"><xsl:value-of select="/paygate/twidth"/></xsl:attribute></input>
				    <input type="hidden" name="theight"><xsl:attribute name="value"><xsl:value-of select="/paygate/theight"/></xsl:attribute></input>
				    <input type="hidden" name="JS" value="false"/>            
				    <input type="hidden" name="notify" value=""/>
				            
					<table width="462" cellspacing="2" cellpadding="4" border="0">
						<tr>
							<td>
								<table width="100%" cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td><strong><xsl:value-of select="/paygate/language/Label/CreditCard"/></strong></td>
										<td style="height:30px" align="right"><img src="Templates/imagesdeucs/visa_neu.png" alt="Visa"/>&#xA0;<img src="Templates/imagesdeucs/mastercard_neu.png" border="0" alt="Master Card"/></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								<table width="100%" cellspacing="2" cellpadding="4" border="0">
									<tr>
										<td align="right" width="139" style="line-height:1.5em;"><xsl:value-of select="/paygate/language/Label/CCHolder"/></td>
										<td align="left" id="creditcardholder_label">
											<xsl:if test="/paygate/PCNr = ''">
												<xsl:attribute name="style">display:none</xsl:attribute>
											</xsl:if>
											<xsl:value-of select="/paygate/creditCardHolder"/>
										</td>
										<td align="left" id="creditcardholder_field">
											<xsl:if test="/paygate/PCNr != ''">
												<xsl:attribute name="style">display:none</xsl:attribute>
											</xsl:if>
											<input type="text" name="creditcardholder" maxLength="50" size="20">
												<xsl:if test="/paygate/PCNr != ''">
													<xsl:attribute name="value"><xsl:value-of select="/paygate/creditCardHolder"/></xsl:attribute>
												</xsl:if>
											</input>
										</td>
									</tr>
									<tr>
										<td align="right" style="line-height:1.5em;"><xsl:value-of select="/paygate/language/Label/Brand"/></td>
										<td align="left" id="CCBrand_label">
											<xsl:if test="/paygate/PCNr = ''">
												<xsl:attribute name="style">display:none</xsl:attribute>
											</xsl:if>
											<xsl:value-of select="/paygate/PCNrBrand"/>
										</td>
										<td align="left" id="CCBrand_field">
											<xsl:if test="/paygate/PCNr != ''">
												<xsl:attribute name="style">display:none</xsl:attribute>
											</xsl:if>
											<select name="CCBrand" size="1">
												<option></option>
												<xsl:for-each select="paygate/brands/brand">
													<option>
														<xsl:if test="/paygate/PCNr != ''">
															<xsl:if test=".=/paygate/PCNrBrand">
																<xsl:attribute name="selected">selected</xsl:attribute>
															</xsl:if>
														</xsl:if>
														<xsl:value-of select="."/>
													</option>
												</xsl:for-each>
											</select>
										</td>
									</tr>
									<tr>
										<td align="right" style="line-height:1.5em;"><xsl:value-of select="/paygate/language/Label/CCNr"/></td>
										<td align="left" id="CCnr_label">
											<xsl:if test="/paygate/PCNr = ''">
												<xsl:attribute name="style">display:none</xsl:attribute>
											</xsl:if>
											*************<xsl:value-of select="substring(/paygate/PCNr,string-length(/paygate/PCNr)-2)"/>
										</td>
										<td align="left" id="CCnr_field">
											<xsl:if test="/paygate/PCNr != ''">
												<xsl:attribute name="style">display:none</xsl:attribute>
											</xsl:if>
											<input type="text" name="CCnr" maxlength="19" size="20">
												<xsl:if test="/paygate/PCNr != ''">
													<xsl:attribute name="value"><xsl:value-of select="/paygate/PCNr"/></xsl:attribute>
												</xsl:if>
											</input>
										</td>
									</tr>
									<tr>
										<td align="right" style="line-height:1.5em;"><xsl:value-of select="/paygate/language/Label/ExpiryDate"/></td>
										<td align="left" id="KKMonthYear_label">
											<xsl:if test="/paygate/PCNr = ''">
												<xsl:attribute name="style">display:none</xsl:attribute>
											</xsl:if>
											<xsl:value-of select="/paygate/PCNrMonth"/>/<xsl:value-of select="/paygate/PCNrYear"/>
										</td>
										<td align="left" id="KKMonthYear_field">
											<xsl:if test="/paygate/PCNr != ''">
												<xsl:attribute name="style">display:none</xsl:attribute>
											</xsl:if>
											<select name="KKMonth" size="1">
												<option value="*">*</option>
												<xsl:call-template name="ExpiryMonth">
													<xsl:with-param name="month">1</xsl:with-param>
												</xsl:call-template>		                          
											</select>
											/
											<select name="KKYear" size="1">
												<option value="*">*</option>
												<xsl:call-template name="ExpiryYear">
													<xsl:with-param name="year"><xsl:value-of select="number(paygate/Year)"/></xsl:with-param>
													<xsl:with-param name="counter">1</xsl:with-param>
												</xsl:call-template>				                          
											</select>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								<table width="100%" cellspacing="2" cellpadding="4" border="0">
									<tr>
										<td align="right" width="139"><xsl:value-of select="/paygate/language/Label/CVV"/></td>
										<td align="left">
											<input type="password" name="cccvc" maxLength="4" size="5">
												<xsl:if test="/paygate/PCNr != ''">
													<xsl:attribute name="style">border-color:red</xsl:attribute>
												</xsl:if>
											</input>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								<table width="100%" cellspacing="2" cellpadding="4" border="0">
									<tr>
										<td>
											<table border="0" width="100%">
												<tr>
													<td id="payStatus" style="visibility:hidden" colspan="3" align="left">
														<font size="-1" color="" face="Arial"><xsl:value-of select="/paygate/language/Action/PayProcess"/><br/><br/></font>
													</td>
												</tr>
												<xsl:apply-templates select="/paygate/errorcode" />
												<tr>	
													<xsl:if test="/paygate/PCNr != ''">
													<td align="left">
														<input type="button" id="chgbut" onclick="javascript:releaseclr()" name="chgbut">
															<xsl:attribute name="value"><xsl:value-of select="/paygate/language/Label/ChangeButton"/></xsl:attribute>
														</input>
													</td>
													</xsl:if>
													<td align="right" colspan="2">
														<input type="submit" id="submit1" name="submit1" style="visibility:visible"/>
													</td>  
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</form>
			</body>
		</html>			
	</xsl:template>
	
	<xsl:template name="ExpiryYear">
		<xsl:param name="year"/>
		<xsl:param name="counter"/>
		<xsl:if test="$counter &lt; 16">
			<option>
				<xsl:attribute name="value">
					<xsl:value-of select="$year"/>
				</xsl:attribute>
				<xsl:if test="/paygate/PCNr != ''">
					<xsl:if test="/paygate/PCNrYear=$year">
						<xsl:attribute name="selected">selected</xsl:attribute>
					</xsl:if>
				</xsl:if>
				<xsl:value-of select="$year"/>
			</option>
			<xsl:call-template name="ExpiryYear">
				<xsl:with-param name="year" select="$year + 1"/>
				<xsl:with-param name="counter" select="$counter + 1"/>
 			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	
	<xsl:template name="ExpiryMonth">
		<xsl:param name="month"/>
		<xsl:if test="$month &lt; 13">
			<option>
				<xsl:attribute name="value"><xsl:if test="$month &lt; 10">0</xsl:if><xsl:value-of select="$month"/></xsl:attribute>
				<xsl:if test="/paygate/PCNr != ''">
					<xsl:if test="number(/paygate/PCNrMonth)=$month">
						<xsl:attribute name="selected">selected</xsl:attribute>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$month &lt; 10">0</xsl:if>
				<xsl:value-of select="$month"/>
			</option>
			<xsl:call-template name="ExpiryMonth">
				<xsl:with-param name="month" select="$month +1"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="errorcode">
		<tr>
			<td colspan="5" align="center">
				<div id="error" style="color:#0000FF;font-weight:bold;font-family:arial,helvetica,sans-serif;font-size:11px;">

					<xsl:if test=".='0015'">
						<xsl:value-of select="/paygate/language/Error/CCNr"/>
					</xsl:if>
					<xsl:if test=".='0016'">
						<xsl:value-of select="/paygate/language/Error/CCNr"/>
					</xsl:if>	
					<xsl:if test=".='0017' or .='0094'">
						<xsl:value-of select="/paygate/language/Error/ExpiryDate"/>
					</xsl:if>	
					<xsl:if test=".='0018'">
						<xsl:value-of select="/paygate/language/Error/Brand"/>
					</xsl:if>	
					<xsl:if test=".='0019'">
						<xsl:value-of select="/paygate/language/Error/CVV"/>
					</xsl:if>	
					<xsl:if test=".='0093'">
						<xsl:value-of select="/paygate/language/Error/JScript"/>
					</xsl:if>	
					<xsl:if test=".='0100' or .='0102' or .='0110' or .='0302' or .='0304' or .='0305' or .='0306' or .='0313' or .='0314' or .='0333' or .='0356' or .='0362'">	
						<xsl:value-of select="/paygate/language/Error/Card"/>
					</xsl:if>		

				</div>
			</td>
		</tr>	
	</xsl:template>
	
</xsl:stylesheet>