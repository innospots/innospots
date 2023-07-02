

脚本节点测试流程：
1. 设置输入参数 调用如下接口 workflowInstanceId=5   force=true
/innospot/admin/flow/{workflowInstanceId}/node-instance/pre-data/{force}
内容：
{
  "preNodeKey": {"i1":"hello","i2":" world!"}
}

2. 测试脚本节点  workflowInstanceId=5   prevNodeKeys=preNodeKey
/innospot/admin/flow/{workflowInstanceId}/node-instance/execute/{prevNodeKeys}
内容：输入脚本内容

mysql脚本测试流程：
执行执行下面接口，workflowInstanceId=5 prevNodeKeys=preNodeKey
/innospot/admin/flow/{workflowInstanceId}/node-instance/execute/{prevNodeKeys}
内容: 输入 mysql脚本内容