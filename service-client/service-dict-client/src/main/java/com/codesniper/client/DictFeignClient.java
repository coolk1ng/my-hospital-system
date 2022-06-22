package com.codesniper.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 字典接口feign
 *
 * @author CodeSniper
 * @since 2022/6/23 00:07
 */
@Repository
@FeignClient("service-dic-man")
public interface DictFeignClient {

    @PostMapping("/admin/cmn/dict/getDictName")
    String getDictName(@RequestParam("dictCode") String dictCode, @RequestParam("value") String value);

    @PostMapping("/admin/cmn/dict/getDictNameByValue")
    String getDictNameByValue(@RequestParam("value") String value);
}
