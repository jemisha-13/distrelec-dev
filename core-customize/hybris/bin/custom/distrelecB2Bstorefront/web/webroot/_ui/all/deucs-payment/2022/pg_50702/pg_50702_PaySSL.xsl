<?xml version='1.0' encoding="windows-1252"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="UTF-8"/>
    <xsl:template match="/">
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
        <xsl:comment><![CDATA[[if IE 7]><html class="ie7"><![endif]]]></xsl:comment>
        <xsl:comment><![CDATA[[if IE 8]><html class="ie8"><![endif]]]></xsl:comment>
        <xsl:comment><![CDATA[[if IE 9]><html class="ie9"><![endif]]]></xsl:comment>
        <xsl:comment><![CDATA[[if (gt IE 9)|!(IE)]><!]]></xsl:comment>
        <html class="">
            <xsl:comment><![CDATA[<![endif]]]></xsl:comment>
            <head>
                <title>PaySSL</title>
                <style type="text/css">
                    @font-face {
                    font-family: 'Montserrat';
                    src: url("Templates/imagespg_50702/Montserrat-Bold.woff2") format("woff2"), url("Templates/imagespg_50702/Montserrat-Bold.woff") format("woff");
                    font-weight: bold;
                    font-display: swap;
                    unicode-range: U+0600-U+06FF,U+1D400—U+1D7FF,U+0590—U+05FF,U+4E00-U+9FFF,U+3400-U+4DBF,U+20000-U+2A6DF, U+2A700–U+2B73F,U+2B740–U+2B81F,U+2B820–U+2CEAF,U+F900-U+FAFF,U+2F800-U+2FA1F,U+0370—U+03FF,U+1D300—U+1D35F U+0700—U+074F,U+0F00—U+0FFF,U+1000—U+109F,U+1200—U+137F,U+1100—U+11FF,U+A500—U+A63F;
                    }

                    @font-face {
                    font-family: 'Montserrat';
                    src: url("Templates/imagespg_50702/Montserrat-SemiBold.woff2") format("woff2"), url("Templates/imagespg_50702/Montserrat-SemiBold.woff") format("woff");
                    font-weight: 600;
                    font-display: swap;
                    unicode-range: U+0600-U+06FF,U+1D400—U+1D7FF,U+0590—U+05FF,U+4E00-U+9FFF,U+3400-U+4DBF,U+20000-U+2A6DF, U+2A700–U+2B73F,U+2B740–U+2B81F,U+2B820–U+2CEAF,U+F900-U+FAFF,U+2F800-U+2FA1F,U+0370—U+03FF,U+1D300—U+1D35F U+0700—U+074F,U+0F00—U+0FFF,U+1000—U+109F,U+1200—U+137F,U+1100—U+11FF,U+A500—U+A63F;
                    }

                    @font-face {
                    font-family: 'Montserrat';
                    src: url("Templates/imagespg_50702/Montserrat-Regular.woff2") format("woff2"), url("Templates/imagespg_50702/Montserrat-Regular.woff") format("woff");
                    font-weight: normal;
                    font-display: swap;
                    unicode-range: U+0600-U+06FF,U+1D400—U+1D7FF,U+0590—U+05FF,U+4E00-U+9FFF,U+3400-U+4DBF,U+20000-U+2A6DF, U+2A700–U+2B73F,U+2B740–U+2B81F,U+2B820–U+2CEAF,U+F900-U+FAFF,U+2F800-U+2FA1F,U+0370—U+03FF,U+1D300—U+1D35F U+0700—U+074F,U+0F00—U+0FFF,U+1000—U+109F,U+1200—U+137F,U+1100—U+11FF,U+A500—U+A63F;
                    }

                    @font-face {
                    font-family: 'Montserrat';
                    src: url("Templates/imagespg_50702/Montserrat-Medium.woff2") format("woff2"), url("Templates/imagespg_50702/Montserrat-Medium.woff") format("woff");
                    font-weight: 500;
                    font-display: swap;
                    unicode-range: U+0600-U+06FF,U+1D400—U+1D7FF,U+0590—U+05FF,U+4E00-U+9FFF,U+3400-U+4DBF,U+20000-U+2A6DF, U+2A700–U+2B73F,U+2B740–U+2B81F,U+2B820–U+2CEAF,U+F900-U+FAFF,U+2F800-U+2FA1F,U+0370—U+03FF,U+1D300—U+1D35F U+0700—U+074F,U+0F00—U+0FFF,U+1000—U+109F,U+1200—U+137F,U+1100—U+11FF,U+A500—U+A63F;
                    }

                    * {
                    -webkit-box-sizing: border-box;
                    box-sizing: border-box;
                    }

                    body {
                    margin: 0;
                    padding: 0;
                    color: #333333;
                    font-family: "Montserrat", Helvetica, Arial, sans-serif !important;
                    font-size: 14px;
                    font-style: normal;
                    font-weight: 400;
                    line-height: 20px;
                    }

                    .hidden {
                    display: none !important;
                    }

                    .payssl__divide {
                    display: inline-block;
                    margin-right: 10px;
                    }

                    .payssl__field {
                    padding-left: 24px;
                    padding-right: 24px;
                    margin-bottom: 15px;
                    width: 100%;
                    vertical-align: top;
                    }

                    .payssl__field__label {
                    display: block;
                    margin-bottom: 5px;
                    color: #000000;
                    letter-spacing: 0.02em;
                    font-weight: 600;
                    }

                    .payssl__field input {
                    height: 40px;
                    padding: 10px 20px 10px 15px;
                    border: 1px solid #DDDFE3;
                    border-radius: 2px;
                    background-color: #F7F7F7;
                    color: #495057;
                    outline: none;
                    width: 100%;
                    font-size: 14px;
                    font-family: "Montserrat", Helvetica, Arial, sans-serif !important;
                    }

                    .payssl__field input.error,
                    .payssl__field select.error {
                    border: 1px solid #DF1417;
                    }

                    .payssl__field input:disabled {
                    background-color: #e9ecef;
                    }

                    .payssl__field--select select {
                    height: 40px;
                    padding: 8px 20px 8px 15px;
                    border: 1px solid #C0C3C7;
                    border-radius: 2px;
                    background-color: #fff;
                    color: #495057;
                    outline: none;
                    width: 100%;
                    font-size: 14px;
                    }

                    .payssl__field--expiry {
                    margin-bottom: 0;
                    }

                    .payssl__field--half {
                    display: inline-block;
                    width: auto;
                    margin-right: 10px;
                    vertical-align: top;
                    max-width: 155px;
                    }

                    .payssl__field--half input {
                    width: 130px;
                    }

                    .payssl__field--cvv {
                    border-radius: 0;
                    background-color: rgba(22, 137, 31, 0.2);
                    border: 1px solid #c3e6cb;
                    color: #155724;
                    margin-top: 10px;
                    margin-bottom: 0;
                    }

                    .payssl__field--cvv .payssl__field__title {
                    margin-top: 10px;
                    margin-bottom: 10px;
                    }

                    .payssl__field--cvv .payssl__field__title span {
                    font-weight: 600;
                    vertical-align: top;
                    margin-left: 0;
                    color: #000000;
                    letter-spacing: 0.02em;
                    }

                    .payssl__field--cvv .payssl__field__input input {
                    width: 100px;
                    height: 40px;
                    font-size: 14px;
                    text-align: left;
                    color: #000;
                    padding-left: 15px;
                    }

                    .payssl__field--cvv .payssl__field__submit {
                    margin-top: 20px;
                    margin-bottom: 24px;
                    position: relative;
                    }

                    .payssl__field--cvv .payssl__field__submit input {
                    height: 48px;
                    background-color: #16891F;
                    letter-spacing: 0.02em;
                    cursor: pointer;
                    -webkit-appearance: none;
                    border: none;
                    color: #fff;
                    font-size: 14px;
                    text-align: center;
                    width: 100%;
                    font-weight: 500;
                    }

                    .payssl__field--cvv .payssl__field__submit input:hover {
                    background-color: #16691F;
                    -webkit-transition: .3s ease;
                    -o-transition: .3s ease;
                    transition: .3s ease;
                    }

                    .payssl__field__submit__status {
                    position: absolute;
                    top: -5px;
                    width: 100%;
                    }

                    .payssl__field__submit__status img {
                    display: block;
                    margin: 0 auto;
                    }

                    .payssl__field--cardno__input-wrapper {
                    position: relative;
                    }

                    .payssl__field.payssl__field--select select {
                    -webkit-appearance: none;
                    -moz-appearance: none;
                    text-indent: 1px;
                    -o-text-overflow: '';
                    text-overflow: '';
                    cursor: pointer;
                    }

                    .payssl__field img.is-lock,
                    .payssl__field img.is-chevron {
                    position: absolute;
                    right: 10px;
                    top: 11px;
                    pointer-events: none;
                    }

                    .payssl__field--half.payssl__field--cardmonth {
                    padding-right: 0;
                    }

                    .payssl__field--half.payssl__field--cardyear {
                    padding-left: 0;
                    }

                    .payssl__field select,
                    .payssl__field input {
                    -webkit-transition: all 0.3s ease;
                    -o-transition: all 0.3s ease;
                    transition: all 0.3s ease;
                    outline: none;
                    }

                    .payssl__field select:hover,
                    .payssl__field input:hover {
                    background-color: #F7F9FC;
                    border-color: #000000;
                    }

                    .payssl__field select:focus,
                    .payssl__field input:focus {
                    border: 1px solid #F69F1D;
                    outline: 1px solid #F69F1D;
                    }
                </style>
                <script id="clientEventHandlersJS" language="JavaScript">
                    function SSLForm_onsubmit() {

                    var formHasSomeErrors = false;
                    var checkCC = /\D.+/;

                    document.SSLForm.JS.value = "true";

                    // Remove Error Classes

                    document.SSLForm.CCBrand.className = document.SSLForm.CCBrand.className.replace(/(?:^|\s)error(?!\S)/g, '');
                    document.SSLForm.CCnr.className = document.SSLForm.CCnr.className.replace(/(?:^|\s)error(?!\S)/g, '');
                    document.SSLForm.KKMonth.className = document.SSLForm.KKMonth.className.replace(/(?:^|\s)error(?!\S)/g, '');
                    document.SSLForm.KKYear.className = document.SSLForm.KKYear.className.replace(/(?:^|\s)error(?!\S)/g, '');
                    document.SSLForm.cccvc.className = document.SSLForm.cccvc.className.replace(/(?:^|\s)error(?!\S)/g, '');

                    document.getElementsByName("CCBrand-error-field")[0].style.display = "none";
                    document.getElementsByName("CCnr")[0].className = "";
                    document.getElementsByName("CCnr-error-field")[0].style.display = "none";
                    document.getElementsByName("creditcardholder")[0].className = "";
                    document.getElementsByName("CCHolder-error-field")[0].style.display = "none";

                    document.getElementsByName("KKMonth-error-field")[0].style.display = "none";
                    document.getElementsByName("KKYear-error-field")[0].style.display = "none";
                    document.getElementsByName("cccvc")[0].className = "";
                    document.getElementsByName("cccvc-error-field")[0].style.display = "none";


                    // Remove white space from CCnr: DISTRELEC-7767

                    document.SSLForm.CCnr.value = document.SSLForm.CCnr.value.replace(/ /g, '');

                    if (document.SSLForm.CCBrand.selectedIndex == 0) {
                    document.getElementsByName("CCBrand-error-field")[0].style.display = "block";
                    document.SSLForm.CCBrand.className += " error";

                    formHasSomeErrors = true;
                    }

                    if (document.SSLForm.CCnr.value == "") {
                    document.SSLForm.CCnr.focus();
                    document.SSLForm.CCnr.className += " error";
                    document.getElementsByName("CCnr-error-field")[0].style.display = "block";

                    formHasSomeErrors = true;
                    }

                    if (document.SSLForm.CCnr.value.length &lt; 13) {
                    document.SSLForm.CCnr.focus();
                    document.SSLForm.CCnr.className += " error";
                    document.getElementsByName("CCnr-error-field")[0].style.display = "block";

                    formHasSomeErrors = true;
                    }

                    if (document.SSLForm.CCnr.value.length &gt; 19) {
                    document.SSLForm.CCnr.focus();
                    document.SSLForm.CCnr.className += " error";
                    document.getElementsByName("CCnr-error-field")[0].style.display = "block";

                    formHasSomeErrors = true;
                    }

                    // Credit card number validation

                    if (document.SSLForm.CCBrand.selectedIndex != 0) {

                    if (!isValidCreditCard(document.SSLForm.CCBrand.value, document.SSLForm.CCnr.value)) {
                    document.SSLForm.CCnr.focus();
                    document.SSLForm.CCnr.className += " error";
                    document.getElementsByName("CCnr-error-field")[0].style.display = "block";

                    formHasSomeErrors = true;
                    }

                    }

                    if (document.SSLForm.creditcardholder.value == "") {
                    document.SSLForm.creditcardholder.focus();
                    document.SSLForm.creditcardholder.className += " error";
                    document.getElementsByName("CCHolder-error-field")[0].style.display = "block";

                    formHasSomeErrors = true;
                    }

                    if (document.SSLForm.KKMonth.value.length &lt; 2) {
                    document.getElementsByName("KKMonth-error-field")[0].style.display = "block";
                    document.SSLForm.KKMonth.className += " error";

                    formHasSomeErrors = true;
                    }

                    if (document.SSLForm.KKYear.value.length &lt; 4) {
                    document.getElementsByName("KKYear-error-field")[0].style.display = "block";
                    document.SSLForm.KKYear.className += " error";

                    formHasSomeErrors = true;
                    }

                    if (document.SSLForm.cccvc.value.length &lt; 3) {
                    document.SSLForm.cccvc.focus();
                    document.SSLForm.cccvc.className += " error";
                    document.getElementsByName("cccvc-error-field")[0].style.display = "block";

                    formHasSomeErrors = true;
                    }

                    if (!formHasSomeErrors) {
                    sendHeight();
                    hiddensubmit1();
                    showpayStatus();
                    return true;
                    } else {
                    sendHeight();
                    return false;
                    }

                    }

                    function sendHeight() {
                    var paySslHeight = document.getElementsByClassName("payssl");

                    var message = {
                    dynamicHeight: paySslHeight[0].offsetHeight
                    };

                    window.parent.postMessage(message, '*');
                    }

                    function showpayStatus() {

                    if (document.getElementById) {
                    document.getElementById("payStatus").style.display = "block";
                    }

                    }

                    function firstField() {
                    document.SSLForm.creditcardholder.focus();

                    // JavaScriptCheck
                    document.SSLForm.JS.value = "true";
                    }

                    function openWin() {
                    window.open("", "kpn", "width=500,height=470,resizable,screenX=0,screenY=0");
                    }

                    function openCT() {
                    window.open("", "Impressum", "width=390,height=450,resizable,screenX=0,screenY=0");
                    }

                    function hiddensubmit1() {

                    if (document.getElementById) {
                    document.getElementById("submit1").value = "";
                    }

                    }

                    function isValidCreditCard(type, ccnum) {

                    if (type == "VISA") {
                    // Visa: length 16, prefix 4, dashes optional.
                    var re = /^4\d{3}-?\d{4}-?\d{4}-?\d{4}$/;
                    } else if (type == "MasterCard") {
                    // Mastercard: length 16, prefix 51-55, dashes optional.
                    var re = /^5[1-5]\d{2}-?\d{4}-?\d{4}-?\d{4}$/;
                    } else if (type == "Disc") {
                    // Discover: length 16, prefix 6011, dashes optional.
                    var re = /^6011-?\d{4}-?\d{4}-?\d{4}$/;
                    } else if (type == "AMEX") {
                    // American Express: length 15, prefix 34 or 37.
                    var re = /^3[4,7]\d{13}$/;
                    } else if (type == "Diners") {
                    // Diners: length 14, prefix 30, 36, or 38.
                    var re = /^3[0,6,8]\d{12}$/;
                    }

                    // Fix Saved Card Error: DISTRELEC-11950
                    <xsl:choose>
                        <xsl:when test="/paygate/PCNr != ''">
                            var isAlias = true;
                        </xsl:when>
                        <xsl:otherwise>
                            var isAlias = false;
                        </xsl:otherwise>
                    </xsl:choose>

                    if (isAlias == true) {
                    if (!re.test(ccnum)) {
                    return true;
                    }
                    } else {
                    if (re.test(ccnum)) {
                    return true;
                    }
                    }

                    // Remove all dashes for the checksum checks to eliminate negative numbers
                    ccnum = ccnum.split("-").join("");

                    // Checksum ("Mod 10")
                    // Add even digits in even length strings or odd digits in odd length strings.

                    var checksum = 0;

                    for (var i = (2 - (ccnum.length % 2)); i &lt; ccnum.length + 1; i += 2) {
                    checksum += parseInt(ccnum.charAt(i - 1));
                    }

                    // Analyze odd digits in even length strings or even digits in odd length strings.

                    for (var i = (ccnum.length % 2) + 1; i &lt; ccnum.length; i += 2) {
                    var digit = parseInt(ccnum.charAt(i - 1)) * 2;

                    if (digit &lt; 10) {
                    checksum += digit;
                    } else {
                    checksum += (digit - 9);
                    }
                    }

                    if ((checksum % 10) == 0) {
                    return true;
                    } else {
                    return false;
                    }

                    }
                </script>
                <script type="text/javascript" src="Templates/imagespg_50702/jquery.1.9.1.js"></script>
                <script type="text/javascript" src="Templates/imagespg_50702/jquery-ui-1.10.3.custom.js"></script>
            </head>
            <body>
                <form name="SSLForm" id="SSLForm" action="https://spg.evopayments.eu/pay/payinterim.aspx" method="POST" language="JavaScript" onsubmit="return SSLForm_onsubmit();" autocomplete="off">
                    <input type="hidden" name="MerchantID"><xsl:attribute name="value"><xsl:value-of select="/paygate/merchantID"/></xsl:attribute></input>
                    <input type="hidden" name="Len"><xsl:attribute name="value"><xsl:value-of select="/paygate/len"/></xsl:attribute></input>
                    <input type="hidden" name="Data"><xsl:attribute name="value"><xsl:value-of select="/paygate/data"/></xsl:attribute></input>
                    <input type="hidden" name="Counter"><xsl:attribute name="value"><xsl:value-of select="/paygate/counter" /></xsl:attribute></input>
                    <input type="hidden" name="Template" value="pg_50702"/>
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

                    <div class="payssl" style="display: block; float: left; width: 100%;">
                        <div class="payssl__field payssl__field--select">
                            <label class="payssl__field__label">
                                <xsl:value-of select="/paygate/language/Placeholder/Typ"/>
                            </label>
                            <xsl:if test="/paygate/PCNrBrand != ''">
                                <label class="payssl__field__label">
                                    <xsl:value-of select="/paygate/PCNrBrand" />
                                </label>
                            </xsl:if>
                            <div class="payssl__field--cardno__input-wrapper">
                                <xsl:choose>
                                    <xsl:when test="/paygate/PCNrBrand != ''">
                                        <select class="ccbrand hidden" name="CCBrand" size="1" tabIndex="1">
                                            <option value="placeholder"><xsl:value-of select="/paygate/language/Placeholder/pleaseSelect" /></option>
                                            <xsl:for-each select="paygate/brands/brand">
                                                <option>
                                                    <xsl:if test=".=/paygate/PCNrBrand">
                                                        <xsl:attribute name="selected">selected</xsl:attribute>
                                                    </xsl:if>
                                                    <xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
                                                    <xsl:value-of select="."/>
                                                </option>
                                            </xsl:for-each>
                                        </select>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <select class="ccbrand" name="CCBrand" size="1" tabIndex="1">
                                            <option value="placeholder"><xsl:value-of select="/paygate/language/Placeholder/pleaseSelect" /></option>
                                            <xsl:for-each select="paygate/brands/brand">
                                                <option>
                                                    <xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
                                                    <xsl:value-of select="."/>
                                                </option>
                                            </xsl:for-each>
                                        </select>
                                        <img class="is-chevron" height="20" width="20" src="Templates/imagespg_50702/chevron.svg" title="Chevron down"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </div>
                            <div name="CCBrand-error-field" style="display:none;">
                                <span style="color:#DF1417; font-size: 12px;"><xsl:value-of select="/paygate/language/JSError/ChooseBrand"/></span>
                            </div>
                        </div>
                        <div class="payssl__field payssl__field--cardno">
                            <label class="payssl__field__label">
                                <xsl:value-of select="/paygate/language/Placeholder/CCNr" />
                            </label>
                            <xsl:if test="/paygate/PCNr != ''">
                                <label class="payssl__field__label">
                                    **** **** **** *<xsl:value-of select="substring(/paygate/PCNr,string-length(/paygate/PCNr)-2)"/>
                                </label>
                            </xsl:if>
                            <div class="payssl__field--cardno__input-wrapper">
                                <input type="text" name="CCnr" maxlength="19" size="20">
                                    <xsl:if test="/paygate/PCNr != ''">
                                        <xsl:attribute name="value">
                                            <xsl:value-of select="/paygate/PCNr"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="style">display: none;</xsl:attribute>
                                    </xsl:if>
                                </input>
                                <xsl:if test="/paygate/PCNr = ''">
                                    <img class="is-lock" height="20" width="20" src="Templates/imagespg_50702/lock.svg" title="Lock icon"/>
                                </xsl:if>
                            </div>
                            <div name="CCnr-error-field" style="display:none;">
                                <span style="color:#DF1417; font-size: 12px;"><xsl:value-of select="/paygate/language/JSError/ValidCC"/></span>
                            </div>
                        </div>
                        <div class="payssl__field payssl__field--cardholder">
                            <label class="payssl__field__label">
                                <xsl:value-of select="/paygate/language/Placeholder/creditCardHolder"/>
                            </label>
                            <xsl:if test="/paygate/creditCardHolder != ''">
                                <label class="payssl__field__label">
                                    <xsl:value-of select="/paygate/creditCardHolder"/>
                                </label>
                            </xsl:if>
                            <input type="text" name="creditcardholder" maxLength="50" size="20">
                                <xsl:if test="/paygate/PCNr != ''">
                                    <xsl:attribute name="value"><xsl:value-of select="/paygate/creditCardHolder"/></xsl:attribute>
                                    <xsl:attribute name="style">display: none;</xsl:attribute>
                                </xsl:if>
                            </input>
                            <div name="CCHolder-error-field" style="display:none;">
                                <span style="color:#DF1417; font-size: 12px;"><xsl:value-of select="/paygate/language/JSError/CCHolder"/></span>
                            </div>
                        </div>
                        <div class="payssl__field payssl__field--expiry">
                            <label class="payssl__field__label">
                                <xsl:value-of select="/paygate/language/Label/ExpiryLabel"/>
                            </label>
                        </div>
                        <div class="payssl__field payssl__field--cardmonth payssl__field--half">
                            <xsl:if test="/paygate/PCNrMonth != ''">
                                <label class="payssl__field__label">
                                    <xsl:value-of select="/paygate/PCNrMonth"/>
                                </label>
                            </xsl:if>
                            <input type="text" name="KKMonth" maxLength="2" size="2">
                                <xsl:if test="/paygate/PCNrMonth != ''">
                                    <xsl:attribute name="value"><xsl:value-of select="/paygate/PCNrMonth"/></xsl:attribute>
                                    <xsl:attribute name="style">display: none;</xsl:attribute>
                                </xsl:if>
                                <xsl:attribute name="placeholder"><xsl:value-of select="/paygate/language/Placeholder/Month"/></xsl:attribute>
                            </input>
                            <div name="KKMonth-error-field" style="display:none;">
                                <span style="color:#DF1417; font-size: 12px;"><xsl:value-of select="/paygate/language/JSError/ChooseMonth"/></span>
                            </div>
                        </div>
                        <xsl:if test="/paygate/PCNrMonth != ''">
                            <span class="payssl__divide"> / </span>
                        </xsl:if>
                        <div class="payssl__field payssl__field--cardyear payssl__field--half">
                            <xsl:if test="/paygate/PCNrYear != ''">
                                <label class="payssl__field__label">
                                    <xsl:value-of select="/paygate/PCNrYear"/>
                                </label>
                            </xsl:if>
                            <input type="text" name="KKYear" maxLength="4" size="4">
                                <xsl:if test="/paygate/PCNrYear != ''">
                                    <xsl:attribute name="value"><xsl:value-of select="/paygate/PCNrYear"/></xsl:attribute>
                                    <xsl:attribute name="style">display: none;</xsl:attribute>
                                </xsl:if>
                                <xsl:attribute name="placeholder"><xsl:value-of select="/paygate/language/Placeholder/Year"/></xsl:attribute>
                            </input>
                            <div name="KKYear-error-field" style="display:none;">
                                <span style="color:#DF1417; font-size: 12px;"><xsl:value-of select="/paygate/language/JSError/ChooseYear"/></span>
                            </div>
                        </div>
                        <div class="payssl__field payssl__field--cvv">
                            <div class="payssl__field__title">
                                <span><xsl:value-of select="/paygate/language/Label/CVCLabel"/></span>
                            </div>
                            <div class="payssl__field__input">
                                <input type="password" name="cccvc" maxLength="4">
                                    <xsl:attribute name="placeholder"><xsl:value-of select="/paygate/language/Placeholder/CVC"/></xsl:attribute>
                                </input>
                                <div name="cccvc-error-field" style="display:none;">
                                    <span style="color:#DF1417; font-size: 12px;"><xsl:value-of select="/paygate/language/JSError/FillCVV"/></span>
                                </div>
                                <div class="payssl__field__submit">
                                    <input type="submit" id="submit1" name="submit1" value="{/paygate/language/Label/Submit}" />
                                    <div class="payssl__field__submit__status" id="payStatus" style="display: none;">
                                        <p><img height="30" width="30" src="Templates/imagespg_50702/spinner.gif" /></p>
                                    </div>
                                </div>
                                <xsl:apply-templates select="/paygate/errorcode" />
                            </div>
                        </div>
                    </div>
                </form>

                <script>
                    var paySslHeight = document.getElementsByClassName("payssl");

                    var message = {
                    dynamicHeight: paySslHeight[0].offsetHeight
                    };

                    window.parent.postMessage(message, '*');
                </script>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="errorcode">
        <div id="error" style="color: #0000FF; font-weight:bold; font-family:arial,helvetica,sans-serif; font-size:12px;">
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
    </xsl:template>

</xsl:stylesheet>
