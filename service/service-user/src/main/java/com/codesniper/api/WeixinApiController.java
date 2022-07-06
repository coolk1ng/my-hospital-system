package com.codesniper.api;

import com.alibaba.fastjson.JSONObject;
import com.codesniper.common.helper.JwtHelper;
import com.codesniper.common.result.Result;
import com.codesniper.service.UserInfoService;
import com.codesniper.utils.HttpClientUtils;
import com.codesniper.yygh.model.user.UserInfo;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信登录
 *
 * @author CodeSniper
 * @since 2022/7/5 05:33
 */
@Controller
@RequestMapping("/api/ucenter/wx")
@Api(tags = "微信登录")
public class WeixinApiController {

    private final static Logger LOGGER = LoggerFactory.getLogger(WeixinApiController.class);

    @Autowired
    private UserInfoService userInfoService;

    @Value("${wx.open.app_id}")
    private String appId;

    @Value("${wx.open.app_secret}")
    private String appSecret;

    @Value("${wx.open.redirect_url}")
    private String redirectUrl;

    @Value("${yygh.baseUrl}")
    private String baseUrl;


    @GetMapping("/getLoginParam")
    @ResponseBody
    public Result<Map<String, Object>> genQrConnect() {
        HashMap<String, Object> map = new HashMap<>();
        try {
            map.put("appid", appId);
            map.put("scope", "snsapi_login");
            map.put("redirectUrl", URLEncoder.encode(redirectUrl, "UTF-8"));
            map.put("state", System.currentTimeMillis() + "");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return Result.ok(map);
    }

    @GetMapping("/callback")
    public String callback(String code, String state) {
        // 获取code
        LOGGER.info("code:{}", code);

        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");

        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(), appId, appSecret, code);

        // 使用httpclient请求
        try {
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);
            LOGGER.info("accessToken信息:{}", accessTokenInfo);
            JSONObject jsonObject = JSONObject.parseObject(accessTokenInfo);
            String accessToken = jsonObject.getString("access_token");
            String openId = jsonObject.getString("openid");

            // 根据openid判断是否存在该用户
            UserInfo user = userInfoService.getUserInfoByOpenId(openId);
            if (user == null) {
                // 用openid,access_token请求微信地址,拿到扫描人信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openId);
                String userInfo = HttpClientUtils.get(userInfoUrl);
                LOGGER.info("扫描人信息:{}", userInfo);

                // 解析用户信息
                JSONObject userInfoJson = JSONObject.parseObject(userInfo);
                // 用户昵称
                String nickname = userInfoJson.getString("nickname");
                // 用户头像
                String headmagurl = userInfoJson.getString("headmagurl");

                // 添加数据库
                user = new UserInfo();
                user.setNickName(nickname);
                user.setStatus(1);
                user.setOpenid(openId);
                userInfoService.save(user);
            }

            // 封装返回信息
            HashMap<String, String> map = new HashMap<>();
            String name = user.getName();
            if (StringUtils.isEmpty(name)) {
                name = user.getNickName();
            }
            map.put("name", name);
            if (StringUtils.isEmpty(user.getPhone())) {
                map.put("openid", user.getOpenid());
            } else {
                map.put("openid", "");
            }

            // 生成token
            String token = JwtHelper.createToken(user.getId(), user.getName());
            map.put("token", token);

            return "redirect:" + baseUrl + "/weixin/callback?token=" +
                    map.get("token") + "&openid=" + map.get("openid") + "&name=" +
                    URLEncoder.encode(map.get("name"), "utf-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
