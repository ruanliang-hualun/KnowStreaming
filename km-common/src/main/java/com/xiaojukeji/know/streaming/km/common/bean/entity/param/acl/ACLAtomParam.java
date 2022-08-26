package com.xiaojukeji.know.streaming.km.common.bean.entity.param.acl;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.kafkauser.KafkaUserParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourceType;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ACLAtomParam extends KafkaUserParam {
    private AclOperation aclOperation;

    private AclPermissionType aclPermissionType;

    private String aclClientHost;

    private ResourceType resourceType;

    private String resourceName;

    private PatternType resourcePatternType;

    public Result<Void> checkFieldLegal() {
        StringBuilder sb = new StringBuilder();
        if (ValidateUtils.isNullOrLessThanZero(clusterPhyId)) {
            sb.append("clusterPhyId不允许为空或小于0;").append("\t");
        }
        if (ValidateUtils.isBlank(kafkaUserName)) {
            sb.append("kafkaUserName不允许为空;").append("\t");
        }
        if (ValidateUtils.isNull(aclOperation)) {
            sb.append("aclOperation不允许为null;").append("\t");
        }
        if (ValidateUtils.isNull(aclPermissionType)) {
            sb.append("aclPermissionType不允许为null;").append("\t");
        }
        if (ValidateUtils.isBlank(aclClientHost)) {
            sb.append("aclClientHost不允许为空;").append("\t");
        }
        if (ValidateUtils.isNull(resourceType)) {
            sb.append("resourceType不允许为null;").append("\t");
        }
        if (ValidateUtils.isBlank(resourceName)) {
            sb.append("resourceName不允许为空;").append("\t");
        }
        if (ValidateUtils.isNull(resourcePatternType)) {
            sb.append("resourcePatternType不允许为null;").append("\t");
        }

        if (sb.length() == 0) {
            return Result.buildSuc();
        }

        return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, sb.toString());
    }
}
