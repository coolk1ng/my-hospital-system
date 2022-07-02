package com.codesniper.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.codesniper.service.MessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 短信ServiceImpl
 *
 * @author CodeSniper
 * @since 2022/7/3 01:30
 */
@Service("MessageService")
public class MessageServiceImpl implements MessageService {

    @Override
    public Boolean sendMessage(String phone, String code, String accessKeyId, String accessKeySecret) throws Exception {
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
}
