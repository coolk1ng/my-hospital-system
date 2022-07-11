package com.codesniper.api;

import com.codesniper.common.result.Result;
import com.codesniper.common.utils.AuthContextHolder;
import com.codesniper.service.PatientService;
import com.codesniper.yygh.model.user.Patient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 就诊人Controller
 *
 * @author CodeSniper
 * @since 2022/7/8 20:47
 */
@RestController
@RequestMapping("/api/user/patient")
@Api(tags = "就诊人")
public class PatientApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientApiController.class);

    @Autowired
    private PatientService patientService;

    @PostMapping("/auth/getPatientList")
    @ApiOperation("查询就诊人列表")
    public Result<List<Patient>> getPatientList(HttpServletRequest request) {
        return Result.ok(patientService.getPatientList(AuthContextHolder.getCurrentUserId(request)));
    }

    @PostMapping("/auth/savePatient")
    @ApiOperation("添加就诊人")
    public Result<Boolean> savePatient(@RequestBody Patient patient,HttpServletRequest request) {
        Long userId = AuthContextHolder.getCurrentUserId(request);
        patient.setUserId(userId);
        return Result.ok(patientService.save(patient));
    }

    @GetMapping("/auth/getPatientById/{id}")
    @ApiOperation("查询就诊人信息")
    public Result<Patient> getPatientById(@PathVariable Long id) {
        return Result.ok(patientService.getPatientById(id));
    }

    @PostMapping("/auth/updatePatient")
    @ApiOperation("修改就诊人")
    public Result<Boolean> updatePatient(@RequestBody Patient patient) {
        return Result.ok(patientService.updateById(patient));
    }

    @DeleteMapping("/auth/deletePatient/{id}")
    @ApiOperation("删除就诊人")
    public Result<Boolean> deletePatient(@PathVariable Long id) {
        return Result.ok(patientService.removeById(id));
    }
}
