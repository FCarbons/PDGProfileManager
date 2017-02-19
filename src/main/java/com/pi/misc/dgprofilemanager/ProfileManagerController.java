package com.pi.misc.dgprofilemanager;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/profile-manager")
public class ProfileManagerController {

	private static final String ACCESS_TOKEN_SESSION_KEY = "access_token";
	private static final String USER_JSONOBJECT_SESSION_KEY = "user_json_object";
	
	@Value("${clientId}")
	private String clientId;

	@Value("${clientSecret}")
	private String clientSecret;

	@Value("${authorizationEndPoint}")
	private String authorizationEndPoint;

	@Value("${tokenEndPoint}")
	private String tokenEndPoint;

	@Value("${userEndPoint}")
	private String userEndPoint;

	@Value("${redirectUri}")
	private String redirectUri;

	@Value("${scope}")
	private String scope;

	@RequestMapping(path = "/profile", method = RequestMethod.GET)
	public String viewProfile(@RequestHeader(value = "username", required = true) String username, Model model, HttpServletRequest request,
			HttpServletResponse response) throws IOException, JSONException {
		String accessToken = (String) request.getSession().getAttribute(ACCESS_TOKEN_SESSION_KEY);
		System.out.println("Access token: " + accessToken);
		if (StringUtils.isEmpty(accessToken)) {
			String redirectUrl = ProtocolHelper.buildAuthorizationUrl(authorizationEndPoint, clientId, redirectUri, scope);
			System.out.println("Access token null, redirecting to: " + redirectUrl);
			return "redirect:" + redirectUrl;
		}
		JSONObject userObject = ProtocolHelper.getUser(userEndPoint, accessToken);
		request.getSession().setAttribute(USER_JSONOBJECT_SESSION_KEY, userObject);
		UserName userNameObject = new UserName (userObject);
		model.addAttribute("name", userNameObject);
		return "greeting";
	}
	
	@RequestMapping(path = "/profile", method = RequestMethod.POST)
	public String modifyProfile(@RequestHeader(value = "username", required = true) String username, Model model, @ModelAttribute UserName userName,HttpServletRequest request,
			HttpServletResponse response) throws IOException, JSONException {
		String accessToken = (String) request.getSession().getAttribute(ACCESS_TOKEN_SESSION_KEY);
		System.out.println("Access token: " + accessToken);
		if (StringUtils.isEmpty(accessToken)) {
			String redirectUrl = ProtocolHelper.buildAuthorizationUrl(authorizationEndPoint, clientId, redirectUri, scope);
			System.out.println("Access token null, redirecting to: " + redirectUrl);
			return "redirect:" + redirectUrl;
		}
		
		JSONObject userObject = (JSONObject) request.getSession().getAttribute(USER_JSONOBJECT_SESSION_KEY); 
		userObject.put("name", userName.returnJsonObject());
		ProtocolHelper.modifyUser(userEndPoint, accessToken, userObject);
		model.addAttribute("name", userName);
		return "greeting";
	}

	@RequestMapping(path = "/oauth-callback", method = RequestMethod.GET)
	public String oauthCallback(@RequestParam(value = "code", required = false) String code, Model model, HttpServletRequest request,
			HttpServletResponse response) throws IOException, JSONException {
		System.out.println("Received code: " + code);
		String token = ProtocolHelper.getAccessToken(tokenEndPoint, code, clientId, clientSecret, redirectUri);
		if (StringUtils.isEmpty(token)) {
			return "redirect:/profile-manager/error";
		}
		request.getSession().setAttribute(ACCESS_TOKEN_SESSION_KEY, token);
		return "redirect:/profile-manager/view-profile";
	}

	private void getAccessToken(String username) {
		// TODO Auto-generated method stub
	}

}
