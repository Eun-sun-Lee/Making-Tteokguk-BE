package com.ricecakesoup.controller.OAuthService;

import com.ricecakesoup.service.OAuthService.OAuthService;
import com.ricecakesoup.service.refrigerator.RefrigeratorService;
import com.ricecakesoup.service.refrigerator.dto.response.RefrigeratorResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.HashMap;


@RestController
@RequiredArgsConstructor
public class HomeController {

    private final OAuthService oAuthService;
    private final RefrigeratorService refrigeratorService;

    @RequestMapping(value="/")
    public String index() {
        return "index";
    }

    @RequestMapping(value="/user/kakao/callback")
    public RefrigeratorResDto login(@RequestParam("code") String code, HttpSession session) {
        String access_Token = oAuthService.getKakaoAccessToken(code);
        HashMap<String, Object> userInfo = oAuthService.getUserInfo(access_Token);
        System.out.println("login Controller : " + userInfo);
        String response_body="";
        String kakaoId = "";

        //    클라이언트의 이메일이 존재할 때 세션에 해당 이메일과 토큰 등록
        session.setAttribute("userId", userInfo.get("email"));
        session.setAttribute("access_Token", access_Token);
        response_body = String.valueOf(userInfo.get("response_body"));

        String target = "\"id\"";
        int target_num = response_body.indexOf(target);
        kakaoId = response_body.substring(6,(response_body.substring(target_num).indexOf(",")+1));
        System.out.println("kakaoid: " + kakaoId);

        try {
            return refrigeratorService.getFridgeById(kakaoId);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
    }
}

