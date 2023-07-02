## 容器化构建

```$shell script
mvn clean package
```

```shell script
mvn docker:build
```

> 后台执行，服务器重启后，容器自动重启

```shell script
docker run \
-d \
--restart always \
--name innospots-data-server \
-p 8787:8787 \
-e CONSUL_HOST=11.11.1.188 \
-e ENV_PROFILE=dev \
innospots-data-server:1.0.0-SNAPSHOT
```

> 本地测试执行，退出后运行容器销毁

```shell script
docker run \
-it \
--rm \
--name innospots-data-server \
-p 8787:8787 \
-e CONSUL_HOST=11.11.1.188 \
-e ENV_PROFILE=dev \
innospots-data-server:1.0.0-SNAPSHOT
```

> 进入容器命令，屏蔽容器默认entrypoint

```shell script
docker run --entrypoint=sh --rm -it -e CONSUL_HOST=11.11.1.188 innospots-data-server:1.0.0-SNAPSHOT
```