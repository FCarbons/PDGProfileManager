package com.pi.misc.dgprofilemanager;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/profile-manager")
public class ProfileManagerController {

	private static final String ACCESS_TOKEN_SESSION_KEY = "access_token";
    
	@Value("${clientId}")
	private String clientId;
	
	@Value("${clientSecret}")
	private String clientSecret;
	
	@Value("${authorizationEndPoint}")
	private String authorizationEndPoint;
	
	@Value("${tokenEndPoint}")
	private String tokenEndPoint;
	
	@Value("${redirectUri}")
	private String redirectUri;
	
	@Value("${scope}")
	private String scope;

	
	@RequestMapping(path="/view-profile", method = RequestMethod.GET)
    public String viewProfile (@RequestHeader(value="username", required=true) String username, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String accessToken = (String) request.getSession().getAttribute(ACCESS_TOKEN_SESSION_KEY);
		System.out.println ("Access token: " + accessToken);
		if (StringUtils.isEmpty(accessToken)) {
			String redirectUrl = ProtocolHelper.buildAuthorizationUrl(authorizationEndPoint, clientId, redirectUri, scope);
			System.out.println ("Access token null, redirecting to: " + redirectUrl);
			response.sendRedirect(redirectUrl);
		}
		//User user = getUser (accessToken);
		model.addAttribute("name", "ciao");
        return "greeting";
    }
	
	@RequestMapping(path="/oauth-callback",method = RequestMethod.GET)
    public String oauthCallback (@RequestParam(value="code", required=false) String code, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException {
		System.out.println ("Received code: " + code);
		String token = ProtocolHelper.getAccessToken(tokenEndPoint, code, clientId, clientSecret,redirectUri);
		if (StringUtils.isEmpty(token)) {
			 return "redirect:/profile-manager/error";
		}
		request.getSession().setAttribute(ACCESS_TOKEN_SESSION_KEY,token);
        return "redirect:/profile-manager/view-profile";
    }

	private User getUser(String accessToken) {
		// TODO Auto-generated method stub
		return null;
	}

	private void getAccessToken(String username) {
		// TODO Auto-generated method stub
	}
	
	
	

}
