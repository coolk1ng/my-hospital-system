package com.codesniper.controller;


import com.codesniper.common.result.Result;
import com.codesniper.service.DicService;
import com.codesniper.yygh.model.dict.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 字典项Controller
 *
 * @author CodeSniper
 * @since 2022-06-02
 */
@RestController
@RequestMapping("/admin/cmn/dict")
@Api(tags = "字典项")
@CrossOrigin
public class DicController {

    @Autowired
    private DicService dicService;

    @ApiOperation("根据id查询子数据")
    @GetMapping("/getChildrenData/{id}")
    public Result<List<Dict>> getChildrenData(@PathVariable Long id) {
        List<Dict> dictList = dicService.getChildrenData(id);
        return Result.ok(dictList);
    }

    @ApiOperation("导出字典数据")
    @GetMapping("/exportDictData")
    public void exportDicData(HttpServletResponse httpServletResponse) {
        dicService.exportDictData(httpServletResponse);
    }

    @ApiOperation("导入Excel")
    @PostMapping("/importDictData")
    public void importDictData(MultipartFile file) {
        dicService.importDictData(file);
    }
}
