package com.codesniper.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.codesniper.service.MessageService;
import com.codesniper.yygh.vo.msm.MsmVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 短信ServiceImpl
 *
 * @author CodeSniper
 * @since 2022/7/3 01:30
 */
@Service("MessageService")
public class MessageServiceImpl implements MessageService {

    @Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.sms.accessKeySecret}")
    private String accessKeySecret;

    @Override
    public Boolean sendMessage(String phone, String code) throws Exception {
        // 校验手机号是否为空
        if (StringUtils.isEmpty(phone)) {
            return false;
        }

        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        // 得到client
        Client client = new Client(config);

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName("阿里云短信测试")
                .setTemplateCode("SMS_154950909")
                .setPhoneNumbers(phone)
                .setTemplateParam("{\"code\":\"" + code+"\"}");

        RuntimeOptions runtime = new RuntimeOptions();
        SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, runtime);
        return sendSmsResponse != null;
    }

    @Override
    public Boolean sendMessage(MsmVo msmVo) throws Exception {
        if (!StringUtils.isEmpty(msmVo.getPhone())) {
            String code = (String) msmVo.getParam().get("code");
            return this.sendMessage(msmVo.getPhone(), code);
        }
        return false;
    }
}
