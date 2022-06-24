package com.codesniper.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.codesniper.yygh.model.dict.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 字典项Service
 *
 * @author CodeSniper
 * @since 2022-06-02
 */
public interface DicService extends IService<Dict> {

    /**
     * 根据id查询子数据
     * @param id
     * @return List<Dict>
     */
    List<Dict> getChildrenData(Long id);

    /** 
     * 导出excel
     * @param httpServletResponse 
     * @return void
     */
    void exportDictData(HttpServletResponse httpServletResponse);

    /** 
     * 导入excel
     * @param file 
     * @return void
     */
    void importDictData(MultipartFile file);

    /** 
     * 根据dicCode,value查询
     * @param dictCode
     * @param value 
     * @return String
     */
    String getDictName(String dictCode, String value);

    /** 
     * 根据dictCode获取子节点数据
     * @param dictCode 
     * @return List<Dict>
     */
    List<Dict> getByDictCode(String dictCode);
}
