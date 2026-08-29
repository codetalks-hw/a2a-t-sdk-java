# Authorization-T Demo 冒烟测试原始输入输出记录

- 位置：`eval-results\authz-demo\authz-report-20260828-193444.json`；题集：`sample/authz-policy/scenarios.json`；LLM：蓝区统一网关（key 不落档）；参数：`-Dauthz.reasoning=true`；结果：**15/15 match**
- 分区：预期成功在前（index 0-7）、预期拒绝在中（index 8-12）、客户端拦截在末尾（index 13-14）
- 每例两段：客户端（输入→生成结果→理由）与服务端（schema 输入→校验提参结果→理由）；服务端段不重复渲染后 prompt

---

## c1-nl-add-01  —  ✅ match

### 客户端（生成）

**原始输入**（from_text）

> 帮我新增一个授权策略，业务场景是校园专网，处置类型是业务抢通，操作名称是隧道调优，有效期从2026-01-01到2030-12-31

**生成结果：成功（渲染后 prompt）**

```
## 授权策略的操作类型
新增授权策略

## 授权策略的操作描述
请根据<授权策略的操作类型>和<动网操作的授权策略列表>完成相应的授权操作，按照<预期输出>中定义的结构返回授权策略的操作执行结果。<预期输出>表示预期返回内容。

## 动网操作的授权策略列表
1. 业务场景是校园专网，处置类型是业务抢通，操作名称是隧道调优，有效期是2026-01-01~2030-12-31

## 预期输出
1. 授权操作执行结果，取值范围： 成功、失败、部分成功；
2. 授权操作执行成功时，返回执行成功的<动网操作的授权策略列表>；
3. 授权操作执行失败或部分成功时，返回失败列表，包含授权策略和失败原因；
```

**提参理由（LLM reasoning）**：输入中出现“新增”这一合法取值的简写，按规则13a映射为“新增授权策略”。策略列表为新增形，输入提供了业务场景、处置类型、操作名称、有效期四个字段，日期“2026-01-01到2030-12-31”按约束归一为“2026-01-01~2030-12-31”，并按编号1及全角逗号组织为列表条目。

### 服务端（校验 + 提参）

**validateAndDataFilling 输入 schema（suite 默认 param-schema.json）**：

```json
{
  "type": "object",
  "properties": {
    "operationType": {
      "type": "string",
      "enum": [
        "新增授权策略",
        "修改授权策略",
        "删除授权策略",
        "查询授权策略"
      ],
      "description": "授权策略的操作类型"
    },
    "policyList": {
      "type": "array",
      "description": "动网操作的授权策略列表，按输入中的出现顺序排列；未提供时为空列表",
      "items": {
        "type": "object",
        "properties": {
          "policyId": {
            "type": "string",
            "description": "策略的唯一标识"
          },
          "scene": {
            "type": "string",
            "description": "动网操作支撑的业务场景"
          },
          "actionType": {
            "type": "string",
            "description": "动网操作的处置类型"
          },
          "operationName": {
            "type": "string",
            "description": "所授权的动网操作的具体名称"
          },
          "validityPeriod": {
            "type": "string",
            "description": "授权策略的生效时间区间"
          }
        }
      }
    }
  }
}
```

（输入 prompt 即上方客户端渲染结果，此处不重复。）

**校验与提参结果**：`success`

**提参结果**：

```json
{
  "operationType": "新增授权策略",
  "policyList": [
    {
      "policyId": null,
      "scene": "校园专网",
      "actionType": "业务抢通",
      "operationName": "隧道调优",
      "validityPeriod": "2026-01-01~2030-12-31"
    }
  ]
}
```

**校验理由（LLM reasoning）**：操作类型为新增授权策略，属于 schema 允许的枚举值。动网操作的授权策略列表包含1条策略；新增授权策略的模板必填字段为业务场景、处置类型、操作名称、有效期，该条目均已提供。有效期起止日期有效且未倒置，日期区间形态未被约束明确排除，不构成格式违规。policyId 在新增场景下非必填，缺失不报错。因此 semantic_verdict 为 true，errors 为空。

**键名泄漏扫描**：无泄漏

**判定**：match=True（client_prompt=True）

---

## c1-nl-add-01-varname  —  ✅ match
[【差分对】配对基线：c1-nl-add-01]

### 客户端（生成）

**原始输入**（from_text）

> 帮我新增一个授权策略，业务场景是校园专网，处置类型是业务抢通，操作名称是隧道调优，有效期从2026-01-01到2030-12-31

**生成结果：成功（渲染后 prompt）**

```
## 授权策略的操作类型
新增授权策略

## 授权策略的操作描述
请根据<授权策略的操作类型>和<动网操作的授权策略列表>完成相应的授权操作，按照<预期输出>中定义的结构返回授权策略的操作执行结果。<预期输出>表示预期返回内容。

## 动网操作的授权策略列表
1. 业务场景是校园专网，处置类型是业务抢通，操作名称是隧道调优，有效期是2026-01-01~2030-12-31

## 预期输出
1. 授权操作执行结果，取值范围： 成功、失败、部分成功；
2. 授权操作执行成功时，返回执行成功的<动网操作的授权策略列表>；
3. 授权操作执行失败或部分成功时，返回失败列表，包含授权策略和失败原因；
```

**提参理由（LLM reasoning）**：输入中出现“新增”且处于新增授权策略的指令语境，按封闭取值范围规则13a映射为“新增授权策略”。策略列表按新增形字段顺序提取：业务场景、处置类型、操作名称、有效期，并将日期区间归一化为YYYY-MM-DD~YYYY-MM-DD格式；操作类型为新增，列表已提供，因此不报告missing_input。

### 服务端（校验 + 提参）

**validateAndDataFilling 输入 schema（本用例变异 schema）**：

```json
{
  "type": "object",
  "properties": {
    "operationType": {
      "type": "string",
      "enum": [
        "新增授权策略",
        "修改授权策略",
        "删除授权策略",
        "查询授权策略"
      ],
      "description": "授权策略的操作类型"
    },
    "policies": {
      "type": "array",
      "description": "动网操作的授权策略列表，按输入中的出现顺序排列；未提供时为空列表",
      "items": {
        "type": "object",
        "properties": {
          "pid": {
            "type": "string",
            "description": "策略的唯一标识"
          },
          "domain": {
            "type": "string",
            "description": "动网操作支撑的业务场景"
          },
          "handling": {
            "type": "string",
            "description": "动网操作的处置类型"
          },
          "opName": {
            "type": "string",
            "description": "所授权的动网操作的具体名称"
          },
          "effectivePeriod": {
            "type": "string",
            "description": "授权策略的生效时间区间"
          }
        }
      }
    }
  }
}
```

（输入 prompt 即上方客户端渲染结果，此处不重复。）

**校验与提参结果**：`success`

**提参结果**：

```json
{
  "operationType": "新增授权策略",
  "policies": [
    {
      "pid": null,
      "domain": "校园专网",
      "handling": "业务抢通",
      "opName": "隧道调优",
      "effectivePeriod": "2026-01-01~2030-12-31"
    }
  ]
}
```

**校验理由（LLM reasoning）**：操作类型为新增授权策略，取值在 schema 枚举范围内。新增操作按模板正文要求必填业务场景、处置类型、操作名称、有效期，输入列表条目均完整提供；未提供策略标识、创建时间、最后修改时间，但新增操作不要求这些字段，不构成缺失。有效期格式为日期区间且起始早于结束，未被约束明确禁止，格式变体通过。参数提取按 schema 结构完成，pid 未提供故为 null。

**键名泄漏扫描**：无泄漏

**判定**：match=True（client_prompt=True）

---

## c2-nl-multi-01  —  ✅ match

### 客户端（生成）

**原始输入**（from_text）

> 新增两条授权策略：第一条，业务场景是校园专网，处置类型是业务抢通，操作名称是隧道调优，有效期从2026-01-01到2030-12-31；第二条，业务场景是医疗专线，处置类型是业务恢复，操作名称是频段调整，有效期从2026-06-01到2030-06-18

**生成结果：成功（渲染后 prompt）**

```
## 授权策略的操作类型
新增授权策略

## 授权策略的操作描述
请根据<授权策略的操作类型>和<动网操作的授权策略列表>完成相应的授权操作，按照<预期输出>中定义的结构返回授权策略的操作执行结果。<预期输出>表示预期返回内容。

## 动网操作的授权策略列表
1. 业务场景是校园专网，处置类型是业务抢通，操作名称是隧道调优，有效期是2026-01-01~2030-12-31
2. 业务场景是医疗专线，处置类型是业务恢复，操作名称是频段调整，有效期是2026-06-01~2030-06-18

## 预期输出
1. 授权操作执行结果，取值范围： 成功、失败、部分成功；
2. 授权操作执行成功时，返回执行成功的<动网操作的授权策略列表>；
3. 授权操作执行失败或部分成功时，返回失败列表，包含授权策略和失败原因；
```

**提参理由（LLM reasoning）**：输入中出现“新增”且处于指令语境，按封闭取值范围规则映射为合法值“新增授权策略”。策略列表提取两条编号条目，并将“有效期从...到...”归一为“有效期是YYYY-MM-DD~YYYY-MM-DD”格式，字段顺序和值均保留。

### 服务端（校验 + 提参）

**validateAndDataFilling 输入 schema（suite 默认 param-schema.json）**：

```json
{
  "type": "object",
  "properties": {
    "operationType": {
      "type": "string",
      "enum": [
        "新增授权策略",
        "修改授权策略",
        "删除授权策略",
        "查询授权策略"
      ],
      "description": "授权策略的操作类型"
    },
    "policyList": {
      "type": "array",
      "description": "动网操作的授权策略列表，按输入中的出现顺序排列；未提供时为空列表",
      "items": {
        "type": "object",
        "properties": {
          "policyId": {
            "type": "string",
            "description": "策略的唯一标识"
          },
          "scene": {
            "type": "string",
            "description": "动网操作支撑的业务场景"
          },
          "actionType": {
            "type": "string",
            "description": "动网操作的处置类型"
          },
          "operationName": {
            "type": "string",
            "description": "所授权的动网操作的具体名称"
          },
          "validityPeriod": {
            "type": "string",
            "description": "授权策略的生效时间区间"
          }
        }
      }
    }
  }
}
```

（输入 prompt 即上方客户端渲染结果，此处不重复。）

**校验与提参结果**：`success`

**提参结果**：

```json
{
  "operationType": "新增授权策略",
  "policyList": [
    {
      "policyId": null,
      "scene": "校园专网",
      "actionType": "业务抢通",
      "operationName": "隧道调优",
      "validityPeriod": "2026-01-01~2030-12-31"
    },
    {
      "policyId": null,
      "scene": "医疗专线",
      "actionType": "业务恢复",
      "operationName": "频段调整",
      "validityPeriod": "2026-06-01~2030-06-18"
    }
  ]
}
```

**校验理由（LLM reasoning）**：操作类型为“新增授权策略”，在枚举范围内。动网操作的授权策略列表包含两条记录，均提供了创建授权策略所需的业务场景、处置类型、操作名称和有效期；policyId在创建场景下非必填，未提供时按schema输出null。两条有效期均为日期区间，起始日期未晚于结束日期，且日期区间写法未被约束明确排除，格式变体默认通过。综上无语义或格式错误。

**键名泄漏扫描**：无泄漏

**判定**：match=True（client_prompt=True）

---

## c3-nl-mod-06  —  ✅ match

### 客户端（生成）

**原始输入**（from_text）

> 把7d8c7b00-3c8c-4f8e-9b1e-9b17b6a3e5c3和a1b2c3d4-5e6f-7a8b-9c0d-1e2f3a4b5c6d的有效期都改成永久生效

**生成结果：成功（渲染后 prompt）**

```
## 授权策略的操作类型
修改授权策略

## 授权策略的操作描述
请根据<授权策略的操作类型>和<动网操作的授权策略列表>完成相应的授权操作，按照<预期输出>中定义的结构返回授权策略的操作执行结果。<预期输出>表示预期返回内容。

## 动网操作的授权策略列表
1. 策略标识是7d8c7b00-3c8c-4f8e-9b1e-9b17b6a3e5c3，有效期是永久生效
2. 策略标识是a1b2c3d4-5e6f-7a8b-9c0d-1e2f3a4b5c6d，有效期是永久生效

## 预期输出
1. 授权操作执行结果，取值范围： 成功、失败、部分成功；
2. 授权操作执行成功时，返回执行成功的<动网操作的授权策略列表>；
3. 授权操作执行失败或部分成功时，返回失败列表，包含授权策略和失败原因；
```

**提参理由（LLM reasoning）**：输入中“改成”属于修改授权策略的认可同义对，因此“授权策略的操作类型”映射为“修改授权策略”。修改对象为两个策略标识，有效期目标为“永久生效”，按修改形列表格式合并提取为两个编号条目。

### 服务端（校验 + 提参）

**validateAndDataFilling 输入 schema（suite 默认 param-schema.json）**：

```json
{
  "type": "object",
  "properties": {
    "operationType": {
      "type": "string",
      "enum": [
        "新增授权策略",
        "修改授权策略",
        "删除授权策略",
        "查询授权策略"
      ],
      "description": "授权策略的操作类型"
    },
    "policyList": {
      "type": "array",
      "description": "动网操作的授权策略列表，按输入中的出现顺序排列；未提供时为空列表",
      "items": {
        "type": "object",
        "properties": {
          "policyId": {
            "type": "string",
            "description": "策略的唯一标识"
          },
          "scene": {
            "type": "string",
            "description": "动网操作支撑的业务场景"
          },
          "actionType": {
            "type": "string",
            "description": "动网操作的处置类型"
          },
          "operationName": {
            "type": "string",
            "description": "所授权的动网操作的具体名称"
          },
          "validityPeriod": {
            "type": "string",
            "description": "授权策略的生效时间区间"
          }
        }
      }
    }
  }
}
```

（输入 prompt 即上方客户端渲染结果，此处不重复。）

**校验与提参结果**：`success`

**提参结果**：

```json
{
  "operationType": "修改授权策略",
  "policyList": [
    {
      "policyId": "7d8c7b00-3c8c-4f8e-9b1e-9b17b6a3e5c3",
      "scene": null,
      "actionType": null,
      "operationName": null,
      "validityPeriod": "永久生效"
    },
    {
      "policyId": "a1b2c3d4-5e6f-7a8b-9c0d-1e2f3a4b5c6d",
      "scene": null,
      "actionType": null,
      "operationName": null,
      "validityPeriod": "永久生效"
    }
  ]
}
```

**校验理由（LLM reasoning）**：操作类型为修改授权策略，取值在enum范围内；修改操作下条目需提供策略标识与有效期，两条策略均提供了策略标识且有效期为合法的“永久生效”，未发现必填字段缺失、格式错误或语义冲突；按schema提取策略列表，并将未提供的业务场景、处置类型、操作名称置为null。

**键名泄漏扫描**：无泄漏

**判定**：match=True（client_prompt=True）

---

## c3-nl-mod-06-varfields  —  ✅ match
[【差分对】配对基线：c3-nl-mod-06]

### 客户端（生成）

**原始输入**（from_text）

> 把7d8c7b00-3c8c-4f8e-9b1e-9b17b6a3e5c3和a1b2c3d4-5e6f-7a8b-9c0d-1e2f3a4b5c6d的有效期都改成永久生效

**生成结果：成功（渲染后 prompt）**

```
## 授权策略的操作类型
修改授权策略

## 授权策略的操作描述
请根据<授权策略的操作类型>和<动网操作的授权策略列表>完成相应的授权操作，按照<预期输出>中定义的结构返回授权策略的操作执行结果。<预期输出>表示预期返回内容。

## 动网操作的授权策略列表
1. 策略标识是7d8c7b00-3c8c-4f8e-9b1e-9b17b6a3e5c3，有效期是永久生效
2. 策略标识是a1b2c3d4-5e6f-7a8b-9c0d-1e2f3a4b5c6d，有效期是永久生效

## 预期输出
1. 授权操作执行结果，取值范围： 成功、失败、部分成功；
2. 授权操作执行成功时，返回执行成功的<动网操作的授权策略列表>；
3. 授权操作执行失败或部分成功时，返回失败列表，包含授权策略和失败原因；
```

**提参理由（LLM reasoning）**：输入中的“改成”属于“修改授权策略”的认可同义对，映射为“修改授权策略”。两个UUID均为修改对象，有效期统一改为“永久生效”，按修改形策略列表格式拆分为两个编号条目。

### 服务端（校验 + 提参）

**validateAndDataFilling 输入 schema（本用例变异 schema）**：

```json
{
  "type": "object",
  "properties": {
    "operationType": {
      "type": "string",
      "enum": [
        "新增授权策略",
        "修改授权策略",
        "删除授权策略",
        "查询授权策略"
      ],
      "description": "授权策略的操作类型"
    },
    "policyList": {
      "type": "array",
      "description": "动网操作的授权策略列表，按输入中的出现顺序排列；未提供时为空列表",
      "items": {
        "type": "object",
        "properties": {
          "policyId": {
            "type": "string",
            "description": "策略的唯一标识"
          },
          "newValidityPeriod": {
            "type": "string",
            "description": "修改后的生效时间区间，YYYY-MM-DD~YYYY-MM-DD或永久生效"
          }
        }
      }
    }
  }
}
```

（输入 prompt 即上方客户端渲染结果，此处不重复。）

**校验与提参结果**：`success`

**提参结果**：

```json
{
  "operationType": "修改授权策略",
  "policyList": [
    {
      "policyId": "7d8c7b00-3c8c-4f8e-9b1e-9b17b6a3e5c3",
      "newValidityPeriod": "永久生效"
    },
    {
      "policyId": "a1b2c3d4-5e6f-7a8b-9c0d-1e2f3a4b5c6d",
      "newValidityPeriod": "永久生效"
    }
  ]
}
```

**校验理由（LLM reasoning）**：（未采集）

**键名泄漏扫描**：无泄漏

**判定**：match=True（client_prompt=True）

---

## c4-nl-del-01  —  ✅ match

### 客户端（生成）

**原始输入**（from_text）

> 删除7d8c7b00-3c8c-4f8e-9b1e-9b17b6a3e5c3这个授权策略

**生成结果：成功（渲染后 prompt）**

```
## 授权策略的操作类型
删除授权策略

## 授权策略的操作描述
请根据<授权策略的操作类型>和<动网操作的授权策略列表>完成相应的授权操作，按照<预期输出>中定义的结构返回授权策略的操作执行结果。<预期输出>表示预期返回内容。

## 动网操作的授权策略列表
1. 策略标识是7d8c7b00-3c8c-4f8e-9b1e-9b17b6a3e5c3

## 预期输出
1. 授权操作执行结果，取值范围： 成功、失败、部分成功；
2. 授权操作执行成功时，返回执行成功的<动网操作的授权策略列表>；
3. 授权操作执行失败或部分成功时，返回失败列表，包含授权策略和失败原因；
```

**提参理由（LLM reasoning）**：操作类型：输入开头出现“删除”，属于合法取值“删除授权策略”的简写，按封闭取值范围规则直接映射为“删除授权策略”。策略列表：删除操作需提供策略标识，输入显式给出 UUID 7d8c7b00-3c8c-4f8e-9b1e-9b17b6a3e5c3，按删除条目格式组织为“1. 策略标识是7d8c7b00-3c8c-4f8e-9b1e-9b17b6a3e5c3”。

### 服务端（校验 + 提参）

**validateAndDataFilling 输入 schema（suite 默认 param-schema.json）**：

```json
{
  "type": "object",
  "properties": {
    "operationType": {
      "type": "string",
      "enum": [
        "新增授权策略",
        "修改授权策略",
        "删除授权策略",
        "查询授权策略"
      ],
      "description": "授权策略的操作类型"
    },
    "policyList": {
      "type": "array",
      "description": "动网操作的授权策略列表，按输入中的出现顺序排列；未提供时为空列表",
      "items": {
        "type": "object",
        "properties": {
          "policyId": {
            "type": "string",
            "description": "策略的唯一标识"
          },
          "scene": {
            "type": "string",
            "description": "动网操作支撑的业务场景"
          },
          "actionType": {
            "type": "string",
            "description": "动网操作的处置类型"
          },
          "operationName": {
            "type": "string",
            "description": "所授权的动网操作的具体名称"
          },
          "validityPeriod": {
            "type": "string",
            "description": "授权策略的生效时间区间"
          }
        }
      }
    }
  }
}
```

（输入 prompt 即上方客户端渲染结果，此处不重复。）

**校验与提参结果**：`success`

**提参结果**：

```json
{
  "operationType": "删除授权策略",
  "policyList": [
    {
      "policyId": "7d8c7b00-3c8c-4f8e-9b1e-9b17b6a3e5c3",
      "scene": null,
      "actionType": null,
      "operationName": null,
      "validityPeriod": null
    }
  ]
}
```

**校验理由（LLM reasoning）**：操作类型为删除授权策略，属于枚举允许范围；删除操作按模板正文仅需策略标识，输入第1条提供了有效策略标识，未发现必填缺失、格式错误或语义冲突。按参数schema提取操作类型和策略列表，未提供的字段置null。

**键名泄漏扫描**：无泄漏

**判定**：match=True（client_prompt=True）

---

## c5-nl-query-01  —  ✅ match

### 客户端（生成）

**原始输入**（from_text）

> 列出所有授权策略

**生成结果：成功（渲染后 prompt）**

```
## 授权策略的操作类型
查询授权策略

## 授权策略的操作描述
请根据<授权策略的操作类型>和<动网操作的授权策略列表>完成相应的授权操作，按照<预期输出>中定义的结构返回授权策略的操作执行结果。<预期输出>表示预期返回内容。

## 动网操作的授权策略列表

## 预期输出
1. 授权操作执行结果，取值范围： 成功、失败、部分成功；
2. 授权操作执行成功时，返回执行成功的<动网操作的授权策略列表>；
3. 授权操作执行失败或部分成功时，返回失败列表，包含授权策略和失败原因；
```

**提参理由（LLM reasoning）**：输入中的“列出”属于“查看/检索/搜索/查找/列出/列举→查询授权策略”的认可同义对，因此将“授权策略的操作类型”映射为“查询授权策略”。输入未提供具体的授权策略列表内容或带字段标签的查询条件，且查询授权策略下“动网操作的授权策略列表”为选填，因此该 slot 取 null，不报告错误。

### 服务端（校验 + 提参）

**validateAndDataFilling 输入 schema（suite 默认 param-schema.json）**：

```json
{
  "type": "object",
  "properties": {
    "operationType": {
      "type": "string",
      "enum": [
        "新增授权策略",
        "修改授权策略",
        "删除授权策略",
        "查询授权策略"
      ],
      "description": "授权策略的操作类型"
    },
    "policyList": {
      "type": "array",
      "description": "动网操作的授权策略列表，按输入中的出现顺序排列；未提供时为空列表",
      "items": {
        "type": "object",
        "properties": {
          "policyId": {
            "type": "string",
            "description": "策略的唯一标识"
          },
          "scene": {
            "type": "string",
            "description": "动网操作支撑的业务场景"
          },
          "actionType": {
            "type": "string",
            "description": "动网操作的处置类型"
          },
          "operationName": {
            "type": "string",
            "description": "所授权的动网操作的具体名称"
          },
          "validityPeriod": {
            "type": "string",
            "description": "授权策略的生效时间区间"
          }
        }
      }
    }
  }
}
```

（输入 prompt 即上方客户端渲染结果，此处不重复。）

**校验与提参结果**：`success`

**提参结果**：

```json
{
  "operationType": "查询授权策略",
  "policyList": []
}
```

**校验理由（LLM reasoning）**：操作类型章节的参数值行为“查询授权策略”，属于 schema 枚举允许的取值。动网操作的授权策略列表章节标题下没有非空参数值行，且查询操作下该列表为选填，因此不构成缺失。按 schema 提取 operationType 为“查询授权策略”，policyList 未提供时输出空数组。整体校验通过。

**键名泄漏扫描**：无泄漏

**判定**：match=True（client_prompt=True）

---

## b4-nl-bad-id-01  —  ✅ match

### 客户端（生成）

**原始输入**（from_text）

> 删除ABC-123这个授权策略

**生成结果：成功（渲染后 prompt）**

```
## 授权策略的操作类型
删除授权策略

## 授权策略的操作描述
请根据<授权策略的操作类型>和<动网操作的授权策略列表>完成相应的授权操作，按照<预期输出>中定义的结构返回授权策略的操作执行结果。<预期输出>表示预期返回内容。

## 动网操作的授权策略列表
1. 策略标识是ABC-123

## 预期输出
1. 授权操作执行结果，取值范围： 成功、失败、部分成功；
2. 授权操作执行成功时，返回执行成功的<动网操作的授权策略列表>；
3. 授权操作执行失败或部分成功时，返回失败列表，包含授权策略和失败原因；
```

**提参理由（LLM reasoning）**：输入中“删除”是合法取值范围中“删除授权策略”的简写，按规则13a直接映射；“ABC-123”是删除操作指向的策略标识，按删除形列表约束整理为“1. 策略标识是ABC-123”。未发现缺失或非法值。

### 服务端（校验 + 提参）

**validateAndDataFilling 输入 schema（suite 默认 param-schema.json）**：

```json
{
  "type": "object",
  "properties": {
    "operationType": {
      "type": "string",
      "enum": [
        "新增授权策略",
        "修改授权策略",
        "删除授权策略",
        "查询授权策略"
      ],
      "description": "授权策略的操作类型"
    },
    "policyList": {
      "type": "array",
      "description": "动网操作的授权策略列表，按输入中的出现顺序排列；未提供时为空列表",
      "items": {
        "type": "object",
        "properties": {
          "policyId": {
            "type": "string",
            "description": "策略的唯一标识"
          },
          "scene": {
            "type": "string",
            "description": "动网操作支撑的业务场景"
          },
          "actionType": {
            "type": "string",
            "description": "动网操作的处置类型"
          },
          "operationName": {
            "type": "string",
            "description": "所授权的动网操作的具体名称"
          },
          "validityPeriod": {
            "type": "string",
            "description": "授权策略的生效时间区间"
          }
        }
      }
    }
  }
}
```

（输入 prompt 即上方客户端渲染结果，此处不重复。）

**校验与提参结果**：`success`

**提参结果**：

```json
{
  "operationType": "删除授权策略",
  "policyList": [
    {
      "policyId": "ABC-123",
      "scene": null,
      "actionType": null,
      "operationName": null,
      "validityPeriod": null
    }
  ]
}
```

**校验理由（LLM reasoning）**：操作类型“删除授权策略”在枚举范围内；删除授权策略的必填参数仅为授权策略标识，输入列表第1条提供了策略标识ABC-123，非空且无明显格式或取值违规，因此校验通过；其余字段在删除场景下未要求，按参数schema输出为null。

**键名泄漏扫描**：无泄漏

**判定**：match=True（client_prompt=True）

---

## b3-nl-invalid-mod-01  —  ✅ match

### 客户端（生成）

**原始输入**（from_text）

> 把7d8c7b00-3c8c-4f8e-9b1e-9b17b6a3e5c3这个授权的业务场景改成校园专网

**生成结果：成功（渲染后 prompt）**

```
## 授权策略的操作类型
修改授权策略

## 授权策略的操作描述
请根据<授权策略的操作类型>和<动网操作的授权策略列表>完成相应的授权操作，按照<预期输出>中定义的结构返回授权策略的操作执行结果。<预期输出>表示预期返回内容。

## 动网操作的授权策略列表
1. 策略标识是7d8c7b00-3c8c-4f8e-9b1e-9b17b6a3e5c3，业务场景改为校园专网

## 预期输出
1. 授权操作执行结果，取值范围： 成功、失败、部分成功；
2. 授权操作执行成功时，返回执行成功的<动网操作的授权策略列表>；
3. 授权操作执行失败或部分成功时，返回失败列表，包含授权策略和失败原因；
```

**提参理由（LLM reasoning）**：输入中的“改成”属于操作类型同义对清单中“修改授权策略”的认可同义词，因此“授权策略的操作类型”提取为“修改授权策略”。修改类输入中提供了策略标识 UUID 和修改描述“业务场景改成校园专网”，按规则17将作用对象标识与修改描述合并保留在“动网操作的授权策略列表”中，并格式化为单条条目。未发现缺失或违反封闭取值范围的情况。

### 服务端（校验 + 提参）

**validateAndDataFilling 输入 schema（suite 默认 param-schema.json）**：

```json
{
  "type": "object",
  "properties": {
    "operationType": {
      "type": "string",
      "enum": [
        "新增授权策略",
        "修改授权策略",
        "删除授权策略",
        "查询授权策略"
      ],
      "description": "授权策略的操作类型"
    },
    "policyList": {
      "type": "array",
      "description": "动网操作的授权策略列表，按输入中的出现顺序排列；未提供时为空列表",
      "items": {
        "type": "object",
        "properties": {
          "policyId": {
            "type": "string",
            "description": "策略的唯一标识"
          },
          "scene": {
            "type": "string",
            "description": "动网操作支撑的业务场景"
          },
          "actionType": {
            "type": "string",
            "description": "动网操作的处置类型"
          },
          "operationName": {
            "type": "string",
            "description": "所授权的动网操作的具体名称"
          },
          "validityPeriod": {
            "type": "string",
            "description": "授权策略的生效时间区间"
          }
        }
      }
    }
  }
}
```

（输入 prompt 即上方客户端渲染结果，此处不重复。）

**校验与提参结果**：`validation_semantic_rejected`

```json
[
  {
    "slot_name": "policyList",
    "code": "invalid_value",
    "message": "修改授权策略仅支持修改有效期，但动网操作的授权策略列表第1条试图将业务场景改为校园专网，属于修改不允许修改的字段。"
  }
]
```

**校验理由（LLM reasoning）**：操作类型为“修改授权策略”，已提取。策略列表第1条提供策略标识和将业务场景改为校园专网的意图。模板正文规定修改授权策略仅支持修改有效期，因此该业务场景修改意图超出可修改范围，判定为invalid_value，semantic_verdict=false。params按参数schema提取：policyId取自策略标识，scene取值为校园专网，其余未提供字段置null。

**键名泄漏扫描**：无泄漏

**判定**：match=True（client_prompt=False）

---

## b2-nl-format-01  —  ✅ match

### 客户端（生成）

**原始输入**（from_text）

> 新增授权，场景校园专网，处置业务抢通，操作隧道调优，有效期2026-13-01~2030-12-31

**生成结果：成功（渲染后 prompt）**

```
## 授权策略的操作类型
新增授权策略

## 授权策略的操作描述
请根据<授权策略的操作类型>和<动网操作的授权策略列表>完成相应的授权操作，按照<预期输出>中定义的结构返回授权策略的操作执行结果。<预期输出>表示预期返回内容。

## 动网操作的授权策略列表
1. 业务场景是校园专网，处置类型是业务抢通，操作名称是隧道调优，有效期是2026-13-01~2030-12-31

## 预期输出
1. 授权操作执行结果，取值范围： 成功、失败、部分成功；
2. 授权操作执行成功时，返回执行成功的<动网操作的授权策略列表>；
3. 授权操作执行失败或部分成功时，返回失败列表，包含授权策略和失败原因；
```

**提参理由（LLM reasoning）**：“新增授权”包含封闭取值范围内的合法简写“新增”，按规则13a映射为“新增授权策略”。策略列表按新增形组织为单条，字段分别从“场景校园专网”“处置业务抢通”“操作隧道调优”“有效期2026-13-01~2030-12-31”映射为业务场景、处置类型、操作名称、有效期，并保留日期原文（已为 YYYY-MM-DD~YYYY-MM-DD 区间格式，月值异常按规则16原样提取，不在此阶段拦截）。

### 服务端（校验 + 提参）

**validateAndDataFilling 输入 schema（suite 默认 param-schema.json）**：

```json
{
  "type": "object",
  "properties": {
    "operationType": {
      "type": "string",
      "enum": [
        "新增授权策略",
        "修改授权策略",
        "删除授权策略",
        "查询授权策略"
      ],
      "description": "授权策略的操作类型"
    },
    "policyList": {
      "type": "array",
      "description": "动网操作的授权策略列表，按输入中的出现顺序排列；未提供时为空列表",
      "items": {
        "type": "object",
        "properties": {
          "policyId": {
            "type": "string",
            "description": "策略的唯一标识"
          },
          "scene": {
            "type": "string",
            "description": "动网操作支撑的业务场景"
          },
          "actionType": {
            "type": "string",
            "description": "动网操作的处置类型"
          },
          "operationName": {
            "type": "string",
            "description": "所授权的动网操作的具体名称"
          },
          "validityPeriod": {
            "type": "string",
            "description": "授权策略的生效时间区间"
          }
        }
      }
    }
  }
}
```

（输入 prompt 即上方客户端渲染结果，此处不重复。）

**校验与提参结果**：`validation_semantic_rejected`

```json
[
  {
    "slot_name": "policyList",
    "code": "format_error",
    "message": "动网操作的授权策略列表第1条的有效期“2026-13-01~2030-12-31”中月份“13”无效，日期格式不合法。"
  }
]
```

**校验理由（LLM reasoning）**：（未采集）

**键名泄漏扫描**：无泄漏

**判定**：match=True（client_prompt=True）

---

## b2-nl-format-01-varreq  —  ✅ match
[【差分对】配对基线：b2-nl-format-01]

### 客户端（生成）

**原始输入**（from_text）

> 新增一个授权，业务场景校园专网，处置类型网络出了问题要赶紧恢复，操作名称频段调整，有效期2026-01-01~2030-12-31

**生成结果：成功（渲染后 prompt）**

```
## 授权策略的操作类型
新增授权策略

## 授权策略的操作描述
请根据<授权策略的操作类型>和<动网操作的授权策略列表>完成相应的授权操作，按照<预期输出>中定义的结构返回授权策略的操作执行结果。<预期输出>表示预期返回内容。

## 动网操作的授权策略列表
1. 业务场景是校园专网，处置类型是网络出了问题要赶紧恢复，操作名称是频段调整，有效期是2026-01-01~2030-12-31

## 预期输出
1. 授权操作执行结果，取值范围： 成功、失败、部分成功；
2. 授权操作执行成功时，返回执行成功的<动网操作的授权策略列表>；
3. 授权操作执行失败或部分成功时，返回失败列表，包含授权策略和失败原因；
```

**提参理由（LLM reasoning）**：输入中的“新增”命中封闭取值范围中“新增授权策略”的简写，按规则13a映射为“新增授权策略”。由于操作类型为新增，授权策略列表为必填，输入中依次给出了业务场景、处置类型、操作名称、有效期四个新增形字段，按值约束顺序整理为编号条目，日期已符合YYYY-MM-DD~YYYY-MM-DD格式，故直接保留。未发现缺失或非法值，slot_errors为空。

### 服务端（校验 + 提参）

**validateAndDataFilling 输入 schema（本用例变异 schema）**：

```json
{
  "type": "object",
  "properties": {
    "operationType": {
      "type": "string",
      "enum": [
        "新增授权策略",
        "修改授权策略",
        "删除授权策略",
        "查询授权策略"
      ],
      "description": "授权策略的操作类型"
    },
    "policyList": {
      "type": "array",
      "description": "动网操作的授权策略列表，按输入中的出现顺序排列；未提供时为空列表",
      "items": {
        "type": "object",
        "properties": {
          "policyId": {
            "type": "string",
            "description": "策略的唯一标识"
          },
          "scene": {
            "type": "string",
            "description": "动网操作支撑的业务场景"
          },
          "actionType": {
            "type": "string",
            "description": "动网操作的处置类型，应为简短的处置动作短语（如业务抢通、业务优化、路由调整），非短语式的整句描述判 invalid_value"
          },
          "operationName": {
            "type": "string",
            "description": "所授权的动网操作的具体名称"
          },
          "validityPeriod": {
            "type": "string",
            "description": "授权策略的生效时间区间，YYYY-MM-DD~YYYY-MM-DD格式或\"永久生效\"；须为完整区间（仅单个日期视为区间不完整），且起始日期必须早于结束日期"
          }
        }
      }
    }
  }
}
```

（输入 prompt 即上方客户端渲染结果，此处不重复。）

**校验与提参结果**：`validation_semantic_rejected`

```json
[
  {
    "slot_name": "policyList",
    "code": "invalid_value",
    "message": "策略列表第1条的处置类型为整句描述“网络出了问题要赶紧恢复”，应使用简短的处置动作短语（如业务抢通、业务优化、路由调整）"
  }
]
```

**校验理由（LLM reasoning）**：操作类型“新增授权策略”在枚举范围内；动网操作的授权策略列表包含1条，新增必填的业务场景、处置类型、操作名称、有效期均已提供，有效期格式为完整区间且起始早于结束；但处置类型“网络出了问题要赶紧恢复”为整句描述，不符合参数 schema 要求的简短处置动作短语，故判定 invalid_value，整体校验不通过。

**键名泄漏扫描**：无泄漏

**判定**：match=True（client_prompt=True）

---

## b1-nl-missing-01  —  ✅ match

### 客户端（生成）

**原始输入**（from_text）

> 加个校园专网的授权，处置用业务抢通，做个隧道调优，有效期先不填后面补

**生成结果：成功（渲染后 prompt）**

```
## 授权策略的操作类型
新增授权策略

## 授权策略的操作描述
请根据<授权策略的操作类型>和<动网操作的授权策略列表>完成相应的授权操作，按照<预期输出>中定义的结构返回授权策略的操作执行结果。<预期输出>表示预期返回内容。

## 动网操作的授权策略列表
1. 业务场景是校园专网，处置类型是业务抢通，操作名称是隧道调优

## 预期输出
1. 授权操作执行结果，取值范围： 成功、失败、部分成功；
2. 授权操作执行成功时，返回执行成功的<动网操作的授权策略列表>；
3. 授权操作执行失败或部分成功时，返回失败列表，包含授权策略和失败原因；
```

**提参理由（LLM reasoning）**：“加个”命中同义对清单中的“加个→新增授权策略”，因此操作类型提取为“新增授权策略”。策略列表中，“校园专网”对应业务场景，“业务抢通”对应处置类型，“隧道调优”对应操作名称；“有效期先不填后面补”属于明确的字段缺省声明，按规则不作为字段值提取，因此仅保留已提供的三个字段并按要求格式化为编号条目。未触发 slot_errors。

### 服务端（校验 + 提参）

**validateAndDataFilling 输入 schema（suite 默认 param-schema.json）**：

```json
{
  "type": "object",
  "properties": {
    "operationType": {
      "type": "string",
      "enum": [
        "新增授权策略",
        "修改授权策略",
        "删除授权策略",
        "查询授权策略"
      ],
      "description": "授权策略的操作类型"
    },
    "policyList": {
      "type": "array",
      "description": "动网操作的授权策略列表，按输入中的出现顺序排列；未提供时为空列表",
      "items": {
        "type": "object",
        "properties": {
          "policyId": {
            "type": "string",
            "description": "策略的唯一标识"
          },
          "scene": {
            "type": "string",
            "description": "动网操作支撑的业务场景"
          },
          "actionType": {
            "type": "string",
            "description": "动网操作的处置类型"
          },
          "operationName": {
            "type": "string",
            "description": "所授权的动网操作的具体名称"
          },
          "validityPeriod": {
            "type": "string",
            "description": "授权策略的生效时间区间"
          }
        }
      }
    }
  }
}
```

（输入 prompt 即上方客户端渲染结果，此处不重复。）

**校验与提参结果**：`validation_semantic_rejected`

```json
[
  {
    "slot_name": "policyList",
    "code": "missing_required",
    "message": "动网操作的授权策略列表第1条缺少创建授权策略时必填的“有效期”字段。"
  }
]
```

**校验理由（LLM reasoning）**：操作类型为“新增授权策略”，在参数 schema 枚举范围内；模板正文要求新增授权策略列表条目必填业务场景、处置类型、操作名称、有效期。输入列表第1条提供了业务场景、处置类型和操作名称，但未提供有效期，因此校验失败并报告 missing_required。参数提取按 schema 结构进行，缺失字段置 null。

**键名泄漏扫描**：无泄漏

**判定**：match=True（client_prompt=True）

---

## c6-nl-mixed-07  —  ✅ match

### 客户端（生成）

**原始输入**（from_text）

> 新增三条授权：第一条业务场景校园专网，处置类型业务抢通，操作名称隧道调优，有效期永久生效；第二条业务场景医疗专线，处置类型业务恢复，操作名称频段调整，有效期2026-13-01~2030-12-31；第三条业务场景政企专网，处置类型业务优化，操作名称路由调整，有效期2027-03-15~2029-09-30

**生成结果：成功（渲染后 prompt）**

```
## 授权策略的操作类型
新增授权策略

## 授权策略的操作描述
请根据<授权策略的操作类型>和<动网操作的授权策略列表>完成相应的授权操作，按照<预期输出>中定义的结构返回授权策略的操作执行结果。<预期输出>表示预期返回内容。

## 动网操作的授权策略列表
1. 业务场景是校园专网，处置类型是业务抢通，操作名称是隧道调优，有效期是永久生效
2. 业务场景是医疗专线，处置类型是业务恢复，操作名称是频段调整，有效期是2026-13-01~2030-12-31
3. 业务场景是政企专网，处置类型是业务优化，操作名称是路由调整，有效期是2027-03-15~2029-09-30

## 预期输出
1. 授权操作执行结果，取值范围： 成功、失败、部分成功；
2. 授权操作执行成功时，返回执行成功的<动网操作的授权策略列表>；
3. 授权操作执行失败或部分成功时，返回失败列表，包含授权策略和失败原因；
```

**提参理由（LLM reasoning）**：输入“新增三条授权”中的“新增”符合封闭取值范围简写映射规则，提取为“新增授权策略”。策略列表提取三条条目，按字段顺序（业务场景、处置类型、操作名称、有效期）和编号格式组织；第二条有效期保留原文日期格式，不因格式异常而丢弃。

### 服务端（校验 + 提参）

**validateAndDataFilling 输入 schema（suite 默认 param-schema.json）**：

```json
{
  "type": "object",
  "properties": {
    "operationType": {
      "type": "string",
      "enum": [
        "新增授权策略",
        "修改授权策略",
        "删除授权策略",
        "查询授权策略"
      ],
      "description": "授权策略的操作类型"
    },
    "policyList": {
      "type": "array",
      "description": "动网操作的授权策略列表，按输入中的出现顺序排列；未提供时为空列表",
      "items": {
        "type": "object",
        "properties": {
          "policyId": {
            "type": "string",
            "description": "策略的唯一标识"
          },
          "scene": {
            "type": "string",
            "description": "动网操作支撑的业务场景"
          },
          "actionType": {
            "type": "string",
            "description": "动网操作的处置类型"
          },
          "operationName": {
            "type": "string",
            "description": "所授权的动网操作的具体名称"
          },
          "validityPeriod": {
            "type": "string",
            "description": "授权策略的生效时间区间"
          }
        }
      }
    }
  }
}
```

（输入 prompt 即上方客户端渲染结果，此处不重复。）

**校验与提参结果**：`validation_semantic_rejected`

```json
[
  {
    "slot_name": "policyList",
    "code": "format_error",
    "message": "策略列表第2条的有效期“2026-13-01~2030-12-31”存在无效月份（13月），请提供有效日期。"
  }
]
```

**校验理由（LLM reasoning）**：（未采集）

**键名泄漏扫描**：无泄漏

**判定**：match=True（client_prompt=True）

---

## a-data-starve-01  —  ✅ match

### 客户端（生成）

**原始输入**（from_data_with_schema）

data:
```json
{
  "scene": "校园专网",
  "actionType": "业务抢通"
}
```
input.schema:
```json
{
  "type": "object",
  "properties": {
    "scene": {
      "type": "string",
      "description": "动网操作支撑的业务场景"
    },
    "actionType": {
      "type": "string",
      "description": "动网操作的处置类型"
    }
  }
}
```

**生成结果：失败**

```json
{
  "code": "slot_validation_error",
  "message": "Required slots are missing or empty: 授权策略的操作类型",
  "slot_errors": [
    {
      "slot_name": "授权策略的操作类型",
      "code": "missing_required",
      "message": "Required slot is missing or empty"
    }
  ]
}
```

**提参理由（LLM reasoning）**：输入中仅有 scene=校园专网、actionType=业务抢通，未出现“新增、修改、删除、查询”等操作类型合法取值或其认可同义词，按封闭取值范围规则不能推断，故“授权策略的操作类型”置为 null 并报告 missing_input。scene 对应“业务场景”，actionType 对应“处置类型”，这两项属于动网操作授权策略列表中允许的查询条件字段，因此按“字段名是值”格式合并为一条编号列表。

### 服务端（校验 + 提参）

（客户端生成失败路径，服务端未被调用。）

**判定**：match=True（client_prompt=None）

---

## a-nl-neg-01  —  ✅ match

### 客户端（生成）

**原始输入**（from_text）

> 不要新增校园专网的授权策略，我还没准备好

**生成结果：失败**

```json
{
  "code": "slot_validation_error",
  "message": "Required slots are missing or empty: 授权策略的操作类型",
  "slot_errors": [
    {
      "slot_name": "授权策略的操作类型",
      "code": "missing_required",
      "message": "Required slot is missing or empty"
    }
  ]
}
```

**提参理由（LLM reasoning）**：输入中虽然出现“新增”，但位于“不要新增……我还没准备好”的否定/排除语义中，依据规则3不得作为正向slot值提取，因此“授权策略的操作类型”视为缺失并报告missing_input；“动网操作的授权策略列表”中的“校园专网”同样处于否定语义中，不提取，且操作类型未确定，不触发该列表的必填条件，故返回null。

### 服务端（校验 + 提参）

（客户端生成失败路径，服务端未被调用。）

**判定**：match=True（client_prompt=None）

---

## 差分对判读汇总

| 差分对 | 基线半 | 变异半 | 判读 |
|---|---|---|---|
| c1-nl-add-01/-varname | ✅ | ✅ | 健康 |
| c3-nl-mod-06/-varfields | ✅ | ✅ | 健康 |
| b2-nl-format-01/-varreq | ✅ | ✅ | 健康 |
