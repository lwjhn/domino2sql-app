
### domino service
#### 启动diiop服务
```
    load diiop
```
### 启动TCP PROXY [可选]
#### window版
```
    tcp-proxy.exe -l=":9898" -r="192.168.211.53:63148"
```
#### window/Linux版（JRE 8）
```shell
    java -jar tcp-proxy.jar 9898 192.168.211.53 63148

    # linux
    # kill app
    kill -9 $(ps -aef | grep port=9898 | grep -v grep | awk '{print $2}')
    # start tcp proxy
    nohup java -Xmx200m -Xms150m -jar tcp-proxy.jar 9898 192.168.211.53 63148 > nohup.log 2>&1 &
```
### 迁移配置文件
#### 例1
##### 表
```sql
    -- xc_demo.egov_dispatch_history definition
    CREATE TABLE `egov_dispatch_history` (
    `ID` varchar(32) NOT NULL,
    `DOMINOID` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `Subject` varchar(512) DEFAULT NULL,
    `DocWord` varchar(128) DEFAULT NULL,
    `DocType` varchar(128) DEFAULT NULL,
    `UrgentLevel` varchar(128) DEFAULT NULL,
    `DraftDept` varchar(128) DEFAULT NULL,
    `SecLevel` varchar(128) DEFAULT NULL,
    `FileType` varchar(128) DEFAULT NULL,
    `IsPublic` varchar(128) DEFAULT NULL,
    `MainSend` varchar(128) DEFAULT NULL,
    `CopySend` varchar(128) DEFAULT NULL,
    `PrintTerm` varchar(128) DEFAULT NULL,
    `Remark` varchar(128) DEFAULT NULL,
    `SignDateTime` timestamp NULL DEFAULT NULL,
    `DraftDate` timestamp NULL DEFAULT NULL,
    `PrintNum` int DEFAULT NULL,
    `JSONATT` text CHARACTER SET utf8 COLLATE utf8_general_ci,
    `OTHER` varchar(2048) DEFAULT NULL,
    PRIMARY KEY (`ID`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
##### 配置
```json
{
  "sql_url": "jdbc:mysql://192.168.210.153:3399?connectTimeout=3000&socketTimeout=60000&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC",
  "sql_username": "*****",
  "sql_password": "*****",
  "sql_driver": "com.mysql.cj.jdbc.Driver",
  "driver_manager_log": false,
  "options": [
    {
      "enable": true,
      "vesion": "1.6.7",
      "ftppath": "/FTP_XC/",
      "domino_server": "OA/SRV/FJSF",
      "domino_query": "Form=\"FlowForm\" & MSSDATABASE!=\"\" & DraftDate > [2020-07-20] & DraftDate < [2020-07-25]",
      "domino_queries": [
        {
          "enable": true,
          "error_continue": true,
          "domino_dbpath": "egov/dispatch.nsf"
        }
      ],
      "domino_after_prepared_driver": "com.lwjhn.domino2sql.driver.AfterPrepareDocument",
      "domino_prepared_sqlquery_driver": "com.rjsoft.prepared.PrepareSqlQueryRJDoc",
      "domino_process_statement_driver": "com.rjsoft.prepared.ProcessStatementRJDocNoCache",
      "extended_options": {
        "export_flow": true,
        "export_opinion": true,
        "export_processing": true,
        "sql_field_attachment": {
          "sql_name": "JSONATT",
          "jdbc_type": "VARCHAR",
          "scale_length": 0
        }
      },
      "sql_table": "EGOV_DISPATCH_HISTORY",
      "sql_field_others": [
        {
          "sql_name": "ID",
          "domino_name": "ArcXC_UUID_16",
          "jdbc_type": "VARCHAR",
          "scale_length": 0
        },
        {
          "sql_name": "DOMINOID",
          "domino_formula": "@Text(@DocumentUniqueID)",
          "jdbc_type": "VARCHAR",
          "scale_length": 0
        },
        {
          "sql_name": "Subject",
          "domino_name": "Subject",
          "jdbc_type": "VARCHAR",
          "scale_length": 0
        },
        {
          "sql_name": "DocWord",
          "domino_name": "DocWord",
          "jdbc_type": "VARCHAR",
          "scale_length": 0
        },
        {
          "sql_name": "DocType",
          "domino_name": "DocType",
          "jdbc_type": "VARCHAR",
          "scale_length": 0
        },
        {
          "sql_name": "UrgentLevel",
          "domino_name": "UrgentLevel",
          "jdbc_type": "VARCHAR",
          "scale_length": 0
        },
        {
          "sql_name": "DraftDept",
          "domino_name": "DraftDept",
          "jdbc_type": "VARCHAR",
          "scale_length": 0
        },
        {
          "sql_name": "SecLevel",
          "domino_name": "SecLevel",
          "jdbc_type": "VARCHAR",
          "scale_length": 0
        },
        {
          "sql_name": "FileType",
          "domino_name": "FileType",
          "jdbc_type": "VARCHAR",
          "scale_length": 0
        },
        {
          "sql_name": "IsPublic",
          "domino_name": "IsPublic",
          "jdbc_type": "VARCHAR",
          "scale_length": 0
        },
        {
          "sql_name": "MainSend",
          "domino_name": "MainSend",
          "jdbc_type": "VARCHAR",
          "scale_length": 0
        },
        {
          "sql_name": "CopySend",
          "domino_name": "CopySend",
          "jdbc_type": "VARCHAR",
          "scale_length": 0
        },
        {
          "sql_name": "PrintTerm",
          "domino_name": "PrintTerm",
          "jdbc_type": "VARCHAR",
          "scale_length": 0
        },
        {
          "sql_name": "Remark",
          "domino_name": "Remark",
          "jdbc_type": "VARCHAR",
          "scale_length": 0
        },
        {
          "sql_name": "SignDateTime",
          "domino_formula": "temp:=SignDate;temp:=@If(@IsTime(temp);temp;@TextToTime(@ReplaceSubstring(temp;\"年\":\"月\":\"日\";\"-\":\"-\":\"\")));@if(@IsError(temp) | @IsNull(temp);@Nothing;temp);",
          "jdbc_type": "TIMESTAMP",
          "scale_length": 0
        },
        {
          "sql_name": "DraftDate",
          "domino_name": "DraftDate",
          "jdbc_type": "TIMESTAMP",
          "scale_length": 0
        },
        {
          "sql_name": "PrintNum",
          "jdbc_type": "INTEGER",
          "domino_formula": "1",
          "scale_length": 0
        },
        {
          "sql_name": "OTHER",
          "jdbc_type": "VARCHAR",
          "domino_formula": "@Nothing",
          "scale_length": 0
        }
      ]
    }
  ]
}
```

### 启动迁移服务
`domino2sql_app_jar`，启动`domino2sql-app.jar`。注意相关依赖包，当前目录下已提供达梦驱动
```
java -DDominoHost="192.168.211.53:63148" -DDominoUser="Admin" -DDominoPassword="*****" -classpath . -jar ./domino2sql-app.jar

java -DDominoHost="192.168.210.153:9898" -DDominoUser="Admin" -DDominoPassword="*****" -DDominoPath="./arc.sql.config.json" -DDominoOutput="./arc.sql.config.output.json" -jar ./domino2sql-app.jar



注意：domino服务器需要启动doiip服务，命令load diiop

```
#### 附件乱码
linux 导出附件中文乱码，运行参数宜加上  `-Dsun.jnu.encoding=UTF-8`

#### 驱动依赖
执行 `java -verbose`
查看jvm lib 路径
```shell
C:\Users\Administrator>java -verbose
[Opened C:\Program Files\Java\jdk1.8.0_112\jre\lib\rt.jar]
```
依赖拷贝到lib的`ext`文件夹中
