<%@ page import = "java.io.*,java.util.*,org.customauth.*" %>
<%@ page import = "org.customauth.restutils.ForgeRockAPIService" %>



<html>
   <head>
       <title>Texas.gov Digital Identity Solution</title>
    <script charset="utf-8" src="./js/ThemeConfiguration.e26135a315.js"></script><link rel="icon" type="image/x-icon" href="https://dev.myaccess.texas.gov/sso/XUI/themes/mytheme//images/TDISicon.png"><link rel="shortcut icon" type="image/x-icon" href="https://dev.myaccess.texas.gov/sso/XUI/themes/mytheme//images/TDISicon.png"><link rel="stylesheet" type="text/css" href="./js/bootstrap.min.css"><link rel="stylesheet" type="text/css" href="./js/structure.css"><link rel="stylesheet" type="text/css" href="./js/mytheme.css"><script charset="utf-8" src="./js/439.a9de7a3567.js"></script><script charset="utf-8" src="./js/441.e4142957cb.js"></script><script charset="utf-8" src="./js/440.5db1c750a8.js"></script><script charset="utf-8" src="./js/438.6cf419eeba.js"></script><script charset="utf-8" src="./js/435.a28f145740.js"></script><script charset="utf-8" src="./js/434.579e82b80b.js"></script><script charset="utf-8" src="./js/433.5c9a9a9258.js"></script><script charset="utf-8" src="./js/432.d41e476a47.js"></script><script charset="utf-8" src="./js/431.25f9e7f0cf.js"></script><script charset="utf-8" src="./js/430.f5978c8a29.js"></script><script charset="utf-8" src="./js/429.bf6d54a957.js"></script><script charset="utf-8" src="./js/427.58568a0e0c.js"></script><script charset="utf-8" src="./js/426.da08d450a0.js"></script><script charset="utf-8" src="./js/425.7e1635884f.js"></script><script charset="utf-8" src="./js/424.79187503d4.js"></script><script charset="utf-8" src="./js/423.f0a290fbeb.js"></script><script charset="utf-8" src="./js/422.4ce5d541dc.js"></script><script charset="utf-8" src="./js/421.ee5ed4e326.js"></script><script charset="utf-8" src="./js/428.f22df5a1cc.js"></script><script charset="utf-8" src="./js/405.4a2556faa2.js"></script><script charset="utf-8" src="./js/403.dfc053f3b1.js"></script><script charset="utf-8" src="./js/404.7938360ab5.js"></script><script charset="utf-8" src="./js/386.53c10a92f3.js"></script><script charset="utf-8" src="./js/420.0db05a13a8.js"></script><script charset="utf-8" src="./js/419.d371155cfd.js"></script><script charset="utf-8" src="./js/401.4aa1e3f0eb.js"></script></head>
	  
	  <SCRIPT LANGUAGE="JavaScript">


function submitForm()
{
	/*if(document.stepup_form1!=undefined){
	    //alert('Form1-1234');
		<% System.out.println("Form 1 called......");%>
		var SMAGENTNAME=document.getElementById("smagentname").value;
		var TARGET = document.getElementById("target").value;
		SMAGENTNAME=encodeURIComponent(SMAGENTNAME);
		var smauthreason = document.getElementById("smauthreason").value;
		//alert(SMAGENTNAME);
		var url = "/testapp/stepup.jsp?SMAGENTNAME="+SMAGENTNAME+"&TARGET="+TARGET+"&SMAUTHREASON="+smauthreason;
		document.stepup_form1.action=url;
		document.stepup_form1.submit();
		return;
	}*/
	if(document.stepup_form2!=undefined){
		var TARGET = document.getElementById("target").value;
		//alert('Auto Submission...');
		var SMAGENTNAME=document.getElementById("smagentname").value;
		SMAGENTNAME=encodeURIComponent(SMAGENTNAME);
		
		//var TARGET = "http://win-ee92ljm8tqb.yasas.com:9580/sample/firstpage.html";
		
		TARGET=encodeURIComponent(TARGET);
		
		var REQUEST=document.getElementById("payloaddata").value;
		REQUEST=encodeURIComponent(REQUEST);
		
		var choice = '<%=request.getParameter("choice")%>';
		
		var smauthreason = '<%=request.getParameter("SMAUTHREASON")%>';
		
		var user = '<%=request.getHeader("sm_user")%>';
		
		//document.stepup_form2.action="http://win-ee92ljm8tqb.yasas.com:9580/siteminderagent/forms/otp.fcc?TYPE=33554432&GUID=&SMAUTHREASON=0&METHOD=GET&SMAGENTNAME=-SM-5wFtlb4fz9Yh%2bRVCgZKbwR4EqL%2fuSa7mGh9uLRqS2MtFTmcVrwdKy6BYIfj0wRyP&TARGET=http://win-ee92ljm8tqb.yasas.com:9580/sample/firstpage.html";
		
		//var url = "http://win-ee92ljm8tqb.yasas.com:9580/siteminderagent/forms/otp.fcc?SMAGENTNAME=-SM-5wFtlb4fz9Yh+RVCgZKbwR4EqL/uSa7mGh9uLRqS2MtFTmcVrwdKy6BYIfj0wRyP&TARGET="+TARGET;
		//document.stepup_form2.action=encodeURI(url);
		
		var url = "http://win-ee92ljm8tqb.yasas.com:9580/siteminderagent/forms/otp.fcc?SMAGENTNAME="+SMAGENTNAME+"&TARGET="+TARGET+"&REQUESTOTP="+REQUEST+"&CHOICE="+choice+"&SMAUTHREASON="+smauthreason+"&USER="+user;
		document.stepup_form2.action=url;
		document.stepup_form2.submit();
		return;
	}
}

</SCRIPT>
   </head>
   
   <%
	String choice = request.getParameter("choice");
						
	//out.println("Choice: " + choice);
	
	if(choice==null){
   %>
		<body >
   <%
	}else {
	%>
		<body onload="setTimeout(function() { submitForm(); }, 1000)">
	<%
	}
	%>
       <div id="messages" class="clearfix"></div>
        <div id="wrapper" aria-busy="false"><div id="login-base">
  <div class="txheader">
    <p id="bannertext"><img class="flagimage" src="./images/texasflag.png" alt="texas flag in a small circle"></p>
</div>
  <div class="main-logo-holder">
    <img class="main-logo" src="./images/TDISwhite.png" alt="txt digital identity solution logo">
  </div>
  
      
  
    
    <div id="content">
<div class="container">
		
         
            <%
              /* Enumeration headerNames = request.getHeaderNames();
               while(headerNames.hasMoreElements()) {
                  String paramName = (String)headerNames.nextElement();
                  out.print("<tr><td>" + paramName + "</td>\n");
                  String paramValue = request.getHeader(paramName);
                  out.println("<td> " + paramValue + "</td></tr>\n");
               }*/
			   
			  String smUser =  request.getHeader("sm_user");
			  try {
				ForgeRockAPIService.init("C:\\siteminder\\config\\forgerock_custom.properties");
			  }catch(Exception e){
				e.printStackTrace();
			  }
			  
			  String jsonPayloadStep1 = null;
			  String jsonPayloadStep2 = null;
			  
			  if(smUser != null) {
			  %>
								
			  <%
			  
						if(choice == null) {
			%>
							
						<form name="stepup_form1" method="post" class="form login col-sm-6 col-sm-offset-3" autocomplete="off" data-stage="">
						
						<fieldset class="row">

                <div class="form-group">
				<div id="callback_0"><font color="green" size="4"><b><%=smUser%></b></font></div>

                </div>
				
				<%
							jsonPayloadStep1 = ForgeRockAPIService.step1(smUser);
							
							int result = ForgeRockAPIService.isMFASuccessfull(jsonPayloadStep1);
							
							//jsonPayloadStep1 = "You do not have access";
							if(result==2){
							%>
							<div class="form-group">
							<label for="callback_1" id="label_1">Please verify for security.</label>
							<label for="callback_1" id="label_callback_1"><%= ForgeRockAPIService.getProperty("account.locked.message") %></label>
								
								</div>
							<%
								return;
							} else if(result==3){
							%>
							<div class="form-group">
							<label for="callback_1" id="label_1">Please verify for security.</label>
							<label for="callback_1" id="label_callback_1"><%= ForgeRockAPIService.getProperty("account.notexists.message") %></label>
								
								</div>
							<%
								return;
							} else if(jsonPayloadStep1==null || jsonPayloadStep1.startsWith("Error") ){
									String genericMsgFromConfig = ForgeRockAPIService.getProperty("generic.error.message");
							%>
							<div class="form-group">
							<label for="callback_1" id="label_1">Please verify for security.</label>
							<label for="callback_1" id="label_callback_1"><%=(genericMsgFromConfig!=null?genericMsgFromConfig:jsonPayloadStep1) %></label>
								
								</div>
							<%
								return;
							}
							
							session.setAttribute("jsonPayloadStep1",jsonPayloadStep1);
							
							System.out.println("JSon : " + jsonPayloadStep1);
			  
						List<String> choices = ForgeRockAPIService.getChoicesFromJsonPayload(jsonPayloadStep1);
			  
						Iterator<String> iterator = choices.iterator();
			%>

                <div class="form-group">
				<label for="callback_1" id="label_1">Please verify for security.</label>
<label for="callback_1" id="title_1">Save this device.</label>
<label for="callback_1" id="label_callback_1">Where would you like your one-time passcode sent?</label>
<label for="callback_1" id="label_device_1">Would you like to save this device to your profile?</label>


    
    <div class="radiochoice" data-toggle="radio" role="radiogroup" aria-labelledby="label_callback_1">
														
							<input type=hidden id=target value="<%=request.getParameter("TARGET")%>">
							<input type=hidden id=smagentname value="<%=request.getParameter("SMAGENTNAME")%>">
							<input type=hidden id=smauthreason value="<%=request.getParameter("SMAUTHREASON")%>">
			
								
			<%
					
							while(iterator.hasNext()){
							String eachChoice = iterator.next();
			%>
								<!--<tr>
									<td><input type="radio" name="choice" value="<%=eachChoice%>"/>&nbsp;&nbsp;<%=eachChoice%></td>
								</tr>-->
								<div>
									<label class="radiolabel">
										
										<input type="radio" name="choice" id="choice" autocomplete="off" checked="" value="<%=eachChoice%>">
											
										<%=eachChoice%>
										
									</label>
								</div>
			<%
							}//while loop ends here
			%>
			</div>
			<div class="form-group">
				<input id="loginButton_0" name="callback_2" type="submit" role="button" index="0" value="Select MFA Method" class="btn btn-lg btn-block btn-uppercase btn-primary">
			</div>
			<%
						}// choices if loop ends here
						
						else {
						
							jsonPayloadStep1 = (String)session.getAttribute("jsonPayloadStep1");
							//out.println(jsonPayloadStep1);
							session.removeAttribute("jsonPayloadStep1");
							
							
							if(jsonPayloadStep1 != null){
								jsonPayloadStep2 = ForgeRockAPIService.step2(smUser,jsonPayloadStep1,choice);
								
						
						System.out.println("Base64 Encoded..");			
						System.out.println(jsonPayloadStep2);						
			%>
				<form name="stepup_form2" action="" method="post" >
						<!--	Auto redirecting....-->
							<input type=hidden id=target value="<%=request.getParameter("TARGET")%>">
							<input type=hidden id=smagentname value="<%=request.getParameter("SMAGENTNAME")%>">
							
							<input type=hidden id=payloaddata value="<%=jsonPayloadStep2%>">
								<!--<tr>
									<td>OTP&nbsp;&nbsp;<input type="text" name="OTP" value=""></td>
								</tr>-->
			<%
							}else {
						
			%>
								<tr>
									<td><font color="red">Step1 failed</font></td>
								</tr>
			<%
							}//if else loop
						}
			  }// sm user if loop ends here
		   %>
		   </fieldset>
		 </form>
     </div>
</div>
</div>

 <footer id="footer" class="footer text-muted"><div class="container">
    <div id="footer">
        
       <p><img class="txfooterimage" src="./images/TDISicon.png" alt="Small circle with texas outline">  Copyright © 2020 . All rights reserved.</p>
            
       
    </div>
</div>
</footer>
   </body>
</html>