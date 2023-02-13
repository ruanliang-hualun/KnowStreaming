package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.AppDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.AppRelateTopicsDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.app.AppVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.app.AppRelateTopicsVO;
import com.xiaojukeji.kafka.manager.service.biz.ha.HaAppManager;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.AppConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/16
 */
@Api(tags = "RD-APP相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX)
public class RdAppController {
    @Autowired
    private AppService appService;

    @Autowired
    private HaAppManager haAppManager;

    @ApiOperation(value = "App列表", notes = "")
    @RequestMapping(value = "apps", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<AppVO>> getApps() {
        return new Result<>(AppConverter.convert2AppVOList(appService.listAll()));
    }

    @ApiOperation(value = "App修改", notes = "")
    @RequestMapping(value = "apps", method = RequestMethod.PUT)
    @ResponseBody
    public Result modifyApps(@RequestBody AppDTO dto) {
        return Result.buildFrom(
                appService.updateByAppId(dto, SpringTool.getUserName(), true)
        );
    }

    @ApiOperation(value = "App关联Topic信息查询", notes = "")
    @PostMapping(value = "apps/relate-topics")
    @ResponseBody
    public Result<List<AppRelateTopicsVO>> appRelateTopics(@Validated @RequestBody AppRelateTopicsDTO dto) {
        if (dto.getUseKafkaUserAndClientId() != null && dto.getUseKafkaUserAndClientId()) {
            return haAppManager.appAndClientRelateTopics(dto.getClusterPhyId(), new HashSet<>(dto.getFilterTopicNameList()));
        }

        return haAppManager.appRelateTopics(dto.getHa(), dto.getClusterPhyId(), dto.getFilterTopicNameList());
    }
}